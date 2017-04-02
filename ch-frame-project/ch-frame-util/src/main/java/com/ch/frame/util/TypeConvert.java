package com.ch.frame.util;

import com.ch.frame.exception.GeneralException;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;


/**
 * 数据类型转换
 * @author Administrator
 *
 */
public class TypeConvert {
	/**
	 * 转换一个对象到指定类型的对象
	 * @param data
	 * @param type
	 * @return
	 */
	public static Object convert(Object data, String type, String format){
		switch (type) {
		case "int":
			return toInt(data);
		case "long":
			return toLong(data);
		case "intnum":
			return toIntnum(data);
		case "longnum":
			return toLongnum(data);
		case "boolean":
			return toBoolean(data);
		case "bool":
			return toBool(data);
		case "string":
			return toString(data);
		case "date":
			return toDate(data, format);
		case "datestart":
			return toDateStart(data, format);
		case "dateend":
			return toDateEnd(data, format);
		case "datetime":
			return toTimestamp(data, format);
		case "datetimestart":
			return toDateTimeStart(data, format);
		case "datetimeend":
			return toDateTimeEnd(data, format);			
		case "time":
			return toTime(data, format);
		case "timenum":
			return toTimenum(data, format);
		case "double":
			return toDouble(data);
		case "doublenum":
			return toDoublenum(data);
		case "object":
			return data;
		default:
			throw new GeneralException("Not support type " + type);
		}
	}
	private static Object toDateTimeEnd(Object data, String format) {
		if(data instanceof String){
			String s = (String)data;
			if(s.indexOf("至") > 0){
				return toTimestamp(s.substring(s.indexOf("至")+1), format);
			}
		}
		return data;
	}
	private static Object toDateTimeStart(Object data, String format) {
		if(data instanceof String){
			String s = (String)data;
			if(s.indexOf("至") > 0){
				return toTimestamp(s.substring(0, s.indexOf("至")), format);
			}
		}
		return data;
	}
	private static Object toDateEnd(Object data, String format) {
		if(data instanceof String){
			String s = (String)data;
			if(s.indexOf("至") > 0){
				return toDate(s.substring(s.indexOf("至")+1), format);
			}
		}
		return data;
	}
	private static Object toDateStart(Object data, String format) {
		if(data instanceof String){
			String s = (String)data;
			if(s.indexOf("至") > 0){
				return toDate(s.substring(0, s.indexOf("至")), format);
			}
		}
		return data;
	}
	public static String toString(Object data){
		if(isNull(data))return "";
		return data.toString();
	}
	/**
	 * 转换成长整数
	 * @param data
	 * @return
	 */
	public static Long toLong(Object data){
		if(isNull(data))return null;
		if(data instanceof Long){
			return (Long)data;
		}
		String str = getNumString(data.toString());
		if(isNull(str))return null;
		return Double.valueOf(str).longValue();
	}
	public static String getNumString(String s){
		if(s == null)return null;
		return s.replaceAll("[^\\.\\d]+", "");
	}
	public static long toLongnum(Object data){
		Long num = toLong(data);
		return num==null?0:num.longValue();
	}
	/**
	 * 转换成双精度
	 * @param data
	 * @return
	 */
	public static Double toDouble(Object data){
		if(isNull(data))return null;
		if(data instanceof Double){
			return (Double)data;
		}
		String s = data.toString();
		double d = Double.valueOf(s);
		return d;
	}
	public static double toDoublenum(Object data){
		Double num = toDouble(data);
		return num==null?0:num.doubleValue();
	}
	/**
	 * 转换成整数
	 * @param data
	 * @return
	 */
	public static Integer toInt(Object data){
		if(isNull(data))return null;
		if(data instanceof Integer){
			return (Integer)data;
		}
		return Double.valueOf(data.toString()).intValue();
	}
	public static int toIntnum(Object data){
		Integer num = toInt(data);
		return num==null?0:num.intValue();
	}
	/**
	 * 转换成布尔
	 * @param data
	 * @return
	 */
	public static Boolean toBoolean(Object data){
		if(isNull(data))return null;
		if(data instanceof Boolean){
			return (Boolean)data;
		}
		if(data.toString().equals("1") || data.toString().toLowerCase().equals("true")
				|| data.toString().toLowerCase().equals("yes") || data.toString().toLowerCase().equals("on")){
			return true;
		}
		return false;
	}
	public static boolean toBool(Object data){
		Boolean obj = toBoolean(data);
		return obj==null?false:obj;
	}
	/**
	 * 转换成时间对象
	 * @param data
	 * @return
	 */
	public static Timestamp toTimestamp(Object data, String format){
		if(isNull(data))return null;
		if(data instanceof Timestamp){
			return (Timestamp)data;			
		}
		if(data instanceof java.util.Date){
			return new Timestamp(((java.util.Date)data).getTime());
		}
		String s = data.toString();
		//如是是时间戳
		if(s.matches("\\d+")){
			return new Timestamp(Long.parseLong(s));
		}
		if(!isNull(format)){
			try {
				return new Timestamp(new SimpleDateFormat(format).parse(data.toString()).getTime());
			} catch (Exception e) {
				throw new GeneralException("Convert to datetime error content " + data + " is not mutch " + format, e);
			}
		}
		
		//带时间
		if(s.indexOf("T") >= 0){
			s = s.replace("T", " ");
		}
		if(s.indexOf(" ") > 0){
			//不带秒
			if(s.indexOf(":") == s.lastIndexOf(":")){
				s = s + ":00";
			}
			return Timestamp.valueOf(s);
		}else{
			//只有日期
			return Timestamp.valueOf(s + " 00:00:00");
		}
	}
	/**
	 * 转成sql日期
	 * @param data
	 * @return
	 */
	public static Date toDate(Object data, String format){
		Timestamp s = toTimestamp(data, format);
		if(s != null){
			return new Date(s.getTime());
		}
		return null;
	}

	/**
	 * 转成sql日期
	 * @param data
	 * @return
	 */
	public static java.util.Date toUtilDate(Object data, String format){
		if(data instanceof java.util.Date){
			return (java.util.Date)data;
		}
		Timestamp s = toTimestamp(data, format);
		if(s != null){
			return new Date(s.getTime());
		}
		return null;
	}
	/**
	 * 转成时间
	 * @param data
	 * @return
	 */
	public static Time toTime(Object data, String format){
		Timestamp s = toTimestamp(data, format);
		if(s != null){
			return new Time(s.getTime());
		}
		return null;
	}
	/**
	 * 转成时间截
	 * @param data
	 * @return
	 */
	public static Long toTimenum(Object data, String format){
		Timestamp s = toTimestamp(data, format);
		if(s != null){
			return s.getTime();
		}
		return null;
	}
	
	private static boolean isNull(Object data){
		return data == null || data.equals("");
	}
	/**
	 * 对数据进行四舍五入
	 * @param d
	 * @param format
	 * @param len
	 * @return
	 */
	public static Number roundDouble(Object dobj, int len){
		if(dobj == null)return null;
		Number d = toDouble(dobj);
		int num2 = 1;
		for(int i=0; i<len; i++){
			num2 = num2 * 10;
		}
		if(num2 > 1){			
			return Math.round(d.doubleValue()*num2)/(num2*1.0);
		}else{
			return Math.round(d.doubleValue());
		}
	}
	
}
