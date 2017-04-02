package com.ch.frame.util;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.ch.frame.exception.GeneralException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * json操作助手
 *
 * @author Administrator
 */
public class JsonUtils {

    /**
     * 将指定属性提取后形成jsonarray返回
     *
     * @param ls
     * @param fields ['age:mage','abc','name']
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static JSONArray toJsonArray(List ls, String[] fields) {
        try {
            JSONArray items = new JSONArray();
            for (Object obj : ls) {
                JSONObject rec = new JSONObject();
                for (String f : fields) {
                    String prop = f;
                    String name = f;
                    if (f.indexOf(":") > 0) {
                        prop = f.substring(0, prop.indexOf(":"));
                        name = f.substring(prop.indexOf(":") + 1);
                    }
                    rec.put(name, PropertyUtils.getProperty(obj, prop));
                }
                items.add(rec);
            }
            return items;
        } catch (Exception e) {
            throw new GeneralException(e);
        }
    }

    public static JSONObject toJSONObject(Object obj, String[] fields) {
        if (obj == null) {
            return new JSONObject();
        }
        try {
            JSONObject rec = new JSONObject();
            if (fields != null) {
                for (String f : fields) {
                    String prop = f;
                    String name = f;
                    if (f.indexOf(":") > 0) {
                        prop = f.substring(0, prop.indexOf(":"));
                        name = f.substring(prop.indexOf(":") + 1);
                    }
                    rec.put(name, PropertyUtils.getProperty(obj, prop));
                }
            } else {
                rec = (JSONObject) JSONObject.toJSON(obj);
            }

            return rec;
        } catch (Exception e) {
            throw new GeneralException(e);
        }
    }

    public static String toJSONString(Object object, SerializerFeature... features) {
        SerializeWriter out = new SerializeWriter();
        String result;
        JSONSerializer serializer = new JSONSerializer(out);
        SerializerFeature arr$[] = features;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$++) {
            SerializerFeature feature = arr$[i$];
            serializer.config(feature, true);
        }
        serializer.getValueFilters().add(new ValueFilter() {
            public Object process(Object obj, String s, Object value) {
                if (null != value) {
                    if (value instanceof java.util.Date) {
                        return String.format("%1$tF %1tT", value);
                    }
                    return value;
                } else {
                    return "";
                }
            }
        });
        serializer.write(object);
        result = out.toString();
        out.close();
        return result;
    }
    public static <T> T populatorObjectWithExcludes(JSONObject json, T target, String... excludes) {
    	List<Field> fs = ClassUtils.getAllDeclaredFields(target.getClass());
    	if(excludes == null){
    		excludes = new String[]{};
    	}
    	List<String> fls = new ArrayList<String>();
    	for(Field f : fs){
    		String name = f.getName();
    		boolean find = false;
    		for(String s : excludes){
    			if(s.equals(name)){
    				find = true;
    				break;
    			}
    		}
    		if(!find && !name.equals("class")){
    			fls.add(name);
    		}
    	}
    	return (T)populatorObject(json, target, fls.toArray(new String[fls.size()]));
    }
    
    /**
     * @param json
     * @param target
     * @return
     */
    public static <T> T populatorObject(JSONObject json, T target, String... fields) {
        if (target == null) return null;
        Method[] ms = target.getClass().getMethods();
        List<String> fls = new ArrayList<String>();
        if(fields != null){
        	fls.addAll(Arrays.asList(fields));
        }
        for (String prop : json.keySet()) {
            Object val = json.get(prop);
            boolean setflag = false;
            if(!fls.isEmpty() && !fls.contains(prop))continue;
            
            if (val != null && prop != null && prop.length() >= 1) {
                //特定类型无法转换的先转类型后再试
                for (Method m : ms) {
                    if (m.getName().equals("set" + prop.substring(0, 1).toUpperCase() + (prop.length() > 1 ? prop.substring(1) : ""))) {
                        Class[] targetClass = m.getParameterTypes();
                        if (targetClass != null && targetClass.length == 1) {
                            Class cla = targetClass[0];
                            if (Date.class.isAssignableFrom(cla)) {
                                val = toDate(val);
                            } else if (Time.class.isAssignableFrom(cla)) {
                                val = toTime(val);
                            } else if (Timestamp.class.isAssignableFrom(cla)) {
                                val = toTimestamp(val);
                            } else if (java.util.Date.class.isAssignableFrom(cla)) {
                                val = toDate(val);
                            } else if (Number.class.isAssignableFrom(cla)) {
                                if (isNull(val)) {
                                    val = null;
                                } else {
                                	val = org.springframework.util.NumberUtils.convertNumberToTargetClass(NumberUtils.createNumber(val.toString()), cla);
                                }
                            }
                            try {
                                PropertyUtils.setProperty(target, prop, val);
                                setflag = true;
                            } catch (Exception e2) {
                            }
                            break;
                        }
                    }
                }
            }
            if (!setflag) {
                try {
                    BeanUtils.setProperty(target, prop, val);
                } catch (Exception e) {

                }
            }
        }
        return (T) target;
    }

    private static boolean isNull(Object data) {
        return data == null || data.equals("");
    }

    /**
     * 转换成时间对象
     *
     * @param data
     * @return
     */
    public static Timestamp toTimestamp(Object data) {
        if (isNull(data)) return null;
        if (data instanceof Timestamp) {
            return (Timestamp) data;
        }
        if (data instanceof java.util.Date) {
            return new Timestamp(((java.util.Date) data).getTime());
        }
        String s = data.toString();
        //如是是时间戳
        if (s.matches("\\d+")) {
            return new Timestamp(Long.parseLong(s));
        }
        //带时间
        if (s.indexOf("T") >= 0) {
            s = s.replace("T", " ");
        }
        if (s.indexOf(" ") > 0) {
            //不带秒
            if (s.indexOf(":") == s.lastIndexOf(":")) {
                s = s + ":00";
            }
            return Timestamp.valueOf(s);
        } else {
            //只有日期
            return Timestamp.valueOf(s + " 00:00:00");
        }
    }

    /**
     * 转成sql日期
     *
     * @param data
     * @return
     */
    public static Date toDate(Object data) {
        Timestamp s = toTimestamp(data);
        if (s != null) {
            return new Date(s.getTime());
        }
        return null;
    }

    /**
     * 转成时间
     *
     * @param data
     * @return
     */
    public static Time toTime(Object data) {
        Timestamp s = toTimestamp(data);
        if (s != null) {
            return new Time(s.getTime());
        }
        return null;
    }

    /**
     * 转成时间截
     *
     * @param data
     * @return
     */
    public static Long toTimenum(Object data) {
        Timestamp s = toTimestamp(data);
        if (s != null) {
            return s.getTime();
        }
        return null;
    }
}
