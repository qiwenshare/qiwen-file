package com.mac.common.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TableData<T> {
    private T data;
    private int count;
    private String msg;
    private boolean success = true;
    private int code = 0;

}
