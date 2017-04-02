package com.ch.frame.util;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * 有精度要求的数字操作工具类
 * <p>
 * Created by luzy on 2017/2/15.
 */
public class NumberOperateUtil {

    private static int ROUND_HALF_UP = BigDecimal.ROUND_HALF_UP;//四舍五入

    /**
     * 将数字字符串转化为BigDecimal
     *
     * @param str 数字字符串
     * @return BigDecimal
     */
    public static BigDecimal getBigDecimalForStr(String str) {
        return new BigDecimal(str);
    }

    /**
     * 将数字字符串转化为double,并保留指定小数位
     *
     * @param str   数字字符串
     * @param scale 指定要保留的小数位(可为空)
     * @return double
     */
    public static double getBigDecimalForStrReturnDouble(String str, Integer scale) {
        BigDecimal one = getBigDecimalForStr(str);
        if (null != scale) {
            return one.setScale(scale, ROUND_HALF_UP).doubleValue();
        }
        return one.doubleValue();
    }

    /**
     * 将double类型数字转化为BigDecimal
     *
     * @param one 数字
     * @return BigDecimal
     */
    public static BigDecimal getBigDecimalForDouble(double one) {
        return getBigDecimalForStr(one + "");
    }

    /**
     * 获取指定小数位的double
     *
     * @param one 数字
     * @return double
     */
    public static double getScaleDouble(double one, Integer scale) {
        return getBigDecimalForStrReturnDouble(one + "", scale);
    }

    /**
     * 获取2位小数点的double
     *
     * @param one 数字
     * @return double
     */
    public static double getScaleDouble(double one) {
        return getScaleDouble(one, 2);
    }

    /**
     * 获取货币格式化字符串(￥#.###.##)
     *
     * @param one 数字
     * @return String
     */
    public static String getCurrencyFormat(BigDecimal one) {
        NumberFormat currency = NumberFormat.getCurrencyInstance(); //建立货币格式化引用
        return currency.format(one);
    }

    /**
     * 获取货币格式化字符串(￥#.###.##)
     *
     * @param one 数字
     * @return String
     */
    public static String getCurrencyFormat(double one) {
        return getCurrencyFormat(getBigDecimalForStr(one + ""));
    }

    /**
     * 两个BigDecimal数字相加
     *
     * @param one 第一个数字
     * @param two 第二个数字
     * @return BigDecimal
     */
    public static BigDecimal add(BigDecimal one, BigDecimal two) {
        return one.add(two);//相加
    }

    /**
     * 两个数字字符串相加
     *
     * @param oneNumber 第一个数字字符串
     * @param twoNumber 第二个数字字符串
     * @return BigDecimal
     */
    public static BigDecimal add(String oneNumber, String twoNumber) {
        BigDecimal one = new BigDecimal(oneNumber);
        BigDecimal two = new BigDecimal(twoNumber);
        return add(one, two);
    }

    /**
     * 两个double数字相加
     *
     * @param oneNumber 第一个数字
     * @param twoNumber 第二个数字
     * @return BigDecimal
     */
    public static BigDecimal add(double oneNumber, double twoNumber) {
        return add(oneNumber + "", twoNumber + "");
    }

    /**
     * 两个double数字相加并保留指定小数位
     *
     * @param one   第一个数字
     * @param two   第二个数字
     * @param scale 指定要保留的小数位(可为空)
     * @return BigDecimal
     */
    public static double add(double one, double two, Integer scale) {
        BigDecimal b = add(one, two);
        if (null != scale) {
            return b.setScale(scale, ROUND_HALF_UP).doubleValue();
        }
        return b.doubleValue();
    }

    /**
     * 两个数字字符串相加并保留指定小数位
     *
     * @param oneNumber 第一个数字字符串
     * @param twoNumber 第二个数字字符串
     * @param scale     指定要保留的小数位(可为空)
     * @return double
     */
    public static double addReturnDouble(String oneNumber, String twoNumber, Integer scale) {
        BigDecimal b = add(oneNumber, twoNumber);
        if (null != scale) {
            return b.setScale(scale, ROUND_HALF_UP).doubleValue();
        }
        return b.doubleValue();
    }

    /**
     * 两个BigDecimal数字相减
     *
     * @param one   第一个数
     * @param two   第二个数
     * @return BigDecimal
     */
    public static BigDecimal subtract(BigDecimal one, BigDecimal two) {
        return one.subtract(two);//相减
    }

    /**
     * 两个BigDecimal数字相减并保留指定小数位
     *
     * @param one   第一个数
     * @param two   第二个数
     * @param scale 指定小数位(可为空)
     * @return double
     */
    public static double subtractReturnDouble(BigDecimal one, BigDecimal two, Integer scale) {
        BigDecimal b = subtract(one, two);
        if (null != scale) {
            return b.setScale(scale, ROUND_HALF_UP).doubleValue();
        }
        return b.doubleValue();
    }

    /**
     * 两个数字字符串相减
     *
     * @param oneNumber 第一个数字字符串
     * @param twoNumber 第二个数字字符串
     * @return BigDecimal
     */
    public static BigDecimal subtract(String oneNumber, String twoNumber) {
        BigDecimal one = new BigDecimal(oneNumber);
        BigDecimal two = new BigDecimal(twoNumber);
        return subtract(one, two);
    }

    /**
     * 两个数字相减并保留指定小数位
     *
     * @param oneNumber 第一个数字
     * @param twoNumber 第二个数字
     * @param scale     指定小数位(可为空)
     * @return double
     */
    public static double subtractReturnDouble(double oneNumber, double twoNumber, Integer scale) {
        BigDecimal one = new BigDecimal(oneNumber + "");
        BigDecimal two = new BigDecimal(twoNumber + "");
        return subtractReturnDouble(one, two, scale);
    }
}
