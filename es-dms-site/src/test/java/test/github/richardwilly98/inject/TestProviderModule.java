package test.github.richardwilly98.inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.guice.aop.ShiroAopModule;

import com.github.richardwilly98.inject.ServicesModule;
import com.github.richardwilly98.shiro.EsShiroModule;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;

public class TestProviderModule extends AbstractModule {

	@Override
	protected void configure() {

	    install(new EsLocalClientModule());
		install(new ServicesModule());
		install(new ShiroAopModule());
//		install(new EsShiroModule());

		Injector injector = com.google.inject.Guice.createInjector(new EsLocalClientModule(), new ServicesModule(), new EsShiroModule());
	    org.apache.shiro.mgt.SecurityManager securityManager = injector.getInstance(org.apache.shiro.mgt.SecurityManager.class);
	    SecurityUtils.setSecurityManager(securityManager);
	}

}
