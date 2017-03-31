package com.ch.frame.redis;

import java.io.Serializable;

/**
 * 一个缓存组
 *
 * @author Administrator
 */
public class CacheGroup implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //所属项目
    private String project;
    //名称
    private String name;
    //标題
    private String title;
    //超时单位为秒，0或空表示永久存储
    private Integer express;

    public CacheGroup(String project2, String name2, String title2,
                      Integer express2) {
        this.project = project2;
        this.name = name2;
        this.title = title2;
        this.express = express2;
        if (this.project == null) {
            this.project = "";
        }
    }

    /**
     * 生成一个缓存KEY   格式：project + "_" + name + "_" + key
     *
     * @param key
     * @return
     */

    public byte[] getKey(String key) {
        try {
            return (project + "_" + name + "_" + key).getBytes("UTF-8");
        } catch (Exception e) {
            throw new RedisException(e);
        }
    }

    public String getKeyToString(String key) {
        return project + "_" + name + "_" + key;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getExpress() {
        return express;
    }

    public void setExpress(Integer express) {
        this.express = express;
    }

    public byte[][] getKeys(String[] ckeys) {
        byte[][] rkeys = new byte[ckeys.length][];
        for (int i = 0; i < ckeys.length; i++) {
            rkeys[i] = getKey(ckeys[i]);
        }
        return rkeys;
    }

    public byte[] getKeyGroupPattern() {
        try {
            return (project + "_" + name + "_*").getBytes("UTF-8");
        } catch (Exception e) {
            throw new RedisException(e);
        }
    }

}
