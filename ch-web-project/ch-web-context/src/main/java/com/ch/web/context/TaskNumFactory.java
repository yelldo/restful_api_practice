package com.ch.web.context;

import com.ch.web.exception.WebException;

import java.util.HashMap;
import java.util.Map;

public class TaskNumFactory {
	private static Map<String, TaskNumProvider> providers = new HashMap<>();
	public static void register(String task, TaskNumProvider p){
		providers.put(task, p);
	}
	/**
	 * 获取任务数
	 * @param task
	 * @return
	 */
	public static int getTaskNum(String task){
		TaskNumProvider tp = providers.get(task);
		if(tp == null){
			throw new WebException("Not found task provider for task " + task);
		}
		return tp.getTaskNum(WebContext.get());
	}
}
