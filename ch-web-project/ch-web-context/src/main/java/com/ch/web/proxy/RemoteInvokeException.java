package com.ch.web.proxy;

import com.ch.web.exception.WebException;

public class RemoteInvokeException extends WebException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public RemoteInvokeException(Throwable e) {
		super(e);
	}
	public RemoteInvokeException(String msg, Throwable e) {
		super(msg, e);
	}
	public RemoteInvokeException(String msg) {
		super(msg);
	}
}
