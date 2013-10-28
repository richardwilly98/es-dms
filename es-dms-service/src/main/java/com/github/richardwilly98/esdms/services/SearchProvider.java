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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.base.Stopwatch;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.esdms.UserImpl;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.Settings;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.search.FacetImpl;
import com.github.richardwilly98.esdms.search.FacetRequestImpl;
import com.github.richardwilly98.esdms.search.SearchResultImpl;
import com.github.richardwilly98.esdms.search.TermImpl;
import com.github.richardwilly98.esdms.search.api.Facet;
import com.github.richardwilly98.esdms.search.api.FacetRequest;
import com.github.richardwilly98.esdms.search.api.SearchResult;
import com.github.richardwilly98.esdms.search.api.Term;
import com.github.richardwilly98.esdms.search.api.TermRequest;

@Singleton
public class SearchProvider implements SearchService<Document> {

    final static Logger log = Logger.getLogger(SearchProvider.class);

    final static ObjectMapper mapper = new ObjectMapper();
    final Client client;
    final Settings settings;
    final String index;
    private final static String type = "document";

    private String currentUser;

    @Inject
    SearchProvider(final Client client, final BootstrapService bootstrapService) throws ServiceException {
        checkNotNull(client);
        checkNotNull(bootstrapService);
        checkNotNull(type);
        this.client = client;
        this.settings = bootstrapService.loadSettings();
        this.index = settings.getLibrary();
    }

    protected void isAuthenticated() throws ServiceException {
        try {
            log.debug("*** isAuthenticated ***");
            Subject currentSubject = SecurityUtils.getSubject();
            log.debug("currentSubject.isAuthenticated(): " + currentSubject.isAuthenticated());
            log.debug("Principal: " + currentSubject.getPrincipal());
            if (currentSubject.getPrincipal() == null) {
                throw new ServiceException("Unauthorize request");
            } else {
                if (currentUser == null) {
                    if (currentSubject.getPrincipal() instanceof UserImpl) {
                        currentUser = ((UserImpl) currentSubject.getPrincipal()).getId();
                    }
                }
            }
        } catch (Throwable t) {
            throw new ServiceException();
        }
    }

    protected String getCurrentUser() throws ServiceException {
        if (currentUser == null) {
            isAuthenticated();
        }
        return currentUser;
    }

    protected SearchResult<Document> parseSearchResult(SearchResponse searchResponse, int first, int pageSize, List<FacetRequest> facets)
            throws ServiceException {
        log.trace("*** parseSearchResult ***");
        try {
            Stopwatch watch = Stopwatch.createStarted();
            Set<Document> items = newHashSet();
            long totalHits = searchResponse.getHits().totalHits();
            long elapsedTime = searchResponse.getTookInMillis();
            log.trace(String.format("Total hist: %s - item count: %s", totalHits, searchResponse.getHits().hits().length));
            for (SearchHit hit : searchResponse.getHits().hits()) {
                String json = convertFieldAsString(hit, "document");
                Document item = mapper.readValue(json, Document.class);
                items.add(item);
            }
            SearchResultImpl.Builder<Document> builder = new SearchResultImpl.Builder<Document>().totalHits(totalHits)
                    .elapsedTime(elapsedTime).items(items).firstIndex(first).pageSize(pageSize);
            if (facets != null && facets.size() > 0 && searchResponse.getFacets() != null
                    && searchResponse.getFacets().facetsAsMap() != null) {
                for (FacetRequest facet : facets) {
                    if (searchResponse.getFacets().facetsAsMap().containsKey(facet.getName())) {
                        TermsFacet tf = (TermsFacet) searchResponse.getFacets().facetsAsMap().get(facet.getName());
                        Set<Term> terms = newHashSet();
                        for (TermsFacet.Entry entry : tf) {
                            terms.add(new TermImpl.Builder().term(entry.getTerm().string()).count(entry.getCount()).build());
                        }
                        Facet f = new FacetImpl.Builder().name(facet.getName()).type(tf.getType()).terms(terms)
                                .missingCount(tf.getMissingCount()).otherCount(tf.getOtherCount()).totalCount(tf.getTotalCount()).build();
                        Map<String, Facet> facetsResponse = newHashMap();
                        facetsResponse.put(facet.getName(), f);
                        builder.facets(facetsResponse);
                    }
                }
            }
            SearchResult<Document> searchResult = builder.build();
            watch.stop();
            log.debug(String.format("Elapsed time to build document list - %s ms", watch.elapsed(TimeUnit.MILLISECONDS)));
            return searchResult;
        } catch (Throwable t) {
            log.error("parseSearchResult failed", t);
            throw new ServiceException(t.getLocalizedMessage());
        }
    }

    private String convertFieldAsString(SearchHit hit, String name) throws IOException {
        XContentBuilder builder = jsonBuilder();
        if (hit.getFields().containsKey(name)) {
            builder.value(hit.getFields().get(name).getValue());
        }
        return builder.string();
    }

    @Override
    public SearchResult<Document> search(String criteria, int first, int pageSize) throws ServiceException {
        return search(criteria, first, pageSize, null);
    }

    @Override
    public SearchResult<Document> search(String criteria, int first, int pageSize, List<FacetRequest> facets) throws ServiceException {
        return search(criteria, first, pageSize, facets, null);
    }

    @Override
    public SearchResult<Document> search(String criteria, int first, int pageSize, List<FacetRequest> facets, Map<String, Object> filters)
            throws ServiceException {
        try {
            log.trace(String.format("search %s - %s - %s - %s - %s", criteria, first, pageSize, facets, filters));
            log.trace(String.format("index: %s - type: %s", index, type));
            QueryBuilder query = new QueryStringQueryBuilder(criteria);
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type)
                    .addPartialField("document", null, "versions").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setFrom(first)
                    .setSize(pageSize).setQuery(query);
            if (facets != null && facets.size() > 0) {
                for (FacetRequest facet : facets) {
                    for (TermRequest term : facet.getTerms()) {
                        searchRequestBuilder
                                .addFacet(FacetBuilders.termsFacet(facet.getName()).field(term.getField()).size(term.getSize()));
                    }
                }
            }
            if (filters != null && filters.size() > 0) {
                AndFilterBuilder andFilter = null;
                for (String key : filters.keySet()) {
                    log.debug(String.format("Add filter %s - %s - %s", key, filters.get(key), filters.get(key).getClass()));
                    if (andFilter == null) {
                        andFilter = FilterBuilders.andFilter(FilterBuilders.termsFilter(key, filters.get(key)));
                    } else {
                        // searchRequestBuilder.setFilter(FilterBuilders.termsFilter(key,
                        // filters.get(key)));
                        andFilter.add(FilterBuilders.termsFilter(key, filters.get(key)));
                    }
                }
                if (andFilter != null) {
                    searchRequestBuilder.setFilter(andFilter);
                }
            }
            log.debug("Search request builder: " + searchRequestBuilder);
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
            return parseSearchResult(searchResponse, first, pageSize, facets);
        } catch (Throwable t) {
            log.error("search failed", t);
            throw new ServiceException(t.getLocalizedMessage());
        }
    }

    public SearchResult<Document> moreLikeThis(String criteria, int first, int pageSize, int minTermFrequency, int maxItems)
            throws ServiceException {
        try {
            log.trace(String.format("moreLikeThis %s - %s - %s - %s - %s", criteria, first, pageSize, minTermFrequency, maxItems));
            log.trace(String.format("index: %s - type: %s", index, type));
            QueryBuilder query = QueryBuilders.moreLikeThisQuery().likeText(criteria).minTermFreq(minTermFrequency).maxQueryTerms(maxItems);
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type)
                    .addPartialField("document", null, "versions").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setFrom(first)
                    .setSize(pageSize).setQuery(query);
            log.debug("Search request builder: " + searchRequestBuilder);
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
            return parseSearchResult(searchResponse, first, pageSize, null);
        } catch (Throwable t) {
            log.error("moreLikeThis failed", t);
            throw new ServiceException(t.getLocalizedMessage());
        }
    }

    public Facet suggestTags(String criteria, int size) throws ServiceException {
        try {
            log.trace(String.format("suggestTags - %s - %s", criteria, size));
            QueryBuilder query = QueryBuilders.matchAllQuery();
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setSize(0).setQuery(query);
            searchRequestBuilder
                    .addFacet(FacetBuilders.termsFacet("Tags").field("tags").size(size).regex(String.format("^%s.*", criteria)));
            log.debug("Search request builder: " + searchRequestBuilder);
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
            List<FacetRequest> facets = newArrayList();
            facets.add(new FacetRequestImpl.Builder().name("Tags").build());
            return parseSearchResult(searchResponse, 0, size, facets).getFacets().values().iterator().next();
        } catch (Throwable t) {
            log.error("suggestTags failed", t);
            throw new ServiceException(t.getLocalizedMessage());
        }
    }
}
