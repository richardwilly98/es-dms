package com.github.richardwilly98.shiro;

import javax.servlet.ServletContext;

import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;

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
		
		bindConstant().annotatedWith(Names.named("shiro.loginUrl")).to("/#/login");
		
		bindRealm().to(EsRealm.class).asEagerSingleton();
		bind(SessionDAO.class).to(EsSessionDAO.class);
		bind(EsSessionDAO.class);
		
		addFilterChain("/api/auth/*", config(SSL, "8443"));
//		addFilterChain(this.securityFilterPath, AUTHC);
		addFilterChain(this.securityFilterPath, Key.get(EsAuthenticationFilter.class));
	}
	
	@Override
	protected void bindSessionManager(
			AnnotatedBindingBuilder<SessionManager> bind) {

//		bind.to(DefaultWebSessionManager.class);
//		bindConstant().annotatedWith(Names.named("shiro.globalSessionTimeout")).to(30000L);
//		bind(DefaultWebSessionManager.class);
//		bind(Cookie.class).toInstance(new SimpleCookie("ES_DMS_TICKET"));
		
		bind.to(EsWebSessionManager.class).in(Scopes.SINGLETON);
		bindConstant().annotatedWith(Names.named("shiro.globalSessionTimeout")).to(30000L);
		bind(EsWebSessionManager.class);
		
	}
	
}
