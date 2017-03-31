package com.ch.frame.session.redis;

import com.ch.frame.redis.RedisHelper;
import com.ch.frame.session.data.SessionCache;
import com.ch.frame.session.data.SessionMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administor
 */
public class RedisSessionCache implements SessionCache {

    private static final Logger LOG = LoggerFactory.getLogger(RedisSessionCache.class);
    private static String SESSIONCACHEGROUP = "_session";

    @Override
    public void put(String sessionId, SessionMap sessionMap, int timeout) {
        RedisHelper.set(SESSIONCACHEGROUP, sessionId, sessionMap);
    }

    @Override
    public SessionMap get(String sessionId) {
        SessionMap sessionMap = null;
        if (RedisHelper.exists(SESSIONCACHEGROUP, sessionId)) {
            try {
                sessionMap = RedisHelper.getData(SESSIONCACHEGROUP, sessionId);
            } catch (Exception e) {
            }

        }
        return sessionMap;
    }

    @Override
    public void setMaxInactiveInterval(String sessionId, int interval) {
        if (RedisHelper.exists(SESSIONCACHEGROUP, sessionId)) {
            RedisHelper.express(SESSIONCACHEGROUP, sessionId, interval);
        }
    }

    @Override
    public void destroy(String sessionId) {
        if (RedisHelper.exists(SESSIONCACHEGROUP, sessionId)) {
            RedisHelper.express(SESSIONCACHEGROUP, sessionId, 0);
        }
    }
}
