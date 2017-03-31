package com.ch.web.context;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;

@Data
public class Menu {
	private String id;
	private String title;
	private String view;
	private String icon;
	private String permision;
	private String task;
	private String helpText;
	private String helpUrl;
	private boolean noext;
	//任务数
	private int taskNum = 0;
	private List<Menu> children = new ArrayList<Menu>();
	@JSONField(serialize=false,deserialize=false)
	private Menu parent = null;
	public Menu(Element em){
		this.id = em.getAttributeValue("id");
		this.title = em.getAttributeValue("title");
		this.view = em.getAttributeValue("view");
		this.icon = em.getAttributeValue("icon");
		this.permision = em.getAttributeValue("permision");
		this.task = em.getAttributeValue("task");
		this.helpText = em.getAttributeValue("helpText");
		this.helpUrl = em.getAttributeValue("helpUrl");
		this.noext = "true".equals(em.getAttributeValue("noext"));
		if(!StringUtils.isBlank(task)){
			taskNum = TaskNumFactory.getTaskNum(task);
		}
		List<Element> ls = em.getChildren("menu");
		for(Element sub : ls){
			Menu sm = new Menu(sub);
			sm.parent = this;
			taskNum = taskNum + sm.getTaskNum();
			children.add(sm);
		}
	}
	public Menu findById(String id2) {
		if(id2 == null)
			return null;
		if(id2.equals(id)){
			return this;
		}
		for(Menu sub : children){
			Menu s = sub.findById(id2);
			if(s != null){
				return s;
			}
		}
		return null;
	}
	@JSONField(serialize=false,deserialize=false)
	public boolean isHasView(){
		return !StringUtils.isBlank(view);
	}
	/**
	 * 获取有VIEW的菜单或子菜单
	 * @return
	 */
	@JSONField(serialize=false,deserialize=false)
	public Menu getFirstView() {
		if(!StringUtils.isBlank(view)){
			return this;
		}
		for(Menu m : children){
			Menu m2 = m.getFirstView();
			if(m2 != null){
				return m2;
			}
		}
		return null;
	}
	@JSONField(serialize=false,deserialize=false)
	public boolean isHasChild(){
		return !children.isEmpty();
	}
	@JSONField(serialize=false,deserialize=false)
	public List<Menu> getTopMenus(){
		List<Menu> tops = new ArrayList<>();
		Menu p = this;
		while(p != null){
			tops.add(0, p);
			p = p.getParent();
		}
		return tops;
	}
}
