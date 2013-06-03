package com.github.richardwilly98.esdms.shiro;

import org.apache.shiro.guice.ShiroModule;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;

import com.google.inject.Scopes;
import com.google.inject.binder.AnnotatedBindingBuilder;

public class EsShiroModule extends ShiroModule {

	@Override
	protected void configureShiro() {
		bindRealm().to(EsRealm.class).asEagerSingleton();
		bind(SessionDAO.class).to(EsSessionDAO.class);
		bind(EsSessionDAO.class);
	}

	@Override
	protected void bindSessionManager(
			AnnotatedBindingBuilder<SessionManager> bind) {
		bind.to(EsSessionManager.class).in(Scopes.SINGLETON);
	}

}
