package com.ch.frame.util;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 关于JSON的一些公共方法
 *
 * @Author XiaoMaYong
 * @date 2017/1/20 16:16
 * @Version V1.0
 */
public class CommJSONUtils {
    /**
     * 将json转化为实体POJO
     *
     * @param jsonObject
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> Object JSON2Bean(JSONObject jsonObject, Class<T> tClass) {
        T t = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            t = objectMapper.readValue(jsonObject.toJSONString(), tClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 将json字符串转化为实体POJO
     *
     * @param jsonStr
     * @param obj
     * @return
     */
    public static <T> Object JSONStr2Bean(String jsonStr, Class<T> obj) {
        T t = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            t = objectMapper.readValue(jsonStr, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 把JSON中的值覆盖到Bean中。（只覆盖JSON中有的属性）
     *
     * @param bean
     * @param jsonObject
     */
    public static void setFieldValues(Object bean, JSONObject jsonObject) {
        Class<?> cls = bean.getClass();
        // 取出bean里的所有方法
        //Method[] methods = cls.getDeclaredMethods();
        List<Field> fields = ClassUtils.getAllDeclaredFields(cls);
        for (Field field : fields) {
            try {
                String fieldSetName = parSetName(field.getName());

                if(!jsonObject.containsKey(field.getName().toString())) continue;
//                System.out.println("属性：" + field.getName() + "值：" + jsonObject.get(field.getName()));
                
                PropertyDescriptor pd = null;
                Method setMethod = null;
                try {
                	pd = PropertyUtils.getPropertyDescriptor(bean, field.getName());
					setMethod = pd.getWriteMethod();
					if(setMethod == null){
						continue;
					}
				} catch (Exception e) {
					continue;
				}

                String fieldType = field.getType().getSimpleName();
                if ("String".equals(fieldType)) {
                    setMethod.invoke(bean, jsonObject.getString(field.getName()));
                } else if ("Date".equals(fieldType)) {
                    Date temp = parseDate(jsonObject.getString(field.getName()));
                    setMethod.invoke(bean, temp);
                } else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
                    Integer intval = jsonObject.getInteger(field.getName());
                    setMethod.invoke(bean, intval);
                } else if ("Long".equalsIgnoreCase(fieldType)) {
                    Long temp = jsonObject.getLong(field.getName());
                    setMethod.invoke(bean, temp);
                } else if ("Double".equalsIgnoreCase(fieldType)) {
                    Double temp = jsonObject.getDouble(field.getName());
                    setMethod.invoke(bean, temp);
                } else if ("Boolean".equalsIgnoreCase(fieldType)) {
                    Boolean temp = jsonObject.getBoolean(field.getName());
                    setMethod.invoke(bean, temp);
                } else {
                    System.out.println("not supper type" + fieldType);
                }
            } catch (Exception e) {
                continue;
            }
        }
    }

    

    /**
     * 格式化string为Date
     *
     * @param datestr
     * @return date
     */
    private static Date parseDate(String datestr) {
        if (null == datestr || "".equals(datestr)) {
            return null;
        }
        try {
            String fmtstr = null;
            if (datestr.indexOf(':') > 0) {
                fmtstr = "yyyy-MM-dd HH:mm:ss";
            } else {
                fmtstr = "yyyy-MM-dd";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(fmtstr, Locale.UK);
            return sdf.parse(datestr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 拼接在某属性的 set方法
     *
     * @param fieldName
     * @return String
     */
    private static String parSetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        return "set" + fieldName.substring(0, 1).toUpperCase()
            + fieldName.substring(1);
    }

    public static void main(String[] args) {
/*        String params = "{\"userAccount\":\"12\",\"mobile\":\"312\",\"_form\":\"Dx.trd.mgr.OrgUserEditWindowSc0\",\"id\":\"\",\"_dxaction\":\"鏂板\uE583-淇濆瓨\",\"userName\":\"312\",\"adminFlag\":true,\"userCode\":\"123\",\"email\":\"312\",\"orgInfoId\":312,\"status\":1}\n";
        OrgUser orgUser = (OrgUser)CommJSONUtils.JSON2Bean(params.toString(),OrgUser.class);*/
    }
}
