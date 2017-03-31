package com.ch.frame.mongodb;


import com.ch.frame.exception.GeneralException;

/**
 * 缓存异常
 */
public class MongodbException extends GeneralException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public MongodbException(Throwable e) {
        super(e);
    }

    public MongodbException(String msg, Throwable e) {
        super(msg, e);
    }

    public MongodbException(String msg) {
        super(msg);
    }
}
