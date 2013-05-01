package com.github.richardwilly98.shiro;

import javax.servlet.ServletContext;

import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

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
//		bind(SessionDAO.class).to(EsSessionDAO.class);
		bind(SessionDAO.class).to(EsSessionDAO.class);
		bind(EsSessionDAO.class);
//		bind(SecurityManager.class).to(DefaultSecurityManager.class);
//		bind(DefaultSecurityManager.class);
		
		addFilterChain("/api/auth/*", config(SSL, "8443"));
		addFilterChain(this.securityFilterPath, AUTHC);
	}
	
	@Override
	protected void bindSessionManager(
			AnnotatedBindingBuilder<SessionManager> bind) {
//		bind.to(EsSessionManager.class).in(Scopes.SINGLETON);
//		bind.to(EsWebSessionManager.class).in(Scopes.SINGLETON);
		bind.to(DefaultWebSessionManager.class);
		bindConstant().annotatedWith(Names.named("shiro.globalSessionTimeout")).to(30000L);
	}
	
//    @Override
//    protected void bindWebSecurityManager(AnnotatedBindingBuilder<? super WebSecurityManager> bind) {
//        bind.to(DefaultWebSecurityManager.class);
//    }
	
	
}
