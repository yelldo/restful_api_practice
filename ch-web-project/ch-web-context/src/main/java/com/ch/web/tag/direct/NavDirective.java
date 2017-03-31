package com.ch.web.tag.direct;

import com.ch.web.freemarker.FreeMarkerHelper;
import com.ch.web.freemarker.ModelData;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

import java.io.IOException;
import java.util.Map;

/**
 * 导航栏
 * @author Administrator
 * <@nav/>
 */
public class NavDirective extends ParentTemplateDirectiveModel{
	
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
						TemplateDirectiveBody body) throws TemplateException, IOException {
		
		//输出模板
		FreeMarkerHelper.exec(new ModelData(), "/tag/nav.ftl", env.getOut());
	}

}
