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

import static org.elasticsearch.common.io.Streams.copyToStringFromClasspath;
import static org.elasticsearch.index.query.QueryBuilders.fieldQuery;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;

import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.api.Version;
import com.github.richardwilly98.esdms.exception.ServiceException;

@Singleton
public class VersionProvider extends ProviderBase<Version> implements VersionService {

    private static final String VERSION_MAPPING_JSON = "/com/github/richardwilly98/esdms/services/version-mapping.json";
    private final static String type = "version";

    @Inject
    VersionProvider(Client client, BootstrapService bootstrapService) throws ServiceException {
	super(client, bootstrapService, bootstrapService.loadSettings().getLibrary() + "-archive", VersionProvider.type, Version.class);
    }

    @Override
    protected void loadInitialData() throws ServiceException {
    }

    @Override
    protected String getMapping() {
	try {
	    return copyToStringFromClasspath(VERSION_MAPPING_JSON);
	} catch (IOException ioEx) {
	    log.error("getMapping failed", ioEx);
	    return null;
	}
    }

    // private SimpleDocument updateModifiedDate(Document document) {
    // SimpleDocument sd = new
    // SimpleDocument.Builder().document(document).build();
    // DateTime now = new DateTime();
    // sd.setReadOnlyAttribute(Document.MODIFIED_DATE, now.toString());
    // return sd;
    // }

    // @RequiresPermissions(CREATE_PERMISSION)
    // @Override
    // public Document create(Document item) throws ServiceException {
    // SimpleDocument sd = new SimpleDocument.Builder().document(item).build();
    // DateTime now = new DateTime();
    // sd.setReadOnlyAttribute(Document.CREATION_DATE, now.toString());
    // sd.setReadOnlyAttribute(Document.AUTHOR, getCurrentUser());
    // return super.create(sd);
    // }
    //
    // @RequiresPermissions(DELETE_PERMISSION)
    // @Override
    // public void delete(Document item) throws ServiceException {
    // super.delete(item);
    // }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.github.richardwilly98.services.BaseService#search(java.lang.String)
     */
    @Override
    public SearchResult<Version> search(String criteria, int first, int pageSize) throws ServiceException {
	try {
	    // Set<Version> versions = newHashSet();

	    SearchResponse searchResponse = client.prepareSearch(index).setTypes(type).setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		    .setFrom(first).setSize(pageSize).setQuery(fieldQuery("file", criteria)).execute().actionGet();
	    // log.debug("totalHits: " + searchResponse.getHits().totalHits());
	    // log.debug(String.format("TotalHits: %s - TookInMillis: %s",
	    // searchResponse.getHits().totalHits(),
	    // searchResponse.getTookInMillis()));
	    // Stopwatch watch = new Stopwatch();
	    // watch.start();
	    // for (SearchHit hit : searchResponse.getHits().hits()) {
	    // String json = hit.getSourceAsString();
	    // Version version = mapper.readValue(json, Version.class);
	    // versions.add(version);
	    // }
	    // watch.stop();
	    // log.debug("Elapsed time to build version list " +
	    // watch.elapsed(TimeUnit.MILLISECONDS));

	    // return versions;
	    return getSearchResult(searchResponse, first, pageSize);
	} catch (Throwable t) {
	    log.error("search failed", t);
	    throw new ServiceException(t.getLocalizedMessage());
	}
    }

    // @Override
    // public Document update(Document item) throws ServiceException {
    // SimpleDocument document = updateModifiedDate(item);
    // return super.update(document);
    // }

    // private String getStatus(Document document) {
    // Map<String, Object> attributes = document.getAttributes();
    // if (attributes == null || !attributes.containsKey(Document.STATUS)) {
    // return null;
    // } else {
    // return attributes.get(Document.STATUS).toString();
    // }
    // }

}
