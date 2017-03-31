package com.ch.web.exception;

import com.ch.frame.exception.GeneralException;

public class WebException extends GeneralException {
	private static final long serialVersionUID = 1L;
	public WebException(Throwable e) {
		super(e);
	}
	public WebException(String msg, Throwable e) {
		super(msg, e);
	}
	public WebException(String msg) {
		super(msg);
	}
}
