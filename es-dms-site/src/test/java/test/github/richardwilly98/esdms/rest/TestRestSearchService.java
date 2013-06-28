package test.github.richardwilly98.esdms.rest;

/*
 * #%L
 * es-dms-site
 * %%
 * Copyright (C) 2013 es-dms
 * %%
 * Copyright 2012-2013 Richard Louapre
 * 
 * This file is part of ES-DMS.
 * 
 * The current version of ES-DMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * ES-DMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.elasticsearch.common.io.Streams.copyToBytesFromClasspath;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.Facet;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.api.Term;
import com.github.richardwilly98.esdms.rest.RestDocumentService;
import com.github.richardwilly98.esdms.rest.RestSearchService;
import com.github.richardwilly98.esdms.rest.entity.FacetedQuery;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

public class TestRestSearchService extends GuiceAndJettyTestBase<Document> {
	// public class TestRestDocumentService extends
	// GuiceAndJerseyTestBase<Document> {

	int tagsCount = 0;

	public TestRestSearchService() throws Exception {
		super();
	}

	@Test()
	public void testSearchDocument() throws Throwable {

		log.info("Start testSearchDocument");
		int max = 15;
		String name = "testtaggingdocument" + System.currentTimeMillis();
		String contentType = "text/plain";
		String file = "/test/github/richardwilly98/services/test-attachment.html";

		Document document = createDocument(name, contentType, file);
		addTag(document.getId(), "tag1", "tag2");
		log.trace(String.format("Document created - %s", getMetadata(document.getId())));

		document = createDocument(name, contentType, file);
		addTag(document.getId(), "tag1", "tag2", "tag3");
		log.trace(String.format("Document created - %s", getMetadata(document.getId())));

		document = createDocument(name, contentType, file);
		addTag(document.getId(), "tag2", "tag3");
		log.trace(String.format("Document created - %s", getMetadata(document.getId())));

		document = createDocument(name, contentType, file);
		addTag(document.getId(), "tag3", "tag4");
		log.trace(String.format("Document created - %s", getMetadata(document.getId())));

		document = createDocument(name, contentType, file);
		log.trace(String.format("Document created - %s", getMetadata(document.getId())));

		SearchResult<Document> result = searchDocument(name, 0, max);
		log.debug(String.format("Search - total hits: %s - item count: %s",
				result.getTotalHits(), result.getItems().size()));
		Assert.assertTrue(result.getTotalHits() >= 0);
		for (Document item : result.getItems()) {
			Assert.assertNotNull(item);
			Assert.assertNull(item.getCurrentVersion());
		}

		result = searchDocument(name, 0, max, "tags");
		log.debug(String.format(
				"Search with facet - total hits: %s - item count: %s",
				result.getTotalHits(), result.getItems().size()));
		Assert.assertTrue(result.getTotalHits() >= 0
				&& result.getItems().size() <= max);
		for (Document item : result.getItems()) {
			Assert.assertNotNull(item);
			Assert.assertNull(item.getCurrentVersion());
		}
		Facet facet = result.getFacets().get("tags");
		Assert.assertNotNull(facet);

		log.debug(facet);
		// Total number of tags
		Assert.assertEquals(facet.getTotalCount(), tagsCount);
		for (Term term : facet.getTerms()) {
			log.debug(term);
		}

		result = searchDocument(name, 0, max, "tags",
				newHashMap(ImmutableMap.of("tags", (Object) "tag1")));
		log.debug(String
				.format("Search with facet and filters - total hits: %s - item count: %s",
						result.getTotalHits(), result.getItems().size()));
		Assert.assertEquals(result.getTotalHits(), 2);

		result = searchDocument(name, 0, max, "tags",
				newHashMap(ImmutableMap.of("tags", (Object) "tag3")));
		log.debug(String
				.format("Search with facet and filters - total hits: %s - item count: %s",
						result.getTotalHits(), result.getItems().size()));
		Assert.assertEquals(result.getTotalHits(), 3);

		result = searchDocument(name, 0, max, "tags",
				newHashMap(ImmutableMap
						.of("tags", (Object) newArrayList(ImmutableList.of(
								"tag1", "tag4")))));
		log.debug(String
				.format("Search with facet and filters - total hits: %s - item count: %s",
						result.getTotalHits(), result.getItems().size()));
		for (Document item : result.getItems()) {
			Assert.assertNotNull(item);
			log.debug(item);
		}
	}

	private SearchResult<Document> searchDocument(String criteria, int first,
			int pageSize) throws Throwable {
		return searchDocument(criteria, first, pageSize, null);
	}

	private SearchResult<Document> searchDocument(String criteria, int first,
			int pageSize, String facet) throws Throwable {
		return searchDocument(criteria, first, pageSize, facet, null);
	}

	private SearchResult<Document> searchDocument(String criteria, int first,
			int pageSize, String facet, Map<String, Object> filters)
			throws Throwable {
		// SearchResult<Document> searchResult = searchService.search(criteria,
		// first, pageSize, facet, filters);
		// Assert.assertNotNull(searchResult);
		FacetedQuery query = new FacetedQuery();
		query.setFacet(facet);
		query.setFilters(filters);
		ClientResponse response = resource()
				.path(RestSearchService.SEARCH_PATH)
				.path(RestSearchService.FACET_SEARCH_ACTION).path(criteria)
				.cookie(adminCookie).type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, query);
		log.debug(String.format("status: %s", response.getStatus()));
		Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
		SearchResult<Document> searchResult = response
				.getEntity(new GenericType<SearchResult<Document>>() {
				});
		Assert.assertNotNull(searchResult);
		return searchResult;
	}

	private void addTag(String id, String... tags) throws Throwable {
		Document document = getMetadata(id);
		for (String tag : tags) {
			document.addTag(tag);
			updateDocument(document);
			document = getMetadata(id);
			Assert.assertTrue(document.getTags().contains(tag));
			tagsCount++;
		}
	}

	private Document createDocument(String name, String contentType, String path)
			throws Throwable {
		byte[] content = copyToBytesFromClasspath(path);
		InputStream is = new ByteArrayInputStream(content);

		FormDataMultiPart form = new FormDataMultiPart();
		form.field("name", name);
		FormDataBodyPart p = new FormDataBodyPart("file", is,
				MediaType.valueOf(contentType));
		form.bodyPart(p);

		ClientResponse response = resource()
				.path(RestDocumentService.DOCUMENTS_PATH)
				.path(RestDocumentService.UPLOAD_PATH).cookie(adminCookie)
				.type(MediaType.MULTIPART_FORM_DATA)
				.post(ClientResponse.class, form);
		log.debug(String.format("status: %s", response.getStatus()));
		Assert.assertTrue(response.getStatus() == Status.CREATED
				.getStatusCode());
		URI uri = response.getLocation();
		Assert.assertNotNull(uri);
		return get(uri, Document.class);
	}

	private void updateDocument(Document document) throws Throwable {
		ClientResponse response = resource()
				.path(RestDocumentService.DOCUMENTS_PATH)
				.path(RestDocumentService.UPDATE_PATH).cookie(adminCookie)
				.type(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, document);
		log.debug(String.format("status: %s", response.getStatus()));
		Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
	}

	private Document getDocument(String id) throws Throwable {
		Document document = get(id, Document.class,
				RestDocumentService.DOCUMENTS_PATH);
		Assert.assertNotNull(document);
		return document;
	}

	private Document getMetadata(String id) throws Throwable {
		ClientResponse response = resource()
				.path(RestDocumentService.DOCUMENTS_PATH).path(id)
				.path(RestDocumentService.METADATA_PATH).cookie(adminCookie)
				.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		log.debug(String.format("status: %s", response.getStatus()));
		Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
		Document document = response.getEntity(Document.class);
		Assert.assertNotNull(document);
		return document;
	}

}
