package com.qiwenshare.common.util.concurrent.locks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * redis实现分布式锁
 *
 */
@Component
public class RedisLock{

    private static final Logger log = LoggerFactory.getLogger(RedisLock.class);

    /**
     * 默认轮休获取锁间隔时间， 单位：毫秒
     */
    private static final int DEFAULT_ACQUIRE_RESOLUTION_MILLIS = 100;

    private static final String UNLOCK_LUA;

    private static final long LOCK_EXPIRE_TIME = 60 * 15; //获取锁最大15分钟就会过期


    @Resource
    RedisTemplate<String, Object> redisTemplate;

    static {
        StringBuilder lua = new StringBuilder();
        lua.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        lua.append("then ");
        lua.append("    return redis.call(\"del\",KEYS[1]) ");
        lua.append("else ");
        lua.append("    return 0 ");
        lua.append("end ");
        UNLOCK_LUA = lua.toString();
    }

    private final ThreadLocal<Map<String, LockVO>> lockMap = new ThreadLocal<>();

    /**
     * 获取锁，没有获取到则一直等待
     *
     * @param key    redis key
     */
    public void lock(final String key) {

        try {
            acquireLock(key, LOCK_EXPIRE_TIME, -1);
        } catch (Exception e) {
            throw new RuntimeException("acquire lock exception", e);
        }
    }

    /**
     * 释放锁
     *
     * @param key redis key
     */
    public void unlock(String key) {
        try {
            release(key);
        } catch (Exception e) {
            throw new RuntimeException("release lock exception", e);
        }
    }

    public boolean tryLock(final String key) {
        try {
            return acquireLock(key, LOCK_EXPIRE_TIME, -1);
        } catch (Exception e) {
            throw new RuntimeException("acquire lock exception", e);
        }
    }

    /**
     * 获取锁，指定时间内没有获取到，返回false。否则 返回true
     *
     * @param key      redis key
     * @param waitTime 获取锁超时时间, -1代表永不超时, 单位 秒
     */
    public boolean tryLock(String key, long time, TimeUnit unit) {
        try {
            return acquireLock(key, LOCK_EXPIRE_TIME, unit.toSeconds(time));
        } catch (Exception e) {
            throw new RuntimeException("acquire lock exception", e);
        }
    }

    /**
     * @param key      redis key
     * @param expire   锁过期时间, 单位 秒
     * @param waitTime 获取锁超时时间, -1代表永不超时, 单位 秒
     * @return if true success else fail
     * @throws InterruptedException 阻塞方法收到中断请求
     */
    private boolean acquireLock(String key, long expire, long waitTime) throws InterruptedException {
        //如果之前获取到了并且没有超时，则返回获取成功
        boolean acquired = acquired(key);
        if (acquired) {
            return true;
        }
        long acquireTime = waitTime == -1 ? -1 : waitTime * 1000 + System.currentTimeMillis();
        //同一个进程，对于同一个key锁，只允许先到的去尝试获取。
        // key.intern() 如果常量池中存在当前字符串, 就会直接返回当前字符串.
        // 如果常量池中没有此字符串, 会将此字符串放入常量池中后, 再返回
        synchronized (key.intern()) {
            String lockId = UUID.randomUUID().toString();
            do {
                long before = System.currentTimeMillis();
                boolean hasLock = tryLock(key, expire, lockId);
                //获取锁成功
                if (hasLock) {
                    long after = System.currentTimeMillis();
                    Map<String, LockVO> map = lockMap.get();
                    if (map == null) {
                        map = new HashMap<>(2);
                        lockMap.set(map);
                    }
                    map.put(key, new LockVO(1, lockId, expire * 1000 + before, expire * 1000 + after));
                    log.debug("acquire lock {} {} ", key, 1);
                    return true;
                }
                Thread.sleep(DEFAULT_ACQUIRE_RESOLUTION_MILLIS);
            } while (acquireTime == -1 || acquireTime > System.currentTimeMillis());
        }
        log.debug("acquire lock {} fail，because timeout ", key);
        return false;
    }



    /**
     * 释放锁
     *
     * @param key redis key
     */
    private void release(String key) {
        Map<String, LockVO> map = lockMap.get();
        if (map == null || map.size() == 0 || !map.containsKey(key)) {
            return;
        }
        LockVO vo = map.get(key);
        if (vo.afterExpireTime < System.currentTimeMillis()) {
            log.debug("release lock {}, because timeout ", key);
            map.remove(key);
            return;
        }
        int after = --vo.count;
        log.debug("release lock {} {} ", key, after);
        if (after > 0) {
            return;
        }
        map.remove(key);
        RedisCallback<Boolean> callback = (connection) ->
            connection.eval(UNLOCK_LUA.getBytes(StandardCharsets.UTF_8), ReturnType.BOOLEAN, 1,
                (key).getBytes(StandardCharsets.UTF_8), vo.lockId.getBytes(StandardCharsets.UTF_8));
        redisTemplate.execute(callback);
    }

    /**
     * @param key    锁的key
     * @param expire 锁的超时时间 秒
     * @param lockId 获取锁后，UUID生成的唯一ID
     * @return if true success else fail
     */
    private boolean tryLock(String key, long expire, String lockId) {
        try{
            RedisCallback<Boolean> callback = (connection) ->
                connection.set(
                        (key).getBytes(StandardCharsets.UTF_8),
                        lockId.getBytes(StandardCharsets.UTF_8),
                        Expiration.seconds(expire),
                        RedisStringCommands.SetOption.SET_IF_ABSENT);
            return (Boolean) redisTemplate.execute(callback);
        } catch (Exception e) {
            log.error("redis lock error.", e);
        }
        return false;
    }

    private static class LockVO {
        /**
         * 锁重入的次数
         */
        private int count;

        /**
         * 获取锁后，UUID生成的唯一ID
         */
        private String lockId;
        /**
         * 获取锁之前的时间戳
         */
        private long beforeExpireTime;
        /**
         * 获取到锁的时间戳
         */
        private long afterExpireTime;

        LockVO(int count, String lockId, long beforeExpireTime, long afterExpireTime) {
            this.count = count;
            this.lockId = lockId;
            this.beforeExpireTime = beforeExpireTime;
            this.afterExpireTime = afterExpireTime;
        }
    }

    private boolean acquired(String key) {
        Map<String, LockVO> map = lockMap.get();
        if (map == null || map.size() == 0 || !map.containsKey(key)) {
            return false;
        }

        LockVO vo = map.get(key);
        if (vo.beforeExpireTime < System.currentTimeMillis()) {
            log.debug("lock {} maybe release, because timeout ", key);
            return false;
        }
        int after = ++vo.count;
        log.debug("acquire lock {} {} ", key, after);
        return true;
    }

}

