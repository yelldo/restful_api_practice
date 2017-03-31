package com.ch.service.context;

import com.ch.frame.Globals;
import com.ch.frame.session.SessionManager;

public class ServiceContext {
	private static ThreadLocal<ServiceContext> cache = new ThreadLocal<>();
	public static ServiceContext get(){
		return cache.get();
	}
	public static void set(ServiceContext ctx){
		cache.set(ctx);
	}
	public static void remove(){
		cache.remove();
	}
	//会话ID
	private String token;
	/**
	 * 添加实体对象删除日志
	 * @param obj
	 */
	public void addRemoveLogger(Object obj) {
		
	}
	/**
	 * 获取当前操作员的ID
	 * @return
	 */
	public Long getUserId(){
		return SessionManager.get().get(token);
	}
	/**
	 * 获取当前机构编号
	 * @return
	 */
	//public Long getOrgId(){
	//	return SessionManager.get().get(token);
	//}
	/**
	 * 新加或更新数据
	 * @param orientData
	 * @param obj
	 */
	public void addUpdateLogger(String orientData, Object obj) {
		
	}

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
