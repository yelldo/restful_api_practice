package com.ch.frame.conf;

import com.ch.frame.util.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * 配置文件读取助手类
 * @author Administrator
 *
 */
public class ConfigHelper {
	protected static Log log = LogFactory.getLog(ConfigHelper.class);
	private static Map<String, PropertiesConfig> pcache = new HashMap<>();
	/**
	 * 在从环境目录下获取[name].properties文件，封装为PropertiesConfig返回，
	 * 如果当前环境目录下没有[name].properties，则从prod目录下获取[name].properties
	 * 否则抛出异常
	 * @param name
	 * @return
	 */
	public static PropertiesConfig getProp(String name){
		if(pcache.containsKey(name)){
			return pcache.get(name);
		}
		String file = "/" + getEnv() + "/" + name + ".properties";
		try {
			Properties p = new Properties();
			p.load(ConfigHelper.class.getResourceAsStream(file));
			PropertiesConfig pc= new PropertiesConfig(p);
			pcache.put(name, pc);
			log.info("Read " + file + " successful.");
			return pc;
		} catch (Exception e) {
			file = "/prod/" + name + ".properties"; 
			try {
				Properties p = new Properties();
				p.load(ConfigHelper.class.getResourceAsStream(file));
				PropertiesConfig pc= new PropertiesConfig(p);
				pcache.put(name, pc);
				log.info("Read " + file + " successful.");
				return pc;
			} catch (Exception e2) {
				throw new ConfigurationException("load " + file + " error", e);
			}
		}
		
	}
	private static Map<String, Element> cacheem = new HashMap<>();
	/**
	 * 获取以prefix开头的XML配置文件，如果有多个文件，将聚合多个文件二级节点一起返回
	 * foogroup_aa.xml
	 * foogroup_bb.xml
	 */
	public static Element getXml(String prefix){
		if(cacheem.containsKey(prefix)){
			return cacheem.get(prefix);
		}
		String file = getEnv();
		Element root = getByEnv(prefix, file);
		if(root == null && !file.equals("prod")){
			root = getByEnv(prefix, "prod");
		}
		if(root == null){
			root = new Element("root");
		}
		cacheem.put(prefix, root);
		return root;
	}
	private static Element getByEnv(String prefix, String file){
		List<URL> ls = ClassUtils.getAllResource(file);
		Element root = null;
		for(URL u : ls){
			String name = u.getPath().replace("\\", "/");
			name = name.substring(name.lastIndexOf("/")+1);
			if(name.startsWith(prefix) && name.endsWith(".xml")){
				try {
					SAXBuilder sb = new SAXBuilder();
					Document doc = sb.build(u);
					Element r = doc.getRootElement();
					if(root == null){
						root = r;
					}else{
						root.addContent(r.getChildren());
					}
				} catch (Exception e) {
					throw new ConfigurationException("Parse " + u.getPath() + " error", e);
				}
			}
		}
		return root;
	}
	
	private static String env = null;
	/**
	 * 获取当前配置环境名称，先获取ch.env系统属性
	 * 如果没有再读取/env.properties
	 * 如果再没有返回prod
	 * @return
	 */
	public synchronized static String getEnv(){
		if(env == null){
			env = System.getProperty("ch.env");
			if(StringUtils.isBlank(env)){
				try {
					Properties prop = new Properties();
					prop.load(ConfigHelper.class.getResourceAsStream("/env.properties"));

					System.out.println(PropertiesConfig.class.getResource("/env.properties").getPath());
					env = prop.getProperty("env");
				} catch (Exception e) {
					return "prod";
				}
			}
			if(StringUtils.isBlank(env)){
				return "prod";
			}
		}
		return env;
	}
	public static void main(String[] args) {
		System.out.println(getProp("redis").get("host"));
	}
}
