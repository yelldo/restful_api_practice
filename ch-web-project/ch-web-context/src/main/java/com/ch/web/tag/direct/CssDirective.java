package com.ch.web.tag.direct;


import com.ch.web.freemarker.SimpleStringSequence;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;

import java.io.IOException;
import java.util.Map;

/**
 * 引入css文件
 * @author Administrator
 *
 */
public class CssDirective extends ParentTemplateDirectiveModel {

	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
						TemplateDirectiveBody body) throws TemplateException, IOException {
		TemplateScalarModel href = (TemplateScalarModel)params.get("href");
		if(href != null){
			SimpleStringSequence _css = (SimpleStringSequence)env.getVariable("_css");
			if(_css != null){
				_css.add(href.getAsString());
			}
		}
	}

}

