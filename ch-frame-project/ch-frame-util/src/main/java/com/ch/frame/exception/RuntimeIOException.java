package com.ch.frame.exception;

/**
 * @author wangmz
 *         2017/1/16
 */
public class RuntimeIOException extends RuntimeException {
    public RuntimeIOException() {
    }

    public RuntimeIOException(String message) {
        super(message);
    }

    public RuntimeIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeIOException(Throwable cause) {
        super(cause);
    }
}
