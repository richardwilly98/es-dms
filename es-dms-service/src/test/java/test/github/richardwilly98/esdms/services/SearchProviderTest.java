package test.github.richardwilly98.esdms.services;

/*
 * #%L
 * es-dms-service
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
import static com.google.common.collect.Sets.newHashSet;
import static org.elasticsearch.common.io.Streams.copyToBytesFromClasspath;

import java.util.Map;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.DocumentImpl;
import com.github.richardwilly98.esdms.FileImpl;
import com.github.richardwilly98.esdms.VersionImpl;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.Facet;
import com.github.richardwilly98.esdms.api.File;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.api.Term;
import com.github.richardwilly98.esdms.api.Version;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SearchProviderTest extends ProviderTestBase {

	int tagsCount = 0;

	private String createDocument(String name, String contentType, String path)
			throws Throwable {
		String id = String.valueOf(System.currentTimeMillis());
		byte[] content = copyToBytesFromClasspath(path);
		File file = new FileImpl.Builder().content(content).name(name)
				.contentType(contentType).build();
		Set<Version> versions = newHashSet();
		versions.add(new VersionImpl.Builder().documentId(id).file(file)
				.current(true).versionId(1).build());

		Document document = new DocumentImpl.Builder().versions(versions)
				.id(id).name(name).roles(null).build();
		Document newDocument = documentService.create(document);
		Assert.assertNotNull(newDocument);
		log.info(String.format("New document created #%s", newDocument.getId()));
		return id;
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
		SearchResult<Document> searchResult = searchService.search(criteria,
				first, pageSize, facet, filters);
		Assert.assertNotNull(searchResult);
		return searchResult;
	}

	private void addTag(String id, String... tags) throws Throwable {
		Document document = documentService.getMetadata(id);
		for (String tag : tags) {
			document.addTag(tag);
			documentService.update(document);
			document = documentService.getMetadata(id);
			Assert.assertTrue(document.getTags().contains(tag));
			tagsCount++;
		}
	}

	@Test()
	public void testSearchDocument() throws Throwable {

		log.info("Start testSearchDocument");
		loginAdminUser();
		int max = 15;
		String name = "test-tagging-document";

		String id = createDocument(name, "text/plain",
				"/test/github/richardwilly98/services/test-attachment.txt");
		addTag(id, "tag1", "tag2");

		id = createDocument(name, "text/plain",
				"/test/github/richardwilly98/services/test-attachment.txt");
		addTag(id, "tag1", "tag2", "tag3");

		id = createDocument(name, "text/plain",
				"/test/github/richardwilly98/services/test-attachment.txt");
		addTag(id, "tag2", "tag3");

		id = createDocument(name, "text/plain",
				"/test/github/richardwilly98/services/test-attachment.txt");
		addTag(id, "tag3", "tag4");

		id = createDocument(name, "text/plain",
				"/test/github/richardwilly98/services/test-attachment.txt");

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

}
