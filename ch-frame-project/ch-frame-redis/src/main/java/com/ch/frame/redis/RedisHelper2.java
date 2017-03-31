package com.ch.frame.redis;


import com.ch.frame.conf.ConfigHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Redis缓存操作
 *
 * @author ludynice
 */
public class RedisHelper2 {
    //public static final int DXCACHEDB = 1;
    protected static Log log = LogFactory.getLog(RedisHelper2.class);
    //Redis 连接池
    private JedisPool pool = null;
    private static Map<Integer, RedisHelper2> map = new HashMap<>();

    private static RedisHelper2 get(int dbindex) {
        RedisHelper2 inst = map.get(dbindex);
        if (inst == null) {
            inst = new RedisHelper2(dbindex);
            map.put(dbindex, inst);
        }
        return inst;
    }

    private RedisHelper2(int dbindex) {
        JedisPoolConfig config = new JedisPoolConfig();
        String host = ConfigHelper.getProp("redis").get("host");
        String port = ConfigHelper.getProp("redis").get("port");
        String pwd = ConfigHelper.getProp("redis").get("pwd");
        if (StringUtils.isBlank(pwd)) {
            pool = new JedisPool(config, host, Integer.valueOf(port), 100000, null, dbindex);
        } else {
            pool = new JedisPool(config, host, Integer.valueOf(port), 100000, pwd, dbindex);
        }
        log.info("pool:" + pool.getResource());
    }

    private JedisPool getPool() {
        return pool;
    }

    /**
     * 在redis上下文下执行
     *
     * @param callback
     * @param curdbindex
     * @return
     */
    private <T> T execute(RedisCallback<T> callback, Integer curdbindex) {
        Jedis edis = null;
        try {
            edis = getPool().getResource();
            if (curdbindex == null) {
                String dbindex = ConfigHelper.getProp("redis").get("redis.database");
                if (dbindex == null) {
                    curdbindex = 0;
                } else {
                    curdbindex = Integer.valueOf(dbindex);
                }
                edis.select(curdbindex);
            }
            return callback.execute(edis);
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            edis.close();//使用完关闭连接,释放回pool
        }
    }

    /**
     * 将对象序列化
     *
     * @param obj
     * @return
     */
    private byte[] toByteData(Serializable obj) {
        if (obj == null) {
            return null;
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream fos = new ObjectOutputStream(bos);
            fos.writeObject(obj);
            fos.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RedisException(e);
        }
    }

    /**
     * 将字节转成对象
     *
     * @param bts
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T toObject(byte[] bts) {
        if (bts == null || bts.length == 0) return null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bts);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object obj = (T) ois.readObject();
            return (T) obj;
        } catch (Exception e) {
            throw new RedisException(e);
        }
    }

    /**
     * 判断指定键是否存在。
     *
     * @return
     */
    public static boolean exists(final String group, final String ckey) {
        return get(0).exists2(group, ckey, 0);
    }

    /*public static boolean exists(final String ckey) {
        return get(0).exists2(null, ckey, 0);
    }*/

    private boolean exists2(final String group, final String ckey, int dbindex) {
        return execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean execute(Jedis edis) throws Exception {
                CacheGroup cg = CacheGroups.getGroup(group);
                return edis.exists(cg.getKey(ckey));
            }
        }, dbindex);
    }

    /**
     * 删除本地db0指定key缓存
     *
     * @param group
     * @param ckeys
     * @return
     */
    public static long delete(final String group, final String... ckeys) {
        return get(0).delete2(group, 0, ckeys);
    }

    /**
     * 删除本地指定db指定key缓存
     *
     * @param group
     * @param dbIndex
     * @param ckeys
     * @return
     */
    public static long delete(final String group, int dbIndex, final String... ckeys) {
        return get(dbIndex).delete2(group, dbIndex, ckeys);
    }

    private long delete2(final String group, int dbindex, final String... ckeys) {
        return (Long) execute(new RedisCallback<Object>() {
            @Override
            public Object execute(Jedis edis) throws Exception {
                return edis.del(CacheGroups.getGroup(group).getKeys(ckeys));
            }
        }, dbindex);
    }

    /**
     * 设置缓存数据
     *
     * @param group
     * @param ckey
     * @param obj
     */
    public static void set(final String group, final String ckey, final Serializable obj) {
        get(0).set2(group, ckey, obj, 0);
    }

    /**
     * 设置缓存数据
     *
     * @param group
     * @param ckey
     * @param obj
     * @param dbIndex
     */
    public static void set(final String group, final String ckey, final Serializable obj, Integer dbIndex) {
        get(dbIndex).set2(group, ckey, obj, dbIndex);
    }

    private void set2(final String group, final String ckey, final Serializable obj, Integer dbIndex) {
        execute(new RedisCallback<Object>() {
            @Override
            public Object execute(Jedis edis) throws Exception {
                //如果为空设置一个空对象
                Serializable cobj = obj;
                if (cobj == null) {
                    cobj = new NullObject();
                }
                CacheGroup cg = CacheGroups.getGroup(group);
                edis.set(cg.getKey(ckey), toByteData(cobj));
                if (cg.getExpress() != null && cg.getExpress() > 0) {
                    edis.expire(cg.getKey(ckey), cg.getExpress());
                }
                return null;
            }
        }, dbIndex);
    }

    /**
     * 获取一个缓存数据，同时返回是否存在标识与数据
     *
     * @param group
     * @param ckey
     * @return
     */
    public static CacheResult get(final String group, final String ckey) {
        return get(0).get2(group, ckey, 0);
    }

    public static CacheResult get(final String group, final String ckey,Integer dbindex) {
        return get(0).get2(group, ckey, dbindex);
    }

    /*public static <T> T getData(final String group, final String ckey, NotExitsCallback<T> callback) {
        try {
            CacheResult r = get(0).get2(group, ckey, 0);
            if (r == null || !r.isExists()) {
                T obj = callback.get();
                set(group, ckey, (Serializable) obj);
                return obj;
            }
            return r.getData();
        } catch (Throwable e) {
            if (e instanceof RedisException) {
                throw (RedisException) e;
            }
            throw new RedisException("Invoke redis callback error", e);
        }
    }*/

    private CacheResult get2(final String group, final String ckey, Integer dbIndex) {
        return (CacheResult) execute(new RedisCallback<Object>() {
            @Override
            public Object execute(Jedis edis) throws Exception {
                Serializable obj = toObject(edis.get(CacheGroups.getGroup(group).getKey(ckey)));
                boolean exists = obj != null;
                if (obj instanceof NullObject) {
                    obj = null;
                }
                return new CacheResult(obj, exists);
            }
        }, dbIndex);
    }

    public static String getDomainCacheGroup(Class entityClass) {
        return "_dm." + entityClass.getSimpleName() + ".";
    }

    /**
     * 清除一组缓存
     */
    public static void clearGroup(final String group) {
        get(0).clearGroup2(group, 0);
    }

    public static void clearGroup(final String group, Integer dbIndex) {
        get(dbIndex).clearGroup2(group, dbIndex);
    }

    private void clearGroup2(final String group, Integer dbIndex) {
        execute(new RedisCallback<Object>() {
            @Override
            public Object execute(Jedis edis) throws Exception {
                CacheGroup cg = CacheGroups.getGroup(group);
                Set<byte[]> set = edis.keys(cg.getKeyGroupPattern());
                if (!set.isEmpty()) {
                    for (byte[] bts : set) {
                        edis.del(bts);
                        //log.debug("clear key " + new String(bts));
                    }
                }
                return null;
            }
        }, dbIndex);
    }

    public static void addToGroup(final String group, Integer dbindex, final String... keys) {
        get(dbindex).addToGroup2(group, dbindex, keys);
    }

    private void addToGroup2(final String group, Integer dbindex, final String... keys) {
        execute(new RedisCallback<Object>() {
            @Override
            public Object execute(Jedis edis) throws Exception {
                edis.sadd(group, keys);
                return null;
            }
        }, dbindex);
    }

    /**
     * 清除一组缓存
     *
     * @param group
     */
    public static void clearByGroup(final String group) {
        get(0).clearByGroup2(group, 0);
    }

    public static void clearByGroup(final String group, Integer dbindex) {
        get(dbindex).clearByGroup2(group, dbindex);
    }

    private void clearByGroup2(final String group, Integer dbindex) {
        execute(new RedisCallback<Object>() {
            @Override
            public Object execute(Jedis edis) throws Exception {
                Set<String> keys = edis.smembers(group);
                for (String key : keys) {
                    edis.del(key);
                }
                return null;
            }
        }, dbindex);
    }

    /**
     * 设置缓存过期时间
     *
     * @param group
     * @param ckey
     * @param secs  过期时间
     */
    public static void express(final String group, final String ckey, final int secs) {
        get(0).express2(group, ckey, secs, 0);
    }

    public static void express(final String group, final String ckey, final int secs, int dbIndex) {
        get(dbIndex).express2(group, ckey, secs, dbIndex);
    }

    private void express2(final String group, final String ckey, final int secs, int dbindex) {
        execute(new RedisCallback<Object>() {
            @Override
            public Object execute(Jedis edis) throws Exception {
                //edis.expire(CacheGroups.getProject(group).getKey(ckey), secs);
                edis.expire(group + "_" + ckey, secs);
                return null;
            }
        }, dbindex);
    }

    /**
     * 清空指定数据库中的所有缓存
     *
     * @param dbindex
     */
    public static void flushDb(int dbindex) {
        get(dbindex).execute(new RedisCallback<Object>() {
            @Override
            public Object execute(Jedis edis) throws Exception {
                edis.flushDB();
                return null;
            }
        }, dbindex);
    }


}
