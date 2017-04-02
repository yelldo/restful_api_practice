package com.ch.frame.util;

import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author wangmz
 *         2017/1/16
 */
public class XmlUtils {
    /**
     * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
     */
    public static Map<String,String> doXMLParse(String strxml) {
        strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");
        if (!StringUtils.hasText(strxml)) {
            return null;
        }
        Map m = new HashMap();
        try (InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));) {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(in);
            Element root = doc.getRootElement();
            List list = root.getChildren();
            Iterator it = list.iterator();
            while (it.hasNext()) {
                Element e = (Element) it.next();
                String k = e.getName();
                String v = "";
                List children = e.getChildren();
                if (children.isEmpty()) {
                    v = e.getTextNormalize();
                } else {
                    v = XmlUtils.getChildrenText(children);
                }
                m.put(k, v);
            }
        } catch (Exception e) {
            throw new RuntimeException("解析xml出错,xml#" + strxml, e);
        }
        return m;
    }

    /**
     * 获取子结点的xml
     */

    public static String getChildrenText(List children) {
        StringBuffer sb = new StringBuffer();
        if (!children.isEmpty()) {
            Iterator it = children.iterator();
            while (it.hasNext()) {
                Element e = (Element) it.next();
                String name = e.getName();
                String value = e.getTextNormalize();
                List list = e.getChildren();
                sb.append("<" + name + ">");
                if (!list.isEmpty()) {
                    sb.append(XmlUtils.getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }

        return sb.toString();
    }

    /**
     * 获取xml编码字符集
     */
    public static String getXMLEncoding(String strxml) throws JDOMException, IOException {
        InputStream in = IOUtils.toInputStream(strxml);
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(in);
        in.close();
        return (String) doc.getProperty("encoding");
    }
}
