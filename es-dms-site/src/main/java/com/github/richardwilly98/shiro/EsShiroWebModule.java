package com.github.richardwilly98.shiro;

import javax.servlet.ServletContext;

import org.apache.shiro.guice.web.ShiroWebModule;

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
		bindConstant().annotatedWith(Names.named("shiro.globalSessionTimeout")).to(30000L);
		
		bindRealm().to(EsRealm.class).asEagerSingleton();
		
		addFilterChain("/api/auth/*", config(SSL, "8443"));
		addFilterChain(this.securityFilterPath, AUTHC);
	}
}
