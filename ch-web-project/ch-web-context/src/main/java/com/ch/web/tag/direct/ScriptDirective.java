package com.ch.web.tag.direct;

import com.ch.web.freemarker.SimpleStringSequence;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * 用于向页面引入js
 * @author Administrator
 *
 */
public class ScriptDirective extends ParentTemplateDirectiveModel {

	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
						TemplateDirectiveBody body) throws TemplateException, IOException {
		TemplateScalarModel src = (TemplateScalarModel)params.get("src");
		if(src != null){
			SimpleStringSequence _script = (SimpleStringSequence)env.getVariable("_script");
			if(_script != null){
				_script.add(src.getAsString());
			}
		}
		//页面中的script
		if(body != null){
			StringWriter sw = new StringWriter();
			body.render(sw);
			String str = sw.getBuffer().toString();
			if(!StringUtils.isBlank(str)){
				SimpleStringSequence _scriptBody = (SimpleStringSequence)env.getVariable("_scriptBody");
				if(_scriptBody != null){
					_scriptBody.add(str);
				}
			}
		}
	}

}
