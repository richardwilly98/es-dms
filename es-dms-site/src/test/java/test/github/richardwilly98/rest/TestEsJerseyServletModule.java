package test.github.richardwilly98.rest;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.apache.shiro.guice.web.GuiceShiroFilter;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import test.github.richardwilly98.inject.TestProviderModule;

import com.github.richardwilly98.rest.RestAuthencationService;
import com.github.richardwilly98.rest.RestDocumentService;
import com.github.richardwilly98.rest.RestRoleService;
import com.github.richardwilly98.rest.RestUserService;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class TestEsJerseyServletModule extends JerseyServletModule {

	private static final String JERSEY_API_CONTAINER_FILTER_POST_REPLACE_FILTER = "com.sun.jersey.api.container.filter.PostReplaceFilter";
	private static final String JERSEY_SPI_CONTAINER_CONTAINER_REQUEST_FILTERS = "com.sun.jersey.spi.container.ContainerRequestFilters";
	private static final String JERSEY_API_JSON_POJO_MAPPING_FEATURE = "com.sun.jersey.api.json.POJOMappingFeature";
	private final Map<String, String> params = new HashMap<String, String>();

	private final String securityFilterPath;
	
	public TestEsJerseyServletModule(String securityFilterPath) {
		this.securityFilterPath = securityFilterPath;

		params.put(JERSEY_API_JSON_POJO_MAPPING_FEATURE, "true");
		params.put(JERSEY_SPI_CONTAINER_CONTAINER_REQUEST_FILTERS,
				JERSEY_API_CONTAINER_FILTER_POST_REPLACE_FILTER);
	}
	
	@Override
	protected void configureServlets() {
		install();
		bindings();
		filters();
	}

	/*
	 * Install modules
	 */
	private void install() {
		/* bind services */
		install(new TestProviderModule());
	}

	private void filters() {
		 filter("/*").through(GuiceShiroFilter.class);
//		 filter("/api/*").through(GuiceContainer.class);
	}

	private void bindings() {
		/* bind the REST resources */
		bind(RestAuthencationService.class);
		bind(RestDocumentService.class);
		bind(RestUserService.class);
		bind(RestRoleService.class);

		/* bind jackson converters for JAXB/JSON serialization */
		bind(MessageBodyReader.class).to(JacksonJsonProvider.class);
		bind(MessageBodyWriter.class).to(JacksonJsonProvider.class);

		// Route all requests through GuiceContainer
//		serve(this.securityFilterPath).with(GuiceContainer.class, params);
		serve("/*").with(GuiceContainer.class, params);
	}

}
