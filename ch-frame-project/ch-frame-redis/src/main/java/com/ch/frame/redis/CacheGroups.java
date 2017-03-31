package com.ch.frame.redis;


import com.ch.frame.conf.ConfigHelper;
import org.jdom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析类路径下的cachegroups.xml
 *
 * @author ludynice
 */
public class CacheGroups {
    private static CacheGroups inst;

    public synchronized static CacheGroups get() {
        if (inst == null) {
            inst = new CacheGroups();
        }
        return inst;
    }
    //key = project.name
    private Map<String, CacheGroup> groups = new HashMap<String, CacheGroup>();

    private CacheGroups() {
        try {
            Element root = ConfigHelper.getXml("cachegroups");
            List<Element> ls = root.getChildren("group");
            String project = ConfigHelper.getProp("redis").get("project");
            if(project == null)
            	project = "";
            for (Element em : ls) {
                String name = em.getAttributeValue("name");
                String title = em.getAttributeValue("title");
                String express = em.getAttributeValue("express");
                String skey = project + "." + name;
                if (groups.containsKey(skey)) {
                    throw new RedisException("存在project与group均相同的缓存配置");
                }
                groups.put(skey, new CacheGroup(project, name, title, (express == null || express.equals("")) ? 0 : Integer.parseInt(express)));
            }
        } catch (Exception e) {
            throw new RedisException("解析cachegroups.xml出错，原因：" + e.getMessage(), e);
        }
    }

    private CacheGroup getGroup2(String group) {
    	String project = ConfigHelper.getProp("redis").get("project");
        if(project == null)
        	project = "";
        if(group == null)
            group = "";
        CacheGroup cg = groups.get(project + "." + group);//project.name
        if (cg == null) {
            throw new RedisException("未找到项目" + project + "下的" + group + "缓存组配置");
        }
        return cg;
    }

	private void addGroup2(String name, String title, int express) {
		String project = ConfigHelper.getProp("redis").get("project");
        if(project == null)
        	project = "";
		CacheGroup cg = new CacheGroup(project, name, title, express);
		groups.put(project + "." + name, cg);
	}

    /**
     * 获取一个缓存组
     * @param group
     * @return
     */
    public static CacheGroup getGroup(String group) {
        return CacheGroups.get().getGroup2(group);
    }

    /**
     * 添加一个缓存组
     *
     * @param name  组名
     * @param title
     * @param express 过期时间，单位为秒，0或空表示永久存储
     */
	public static void addGroup(String name,String title,int express){
        CacheGroups.get().addGroup2(name,title,express);
    }
}
