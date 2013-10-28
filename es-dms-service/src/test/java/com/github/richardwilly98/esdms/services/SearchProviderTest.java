package com.github.richardwilly98.esdms.services;

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

import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.search.FacetRequestImpl;
import com.github.richardwilly98.esdms.search.TermRequestImpl;
import com.github.richardwilly98.esdms.search.api.Facet;
import com.github.richardwilly98.esdms.search.api.FacetRequest;
import com.github.richardwilly98.esdms.search.api.SearchResult;
import com.github.richardwilly98.esdms.search.api.Term;
import com.github.richardwilly98.esdms.search.api.TermRequest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class SearchProviderTest extends ProviderTestBase {

    int tagsCount = 0;

    private SearchResult<Document> searchDocument(String criteria, int first, int pageSize) throws Throwable {
	return searchDocument(criteria, first, pageSize, null);
    }

    private SearchResult<Document> searchDocument(String criteria, int first, int pageSize, List<FacetRequest> facets) throws Throwable {
	return searchDocument(criteria, first, pageSize, facets, null);
    }

    private SearchResult<Document> searchDocument(String criteria, int first, int pageSize, List<FacetRequest> facets, Map<String, Object> filters)
	    throws Throwable {
	SearchResult<Document> searchResult = searchService.search(criteria, first, pageSize, facets, filters);
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
	List <String> ids = newArrayList();
	tagsCount = 0;
	int max = 15;
	String name = "test-tagging-document";

	String id = createDocument(name, "text/plain", "/test/github/richardwilly98/services/test-attachment.txt");
	ids.add(id);
	addTag(id, "tag1", "tag2");

	id = createDocument(name, "text/plain", "/test/github/richardwilly98/services/test-attachment.txt");
	ids.add(id);
	addTag(id, "tag1", "tag2", "tag3");

	id = createDocument(name, "text/plain", "/test/github/richardwilly98/services/test-attachment.txt");
	ids.add(id);
	addTag(id, "tag2", "tag3");

	id = createDocument(name, "text/plain", "/test/github/richardwilly98/services/test-attachment.txt");
	ids.add(id);
	addTag(id, "tag3", "tag4");

	id = createDocument(name, "text/plain", "/test/github/richardwilly98/services/test-attachment.txt");
	ids.add(id);

	SearchResult<Document> result = searchDocument(name, 0, max);
	log.debug(String.format("Search - total hits: %s - item count: %s", result.getTotalHits(), result.getItems().size()));
	Assert.assertTrue(result.getTotalHits() >= 0);
	for (Document item : result.getItems()) {
	    Assert.assertNotNull(item);
	    Assert.assertNull(item.getCurrentVersion());
	}

	TermRequest termRequest = new TermRequestImpl.Builder().fieldName("tags").size(10).build();
	FacetRequest facetRequest = new FacetRequestImpl.Builder().name("tags").terms(ImmutableSet.of(termRequest)).build();
	List<FacetRequest> facetsRequest = newArrayList(facetRequest);
	result = searchDocument(name, 0, max, facetsRequest);
	log.debug(String.format("Search with facet - total hits: %s - item count: %s", result.getTotalHits(), result.getItems().size()));
	Assert.assertTrue(result.getTotalHits() >= 0 && result.getItems().size() <= max);
	for (Document item : result.getItems()) {
	    Assert.assertNotNull(item);
	    Assert.assertNull(item.getCurrentVersion());
	}
	Facet facet = result.getFacets().get("tags");
	Assert.assertNotNull(facet);

	log.debug(facet);
	// Danilo not sure about this test tagsCount is incremental
	// Total number of tags
	Assert.assertEquals(facet.getTotalCount(), tagsCount);
	for (Term term : facet.getTerms()) {
	    log.debug(term);
	}

	result = searchDocument(name, 0, max, facetsRequest, newHashMap(ImmutableMap.of("tags", (Object) "tag1")));
	log.debug(String.format("Search with facet and filters - total hits: %s - item count: %s", result.getTotalHits(), result.getItems()
	        .size()));
	Assert.assertEquals(result.getTotalHits(), 2);

	result = searchDocument(name, 0, max, facetsRequest, newHashMap(ImmutableMap.of("tags", (Object) "tag3")));
	log.debug(String.format("Search with facet and filters - total hits: %s - item count: %s", result.getTotalHits(), result.getItems()
	        .size()));
	Assert.assertEquals(result.getTotalHits(), 3);

	result = searchDocument(name, 0, max, facetsRequest,
	        newHashMap(ImmutableMap.of("tags", (Object) newArrayList(ImmutableList.of("tag1", "tag4")))));
	log.debug(String.format("Search with facet and filters - total hits: %s - item count: %s", result.getTotalHits(), result.getItems()
	        .size()));
	for (Document item : result.getItems()) {
	    Assert.assertNotNull(item);
	}
	
	for(String _id  : ids) {
	    deleteDocument(_id);
	}
    }

    @Test()
    public void testFacetedSearchDocument() throws Throwable {

        log.info("Start testFacetedSearchDocument");
        loginAdminUser();
        List <String> ids = newArrayList();
        tagsCount = 0;
        int max = 15;
        String name = "test-tagging-document";
        String tag1= "tag1_" + System.currentTimeMillis();
        String tag2= "tag2_" + System.currentTimeMillis();
        String tag3= "tag3_" + System.currentTimeMillis();
        String tag4= "tag4_" + System.currentTimeMillis();

        String id = createDocument(name, "text/plain", "/test/github/richardwilly98/services/test-attachment.txt");
        ids.add(id);
        addTag(id, tag1, tag2);

        id = createDocument(name, "text/plain", "/test/github/richardwilly98/services/test-attachment.txt");
        ids.add(id);
        addTag(id, tag1, tag2, tag3);

        id = createDocument(name, "text/plain", "/test/github/richardwilly98/services/test-attachment.txt");
        ids.add(id);
        addTag(id, tag2, tag3);

        id = createDocument(name, "text/plain", "/test/github/richardwilly98/services/test-attachment.txt");
        ids.add(id);
        addTag(id, tag3, tag4);

        id = createDocument(name, "text/plain", "/test/github/richardwilly98/services/test-attachment.txt");
        ids.add(id);

        SearchResult<Document> result = searchDocument(name, 0, max);
        log.debug(String.format("Search - total hits: %s - item count: %s", result.getTotalHits(), result.getItems().size()));
        Assert.assertTrue(result.getTotalHits() >= 0);
        for (Document item : result.getItems()) {
            Assert.assertNotNull(item);
            Assert.assertNull(item.getCurrentVersion());
        }

        TermRequest termRequest = new TermRequestImpl.Builder().fieldName("tags").size(10).build();
        FacetRequest facetRequest = new FacetRequestImpl.Builder().name("tags").terms(ImmutableSet.of(termRequest)).build();
        List<FacetRequest> facetsRequest = newArrayList();
        facetsRequest.add(facetRequest);
        
        termRequest = new TermRequestImpl.Builder().fieldName("attributes.author").size(10).build();
        facetRequest = new FacetRequestImpl.Builder().name("Author").terms(ImmutableSet.of(termRequest)).build();
        facetsRequest.add(facetRequest);
        
        result = searchDocument(name, 0, max, facetsRequest);
        log.debug(String.format("Search with facet - total hits: %s - item count: %s", result.getTotalHits(), result.getItems().size()));
        Assert.assertTrue(result.getTotalHits() >= 0 && result.getItems().size() <= max);
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

        facet = result.getFacets().get("Author");
        Assert.assertNotNull(facet);

        result = searchDocument(name, 0, max, facetsRequest, newHashMap(ImmutableMap.of("tags", (Object) tag1)));
        log.debug(String.format("Search with facet and filters - total hits: %s - item count: %s", result.getTotalHits(), result.getItems()
                .size()));
        Assert.assertEquals(result.getTotalHits(), 2);

        result = searchDocument(name, 0, max, facetsRequest, newHashMap(ImmutableMap.of("tags", (Object) tag3)));
        log.debug(String.format("Search with facet and filters - total hits: %s - item count: %s", result.getTotalHits(), result.getItems()
                .size()));
        Assert.assertEquals(result.getTotalHits(), 3);

        result = searchDocument(name, 0, max, facetsRequest,
                newHashMap(ImmutableMap.of("tags", (Object) newArrayList(ImmutableList.of(tag1, tag4)))));
        log.debug(String.format("Search with facet and filters - total hits: %s - item count: %s", result.getTotalHits(), result.getItems()
                .size()));
        for (Document item : result.getItems()) {
            Assert.assertNotNull(item);
            log.debug(item);
        }

        for(String _id  : ids) {
            deleteDocument(_id);
        }
    }

    @Test
    public void testSuggestTags() throws Throwable {

        log.info("Start testSuggestTags");
        loginAdminUser();
        List <String> ids = newArrayList();
        tagsCount = 0;
        int max = 15;
        String name = "test-suggest-tag-document";
        String tag1= "java";
        String tag2= "javascript";
        String tag3= "c#";
        String tag4= "csharp";

        String id = createDocument(name, "text/plain", "/test/github/richardwilly98/services/test-attachment.txt");
        ids.add(id);
        addTag(id, tag1, tag2);

        id = createDocument(name, "text/plain", "/test/github/richardwilly98/services/test-attachment.txt");
        ids.add(id);
        addTag(id, tag1, tag2, tag3);

        id = createDocument(name, "text/plain", "/test/github/richardwilly98/services/test-attachment.txt");
        ids.add(id);
        addTag(id, tag2, tag3);

        id = createDocument(name, "text/plain", "/test/github/richardwilly98/services/test-attachment.txt");
        ids.add(id);
        addTag(id, tag3, tag4);

        id = createDocument(name, "text/plain", "/test/github/richardwilly98/services/test-attachment.txt");
        ids.add(id);

        SearchResult<Document> result = searchDocument(name, 0, max);
        log.debug(String.format("Search - total hits: %s - item count: %s", result.getTotalHits(), result.getItems().size()));
        Assert.assertTrue(result.getTotalHits() >= 0);
        for (Document item : result.getItems()) {
            Assert.assertNotNull(item);
        }

        Facet facet = searchService.suggestTags("ja", 10);
        Assert.assertNotNull(facet);
        Assert.assertEquals(facet.getName(), "Tags");
        Assert.assertNotNull(facet.getTerms());
        Assert.assertEquals(facet.getTerms().size(), 2);
        for (Term term : facet.getTerms()) {
            Assert.assertTrue(term.getTerm().startsWith("ja"));
        }
        
        facet = searchService.suggestTags("jo", 10);
        Assert.assertNotNull(facet);
        Assert.assertNotNull(facet.getTerms());
        Assert.assertEquals(facet.getTerms().size(), 0);
        
        facet = searchService.suggestTags("cs", 10);
        Assert.assertNotNull(facet);
        Assert.assertNotNull(facet.getTerms());
        Assert.assertEquals(facet.getTerms().size(), 1);
        for (Term term : facet.getTerms()) {
            Assert.assertTrue(term.getTerm().startsWith("cs"));
        }

        for(String _id  : ids) {
            deleteDocument(_id);
        }

    }
}
