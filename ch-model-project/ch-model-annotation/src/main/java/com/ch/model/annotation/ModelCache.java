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
public @interface ModelCache {
	//缓存项
	public CacheItem[] items();
}
