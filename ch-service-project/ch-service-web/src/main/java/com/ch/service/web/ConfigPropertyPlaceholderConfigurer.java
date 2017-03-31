package com.ch.service.web;

import com.ch.frame.conf.ConfigHelper;
import com.ch.frame.conf.PropertiesConfig;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;

public class ConfigPropertyPlaceholderConfigurer extends
		PropertyPlaceholderConfigurer {
	public ConfigPropertyPlaceholderConfigurer() {
		Properties props = new Properties();
		PropertiesConfig pc = ConfigHelper.getProp("mysql");
		String url = "jdbc:mysql://" + pc.get("host") + ":" + pc.get("port")
	            + "/" + pc.get("dbname") + "?useUnicode=true&characterEncoding=utf8&autoReconnect=true";
		props.put("db.url", url);
		props.put("db.user", pc.get("user"));
		props.put("db.pwd", pc.get("pwd"));
		setProperties(props);
	}

}
