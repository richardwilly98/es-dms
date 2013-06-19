package com.github.richardwilly98.esdms.shiro;

import javax.servlet.ServletContext;

import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;

import com.github.richardwilly98.esdms.shiro.EsRealm;
import com.github.richardwilly98.esdms.shiro.EsSessionDAO;
import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.name.Names;

public class EsShiroWebModule extends ShiroWebModule {

	private final String securityFilterPath;
	
	public EsShiroWebModule(ServletContext servletContext, String securityFilterPath) {
		super(servletContext);
		this.securityFilterPath = securityFilterPath;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void configureShiroWeb() {
		
		bindRealm().to(EsRealm.class).asEagerSingleton();
		bind(SessionDAO.class).to(EsSessionDAO.class);
		bind(EsSessionDAO.class);
		
		addFilterChain("/api/auth/**", config(SSL, "8443"));
		addFilterChain("/api/**", Key.get(EsAuthenticationFilter.class));
	}
	
	@Override
	protected void bindSessionManager(
			AnnotatedBindingBuilder<SessionManager> bind) {
		bind.to(EsWebSessionManager.class).in(Scopes.SINGLETON);
		bindConstant().annotatedWith(Names.named("shiro.globalSessionTimeout")).to(30000L);
		bind(EsWebSessionManager.class);
		
	}
	
}
