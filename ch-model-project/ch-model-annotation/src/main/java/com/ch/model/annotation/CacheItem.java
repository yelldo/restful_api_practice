package com.ch.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 依附于实体对象的缓存键，当实体对象更新或删除时同步更新该缓存键
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheItem {
	/**
	 * 缓存的KEY值，可以使用{属性名}替代，所有key前都会加上"_dm." + 实体类名(不含包名) + "."
	 * 不要选择可能会被修改的键作为缓存的动态键，这样有可能导至数据混乱
	 */
	public String value() default "";
	/**
	 * 在对象更新或删除前，是否需要按指定的{property}值生成key刷新缓存，一般用于value中的{xxx}可能存在变更的情况
	 * 所有缓存只保存10分钟
	 * @return
	 */
	public boolean needRefreshBeforeUpdate() default false;
	
	/**
	 * 查询列表显示的属性名称，多个属性时以,号隔开
	 * 可以使用count(a), sum(a.price*a.num)
	 * @return
	 */
	public String listprops() default "";
	/**
	 * 列表项元素类型
	 * 1.JSONObject对象,2.数组对象
	 * 未指定时，
	 * 		未果未指定listprops，直接返回对象列表
	 * 		如果有listprops，如果listprops为1个，直接返回该属性列表，否则返回为的列表元素类型为数组
	 * @return
	 */
	public int listitemtype() default 0;
	/**
	 * 附加在搜索语句后的排序语句，不用加Order By，例如：a.createdTime desc
	 * @return
	 */
	public String orderby() default "";
	/**
	 * 当参数值为空时转换成is null
	 * @return
	 */
	public boolean nullToIsNull() default false;
	/**
	 * 查询名
	 * @return
	 */
	public String key() default "";
	/**
	 * 查询语句
	 * @return
	 */
	public String hql() default "";
	
}
