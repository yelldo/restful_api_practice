package com.ch.service.general.impl;

import com.ch.frame.util.ClassUtils;
import com.ch.model.vo.Hql;
import com.ch.model.vo.PageData;
import com.ch.service.base.ParentServiceImpl;
import com.ch.service.general.intel.QueryService;
import com.ch.service.util.ServiceExcepiton;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Service
public class QueryServiceImpl extends ParentServiceImpl implements QueryService{
	private List<String> alldoamins = new ArrayList<String>();
	@Override
	public <T> T get(String claName, Object id) {
		if(!(id instanceof Long) && id != null){
			id = Long.valueOf(id+"");
		}
		if(alldoamins.isEmpty()){
			List ls = ClassUtils.getClasses("com.hx.model");
			for(Object obj : ls){
				Class cla = (Class)obj;
				if(cla.getAnnotation(Entity.class) != null){
					alldoamins.add(cla.getName());
				}
			}
		}
		Class cla = null;
		try {
			for(String s : alldoamins){
				if(s.endsWith(claName)){
					cla = Class.forName(s);
					break;
				}
			}
		} catch (Exception e) {
			throw new ServiceExcepiton("Not found model " + claName);
		}
		return (T)get(cla, id);
	}
	@Override
	public <T> T get(Class<T> cla, Object id) {
		return em.get(cla, id);
	}

	@Override
	public Object getExists(Class<?> cla, Object id) {
		return em.getExists(cla, id);
	}

	@Override
	public List<Object> toList(Class<?> cla, List ls) {
		return em.toList(cla, ls);
	}

	@Override
	public <T> T findFirst(String hql, Object... params) {
		return em.findFirst(hql,false, params);
	}

	@Override
	public <T> T findFirst(String hql, List<Object> params) {
		return em.findFirst(hql,false, params);
	}

	@Override
	public double findDouble(String hql, Object... params) {
		return em.findDouble(hql, false, params);
	}

	@Override
	public int findInt(String hql, Object... params) {
		return em.findInt(hql, false, params);
	}

	@Override
	public <T> T findFirst(Hql hql) {
		return em.findFirst(hql);
	}

	@Override
	public <T> List<T> find(Hql hql) {
		return em.find(hql);
	}


	@Override
	public List find(String hql, Object... params) {
		return em.find(hql, false, params);
	}

	@Override
	public PageData findPage(String hql, Integer page, Integer pageSize,
			Object... params) {
		return em.findPage(hql, false, page, pageSize, params);
	}

	@Override
	public PageData findPageByStartLimit(String hql, Integer start,
			Integer limit, Object... params) {
		return em.findPageByStartLimit(hql, false, start, limit, params);
	}

	@Override
	public PageData findPage(Hql hql, Integer page, Integer pageSize) {
		return em.findPage(hql, false, page, pageSize);
	}

	@Override
	public List findLimit(String hql, Integer start, Integer limit,
			Object... params) {
		return em.findLimit(hql, false, start, limit, params);
	}

	@Override
	public List findByCacheItem(Class domainClass, String cackeItemPrefix,
			Object... args) {
		return em.findByCacheItem(domainClass, cackeItemPrefix, args);
	}

	@Override
	public <T> T findFirstByCacheItem(Class domainClass,
			String cackeItemPrefix, Object... args) {
		return em.findFirstByCacheItem(domainClass, cackeItemPrefix, args);
	}

	@Override
	public int findIntByCacheItem(Class domainClass, String cackeItemPrefix,
			Object... args) {
		return em.findIntByCacheItem(domainClass, cackeItemPrefix, args);
	}

	@Override
	public Long findLongByCacheItem(Class domainClass, String cackeItemPrefix,
			Object... args) {
		return em.findLongByCacheItem(domainClass, cackeItemPrefix, args);
	}

}
