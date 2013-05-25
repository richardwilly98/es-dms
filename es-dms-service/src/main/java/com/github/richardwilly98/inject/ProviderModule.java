package com.github.richardwilly98.inject;

import org.apache.shiro.guice.aop.ShiroAopModule;

import com.google.inject.AbstractModule;

public class ProviderModule extends AbstractModule {

	@Override
	protected void configure() {

		install(new BootstrapModule());
		install(new EsClientModule());
		install(new ServicesModule());
		install(new ShiroAopModule());

	}

}
