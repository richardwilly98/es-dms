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

import java.io.IOException;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.github.richardwilly98.esdms.api.AuditEntry;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.services.audit.AuditStrategy;

@Singleton
public class AuditProvider extends ProviderItemBase<AuditEntry> implements AuditService {

    private static Logger log = Logger.getLogger(AuditProvider.class);

    private static final String AUDIT_MAPPING_JSON = "/com/github/richardwilly98/esdms/services/audit-mapping.json";
    private final static String type = "audit";
    private static final EnumSet<AuditEntry.Event> events = EnumSet.allOf(AuditEntry.Event.class);
    private final AuditStrategy strategy;

    @Inject
    AuditProvider(Client client, BootstrapService bootstrapService, final UserService userService, final AuditStrategy strategy)
	    throws ServiceException {
	super(client, bootstrapService, bootstrapService.loadSettings().getLibrary() + "-archive", AuditProvider.type, AuditEntry.class);
	this.strategy = strategy;
    }

    @Override
    public void clear(List<String> ids) throws ServiceException {
	for (String id : ids) {
	    AuditEntry audit = super.get(id);
	    if (audit != null) {
		super.delete(audit);
	    }
	}
    }

    @Override
    public void clear(Date from, Date to) throws ServiceException {
	try {
	    QueryBuilder query = QueryBuilders.rangeQuery("date").from(from).to(to).includeLower(true).includeUpper(true);
	    SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type)
		    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(query);
	    log.debug("Search request builder: " + searchRequestBuilder);
	    SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
	    for (SearchHit hit : searchResponse.getHits().hits()) {
		String json = hit.getSourceAsString();
		AuditEntry audit;
		audit = mapper.readValue(json, AuditEntry.class);
		super.delete(audit);
	    }
	} catch (Throwable t) {
	    log.error("clear failed", t);
	    throw new ServiceException(t.getLocalizedMessage());
	}
    }

    @Override
    public AuditEntry create(AuditEntry.Event event, String itemId, String user) throws ServiceException {
	AuditEntry audit = strategy.convert(event, itemId, user);
	return super.create(audit);
    }

    @Override
    protected void loadInitialData() throws ServiceException {
    }

    @Override
    protected String getMapping() {
	try {
	    return copyToStringFromClasspath(AUDIT_MAPPING_JSON);
	} catch (IOException ioEx) {
	    log.error("getMapping failed", ioEx);
	    return null;
	}
    }

    @Override
    public EnumSet<AuditEntry.Event> getEvents() {
	return events;
    }

    @Override
    protected void doStart() throws ServiceException {
    }

    @Override
    protected void doStop() throws ServiceException {
    }
}
