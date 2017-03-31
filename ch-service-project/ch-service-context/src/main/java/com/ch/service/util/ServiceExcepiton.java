package com.ch.service.util;

import com.ch.frame.exception.GeneralException;

public class ServiceExcepiton extends GeneralException {
	private static final long serialVersionUID = 1L;
	public ServiceExcepiton(Throwable e) {
		super(e);
	}
	public ServiceExcepiton(String msg, Throwable e) {
		super(msg, e);
	}
	public ServiceExcepiton(String msg) {
		super(msg);
	}
}
