package com.ch.web.proxy;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * 后台业务的代理工厂
 * @author Administrator
 *
 */
public class ServiceProxyFactory {
	private static Map<Class<?>, Object> proxies = new HashMap<Class<?>, Object>();
	/**
	 * 获取代理对象
	 * @param intelClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(Class<T> intelClass){
		if(proxies.containsKey(intelClass)){
			return (T)proxies.get(intelClass);
		}
		RemoteServiceInvokeHandler handler 
			= new RemoteServiceInvokeHandler(intelClass);
		Object proxy = Proxy.newProxyInstance(intelClass.getClassLoader(), new Class[]{intelClass}, handler);
		proxies.put(intelClass, proxy);
		return (T)proxy;
	}
}
