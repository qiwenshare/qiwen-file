package com.qiwenshare.common.exception;

public abstract class UploadException extends RuntimeException{

    protected UploadException(String message) {
        super(message);
    }

    protected UploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
