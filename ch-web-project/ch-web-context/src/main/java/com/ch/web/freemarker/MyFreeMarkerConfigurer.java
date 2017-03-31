package com.ch.web.freemarker;

import com.ch.web.tag.direct.CssDirective;
import com.ch.web.tag.direct.NavDirective;
import com.ch.web.tag.direct.PageDirective;
import com.ch.web.tag.direct.ScriptDirective;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;


public class MyFreeMarkerConfigurer extends FreeMarkerConfigurer {
	public void init(){
		freemarker.template.Configuration cfg = getConfiguration();
		//<@page title="页面标題"/>
		cfg.setSharedVariable("page", new PageDirective());
		//导航栏
		cfg.setSharedVariable("nav", new NavDirective());
		//<@css href="/foo.css,/bar.css"/>
		cfg.setSharedVariable("css", new CssDirective());
		//<@script src="/bar.js,/foo.js"/>
		//<@script> alert('foo'); </@script>
		cfg.setSharedVariable("script", new ScriptDirective());
		
	}
}
