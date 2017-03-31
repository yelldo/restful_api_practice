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
 * @author Administrator
 */
public class RedisHelper {
    public static final int DXCACHEDB = 1;
    protected static Log log = LogFactory.getLog(RedisHelper.class);
    private JedisPool pool = null;
    //private static RedisHelper inst = null;
    private static Map<Integer, RedisHelper> map = new HashMap<>();

    private static RedisHelper get(int dbindex) {
        RedisHelper inst = map.get(dbindex);
        if (inst == null) {
            inst = new RedisHelper(dbindex);
            map.put(dbindex, inst);
        }
        return inst;
    }

    private RedisHelper(int dbindex) {
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

    public JedisPool getPool() {
        return pool;
    }

    /**
     * 在redis上下文下执行
     *
     * @param callback
     * @return
     */
    public <T> T execute(RedisCallback<T> callback) {
        Jedis edis = null;
        try {
            edis = getPool().getResource();
            return callback.execute(edis);
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            returnResource(edis);
        }
    }

    private void returnResource(Jedis jedis) {
        if (jedis == null) {
            return;
        }
        try {
            //容错
            getPool().returnBrokenResource(jedis);
        } catch (Exception e) {
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
     * 语法：exists key
     * 判断指定键是否存在。
     *
     * @return
     */
    public static boolean exists(final String group, final String ckey) {
        return get(0).exists2(group, ckey);
    }

    public boolean exists2(final String group, final String ckey) {
        return execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean execute(Jedis edis) throws Exception {
                CacheGroup cg = CacheGroups.getGroup(group);
                return edis.exists(cg.getKey(ckey));
            }
        });
    }

    /**
     * 删除本地的一个缓存
     *
     * @param group
     * @param ckeys
     * @return
     */
    public static long delete(final String group, final String... ckeys) {
        return get(0).delete2(group, ckeys);
    }

    /**
     * 删除本地的一个缓存
     */
    public static long delete(final String group, int dbIndex, final String... ckeys) {
        return get(dbIndex).delete2(group, ckeys);
    }

    public long delete2(final String group, final String... ckeys) {
        return (Long) execute(new RedisCallback<Object>() {
            @Override
            public Object execute(Jedis edis) throws Exception {
                return edis.del(CacheGroups.getGroup(group).getKeys(ckeys));
            }
        });
    }

    /**
     * 设置缓存数据
     *
     * @param group
     * @param ckey
     * @param obj
     */
    public static void set(final String group, final String ckey, final Serializable obj) {
        get(0).set2(group, ckey, obj, null);
    }

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
        });
    }

    /**
     * 获取一个缓存数据，同时返回是否存在标识与数据
     *
     * @param group
     * @param ckey
     * @return
     */
    public static CacheResult get(final String group, final String ckey) {
        return get(0).get2(group, ckey, null);
    }

    public static <T> T getData(final String group, final String ckey) {
        return get(0).get2(group, ckey, null).getData();
    }

    public static <T> T getData(final String group, final String ckey, final Integer dbIndex) {
        return get(dbIndex).get2(group, ckey, dbIndex).getData();
    }

    public static <T> T getData(final String group, final String ckey, NotExitsCallback<T> callback) {
        try {
            CacheResult r = get(0).get2(group, ckey, null);
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
    }

    /**
     * 根据实体类上注解的缓存定义来获得缓存值
     *
     * @param domainClass
     * @param domainCacheKeyPrefix
     * @param callback
     * @param args
     * @return
     */
    /*public static <T> T getData(final Class domainClass, final String domainCacheKeyPrefix, NotExitsCallback<T> callback, Object... args) {
        return getData(getDomainCacheGroup(domainClass), getDomainCacheKey(domainClass, domainCacheKeyPrefix, args), callback);
    }*/

    /**
     * 根据实体对象来刷新缓存
     */
    /*public static void refreshCacheByDomainObject(Object obj, boolean onlyNeedRefreshBeforeUpdate) {
        if (obj == null) return;
        ModelCache model = obj.getClass().getAnnotation(ModelCache.class);
        if (model == null) return;
        CacheItem[] caches = model.items();
        if (caches == null) return;
        Pattern pttern = Pattern.compile("\\{\\w+\\}", Pattern.CASE_INSENSITIVE);
        String group = getDomainCacheGroup(obj.getClass());
        for (CacheItem ckey : caches) {
            if (onlyNeedRefreshBeforeUpdate && ckey.needRefreshBeforeUpdate()) {
                continue;
            }
            String str = ckey.value();
            Matcher m = pttern.matcher(str);
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                String prop = m.group();
                try {
                    prop = prop.substring(1, prop.length() - 1);
                    Object vobj = PropertyUtils.getProperty(obj, prop);
                    m.appendReplacement(sb, vobj == null ? "" : vobj.toString());
                } catch (Exception e) {
                    throw new RedisException("刷新实体【" + obj.getClass().getSimpleName() + "】的缓存【" + str + "】时失败", e);
                }
            }
            m.appendTail(sb);
            String key = sb.toString();
            RedisHelper.delete(group, key);
            //log.debug("REMOVE CACHEKEY:" + group + key);

        }
    }*/
    public static String getDomainCacheGroup(Class entityClass) {
        return "_dm." + entityClass.getSimpleName() + ".";
    }

    /**
     * 根据实体类Model注解中的cacheKeys的注解来自动生成缓存的key
     *
     * @return
     */
    /*public static String getDomainCacheKey(Class entityClass, String prefix, Object... args) {
        ModelCache model = (ModelCache) entityClass.getAnnotation(ModelCache.class);
        if (model != null) {
            CacheItem ckey2 = null;
            for (CacheItem ckey : model.items()) {
                if (ckey.value().startsWith(prefix)) {
                    if (ckey2 != null) {
                        throw new RedisException("实体【" + entityClass.getSimpleName() + "】存在多个以【" + prefix + "】开头的缓存键");
                    }
                    ckey2 = ckey;
                }
            }
            if (ckey2 == null) {
                throw new RedisException("实体【" + entityClass.getSimpleName() + "】不存在以【" + prefix + "】开头的缓存键");
            }
            String str = ckey2.value();
            Pattern pttern = Pattern.compile("\\{\\w+\\}", Pattern.CASE_INSENSITIVE);
            Matcher m = pttern.matcher(str);
            StringBuffer sb = new StringBuffer();
            int index = 0;
            while (m.find()) {
                if (args == null || args.length <= index) {
                    throw new RedisException("获取实体【" + entityClass.getSimpleName() + "】以【" + prefix + "】开头的缓存键时失败，给定的参数个数不够");
                }
                Object vobj = args[index];
                index++;
                m.appendReplacement(sb, vobj == null ? "" : vobj.toString());
            }
            m.appendTail(sb);
            return sb.toString();
        }
        throw new RedisException("实体【" + entityClass.getSimpleName() + "】未定义缓存键");
    }*/
    private CacheResult get2(final String group, final String ckey, Integer dbIndex) {
        return (CacheResult) execute(new RedisCallback<Object>() {
            @Override
            public Object execute(Jedis edis) throws Exception {
                /*if(dbIndex != null){
                    edis.select(dbIndex);
            	}*/
                Serializable obj = toObject(edis.get(CacheGroups.getGroup(group).getKey(ckey)));
                boolean exists = obj != null;
                if (obj instanceof NullObject) {
                    obj = null;
                }
                return new CacheResult(obj, exists);
            }
        });
    }

    /**
     * 清除一组缓存
     */
    public static void clearGroup(final String group) {
        get(0).clearGroup2(group, null);
    }

    public static void clearGroup(final String group, Integer dbIndex) {
        get(dbIndex).clearGroup2(group, dbIndex);
    }

    private void clearGroup2(final String group, Integer dbIndex) {
        execute(new RedisCallback<Object>() {
            @Override
            public Object execute(Jedis edis) throws Exception {
                /*if(dbIndex != null){
                    edis.select(dbIndex);
            	}*/
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
        });
    }

    public static void express(final String group, final String ckey, final int secs) {
        get(0).express2(group, ckey, secs);
    }

    public static void express(final String group, final String ckey, final int secs, int dbIndex) {
        get(dbIndex).express2(group, ckey, secs);
    }

    public void express2(final String group, final String ckey, final int secs) {
        execute(new RedisCallback<Object>() {
            @Override
            public Object execute(Jedis edis) throws Exception {
                edis.expire(CacheGroups.getGroup(group).getKey(ckey), secs);
                return null;
            }
        });
    }

    public static void flushDb(int dbindex) {
        /*execute1(new RedisCallback<Object>() {
            @Override
			public Object execute(Jedis edis) throws Exception {
				edis.select(dbindex);
				edis.flushDB();
				return null;
			}
		});*/
        get(dbindex).execute(new RedisCallback<Object>() {
            @Override
            public Object execute(Jedis edis) throws Exception {
                //edis.select(dbindex);
                edis.flushDB();
                return null;
            }
        });
    }


}
