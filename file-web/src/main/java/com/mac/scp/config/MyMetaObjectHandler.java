package com.mac.scp.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 添加数据和修改数据填充修改时间和创建时间
 *
 * @author WeiHongBin
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

	@Override
	public void insertFill(MetaObject metaObject) {
		this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		Boolean delete = (Boolean) this.getFieldValByName("delete", metaObject);
		// 逻辑删除 ，填充 删除时间
		Optional.ofNullable(delete).ifPresent(del -> {
			if (del) {
				this.setFieldValByName("deleteTime", LocalDateTime.now(), metaObject);
			}
		});
		this.setFieldValByName("modifyTime", LocalDateTime.now(), metaObject);
	}

}