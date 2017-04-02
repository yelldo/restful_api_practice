package com.ch.frame.util;


import com.hx.frame.exception.TemplateException;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * @author wangmz
 */
public class FreemarkerTemplate {
    private final Logger logger = LoggerFactory.getLogger(FreemarkerTemplate.class);
    private final Configuration config;
    private final StringTemplateLoader loader;

    private static final String TEMPLATE_NAME = "template";

    public FreemarkerTemplate() {
        config = new Configuration(Configuration.VERSION_2_3_23);

        loader = new StringTemplateLoader();

        config.setTemplateLoader(loader);
        config.setLocalizedLookup(false);
        config.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_23));
    }

    private void setTemplate(String template) {
        loader.putTemplate(TEMPLATE_NAME, template);
    }

    public String transform(String templateSource, Map<String, Object> context) {
        setTemplate(templateSource);
        try {
            Template template = config.getTemplate(TEMPLATE_NAME);
            StringWriter writer = new StringWriter();
            template.process(context, writer);
            return writer.toString();
        } catch (IOException | freemarker.template.TemplateException e) {
            throw new TemplateException(e);
        }
    }
}
