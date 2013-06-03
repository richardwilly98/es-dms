package com.github.richardwilly98.esdms.inject;

import com.github.richardwilly98.esdms.services.BootstrapProvider;
import com.github.richardwilly98.esdms.services.BootstrapService;
import com.google.inject.AbstractModule;

public class BootstrapModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(BootstrapService.class).to(BootstrapProvider.class)
				.asEagerSingleton();
	}

}
