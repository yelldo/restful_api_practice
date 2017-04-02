package com.ch.frame.util;

import com.alibaba.fastjson.JSONArray;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具
 * 
 * @author nan.Zheng
 */
public class StringUtils {

	public static boolean isEmpty(String str) {
		return str == null || "".equals(str.trim())
				|| "null".equalsIgnoreCase(str.trim());
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public static boolean hasText(String text) {
		if (text == null)
			return false;
		for (int i = 0; i < text.length(); i++) {
			if (!Character.isWhitespace(text.charAt(i)))
				return true;
		}
		return false;
	}

	/**
	 * 判断字符串是否全为数字
	 * @param text
	 * @return
	 */
	public static boolean isDigest(String text) {
		if (text == null)
			return false;
		for (int i = 0; i < text.length(); i++) {
			if (!Character.isDigit(text.charAt(i)))
				return false;
		}
		return true;
	}
	public static String nullToBlank(String str) {
		if(hasText(str)){
			return str;
		}else{
			return "";
		}
	}
	/**
	 * 字符转转为数组
	 * @param text 以逗号为分隔符
	 * @return
     */
	public static String[] toArray(String text){

		String array[] = null;
		if(text == null || "".equals(text.trim())){
			array = new String[0];
		}else {
			if (text.indexOf(",") < 0) {
				array = new String[1];
				array[0] = text;
			} else {
				array = text.split(",");
			}
		}
		return array;
	}

	public static String join(String[] arrays, String joinFlag) {
		if (arrays == null || arrays.length == 0)
			return "";

		StringBuffer sb = new StringBuffer();
		int offset = arrays.length - 1;
		for (int i = 0; i < offset; i++) {
			sb.append(arrays[i]).append(joinFlag);
		}
		sb.append(arrays[offset]);

		return sb.toString();
	}

	public static String join(JSONArray arrays, String joinFlag) {
		if (arrays == null || arrays.size() == 0)
			return "";
		String[] a = new String[arrays.size()];
		for (int i = 0; i < a.length; i++) {
			a[i] = arrays.getString(i);
		}

		return join(a, joinFlag);
	}

	public static String join(List<String> list, String joinFlag) {
		String[] s = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			s[i] = list.get(i);
		}
		return join(s, joinFlag);
	}

	/**
	 * 替换替换半角全角 & , .
	 */
	public static String replaceSbcCaseOrDbcCase(String str) {
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(str)) {
			return str.replace(",", "&").replace(".", "&").replace("&", "&")
					.replace("，", "&").replace("．", "&").replace("＆", "&");
		}
		return null;

	}

	/**
	 * 替换 \t
	 * 
	 * @param str
	 * @return
	 */
	public static String clearString(String str) {
		if (str != null)
			str = str.replaceAll("\t", " ");
		return str;
	}

	public static String random(int len) {
		char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
				.toCharArray();
		String s = "";
		Random r = new Random();
		for (int i = 0; i < len; i++) {
			s = s + chars[r.nextInt(chars.length)];
		}
		return s;
	}

	public static String randomUuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static String getAnonName(String name) {
		if (null == name || "".equals(name)) {
			return "";
		}
		if (name.length() <= 1) {
			return "***";
		}
		return new StringBuilder().append(name.charAt(0)).append("***")
				.append(name.charAt(name.length() - 1)).toString();
	}

	/**
	 * 验证手机号码
	 * @param mobileNumber
	 * @return
	 */
	public static Boolean isValidMobileNumber(String mobileNumber){
		String regEx = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
		return matches(mobileNumber,regEx);
	}

	/**
	 * 验证邮箱
	 * @param email
	 * @return
	 */
	public static Boolean isValidEmail(String email){
		String regEx = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		return matches(email,regEx);
	}

	/**
	 * 判断字符是否能匹配指定正则表达式
	 * @param str 常用正则表达式
	 *			  数字：^[0-9]*$
	 *			  非零的正整数：^[1-9]\d*$ 或 ^([1-9][0-9]*){1,3}$ 或 ^\+?[1-9][0-9]*$
	 *	          正浮点数：^[1-9]\d*\.\d*|0\.\d*[1-9]\d*$ 或 ^(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*))$
	 *	          科学计数法：^((-?\d+.?\d*)[Ee]{1}(-?\d+))$
	 *	          汉字：^[\u4e00-\u9fa5]{0,}$
	 *	          英文和数字：^[A-Za-z0-9]+$ 或 ^[A-Za-z0-9]{4,40}$
	 *	          中文、英文、数字包括下划线：^[\u4E00-\u9FA5A-Za-z0-9_]+$
	 *	          Email地址：^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$
	 *	          密码(以字母开头，长度在6~18之间，只能包含字母、数字和下划线)：^[a-zA-Z]\w{5,17}$
	 *	          强密码(必须包含大小写字母和数字的组合，不能使用特殊字符，长度在8-10之间)：^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,10}$
	 * @return
	 */
	public static Boolean matches(String str,String regEx){
		boolean flag = false;
		try{
			if(hasText(str)) {
				Pattern pattern = Pattern.compile(regEx);
				Matcher matcher = pattern.matcher(str);
				flag = matcher.matches();
			}
		}catch(Exception e){
			flag = false;
		}
		return flag;
	}
	public static Boolean matchesDouble(String priceStr){
		String regEx = "^[-+]?(\\d+\\.?\\d*|\\d*\\.?\\d+)$";
		return matches(priceStr,regEx);
	}
	/**
	 * 正则匹配价格字符串(要求大于零)
	 * @param priceStr
	 * @return
	 */
	public static Boolean matchesPrice(String priceStr){
		String regEx = "^[1-9]\\d*$|^[1-9]\\d*\\.\\d*$|^0\\.\\d*[1-9]\\d*$";
		return matches(priceStr,regEx);
	}
	/**
	 * 正则匹配科学计数法字符串
	 * @param str
	 * @return
	 */
	public static Boolean matchesScientificNotation(String str){
		String regEx = "^((-?\\d+.?\\d*)[Ee]{1}(-?\\d+))$";
		return matches(str,regEx);
	}

	/**
	 * 转为36进制字符串
	 * @param prefix		前缀
	 * @param longNumStr 数字
	 * @param formatLen  格式化长度，为0时表示不格式化
	 * @return
	 */
	public static  String generate36HexCode(String prefix,String longNumStr,Integer formatLen){
		String code36Hex ="";
		long number = 0;
		if(matches(longNumStr,"^[0-9]*$")){
			number=Long.parseLong(longNumStr);
			code36Hex = Long.toString(number, 36);
			if (code36Hex.length() < formatLen) {
				int size = formatLen - code36Hex.length();
				for (int i = 0; i < size; i++) {
					code36Hex = "0" + code36Hex;
				}
			}
			code36Hex = StringUtils.nullToBlank(prefix) + code36Hex;
		}
		return code36Hex.toUpperCase();
	}
	// 生成长度为6的随机数（机构注册使用）

	/**
	 * 生成指定长度的随机数
	 * @return
	 */
	public static String generateRandom(Integer length) {
		String random= String.valueOf(Math.round(Math.random()*1000000));
		if(random.length()!=length){
			random = generateRandom(6);
		}
		return random;
	}
}
