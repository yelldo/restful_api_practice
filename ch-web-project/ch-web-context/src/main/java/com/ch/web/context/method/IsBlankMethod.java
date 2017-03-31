package com.ch.web.context.method;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class IsBlankMethod implements TemplateMethodModelEx {

	@Override
	public Object exec(List arg0) throws TemplateModelException {
		if(arg0 == null || arg0.isEmpty()){
			return true;
		}
		return arg0.get(0) == null || StringUtils.isBlank(arg0.get(0)+"");
	}

}
