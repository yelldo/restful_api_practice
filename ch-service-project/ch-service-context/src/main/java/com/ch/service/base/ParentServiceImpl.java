package com.ch.service.base;

import com.ch.service.util.EntityManagerWrapper;

import javax.annotation.Resource;

/**
 * 业务类的父类
 * @author Administrator
 *
 */
public abstract class ParentServiceImpl {
	//DAO操作类
	@Resource
	protected EntityManagerWrapper em;
}
