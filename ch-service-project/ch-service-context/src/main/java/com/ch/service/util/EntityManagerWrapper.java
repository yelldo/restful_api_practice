package com.ch.service.util;

import com.ch.model.vo.Hql;
import com.ch.model.vo.PageData;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.List;

public interface EntityManagerWrapper {

	/**
	 * 持久化一个实体对象
	 *
	 * @param obj
	 * @return
	 */
	@Transactional
	public abstract Object persist(Object obj);

	public abstract <T> T get(Class<T> cla, Object id);

	public abstract Object getExists(Class<?> cla, Object id);

	public abstract Object get(Class<?> cla, Object id, LockModeType lt);

	public abstract void refresh(Class<?> cla, LockModeType lt);

	/**
	 * 删除对象
	 *
	 * @param obj
	 */
	@Transactional
	public abstract void remove(Object obj);

	/**
	 * 根据主键值获得对象列表
	 * @param ls
	 * @return
	 */
	public abstract List<Object> toList(Class<?> cla, List ls);

	/**
	 * 新加或修改一个现有对象
	 *
	 * @param obj
	 * @return
	 */
	@Transactional
	public abstract <T> T merge(Object obj);
	@Transactional
	public abstract void removeById(Class<?> cla, Object id);

	/**
	 * 找查符合条件的第一个元素
	 *
	 * @param hql
	 * @param params
	 * @return
	 */
	public abstract <T> T findFirst(String hql, boolean cacheable, Object... params);

	/**
	 * 返回双精度数
	 *
	 * @param hql
	 * @param cacheable
	 * @param params
	 * @return
	 */
	public abstract double findDouble(String hql, boolean cacheable, Object... params);

	/**
	 * 返回整数
	 *
	 * @param hql
	 * @param cacheable
	 * @param params
	 * @return
	 */
	public abstract int findInt(String hql, boolean cacheable, Object... params);

	/**
	 * 找查符合条件的第一个元素
	 *
	 * @param hql
	 * @param params
	 * @return
	 */
	public abstract <T> T findFirst(Hql hql);

	/**
	 * 查找
	 *
	 * @param hql
	 * @param <T>
	 * @return
	 */
	public abstract <T> List<T> find(Hql hql);

	/**
	 * 找查符合条件的第一个元素
	 *
	 * @param hql
	 * @param params
	 * @return
	 */
	public abstract <T> T findFirstWithCache(Hql hql);

	/**
	 * 查询
	 *
	 * @param hql
	 * @param cacheable
	 * @param params
	 * @return
	 */
	public abstract List find(String hql, boolean cacheable, Object... params);

	/**
	 * 分页查询
	 */
	public abstract PageData findPage(String hql, boolean cacheable, Integer page, Integer pageSize, Object... params);

	public abstract PageData findPageByStartLimit(String hql, boolean cacheable, Integer start, Integer limit, Object... params);

	/**
	 * 分页查询
	 */
	public abstract PageData findPage(Hql hql, boolean cacheable, Integer page, Integer pageSize);

	public abstract PageData findPage(Hql hql, boolean cacheable, PageData pageData);

	/**
	 * 指定行号开始查询
	 *
	 * @param hql
	 * @param cacheable
	 * @param start
	 * @param limit
	 * @return
	 */
	public abstract List findLimit(String hql, boolean cacheable, Integer start, Integer limit, Object... params);
	/**
	 * 搜索实体对象清单
	 * @param domainClass
	 * @param cackeItemPrefix
	 * @return
	 */
	public abstract List findByCacheItem(Class domainClass, String cackeItemPrefix, Object... args);
	/**
	 * 返回首个值
	 * @param domainClass
	 * @param cackeItemPrefix
	 * @param args
	 * @return
	 */
	public abstract <T> T findFirstByCacheItem(Class domainClass, String cackeItemPrefix, Object... args);
	/**
	 * 返回首个值
	 * @param domainClass
	 * @param cackeItemPrefix
	 * @param args
	 * @return
	 */
	public abstract int findIntByCacheItem(Class domainClass, String cackeItemPrefix, Object... args);
	
	/**
	 * 返回首个值
	 * @param domainClass
	 * @param cackeItemPrefix
	 * @param args
	 * @return
	 */
	public abstract Long findLongByCacheItem(Class domainClass, String cackeItemPrefix, Object... args);
}