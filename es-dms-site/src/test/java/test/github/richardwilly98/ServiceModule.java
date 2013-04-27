package test.github.richardwilly98;

import com.github.richardwilly98.service.HashService;
import com.github.richardwilly98.service.SHA512HashService;
import com.google.inject.AbstractModule;

public class ServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(HashService.class).to(SHA512HashService.class);
	}

}
