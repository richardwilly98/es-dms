package test.github.richardwilly98.inject;

import org.apache.shiro.guice.aop.ShiroAopModule;
import org.elasticsearch.client.Client;

import com.github.richardwilly98.inject.ServicesModule;
import com.github.richardwilly98.shiro.EsShiroModule;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class ProviderModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(Client.class).toProvider(LocalClientProvider.class).in(
				Scopes.SINGLETON);

		install(new ServicesModule());
		install(new ShiroAopModule());
		install(new EsShiroModule());
	}

}
