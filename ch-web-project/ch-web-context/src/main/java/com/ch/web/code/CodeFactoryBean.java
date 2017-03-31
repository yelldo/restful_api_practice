package com.ch.web.code;

import com.ch.frame.util.ClassUtils;
import com.ch.model.annotation.CodeGroup;
import com.ch.web.exception.WebException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典数据
 * @author Administrator
 */
public class CodeFactoryBean {
	private static Map<String, List<CodeItem>> map = new HashMap<String, List<CodeItem>>();
	protected Log log = LogFactory.getLog(this.getClass());
	public CodeFactoryBean() {
		
	}
	public void init(){
		log.debug("Begin init codegroup...");
		//从所有业务模型中提取字典定义
		List<Class<?>> ls = ClassUtils.getClasses("com.hx.model");
		for(Class cla : ls){
			CodeGroup group = (CodeGroup)cla.getAnnotation(CodeGroup.class);
			if(group != null){
				initGroup(group);
			}
			Field[] fields = cla.getDeclaredFields();
			for(Field f : fields){
				group = (CodeGroup)f.getAnnotation(CodeGroup.class);
				if(group != null)
					initGroup(group);
			}
		}
	}
	private void initGroup(CodeGroup group) {
		List<CodeItem> items = map.get(group.value());
		if(items == null){
			items = new ArrayList<CodeItem>();
		}
		for(com.ch.model.annotation.CodeItem item : group.items()){
			CodeItem it = new CodeItem();
			it.setCode(item.value());
			it.setText(item.text());
			items.add(it);
		}
		map.put(group.value(), items);
		log.debug("ADD CODE GROUP:" + group.value() + ","  + items);
	}
	/**
	 * 获取一组字典
	 * @param group
	 * @return
	 */
	public static List<CodeItem> getCodes(String group){
		if(!map.containsKey(group))
			throw new WebException("未找到字典组");
		return map.get(group);
	}
	/**
	 * 根据字典值获取字典显示
	 * @param group
	 * @param value
	 * @return
	 */
	public static String getText(String group, String value){
		CodeItem i = getCode(group, value);
		return i==null?value:i.getText();
	}
	/**
	 * 根据字典值获取字典对象
	 * @param group
	 * @param value
	 * @return
	 */
	public static CodeItem getCode(String group, String value){
		List<CodeItem> ls = getCodes(group);
		for(CodeItem i : ls){
			if(i.getCode().equals(value)){
				return i;
			}
		}
		return null;
	}
}
