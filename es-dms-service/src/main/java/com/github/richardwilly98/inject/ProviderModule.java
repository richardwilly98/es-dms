package com.github.richardwilly98.inject;

import org.elasticsearch.client.Client;

import com.github.richardwilly98.api.services.HashService;
import com.github.richardwilly98.services.SHA512HashService;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class ProviderModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Client.class).toProvider(ClientProvider.class).in(Scopes.SINGLETON);
		bind(HashService.class).to(SHA512HashService.class).in(Scopes.SINGLETON);
	}

}
