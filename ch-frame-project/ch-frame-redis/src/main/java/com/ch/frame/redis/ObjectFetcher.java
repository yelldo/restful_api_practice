package com.ch.frame.redis;

/***
 * 缓存获取数据接口
 * @author huyuangui
 * @time 2016-12-28
 * @param <T>
 */
public interface ObjectFetcher<T> {
    public T fetch();
}
