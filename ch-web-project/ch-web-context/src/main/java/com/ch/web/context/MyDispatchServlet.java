package com.ch.web.context;


import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class MyDispatchServlet extends DispatcherServlet {
	public static ApplicationContext springcontext = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected WebApplicationContext initWebApplicationContext() {
		springcontext = super.initWebApplicationContext();
		return (WebApplicationContext)springcontext;
	}
}
