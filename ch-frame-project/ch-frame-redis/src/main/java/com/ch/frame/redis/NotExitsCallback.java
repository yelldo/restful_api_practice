package com.ch.frame.redis;

public interface NotExitsCallback<T> {
	public T get()throws Throwable;
}
