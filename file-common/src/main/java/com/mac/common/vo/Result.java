package com.mac.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * 响应对象
 *
 * @author WeiHongBin
 */
@ApiModel(description = "响应数据对象")
@Component
@Data
public class Result<T> {

	@ApiModelProperty(value = "响应状态码")
	private Integer code;

	@ApiModelProperty(value = "响应消息")
	private String msg;

	@ApiModelProperty(value = "响应对象")
	private T data;

	/**
	 * 请求成功并且无响应
	 */
	public Result() {
		this.code = HttpStatus.OK.value();
		this.msg = HttpStatus.OK.name();
	}

	/**
	 * 请求成功
	 *
	 * @param data 响应的数据
	 */
	public Result(T data) {
		this.code = HttpStatus.OK.value();
		this.msg = HttpStatus.OK.name();
		this.data = data;
	}

	/**
	 * 请求失败
	 *
	 * @param code 错误 code
	 * @param msg  错误消息
	 */
	public Result(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	/**
	 * 请求失败
	 * code 默认: 500
	 *
	 * @param msg 错误消息
	 */
	public Result(String msg) {
		this.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
		this.msg = msg;
	}
}
