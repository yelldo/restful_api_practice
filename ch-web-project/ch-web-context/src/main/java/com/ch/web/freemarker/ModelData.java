package com.ch.web.freemarker;

import com.ch.web.context.WebConfig;
import com.ch.web.context.WebContext;
import com.ch.web.context.method.IsBlankMethod;
import com.ch.web.exception.WebException;

import java.util.HashMap;

public class ModelData extends HashMap<String, Object>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ModelData() {
		//初始化基本参数
		WebContext ctx = WebContext.get();
		if(ctx == null){
			throw new WebException("Not found web context");
		}
		//工程上下文路径		
		addObject("_baseurl", ctx.getBaseUrl());
		//当前用户ID
		addObject("_userid", ctx.getUserId());
		//当前机构ID
		addObject("_orgid", ctx.getOrgId());
		//所有session数据
		addObject("_session", ctx.getAllSessionData());
		//页面配置
		addObject("_config", WebConfig.get());
		//自定义方法
		addObject("isBlank", new IsBlankMethod());
	}

	public ModelData add(String key, Object obj){
		this.put(key, obj);
		return this;
	}

	public ModelData addObject(String key, Object obj) {
		return this.add(key, obj);
	}
}
