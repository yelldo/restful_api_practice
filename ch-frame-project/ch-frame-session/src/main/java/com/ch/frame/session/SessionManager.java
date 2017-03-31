package com.ch.frame.session;

import com.alibaba.fastjson.JSONObject;
import com.ch.frame.conf.ConfigHelper;
import com.ch.frame.exception.GeneralException;
import com.ch.frame.redis.CacheResult;
import com.ch.frame.redis.RedisHelper2;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * 会话数据管理器
 *
 * @author ludynice
 */
public class SessionManager {
    private static SessionManager inst = null;

    public synchronized static SessionManager get() {
        if (inst == null) {
            inst = new SessionManager();
        }
        return inst;
    }

    //private static String SESSIONCACHEGROUP = "_session";
    private final String TOKENCACHEGROUP = "token";

    private SessionManager() {
        //CacheGroups.get().addGroup(SESSIONCACHEGROUP, "系统缓存", 30);
    }

    /**
     * 生成一个新的token
     *
     * @param params {username,password,timestamp}
     * @return
     */
    public String createToken(JSONObject params) {
        String md5key = ConfigHelper.getProp("token").get("md5key");
        String username = params.getString("username");
        String password = params.getString("password");//经过MD5加密
        String timestamp = params.getString("timestamp");
        String sign = DigestUtils.md5Hex(username + password + md5key);
        String token = sign + "+" + timestamp;
        //检查redis保存的key,保证不存在重复的key
        if (exists(TOKENCACHEGROUP, token)) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                throw new GeneralException(e);
            }
            return createToken(params);
        }
        RedisHelper2.set("token", token, params);
        return token;
    }

    private boolean exists(String group, String token) {
        return RedisHelper2.exists(group, token);
    }

    /**
     * 验证token
     *
     * @param params
     * @return
     */
    public boolean validateToken(JSONObject params) {
        String token = params.getString("token");
        String username = params.getString("username");
        String password = params.getString("password");
        String timestamp2 = params.getString("timestamp");
        String md5key = ConfigHelper.getProp("token").get("md5key");
        String expiretime = ConfigHelper.getProp("token").get("expiretime");
        String timestamp1 = token.substring(token.indexOf("+") + 1);
        // 校验token是否相等
        Boolean tokenEquals = token.equals(DigestUtils.md5Hex(username + password + md5key));
        // 检查token是否过期
        Boolean timeout = tokenTimeoutOrNot(token);
        // 检查距离上次请求是否相隔太久
        Boolean withinTimes = withinTimesOrNot(timestamp1, timestamp2, Integer.valueOf(expiretime));
        if (tokenEquals && timeout && withinTimes) {
            return exists(TOKENCACHEGROUP, token);
        }else{
            return false;
        }
    }

    private Boolean withinTimesOrNot(String timestamp1, String timestamp2, Integer secs) {
        Long diffSecs = (Long.valueOf(timestamp2) - Long.valueOf(timestamp1)) / 1000;
        return diffSecs < secs;
    }

    private Boolean tokenTimeoutOrNot(String token) {
        CacheResult result = RedisHelper2.get(TOKENCACHEGROUP, token);
        return !result.isExists();
    }

    /**
     * 删除token
     *
     * @param token
     */
    public void clear(String token) {
        RedisHelper2.delete(TOKENCACHEGROUP, token);
    }

    /**
     * 获取会话数据
     *
     * @param token
     * @return
     */
    public <T> T get(String token) {
        CacheResult result = RedisHelper2.get(TOKENCACHEGROUP, token);
        if (result.isExists()) {
            return (T) result.getData();
        }
        return null;
    }
}
