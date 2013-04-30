package com.github.richardwilly98.inject;

import org.apache.shiro.guice.aop.ShiroAopModule;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.elasticsearch.client.Client;

import com.github.richardwilly98.api.services.HashService;
import com.github.richardwilly98.services.SHA512HashService;
import com.github.richardwilly98.shiro.EsSessionDAO;
import com.github.richardwilly98.shiro.EsShiroModule;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class ProviderModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(Client.class).toProvider(ClientProvider.class).in(Scopes.SINGLETON);
		bind(HashService.class).to(SHA512HashService.class).in(Scopes.SINGLETON);
		
		bind(SessionDAO.class).to(EsSessionDAO.class).asEagerSingleton();
		
		install(new ShiroAopModule());
		install(new EsShiroModule());
	}

}
