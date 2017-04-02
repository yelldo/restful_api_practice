package com.ch.frame.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * FreeMarker工具类
 *
 * @Author XiaoMaYong
 * @date 2017/1/17 10:29
 * @Version V1.0
 */
public class FreeMarkerUtils {

    private static final Logger LOG = Logger.getLogger(FreeMarkerUtils.class);


    /** 应用所在路径 */
    private static String appPath = null;
    /** 编码格式 UTF-8 */
    private static final String ENCODING = "UTF-8";
    /** 路径分割符 */
    public static final String PATH_SEPARATOR = "/";

    /**
     * 将解析之后的文件内容返回字符串
     * @param name 模板文件名
     * @param root 数据Map
     * @return
     */
    public static String printString(Template temp,Map<String,?> root) {
        StringWriter out = new StringWriter();
        try {
            temp.process(root, out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(out!=null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return out.toString();
    }

    public static String renderString(String templateString, Map<String, ?> model) {
        try {
            StringWriter result = new StringWriter();
            Template t = new Template("name", new StringReader(templateString), new Configuration());
            t.process(model, result);
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public static String renderTemplate(Template template, Object model) {
        try {
            StringWriter result = new StringWriter();
            template.process(model, result);
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public static Configuration buildConfiguration(String directory) throws IOException {
        Configuration cfg = new Configuration();
        Resource path = new DefaultResourceLoader().getResource(directory);
        System.out.println(path.getFile());
        cfg.setDirectoryForTemplateLoading(path.getFile());
        cfg.setDefaultEncoding("UTF-8");
        return cfg;
    }

    /**
     * 据数据及模板生成文件
     *
     * @param data 一个Map的数据结果集
     * @param templateFileName ftl模版路径(默认为freemark配置的视图路径)
     * @param outFileName 生成文件名称(可带路径)
     * @param isAbsPath 是否绝对路径
     */
    public static void crateFile(Map<String, Object> data, String templateFileName, String outFileName, boolean isAbsPath) {
        Configuration config = null;
        Writer out = null;
        try {
            config = buildConfiguration("classpath:/");
            // 获取模板,并设置编码方式，这个编码必须要与页面中的编码格式一致
            Template template = config.getTemplate(templateFileName);
            // 生成文件路径
            // 如果是绝对路径则直接使用
            if(isAbsPath){
                outFileName = new StringBuffer(outFileName).toString();
            } else{
                // 相对路径则使用默认的appPath加上输入的文件路径
                outFileName = new StringBuffer(appPath).append(File.separator).append(outFileName).toString();
            }
            String outPath = outFileName.substring(0, outFileName.lastIndexOf(PATH_SEPARATOR));
            // 创建文件目录
            FileUtils.forceMkdir(new File(outPath));
            File outFile = new File(outFileName);
            out = new OutputStreamWriter(new FileOutputStream(outFile), ENCODING);
            // 处理模版
            template.process(data, out);
            out.flush();
            LOG.info("由模板文件" + templateFileName + "生成" + outFileName + "成功.");
        } catch (Exception e) {
            LOG.error("由模板文件" + templateFileName + "生成" + outFileName + "出错.", e);
        } finally{
            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                LOG.error("关闭Write对象出错", e);
            }
        }
    }


    public  static void main(String[] args) throws IOException {
//		// renderString
//        Map<String, String> model = new HashMap();
//        model.put("userName", "calvin");
//        String result = FreeMarkerUtils.renderString("hello ${userName}", model);
//        System.out.println(result);
//		// renderTemplate
//		Configuration cfg = FreeMarkerUtils.buildConfiguration("classpath:/");
//		Template template = cfg.getTemplate("testTemplate.ftl");
//		result = FreeMarkerUtils.renderTemplate(template, model);
//		System.out.println(result);

//        result = FreeMarkerUtils.renderString("hello ${userName} ${r'${userName}'}", model);
//        System.out.println(result);
        Map<String, Object> model = new HashMap();
        model.put("name", "calvin");
        FreeMarkerUtils.crateFile(model,"ftl/test.ftl","e://e.html",true);

    }
}
