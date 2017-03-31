package com.ch.web.base;

import com.ch.web.proxy.ServiceProxyFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 所有action父类
 * @author Administrator
 *
 */
public abstract class ParentAction {
	protected Log log = LogFactory.getLog(this.getClass());
	/**
	 * 获取代理服务对象
	 * @param cla
	 * @return
	 */
	protected <T> T getProxy(Class<T> cla){
		return ServiceProxyFactory.get(cla);
	}
	
}
