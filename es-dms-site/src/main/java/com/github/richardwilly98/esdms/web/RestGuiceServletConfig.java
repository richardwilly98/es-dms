package com.github.richardwilly98.esdms.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.log4j.Logger;
import org.apache.shiro.guice.aop.ShiroAopModule;

import com.github.richardwilly98.esdms.inject.EsJerseyServletModule;
import com.github.richardwilly98.esdms.shiro.EsShiroWebModule;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.mycila.inject.jsr250.Jsr250;

public class RestGuiceServletConfig extends GuiceServletContextListener {

	Logger log = Logger.getLogger(this.getClass());

	private ServletContext servletContext;

	@Override
	protected Injector getInjector() {
		String securityFilterPath = "/api/*";
		return Jsr250.createInjector(new EsShiroWebModule(servletContext,
				securityFilterPath), new ShiroAopModule(), new EsJerseyServletModule(
						securityFilterPath)/*, ShiroWebModule.guiceFilterModule()*/);
//		return Jsr250.createInjector(new EsConfigModule(servletContext,
//				securityFilterPath), new EsJerseyServletModule(
//						securityFilterPath)/*, ShiroWebModule.guiceFilterModule()*/);
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		servletContext = servletContextEvent.getServletContext();
		super.contextInitialized(servletContextEvent);
	}

}
