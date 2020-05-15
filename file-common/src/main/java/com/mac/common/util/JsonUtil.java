package com.mac.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.mac.common.exception.UnifiedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


/**
 * ObjectMapper 序列化特殊处理
 * springboot 底层ObjectMapper 是缺省Bean
 * 注册成为Bean 会覆盖系统默认的 ObjectMapper
 *
 * @author WeiHongBin
 */
@Slf4j
@Component
public class JsonUtil extends ObjectMapper {


	public JsonUtil() {
		super();
		JavaTimeModule module = new JavaTimeModule();
		module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		module.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
		module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		this.registerModules(new Jdk8Module(), module).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Override
	public String writeValueAsString(Object value) {
		try {
			return super.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new UnifiedException("JSON 转换异常", e);
		}
	}

	@Override
	public <T> T readValue(String content, TypeReference<T> valueTypeRef) {
		try {
			return super.readValue(content, valueTypeRef);
		} catch (JsonProcessingException e) {
			throw new UnifiedException("JSON 转换异常", e);
		}
	}

	@Override
	public <T> T readValue(String content, Class<T> valueType) {
		try {
			return super.readValue(content, valueType);
		} catch (IOException e) {
			throw new UnifiedException("JSON 转换异常", e);
		}
	}

	@Override
	public JsonNode readTree(String content) {
		try {
			return super.readTree(content);
		} catch (IOException e) {
			throw new UnifiedException("JSON 转换异常", e);
		}
	}
}
