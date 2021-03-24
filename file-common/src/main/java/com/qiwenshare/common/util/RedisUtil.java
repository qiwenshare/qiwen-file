package com.qiwenshare.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;
 
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisUtil {
 
    @Resource
    RedisTemplate<String, Object> redisTemplate;
 
    /**
     * 将值放入缓存
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
 
    /**
     * 字符串：取对象
     */
    public <T> T getObject(String key) {
        Object o = redisTemplate.opsForValue().get(key);
        if (o != null) {
            return (T) o;
        }
        return null;
    }
 
    /**
     * 将值放入缓存并设置时间-秒
     */
    public void set(String key, Object value, long time) {
        if (time > 0) {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }
 
    /**
     * 删除key
     */
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }
 
    /**
     * 创建锁
     * @param key         锁的Key
     * @param value       值(随便写毫无意义)
     * @param releaseTime 锁过期时间 防止死锁
     * @return boolean
     */
    public boolean lock(String key, int value, long releaseTime) {
        // 尝试获取锁
        Boolean boo = redisTemplate.opsForValue().setIfAbsent(key, value, releaseTime, TimeUnit.SECONDS);
        // 判断结果
        return boo != null && boo;
    }
 

    /**
     * 根据key删除锁
     */
    public void deleteLock(String key) {
        // 删除key即可释放锁
        deleteKey(key);
    }
}