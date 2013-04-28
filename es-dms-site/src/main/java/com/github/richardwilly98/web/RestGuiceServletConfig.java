package com.github.richardwilly98.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import com.github.richardwilly98.rest.EsJerseyServletModule;
import com.github.richardwilly98.shiro.EsConfigModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class RestGuiceServletConfig extends GuiceServletContextListener {

	private ServletContext servletContext;

	@Override
	protected Injector getInjector() {

		String securityFilterPath = "/api/*";
		return Guice.createInjector(new EsJerseyServletModule(
				securityFilterPath), new EsConfigModule(servletContext,
						securityFilterPath));

	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		servletContext = servletContextEvent.getServletContext();
		super.contextInitialized(servletContextEvent);
	}

}
