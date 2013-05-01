package com.github.richardwilly98.inject;

import org.apache.shiro.guice.aop.ShiroAopModule;

import com.github.richardwilly98.shiro.EsShiroModule;
import com.google.inject.AbstractModule;

public class ProviderModule extends AbstractModule {

	@Override
	protected void configure() {

//		bind(Client.class).toProvider(ClientProvider.class).in(Scopes.SINGLETON);
//		bind(HashService.class).to(SHA512HashService.class).in(Scopes.SINGLETON);
//		
//		bind(SessionDAO.class).to(EsSessionDAO.class).asEagerSingleton();

		install(new EsClientModule());
		install(new ServicesModule());
		install(new ShiroAopModule());
//		install(new EsShiroModule());
	}

}
