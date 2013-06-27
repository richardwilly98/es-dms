package com.github.richardwilly98.esdms.rest;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.rest.exception.RestServiceException;
import com.github.richardwilly98.esdms.services.AuthenticationService;
import com.github.richardwilly98.esdms.services.SearchService;
import com.google.inject.Inject;

@Path(RestSearchService.SEARCH_PATH)
public class RestSearchService extends RestServiceBase {

	public static final String SEARCH_PATH = "search";
	public static final String SEARCH_FIRST_PARAMETER = "fi";
	public static final String SEARCH_PAGE_SIZE_PARAMETER = "ps";
	public static final String SEARCH_FACET_PARAMETER = "fa";

	protected final Logger log = Logger.getLogger(getClass());
	protected final SearchService<Document> service;

	@Context
	UriInfo url;

	@Inject
	public RestSearchService(final AuthenticationService authenticationService,
			final SearchService<Document> searchService) {
		super(authenticationService);
		this.service = searchService;
	}

//	protected URI getItemUri(T item) {
//		checkNotNull(item);
//		return url.getBaseUriBuilder().path(getClass()).path(item.getId())
//				.build();
//	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path(SEARCH_PATH + "/{criteria}")
	public Response search(
			@PathParam("criteria") String criteria,
			@QueryParam(SEARCH_FIRST_PARAMETER) @DefaultValue("0") int first,
			@QueryParam(SEARCH_PAGE_SIZE_PARAMETER) @DefaultValue("20") int pageSize,
			@QueryParam(SEARCH_FACET_PARAMETER) String facet) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("search - %s", criteria));
		}
		try {
			SearchResult<Document> items = service.search(criteria, first, pageSize);
			return Response.ok(items).build();
		} catch (ServiceException e) {
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}
}
