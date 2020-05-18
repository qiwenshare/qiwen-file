package com.mac.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/**
 * 统一异常
 *
 * @author WeiHongBin
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class UnifiedException extends RuntimeException {

	private Integer status;
	private String message;

	/**
	 * Unified exception
	 * 统一异常
	 */
	public UnifiedException() {
		super("服务器忙,请稍候重试");
		this.status = HttpStatus.INTERNAL_SERVER_ERROR.value();
		this.message = "服务器忙,请稍候重试";
	}

	/**
	 * 统一异常
	 *
	 * @param status  异常状态码
	 * @param message 异常消息
	 */
	public UnifiedException(HttpStatus status, String message) {
		super(message);
		this.status = status.value();
		this.message = message;
	}

	/**
	 * 统一异常
	 *
	 * @param message 异常消息
	 */
	public UnifiedException(String message) {
		super(message);
		this.status = HttpStatus.INTERNAL_SERVER_ERROR.value();
		this.message = message;
	}

	public UnifiedException(String message, Throwable e) {
		super(message, e);
		this.status = HttpStatus.INTERNAL_SERVER_ERROR.value();
		this.message = message;
	}

}

