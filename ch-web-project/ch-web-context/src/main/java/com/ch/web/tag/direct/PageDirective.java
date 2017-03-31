package com.ch.web.tag.direct;

import com.ch.web.freemarker.FreeMarkerHelper;
import com.ch.web.freemarker.ModelData;
import com.ch.web.freemarker.SimpleStringSequence;
import freemarker.core.Environment;
import freemarker.template.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;

/**
 * 页面标签
 * @author Administrator
 * <@page title="页面标題" hasHeader="true/false是否显示导航头" extjs="true/false"/>
 */
public class PageDirective extends ParentTemplateDirectiveModel{
	
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
						TemplateDirectiveBody body) throws TemplateException, IOException {
		SimpleStringSequence _css = new SimpleStringSequence(new ArrayList<>());
		SimpleStringSequence _script = new SimpleStringSequence(new ArrayList<>());
		SimpleStringSequence _scriptBody = new SimpleStringSequence(new ArrayList<>());
		env.setVariable("_css", _css);
		env.setVariable("_script", _script);
		env.setVariable("_scriptBody", _scriptBody);
		TemplateScalarModel title = (TemplateScalarModel)params.get("title");
		StringWriter sw = new StringWriter();
		body.render(sw);
		TemplateBooleanModel extjs = (TemplateBooleanModel)params.get("extjs");
		TemplateBooleanModel _copyright = (TemplateBooleanModel)params.get("copyright");
		//输出模板
		FreeMarkerHelper.exec(new ModelData().add("_title", title==null?"":title.getAsString())
				.add("_css", _css.convertToList(true, true))
				.add("_script", _script.convertToList(true, true))
				.add("_scriptBody", _scriptBody)
				.add("_extjs", extjs)
				.add("_body", sw.getBuffer())
				.add("_copyright", _copyright)
				, "/tag/page.ftl", env.getOut());
	}

}
