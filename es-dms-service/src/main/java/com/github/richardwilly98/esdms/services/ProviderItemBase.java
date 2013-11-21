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
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.github.richardwilly98.esdms.api.ItemBase;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.search.SearchResultImpl;
import com.github.richardwilly98.esdms.search.api.SearchResult;

abstract class ProviderItemBase<T extends ItemBase> extends ServiceBase implements BaseService<T> {

    private static Validator validator;
    final Class<T> clazz;

    @Inject
    ProviderItemBase(final Client client, final BootstrapService bootstrapService, final String index, final String type,
            final Class<T> clazz) throws ServiceException {
        super(client, bootstrapService, index, type);
        checkNotNull(clazz);
        this.clazz = clazz;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    protected void validate(T item) throws ServiceException {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(item);
        if (constraintViolations.size() != 0) {
            StringBuffer message = new StringBuffer();
            for (ConstraintViolation<T> constraintVioldation : constraintViolations) {
                message.append(constraintVioldation.getMessage()).append(" ");
            }
            throw new ServiceException(message.toString());
        }
    }

    @Override
    public T get(String id) throws ServiceException {
        try {
            if (log.isTraceEnabled()) {
                log.trace(String.format("get - %s", id));
            }
            GetResponse response = client.prepareGet(index, type, id).execute().actionGet();
            if (!response.isExists()) {
                log.info(String.format("Cannot find item %s", id));
                return null;
            }
            StreamInput stream = response.getSourceAsBytesRef().streamInput();
            T item = mapper.readValue(stream, clazz);
            validate(item);
            return item;
        } catch (Throwable t) {
            log.error("get failed", t);
            throw new ServiceException(t.getLocalizedMessage());
        }
    }

    @Override
    public boolean exists(String id) throws ServiceException {
        try {
            if (log.isTraceEnabled()) {
                log.trace(String.format("exists - %s", id));
            }
            return client.prepareGet(index, type, id).setFields(new String[0]).execute().actionGet().isExists();
        } catch (Throwable t) {
            log.error("exists failed", t);
            throw new ServiceException(t.getLocalizedMessage());
        }
    }

    @Override
    public SearchResult<T> search(String criteria, int first, int pageSize) throws ServiceException {
        try {
            SearchResponse searchResponse = client.prepareSearch(index).setTypes(type).setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setFrom(first).setSize(pageSize).setQuery(new QueryStringQueryBuilder(criteria)).execute().actionGet();
            return parseSearchResult(searchResponse, first, pageSize);
        } catch (Throwable t) {
            log.error("search failed", t);
            throw new ServiceException(t.getLocalizedMessage());
        }
    }

    protected SearchResult<T> search(QueryBuilder query, int first, int pageSize) throws ServiceException {
        try {
            SearchResponse searchResponse = client.prepareSearch(index).setTypes(type).setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setFrom(first).setSize(pageSize).setQuery(query).execute().actionGet();
            return parseSearchResult(searchResponse, first, pageSize);
        } catch (Throwable t) {
            log.error("search failed", t);
            throw new ServiceException(t.getLocalizedMessage());
        }
    }

    protected SearchResult<T> parseSearchResult(SearchResponse searchResponse, int first, int pageSize) throws ServiceException {
        try {
            Set<T> items = newHashSet();
            long totalHits = searchResponse.getHits().totalHits();
            long elapsedTime = searchResponse.getTookInMillis();
            for (SearchHit hit : searchResponse.getHits().hits()) {
                StreamInput stream = hit.getSourceRef().streamInput();
                T item = mapper.readValue(stream, clazz);
                items.add(item);
            }
            SearchResult<T> searchResult = new SearchResultImpl.Builder<T>().totalHits(totalHits).elapsedTime(elapsedTime).items(items)
                    .firstIndex(first).pageSize(pageSize).build();
            return searchResult;
        } catch (Throwable t) {
            log.error("getSearchResult failed", t);
            throw new ServiceException(t.getLocalizedMessage());
        }
    }

    @Override
    public T create(T item) throws ServiceException {
        try {
            if (log.isTraceEnabled()) {
                log.trace(String.format("create - %s", item));
            }
            if (item.getId() == null) {
                item.setId(generateUniqueId(item));
            }
            validate(item);
            byte[] data = mapper.writeValueAsBytes(item);
            IndexResponse response = client.prepareIndex(index, type).setId(item.getId()).setSource(data).execute().actionGet();
            log.trace(String.format("Index: %s - Type: %s - Id: %s", response.getIndex(), response.getType(), response.getId()));
            refreshIndex();
            T newItem = get(response.getId());
            return newItem;
        } catch (Throwable t) {
            log.error("create failed", t);
            throw new ServiceException(t.getLocalizedMessage());
        }
    }

    // TODO: Must improve the update to avoid 2 update API calls
    @Override
    public T update(T item) throws ServiceException {
        try {
            if (log.isTraceEnabled()) {
                log.trace(String.format("update - %s", item));
            }
            byte[] data = mapper.writeValueAsBytes(item);
            UpdateResponse response = client.prepareUpdate(index, type, item.getId())
                    .setScript("ctx._source.remove('attributes'); ctx._source.remove('tags'); ctx._source.remove('ratings');").execute()
                    .actionGet();
            response = client.prepareUpdate(index, type, item.getId()).setDoc(data).execute().actionGet();
            refreshIndex();
            T updatedItem = get(response.getId());
            return updatedItem;
        } catch (Throwable t) {
            log.error("update failed", t);
            throw new ServiceException(t.getLocalizedMessage());
        }
    }

    @Override
    public void delete(T item) throws ServiceException {
        try {
            if (log.isTraceEnabled()) {
                log.trace(String.format("delete - %s", item));
            }
            if (item == null) {
                throw new IllegalArgumentException("item is null");
            }
            client.prepareDelete(index, type, item.getId()).execute().actionGet();
            refreshIndex();
        } catch (Throwable t) {
            log.error("delete failed", t);
            throw new ServiceException(t.getLocalizedMessage());
        }
    }

    protected String generateUniqueId(T item) {
        return UUID.randomUUID().toString();
    }

    @PostConstruct
    private void start() throws ServiceException {
        log.info("start " + this.getClass().getName());
        doStart();
    }
    
    protected abstract void doStart() throws ServiceException;
    
    @PreDestroy
    private void stop() throws ServiceException {
        log.info("stop " + this.getClass().getName());
        client.close();
        doStop();
    }
    
    protected abstract void doStop() throws ServiceException;

    @PostConstruct
    private void createIndex() throws ServiceException {
        if (!client.admin().indices().prepareExists(index).execute().actionGet().isExists()) {
            client.admin().indices().prepareCreate(index).execute().actionGet();
            refreshIndex();
        }
        boolean exists = client.admin().indices().prepareTypesExists(index).setTypes(type).execute().actionGet().isExists();
        log.info(String.format("Exists type %s in index %s: %s", type, index, exists));
        if (!exists) {
            String mapping = getMapping();
            if (mapping != null) {
                log.debug(String.format("Create mapping %s", mapping));
                PutMappingResponse mappingResponse = client.admin().indices().preparePutMapping(index).setType(type).setSource(mapping)
                        .execute().actionGet();
                log.info(String.format("Mapping response acknowledged: %s", mappingResponse.isAcknowledged()));
            }
            loadInitialData();
            refreshIndex();
        }
    }

    @Override
    public boolean disabled(T item) throws ServiceException {
        try {
            checkNotNull(item);
            return item.isDisabled();
        } catch (Throwable t) {
            log.error("disabled failed", t);
            throw new ServiceException(t.getLocalizedMessage());
        }
    }

    @Override
    public void disable(T item, boolean b) throws ServiceException {
        try {
            checkNotNull(item);
            item.setDisabled(b);
        } catch (Throwable t) {
            log.error("disable failed", t);
            throw new ServiceException(t.getLocalizedMessage());
        }
    }

    protected void refreshIndex() {
        refreshIndex(settings.isIndexRefresh());
    }

    protected void refreshIndex(boolean force) {
        if (force) {
            client.admin().indices().refresh(new RefreshRequest(index)).actionGet();
        }
    }

    protected abstract void loadInitialData() throws ServiceException;

    protected abstract String getMapping();
}
