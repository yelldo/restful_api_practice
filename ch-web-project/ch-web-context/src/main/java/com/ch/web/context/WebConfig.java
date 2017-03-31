package com.ch.web.context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ch.frame.conf.ConfigHelper;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于读以webconfig.xml
 * @author Administrator
 *
 */
@Data
public class WebConfig {
	private static WebConfig inst = null;
	private static Log log = LogFactory.getLog(WebConfig.class);
	public static WebConfig get(){
		if(inst == null || log.isDebugEnabled()){
			inst = new WebConfig();
		}
		return inst;
	}
	//页面标題
	private String title;
	//LOGO图片
	private String logo;
	//小图标
	private String smallLogo;
	//业务服务器地址
	private String serviceHost;
	//登录地址
	private String loginView;
	//登录后的主页
	private String homeView;
	//菜单层级
	private int menuLevel = 2;
	//DX缓存的前缀
	private String dxCachePrefix = "_dx2";
	private String tipLogo;
	private String tipTitle;
	private String tipSite;
	//菜单
	private List<Menu> menus = new ArrayList<Menu>();
	private WebConfig() {
		Element root = ConfigHelper.getXml("webconfig");
		title = root.getAttributeValue("title");
		logo = root.getAttributeValue("logo");
		smallLogo = root.getAttributeValue("smallLogo");
		serviceHost = root.getAttributeValue("serviceHost");
		loginView = root.getAttributeValue("loginView");
		homeView = root.getAttributeValue("homeView");
		menuLevel = Integer.parseInt(root.getAttributeValue("menuLevel", "2"));
		dxCachePrefix = root.getAttributeValue("dxCachePrefix");
		tipLogo = root.getAttributeValue("tipLogo");
		tipSite = root.getAttributeValue("tipSite");
		tipTitle = root.getAttributeValue("tipTitle");
		if(dxCachePrefix == null){
			dxCachePrefix = "_dx2";
		}
		List<Element> ls = root.getChildren("menu");
		for(Element mem : ls){
			menus.add(new Menu(mem));
		}
	}
	public Menu getMenu(String id) {
		for(Menu m : menus){
			Menu r = m.findById(id);
			if(r != null){
				return r;
			}
		}
		return null;
	}
	/**
	 * 获取第一个有View的视图
	 * @return
	 */
	public Menu getFirstView() {
		for(Menu menu : menus){
			Menu m = menu.getFirstView();
			if(m != null){
				return m;
			}
		}
		return null;
	}
	/**
	 * 获取二级菜单列表
	 * @param topMenuId
	 * @return
	 */
	public List<Menu> getSecondMenus(String topMenuId){
		if(menuLevel > 2){
			Menu top = getMenu(topMenuId);
			if(top == null){
				return new ArrayList<>();
			}
			return top.getChildren();
		}else{
			return menus;
		}
	}
	/**
	 * 获取前端展示用的菜单
	 * @return
	 */
	public String getJsMenus(String firstMenu){
		List<Menu> ls = menus;
		if(!StringUtils.isBlank(firstMenu)){
			ls = getMenu(firstMenu).getChildren();
		}
		return toArray(ls).toJSONString();
	}
	private JSONArray toArray(List<Menu> ls){
		JSONArray rls = new JSONArray();
		for(Menu m : ls){
			JSONObject rec = new JSONObject();
			rec.put("id", m.getId());
			rec.put("title", m.getTitle());
			rec.put("helpText", m.getHelpText());
			rec.put("helpUrl", m.getHelpUrl());
			String url = m.getView();
			if(url != null && url.startsWith("/")){
				url = WebContext.get().getBaseUrl() + url;
			}
			rec.put("view", url);
			JSONArray navs = new JSONArray();
			Menu p = m;
			while(p != null){
				JSONObject pobj = new JSONObject();
				pobj.put("id", p.getId());
				pobj.put("title", p.getTitle());
				navs.add(0, pobj);
				p = p.getParent();
			}
			rec.put("navs", navs);
			rec.put("children", toArray(m.getChildren()));
			rls.add(rec);
		}
		return rls;
	}
	
}
