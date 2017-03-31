package com.ch.frame.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class DbObjectBuilderWraper {
	public static DbObjectBuilderWraper newInstance(){
		return new DbObjectBuilderWraper();
	}
	private BasicDBObjectBuilder builder;
	public DbObjectBuilderWraper(BasicDBObjectBuilder builder) {
		this.builder = builder;
	}
	public DbObjectBuilderWraper() {
		this.builder = new BasicDBObjectBuilder();
	}
	public BasicDBObject get(){
		return (BasicDBObject)builder.get();
	}
	private boolean isNull(Object obj){
		if(obj == null || obj.equals(""))return true;
		if(obj instanceof Long && obj.equals(0L))return true;
		if(obj instanceof Integer && obj.equals(0))return true;
		return false;
	}
	
	public DbObjectBuilderWraper addOption(String key, Object obj){
		if(obj == null || obj.equals(""))return this;
		if(obj instanceof BasicDBObject)
			this.builder.append(key, obj);
		if(obj.getClass().isArray())
			return in(key, (Object[])obj);
		if(obj instanceof Collection)
			return in(key, ((Collection)obj).toArray());
		
		this.builder.append(key, obj);
		return this;
	}
	public DbObjectBuilderWraper lt(String key, Object obj){
		if(obj == null || obj.equals(""))return this;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("$lt", obj);
		this.builder.append(key, map);
		return this;
	}
	/**
	 * 不符，支持数组
	 * {_id:xxx, groups:['a','b','c'}]
	 * ne("groups", "a")//表示不含"a"
	 * @param key
	 * @param obj
	 * @return
	 */
	public DbObjectBuilderWraper ne(String key, Object obj){
		if(obj == null || obj.equals(""))return this;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("$ne", obj);
		this.builder.append(key, map);
		return this;
	}
	
	public DbObjectBuilderWraper gt(String key, Object obj){
		if(obj == null || obj.equals(""))return this;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("$gt", obj);
		this.builder.append(key, map);
		return this;
	}
	public DbObjectBuilderWraper lte(String key, Object obj){
		if(obj == null || obj.equals(""))return this;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("$lte", obj);
		this.builder.append(key, map);
		return this;
	}
	public DbObjectBuilderWraper gte(String key, Object obj){
		if(obj == null || obj.equals(""))return this;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("$gte", obj);
		this.builder.append(key, map);
		return this;
	}
	public DbObjectBuilderWraper addOptionBetween(String key, Object start, Object end){
		Map<String, Object> map = new HashMap<String, Object>();
		if(!isNull(start))map.put("$gte", start);
		if(!isNull(end))map.put("$lt", end);
		if(map.isEmpty())return this;
		this.builder.append(key, map);
		return this;
	}
	public DbObjectBuilderWraper in(String key, Object[] items){
		if(items == null || items.length == 0)return this;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("$in", items);
		this.builder.append(key, map);
		return this;
	}
	public DbObjectBuilderWraper in(String key, Collection items){
		if(items == null || items.isEmpty())return this;
		if(items.size() == 0){
			addOption(key, items.iterator().next());
			return this;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("$in", items);
		this.builder.append(key, map);
		return this;
	}
	public DbObjectBuilderWraper nin(String key, Object[] items){
		if(items == null || items.length == 0)return this;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("$nin", items);
		this.builder.append(key, map);
		return this;
	}
	public DbObjectBuilderWraper nin(String key, Collection items){
		if(items == null || items.isEmpty())return this;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("$nin", items);
		this.builder.append(key, map);
		return this;
	}
	public DbObjectBuilderWraper eqOrIn(String key, Object[] vals) {
		if(vals != null && vals.length > 1){
			return this.in(key, vals);
		}
		if(vals != null && vals.length == 1 && vals[0] != null){
			return this.addOption(key, vals[0]);
		}
		return this;
	}
	public DbObjectBuilderWraper append(String key, BasicDBObject obj){
		this.builder.append(key, obj);
		return this;
	}
	/**
	 * 添加$or过滤，类似于
	 * BasicDBList ls = new BasicDBList();
		ls.add(new BasicDBObject("foo", new BasicDBObject("$exists", false)));
		ls.add(new BasicDBObject("foo", 1));
		ls.add(new BasicDBObject("bar", 1));
		query.append("$or", ls);
	 * @param ls
	 * @return
	 */
	public DbObjectBuilderWraper or(DBObject... items){
		this.builder.append("$or", items);
		return this;
	}
	/**
	 * 是否存在
	 * @param col
	 * @param flag
	 * @return
	 */
	public DbObjectBuilderWraper exists(String col, boolean flag){
		this.builder.append(col, new BasicDBObject("$exists", flag));
		return this;
	}
	/**
	 * 添加js过滤，相当于: new BasicDbObject("$where", "this.rank > 1");
	 * @param expression
	 * @return
	 */
	public DbObjectBuilderWraper where(String expression){
		if(isNull(expression))return this;
		this.builder.append("$where", expression);
		return this;
	}
	/**
	 * Like处理
	 * @param key
	 * @param content
	 * @return
	 */
	public DbObjectBuilderWraper like(String key, String content){
		if(isNull(content))return this;
		Pattern pattern = Pattern.compile("^.*" + content+ ".*$", Pattern.CASE_INSENSITIVE);
		this.builder.append(key, pattern);
		return this;
	}
	/**
	 * Like处理多个词[]
	 * @param key
	 * @param contents
	 * @return
	 */
	public DbObjectBuilderWraper likes(String key, String... contents){
		String pstr = null;
		for(String s : contents){
			if(isNull(s))continue;
			String str = s;
			if(pstr == null)
				pstr = str;
			else 
				pstr = pstr+"|" + str;
		}
		pstr = "^.*("+pstr+").*$";
		Pattern pattern = Pattern.compile(pstr, Pattern.CASE_INSENSITIVE);
		this.builder.append(key, pattern);
		return this;
	}

	public DbObjectBuilderWraper match(Object val){
		this.builder.append("$elemMatch", val);
		return this;
	}

	/**
	 * Like返回DB对象
	 * @param key
	 * @param content
	 * @return
	 */
	public DBObject likeObj(String key, String content){
		Pattern pattern = Pattern.compile("^.*" + content+ ".*$", Pattern.CASE_INSENSITIVE);
		return new BasicDBObject(key, pattern);
	}
	/**
	 * Like返回DB对象，多个词[]
	 * @param key
	 * @param contents
	 * @return
	 */
	public DBObject likeObjs(String key, String... contents){
		String pstr = null;
		for(String s : contents){
			if(isNull(s))continue;
			String str = s;
			if(pstr == null)
				pstr = str;
			else 
				pstr = pstr + "|" + str;
		}
		pstr = "^.*("+pstr+").*$";
		Pattern pattern = Pattern.compile(pstr, Pattern.CASE_INSENSITIVE);
		return new BasicDBObject(key, pattern);
	}
}
