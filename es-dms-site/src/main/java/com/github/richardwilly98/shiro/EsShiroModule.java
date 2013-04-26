package com.github.richardwilly98.shiro;

import javax.servlet.ServletContext;

import org.apache.shiro.guice.web.ShiroWebModule;

import com.github.richardwilly98.shiro.realm.EsRealm;
import com.google.inject.name.Names;

public class EsShiroModule extends ShiroWebModule {

	private final String securityFilterPath;
	
	public EsShiroModule(ServletContext servletContext, String securityFilterPath) {
		super(servletContext);
		this.securityFilterPath = securityFilterPath;
	}

	@Override
	protected void configureShiroWeb() {

		bindConstant().annotatedWith(Names.named("shiro.globalSessionTimeout")).to(30000L);
		
		bindRealm().to(EsRealm.class).asEagerSingleton();
		addFilterChain(this.securityFilterPath, AUTHC);
	}
}
