package com.ch.model.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PageData implements Serializable {
	private List data = null;
	private int total = 0;
	private int pageSize = 20;
	private int page = 0;
	private Map<String, Object> stats = new HashMap<>();
	public PageData() {
		this.data = new ArrayList<>();
	}
	@SuppressWarnings("unchecked")
	public PageData(int total, List data) {
		if(data == null){
			data = new ArrayList();
		}
		this.data = data;
		this.total = total;
	}
	//总页数
	public int getTotalPage(){
		int tpage = total / pageSize;
		if(total % pageSize != 0)
			tpage ++;
		return tpage;
	}
	public Integer getPage() {
		if(page > getTotalPage()){
			return getTotalPage();
		}
		return page;
	}
	
	public List getData() {
		return data;
	}
	public void setData(List data) {
		this.data = data;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public Map<String, Object> getStats() {
		return stats;
	}
	public void setStats(Map<String, Object> stats) {
		this.stats = stats;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public PageData convert(RecordConvertCallback convert){
		if(getData() == null)return this;
		for(int i=0,len=getData().size(); i<len; i++){
			Object rec = getData().get(i);
			Object newrec = convert.convert(rec);			
			getData().remove(i);
			if(newrec != null)
				getData().add(i, newrec);
		}
		return this;
	}
	public void addStats(String key, Object data){
		if(getStats() == null){
			setStats(new HashMap<String, Object>());
		}
		getStats().put(key, data);
	}
	@Override
	public String toString() {
		return "{total:" + getTotal() + ",data:" + getData() + ",stats:" + getStats() + "}";
	}
	
}
