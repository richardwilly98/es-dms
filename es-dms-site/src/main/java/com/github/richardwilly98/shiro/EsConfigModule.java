package com.github.richardwilly98.shiro;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.shiro.guice.aop.ShiroAopModule;

import com.google.inject.AbstractModule;

public class EsConfigModule extends AbstractModule {

	private final ServletContext servletContext;
	private final String securityFilterPath;
	private final Logger log = Logger.getLogger(getClass());

	public EsConfigModule(ServletContext servletContext,
			String securityFilterPath) {
		this.servletContext = servletContext;
		this.securityFilterPath = securityFilterPath;
	}

	@Override
	protected void configure() {
		log.debug("*** configure ***");
		install(new EsShiroWebModule(servletContext, securityFilterPath));
		install(new ShiroAopModule());
	}

}
