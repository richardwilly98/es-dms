package com.github.richardwilly98.esdms.api;

/*
 * #%L
 * es-dms-core
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

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.esdms.DocumentImpl;
import com.github.richardwilly98.esdms.FileImpl;
import com.github.richardwilly98.esdms.VersionImpl;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.Version;
import com.github.richardwilly98.esdms.search.FacetRequestImpl;
import com.github.richardwilly98.esdms.search.SearchResultImpl;
import com.github.richardwilly98.esdms.search.TermRequestImpl;
import com.github.richardwilly98.esdms.search.api.FacetRequest;
import com.github.richardwilly98.esdms.search.api.TermRequest;
import com.google.common.collect.ImmutableSet;

public class SearchTest {

    private static Logger log = Logger.getLogger(SearchTest.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private Set<Document> getTestDocuments(int number) {

        LinkedHashSet<Document> docs = new LinkedHashSet<Document>();

        for (int i = 0; i < number; i++) {
            String attributeKey = "attribute: " + i;
            String attributeValue = "value: " + i;
            String id = "id-" + i + " @time: " + System.currentTimeMillis();
            String name = "name-" + i + " @time: " + System.currentTimeMillis();
            String html = "<html><body><h1>This is document number: " + i + "</h1></body></html>";
            byte[] content = html.getBytes();
            Map<String, Object> attributes = newHashMap();
            attributes.put(attributeKey, attributeValue);
            // DocumentTest document = new DocumentTest(new DocumentImpl(id,
            // name, new FileImpl(content, "test.html", "text/html"),
            // attributes));
            Set<Version> versions = newHashSet();
            versions.add(new VersionImpl.Builder().documentId(id)
                    .file(new FileImpl.Builder().content(content).name("test" + i + ".html").contentType("text/html").build())
                    .current(true).versionId(1).build());
            DocumentTest document = new DocumentTest(new DocumentImpl.Builder().versions(versions).id(id).name(name).attributes(attributes)
                    .roles(null));
            // DocumentTest document = new DocumentTest(new
            // DocumentImpl.Builder().file(new
            // FileImpl.Builder().content(content).name("test" + i +
            // ".html").contentType("text/html").build()).id(id).name(name).attributes(attributes).roles(null));
            docs.add(document);
        }
        return docs;
    }

    @Test
    public void testSearchResultAPI() throws Throwable {
        int totalHits = 55;
        int pageSize = 10;
        long elapsedTime = 550;
        int firstIndex = 0;
        Set<Document> documents = getTestDocuments(10);
        SearchResultImpl.Builder<Document> builder = new SearchResultImpl.Builder<Document>().items(documents).elapsedTime(elapsedTime)
                .firstIndex(firstIndex).totalHits(totalHits).pageSize(pageSize);
        SearchResultImpl<Document> searchResult = builder.build();
        Assert.assertEquals(searchResult.getPageSize(), pageSize);
        Assert.assertEquals(searchResult.getElapsedTime(), elapsedTime);
        Assert.assertEquals(searchResult.getFirstIndex(), firstIndex);
        Assert.assertEquals(searchResult.getItems(), documents);

        String json = mapper.writeValueAsString(searchResult);
        log.debug(json);
        Assert.assertNotNull(json);
        SearchResultImpl<Document> searchResult2 = mapper.readValue(json, new TypeReference<SearchResultImpl<Document>>() {
        });
        Assert.assertEquals(searchResult2.getPageSize(), pageSize);
        Assert.assertEquals(searchResult2.getElapsedTime(), elapsedTime);
        Assert.assertEquals(searchResult2.getFirstIndex(), firstIndex);
        Assert.assertEquals(searchResult2.getItems().size(), documents.size());
    }

    @Test
    public void testFacetRequest() throws Throwable {
        TermRequest termRequest = new TermRequestImpl.Builder().fieldName("field1").size(20).build();
        Assert.assertNotNull(termRequest);
        FacetRequest facetRequest = new FacetRequestImpl.Builder().name("facet-1").terms(ImmutableSet.of(termRequest)).build();
        Assert.assertNotNull(facetRequest);
        String json = mapper.writeValueAsString(facetRequest);
        log.debug(json);
        Assert.assertNotNull(json);
        FacetRequest facetRequest2 = mapper.readValue(json, FacetRequest.class);
        Assert.assertEquals(facetRequest, facetRequest2);
        Assert.assertEquals(termRequest, facetRequest2.getTerms().iterator().next());
    }

}
