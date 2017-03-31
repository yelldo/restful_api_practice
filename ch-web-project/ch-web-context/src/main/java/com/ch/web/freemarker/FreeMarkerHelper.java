package com.ch.web.freemarker;

import com.ch.web.context.WebContext;
import com.ch.web.exception.WebException;
import freemarker.core.Environment;
import freemarker.template.Template;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.Writer;


public class FreeMarkerHelper {
	/**
	 * 执行模板
	 * @param data
	 * @param template
	 * @param out
	 * @return
	 */
	public static Environment exec(ModelData data, String template, Writer out){
		try {			
			//获取模板并执行
			FreeMarkerConfigurer fc = WebContext.get().getBean(FreeMarkerConfigurer.class);
			Template tp = fc.getConfiguration().getTemplate(template);
			Environment e = tp.createProcessingEnvironment(data, out);
			e.process();
			return e;
		} catch (Exception e) {
			throw new WebException("Execute freemark error", e);
		}
	}
}
