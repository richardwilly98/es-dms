package test.github.richardwilly98.esdms.inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.guice.aop.ShiroAopModule;

import test.github.richardwilly98.esdms.inject.TestEsClientModule;

import com.github.richardwilly98.esdms.inject.BootstrapModule;
import com.github.richardwilly98.esdms.inject.ServicesModule;
import com.github.richardwilly98.esdms.shiro.EsShiroModule;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.mycila.inject.jsr250.Jsr250;

public class TestProviderModule extends AbstractModule {

	@Override
	protected void configure() {

		install(new BootstrapModule());
		install(new TestEsClientModule());
		install(new ServicesModule());
		install(new ShiroAopModule());
//		install(new EsShiroModule());

		Injector injector = Jsr250.createInjector(new BootstrapModule(), new TestEsClientModule(), new ServicesModule(), new EsShiroModule());
	    org.apache.shiro.mgt.SecurityManager securityManager = injector.getInstance(org.apache.shiro.mgt.SecurityManager.class);
	    SecurityUtils.setSecurityManager(securityManager);
	}

}
