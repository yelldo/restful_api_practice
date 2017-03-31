package com.ch.frame.exception;
/**
 * 所有异常的父类
 * @author Administrator
 *
 */
public class GeneralException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public GeneralException(Throwable e) {
		super(e);
	}
	public GeneralException(String msg, Throwable e) {
		super(msg, e);
	}
	public GeneralException(String msg) {
		super(msg);
	}
}
