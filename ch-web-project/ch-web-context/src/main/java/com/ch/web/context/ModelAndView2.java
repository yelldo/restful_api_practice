package com.ch.web.context;

import com.ch.web.freemarker.ModelData;
import org.springframework.web.servlet.ModelAndView;

public class ModelAndView2 extends ModelAndView {
	public final static String NOTFOUND_VIEW_NAME = "notfound";
	public ModelAndView2() {
		super();
		this.init();
	}
	public ModelAndView2(String name) {
		super(name);
		this.init();
	}
	private void init() {
		//自动添加相关的模型数据
		ModelData data = new ModelData();
		this.addAllObjects(data);
	}
	public ModelAndView2 setError(String error) {
		this.addObject("error", error);
		return this;
	}
	public ModelAndView2 redirectTo(String path) {
		setViewName("redirect:" + path);
		return this;
	}
}
