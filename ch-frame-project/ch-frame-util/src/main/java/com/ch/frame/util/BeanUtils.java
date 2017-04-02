package com.ch.frame.util;

import com.ch.frame.exception.GeneralException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * @author wangmz
 *         2017/2/26
 */
public class BeanUtils {
    private static Log log = LogFactory.getLog(BeanUtils.class);

    public static <T> T mapToObject(Map<String, Object> map, Class<T> beanClass) {
        if (map == null)
            return null;

        try {
            T obj = beanClass.newInstance();

            org.apache.commons.beanutils.BeanUtils.populate(obj, map);
            return obj;
        } catch (Exception e) {
            log.error(e);
            throw new GeneralException("数据转换时出错");
        }

    }
}
