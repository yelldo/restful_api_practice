package com.ch.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelField{
	//列名称
	public String label();
	//未设置取jpa的column
	public boolean nullable() default true;
	//最小值
	public double min() default 0d;
	//最大值
	public double max() default 0d;
	//最小长度
	public int minlen() default 0;
	//最大长度
	public int maxlen() default 255; 
	//正则表达式
	public String regex() default "";
	//表达式格式验证失败的错误提示
	public String regexmsg() default "";
	//常见验证类型
	public ColumnValidateType vtype() default ColumnValidateType.NONE;
	//关联实体显示
	public Class<?> referClass() default Class.class;
	//关联实体的显示字段
	public String referClassDisplayField() default "name";
	//是否为文件字段
	public boolean fileField() default false;
	//是否为图片字段
	public boolean imgField() default false;
	//是否为存储html的字段
	public boolean htmlField() default false;
	//是否为可过滤字段
	public boolean filterable() default false;
	//是否为不需要显示字段
	public boolean noshow() default false;
	//是否为,号分割的内容
	public boolean splitcontent() default false;
	//当前列是否为关键词的组成部分，主要用于关键词搜索
	public boolean iskeyword() default false;
	//该字段是否可编辑，默认为true
	public boolean editfield() default true;
	
}
