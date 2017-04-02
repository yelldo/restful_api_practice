package com.ch.frame.util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public class FormatHelper {
	/**
	 * 格式化数字
	 */
	public static String format(Object obj, int len){
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(len);
		return nf.format(TypeConvert.toDoublenum(obj));
	}
	/**
	 * 格式化日期
	 * @param obj
	 * @param format
	 * @return
	 */
	public static String format(Object obj, String format){
		if(obj == null)return null;
		java.util.Date ns = TypeConvert.toUtilDate(obj, format);
		if(ns != null){
			return new SimpleDateFormat(format==null?"yyyy-MM-dd":format).format(ns);
		}
		return "";
	}
}
