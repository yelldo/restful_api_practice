package com.ch.web.tag.direct;

import freemarker.template.TemplateDirectiveModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class ParentTemplateDirectiveModel implements TemplateDirectiveModel {
	protected Log log = LogFactory.getLog(this.getClass());
	

}
