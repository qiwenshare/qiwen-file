package com.mac.common.cbb;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RestResult<T> {
	private boolean success = true;
	private String errorCode;
	private String errorMessage;
	private T data;
}
