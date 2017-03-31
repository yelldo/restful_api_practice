package com.ch.frame.redis;

import redis.clients.jedis.Jedis;


public interface RedisCallback<T> {
	public T execute(Jedis edis)throws Exception;
}
