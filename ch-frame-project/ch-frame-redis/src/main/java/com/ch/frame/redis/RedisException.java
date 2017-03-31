package com.ch.frame.redis;


import com.ch.frame.exception.GeneralException;

/**
 * 缓存异常
 */
public class RedisException extends GeneralException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public RedisException(Throwable e) {
        super(e);
    }

    public RedisException(String msg, Throwable e) {
        super(msg, e);
    }

    public RedisException(String msg) {
        super(msg);
    }
}
