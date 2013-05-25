package com.github.richardwilly98.inject;

import com.github.richardwilly98.api.services.BootstrapService;
import com.github.richardwilly98.services.BootstrapProvider;
import com.google.inject.AbstractModule;

public class BootstrapModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(BootstrapService.class).to(BootstrapProvider.class)
				.asEagerSingleton();
	}

}
