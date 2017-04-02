package com.ch.frame.session;

import com.alibaba.fastjson.JSONObject;
import com.ch.frame.conf.ConfigHelper;
import com.ch.frame.exception.GeneralException;
import com.ch.frame.redis.CacheGroup;
import com.ch.frame.redis.CacheGroups;
import com.ch.frame.redis.CacheResult;
import com.ch.frame.redis.RedisHelper2;
import com.ch.frame.util.DESEncrypt;
import com.ch.frame.util.RsaDesUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;

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
    public final String TOKENCACHEGROUP = "token";

    private SessionManager() {
        //CacheGroups.get().addGroup(SESSIONCACHEGROUP, "系统缓存", 30);
    }

    public JSONObject generateToken (JSONObject param){
        String token = UUID.randomUUID().toString();
        JSONObject info = new JSONObject();
        info.put("username",param.getString("mobile"));
        info.put("userid",param.getLong("id"));
        RedisHelper2.set(TOKENCACHEGROUP,token,info);
        CacheGroup cg = CacheGroups.getGroup(TOKENCACHEGROUP);
        JSONObject resp = new JSONObject();
        resp.put("token",token);
        resp.put("expire_time",cg.getExpress());
        return resp;
    }

    /**
     * 生成一个新的token
     * 客户端URL
     *
     * @param params {username,password,timestamp}
     * @return
     */
    public String createToken2(JSONObject params) {
        String md5key = ConfigHelper.getProp("apprequest").get("md5key");
        String username = params.getString("username");
        String password = params.getString("password");//经过MD5加密
        String key = RsaDesUtils.genRandomKey(8);
        String timestamp = params.getString("timestamp");
        String sign = DigestUtils.md5Hex(username + password + md5key);
        //String sign = DigestUtils.md5Hex( + md5key);
        String token = sign + "+" + timestamp;
        //检查redis保存的key,保证不存在重复的key
        if (exists(TOKENCACHEGROUP, token)) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                throw new GeneralException(e);
            }
            return createToken2(params);
        }
        RedisHelper2.set("token", token, params);
        return token;
    }

    public String createToken() {
        return UUID.randomUUID().toString();
    }

    public String validSuccess(JSONObject param){
        JSONObject userInfo = new JSONObject();
        String key = RsaDesUtils.genRandomKey(8);
        String token = createToken();
        userInfo.put("key",key);
        userInfo.put("username",param.getString("username"));
        userInfo.put("password",param.getString("password"));
        RedisHelper2.set("token",token , userInfo);

        JSONObject respInfo = new JSONObject();
        respInfo.put("key",key);
        respInfo.put("token",token);

        return respDESEncryptContent(key,respInfo);
    }

    public String respDESEncryptContent(String key,Object content){
        String cryperContent = "";
        try {
            cryperContent = DESEncrypt.toHexString(DESEncrypt.encrypt(key,content.toString()));
            System.out.println("加密后的明文:" + cryperContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cryperContent;
    }

    public boolean exists(String group, String token) {
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
        String md5key = ConfigHelper.getProp("apprequest").get("md5key");
        String expiretime = ConfigHelper.getProp("apprequest").get("expiretime");
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
    public void deleteToken(String token) {
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
