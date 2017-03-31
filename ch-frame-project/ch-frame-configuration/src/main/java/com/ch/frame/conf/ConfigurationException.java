package com.ch.frame.conf;


import com.ch.frame.exception.GeneralException;

public class ConfigurationException extends GeneralException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ConfigurationException(Throwable e) {
        super(e);
    }

    public ConfigurationException(String msg, Throwable e) {
        super(msg, e);
    }

    public ConfigurationException(String msg) {
        super(msg);
    }
}
