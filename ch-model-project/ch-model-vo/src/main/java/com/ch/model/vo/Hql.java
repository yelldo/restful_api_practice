package com.ch.model.vo;

import java.util.ArrayList;
import java.util.List;


public class Hql {
	private String sqlstr;
	@SuppressWarnings("unchecked")
	private List params = new ArrayList();
	private Hql(String sqlstr) {
		this.sqlstr = sqlstr;
	}
	public String getSqlstr() {
		return sqlstr;
	}
	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}
	@SuppressWarnings("unchecked")
	public List getParams() {
		return params;
	}
	public static Hql get(String str, Object ...params){
		Hql hql = new Hql(str);
		if(params != null){
			for(Object param : params){
				hql.params.add(param);
			}
		}
		return hql;
	}
	@SuppressWarnings("unchecked")
	public Hql addWhere(Where where){
		if(where == null)return this;
		if(where.getSqlstr() == null || where.getSqlstr().equals("")){
			return this;
		}
		sqlstr = sqlstr + " " + where.getPrefix() + " " + where.getSqlstr();
		params.addAll(where.getParams());
		return this;
	}
	public Hql addWhere(String prefix, Where where){
		if(where == null)return this;
		where.setPrefix(prefix);
		return addWhere(where);
	}
	public Object[] getParamArray(){
		if(params == null || params.isEmpty())return new Object[]{};
		return params.toArray();
	}
	@Override
	public String toString() {
		return getSqlstr();
	}
	public Hql add(String sql, Object ... objs){
		sqlstr = sqlstr + " " + sql;
		if(objs != null){
			if(params == null){
				params = new ArrayList();
			}
			for(Object obj : objs){
				params.add(obj);
			}
		}
		return this;
	}
	public Hql add2(String sql, Object[] objs){
		sqlstr = sqlstr + " " + sql;
		if(objs != null){
			if(params == null){
				params = new ArrayList();
			}
			for(Object obj : objs){
				params.add(obj);
			}
		}
		return this;
	}
	public Hql add(Where w){
		return addWhere(w);
	}
	public Hql append(String sql){
		return this.add(sql);
	}
	public Hql append(Where w){
		return this.add(w);
	}
	
	public static Hql get(Class cla) {
		return Hql.get("from " + cla.getName() + " a");
	}
	
}
