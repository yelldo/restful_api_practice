package com.ch.service.general.intel;

import com.ch.model.vo.Hql;
import com.ch.model.vo.PageData;

import java.util.List;

public interface QueryService {
	
	/**
	 * 根据业务对象ID获取业务对象
	 * @param cla
	 * @param id
	 * @return
	 */
	public abstract <T> T get(Class<T> cla, Object id);

	/**
	 * 根据业务对象ID获取业务对象
	 * @param claName 如果找到多个后缀名相同的业务类时将抛出异常
	 * @param id
	 * @return
	 */
	public abstract <T> T get(String claName, Object id);

	/**
	 * 根据业务对象ID获取业务对象，如果不存在将抛出异常
	 * @param cla
	 * @param id
	 * @return
	 */
	public abstract Object getExists(Class<?> cla, Object id);

	/**
	 * 将业务对象的ID列表，转换成对象列表
	 * @param ls <Long>
	 * @return
	 */
	public abstract List<Object> toList(Class<?> cla, List ls);

	/**
	 * 找查符合条件的第一个元素
	 *
	 * @param hql
	 * @param params
	 * @return
	 */
	public abstract <T> T findFirst(String hql, Object... params);

	/**
	 * 找查符合条件的第一个元素
	 *
	 * @param hql
	 * @param params
	 * @return
	 */
	public abstract <T> T findFirst(String hql, List<Object> params);

	/**
	 * 返回双精度数
	 *
	 * @param hql
	 * @param params
	 * @return
	 */
	public abstract double findDouble(String hql, Object... params);

	/**
	 * 返回整数
	 *
	 * @param hql
	 * @param params
	 * @return
	 */
	public abstract int findInt(String hql, Object... params);

	/**
	 * 找查符合条件的第一个元素
	 *
	 * @param hql
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
	 * 查询
	 *
	 * @param hql
	 * @param params
	 * @return
	 */
	public abstract List find(String hql, Object... params);

	/**
	 * 根据页码及每页大小执行分页查询
	 */
	public abstract PageData findPage(String hql, Integer page, Integer pageSize, Object... params);

	/**
	 * 根据开始行号，每页大小分页查询
	 * @param hql
	 * @param start
	 * @param limit
	 * @param params
	 * @return
	 */
	public abstract PageData findPageByStartLimit(String hql, Integer start, Integer limit, Object... params);

	/**
	 * 分页查询
	 */
	public abstract PageData findPage(Hql hql, Integer page, Integer pageSize);

	/**
	 * 指定行号开始查询
	 * @param hql
	 * @param start
	 * @param limit
	 * @return
	 */
	public abstract List findLimit(String hql, Integer start, Integer limit, Object... params);

	/**
	 * 根据业务类上标注的CacheItem搜索实体对象清单
	 * @param domainClass
	 * @param cackeItemPrefix
	 * @return
	 */
	public abstract List findByCacheItem(Class domainClass, String cackeItemPrefix, Object... args);

	/**
	 * 返回首个值
	 *
	 * @param domainClass
	 * @param cackeItemPrefix
	 * @param args
	 * @return
	 */
	public abstract <T> T findFirstByCacheItem(Class domainClass, String cackeItemPrefix, Object... args);

	/**
	 * 返回首个值
	 *
	 * @param domainClass
	 * @param cackeItemPrefix
	 * @param args
	 * @return
	 */
	public abstract int findIntByCacheItem(Class domainClass, String cackeItemPrefix, Object... args);

	/**
	 * 返回首个值
	 *
	 * @param domainClass
	 * @param cackeItemPrefix
	 * @param args
	 * @return
	 */
	public abstract Long findLongByCacheItem(Class domainClass, String cackeItemPrefix, Object... args);

}
