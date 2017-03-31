package com.ch.frame;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;

public class Globals {
	//存放在session中的用户ID
	public final static String USERID = "userid";
	//存放在session中的机构ID
	public final static String ORGID = "orgid";
    
	/**
	 * 格式化数字串
	 * @param num
	 * @param len
	 * @return
	 */
    public static String formatNum(double num, int len) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(len);
        String s = nf.format(num);
        if (len <= 0)
            return s;
        int has = s.indexOf(".");
        if (has < 0) {
            s = s + ".0";
        }
        int length = s.substring(s.indexOf(".") + 1).length();
        int slen = len - length;
        for (int i = 0; i < slen; i++) {
            s = s + "0";
        }
        return s;
    }

    /**
     * 把数据格式化成价格式
     *
     * @param num
     * @param len
     * @return
     */
    public static String formatPrice(double num, int len) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(len);
        String s = nf.format(num);
        if (len <= 0)
            return s;
        int has = s.indexOf(".");
        if (has < 0) {
            s = s + ".0";
        }
        int length = s.substring(s.indexOf(".") + 1).length();
        int slen = 0;
        if (length < 2) {
            slen = length < 2 ? 1 : 0;
        }

        for (int i = 0; i < slen; i++) {
            s = s + "0";
        }
        return s;
    }
    /**
     * 连接数组
     * @param ls
     * @param split
     * @return
     */
    public static String join(Object[] ls, String split) {
        if (ls == null)
            return null;
        return join(Arrays.asList(ls), split);
    }
    /**
     * 连接集合
     * @param ls
     * @param split
     * @return
     */
    public static String join(Collection ls, String split) {
        String s = null;
        if (ls == null || ls.isEmpty())
            return null;
        for (Object obj : ls) {
            if (obj != null) {
                if (s == null) {
                    s = obj.toString();
                } else {
                    s = s + (split == null ? "" : split) + obj.toString();
                }
            }
        }
        return s;
    }

    public static int length(String s) {
        if (s == null)
            return 0;
        char[] c = s.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            if (!isLetter(c[i])) {
                len++;
            }
        }
        return len;
    }

    public static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0 ? true : false;
    }

    public static boolean isEqual(double a, double b) {
        return Math.abs(a - b) < 0.0001;
    }
}
