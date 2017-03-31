package com.ch.frame.redis;

import java.io.Serializable;

/**
 * 获取缓存时返回的结果
 * @author Administrator
 *
 */
public class CacheResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//数据
	private Object data;
	//是否存在
	private boolean exists;
	public CacheResult(Object data, boolean exists) {
		this.data = data;
		this.exists = exists;
	}
	@SuppressWarnings("unchecked")
	public <T> T getData() {
		return (T)data;
	}
	public boolean isExists() {
		return exists;
	}
}
