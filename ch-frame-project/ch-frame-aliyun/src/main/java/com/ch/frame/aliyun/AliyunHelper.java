package com.ch.frame.aliyun;

import com.ch.frame.conf.ConfigHelper;

public class AliyunHelper {
	public static void test(){
		System.out.println(ConfigHelper.getProp("aliyun").get("name"));
	}
}
