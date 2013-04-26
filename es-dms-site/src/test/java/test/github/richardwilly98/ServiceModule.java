package test.github.richardwilly98;

import com.github.richardwilly98.rest.HashService;
import com.github.richardwilly98.rest.Md5HashService;
import com.google.inject.AbstractModule;

public class ServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(HashService.class).to(Md5HashService.class);
	}

}
