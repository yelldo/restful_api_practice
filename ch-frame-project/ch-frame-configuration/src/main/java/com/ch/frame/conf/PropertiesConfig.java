package com.ch.frame.conf;

import org.apache.commons.lang3.StringUtils;

import java.util.Properties;


public class PropertiesConfig{

	private Properties prop;
	public PropertiesConfig(Properties prop) {
		this.prop = prop;
	}
	public int getIntNum(String key, int def){
		String val = prop.getProperty(key, def+"");
		return Integer.parseInt(val.trim());
	}
	public Integer getInt(String key){
		String val = prop.getProperty(key);
		if(StringUtils.isBlank(val))
			return null;
		return Integer.valueOf(val.trim());
	}
	public Boolean getBoolean(String key){
		String val = prop.getProperty(key);
		if(StringUtils.isBlank(val))
			return null;
		return Boolean.valueOf(val.trim());
	}
	public boolean getBool(String key, boolean def){
		String val = prop.getProperty(key);
		if(StringUtils.isBlank(val))
			return def;
		return Boolean.valueOf(val.trim());
	}
	public long getLongNum(String key, long def){
		String val = prop.getProperty(key, def+"");
		return Long.parseLong(val.trim());
	}
	public Long getLong(String key){
		String val = prop.getProperty(key);
		if(StringUtils.isBlank(val))
			return null;
		return Long.valueOf(val.trim());
	}
	public String get(String key, String def){
		String val = prop.getProperty(key);
		if(StringUtils.isBlank(val))
			return def;
		return val.trim();
	}
	public String get(String key){
		String val = prop.getProperty(key);
		if(StringUtils.isBlank(val))
			return null;
		return val.trim();
	}
}
