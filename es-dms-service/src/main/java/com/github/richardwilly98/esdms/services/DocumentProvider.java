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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.elasticsearch.common.io.Streams.copyToStringFromClasspath;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightField;

import com.github.richardwilly98.esdms.DocumentImpl;
import com.github.richardwilly98.esdms.SearchResultImpl;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.Document.DocumentStatus;
import com.github.richardwilly98.esdms.api.File;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.api.Version;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

@Singleton
public class DocumentProvider extends ProviderBase<Document> implements DocumentService {

    private static final String DOCUMENT_MAPPING_JSON = "/com/github/richardwilly98/esdms/services/document-mapping.json";
    private final static String type = "document";
    private final VersionService versionService;

    @Inject
    DocumentProvider(Client client, BootstrapService bootstrapService, VersionService versionService) throws ServiceException {
	super(client, bootstrapService, null, DocumentProvider.type, Document.class);
	this.versionService = versionService;
    }

    private SimpleDocument getSimpleDocument(Document document) {
	checkNotNull(document);
	return new SimpleDocument.Builder().document(document).build();
    }

    private SimpleVersion getSimpleVersion(Version version) {
	checkNotNull(version);
	return new SimpleVersion.Builder().version(version).build();
    }

    @Override
    protected void loadInitialData() throws ServiceException {
    }

    @Override
    protected String getMapping() {
	try {
	    return copyToStringFromClasspath(DOCUMENT_MAPPING_JSON);
	} catch (IOException ioEx) {
	    log.error("getMapping failed", ioEx);
	    return null;
	}
    }

    private SimpleDocument updateModifiedDate(SimpleDocument document) {
	document.setReadOnlyAttribute(Document.MODIFIED_DATE, new Date());
	return document;
    }

    // @RequiresPermissions(PROFILE_READ_PERMISSION)
    @SuppressWarnings("unchecked")
    @Override
    public Document getMetadata(String id) throws ServiceException {
	try {
	    if (log.isTraceEnabled()) {
		log.trace(String.format("getMetadata - %s", id));
	    }
	    GetResponse response = client.prepareGet(index, type, id).setFields("id", "name", "attributes", "tags").execute().actionGet();
	    if (!response.isExists()) {
		log.info(String.format("Cannot find item %s", id));
		return null;
	    }

	    checkNotNull(response.getField("name"));
	    String name = response.getField("name").getValue().toString();
	    Map<String, Object> attributes = newHashMap();
	    if (response.getField("attributes") != null && response.getField("attributes").getValue() instanceof Map<?, ?>) {
		attributes.putAll((Map<String, Object>) response.getField("attributes").getValue());
	    }
	    Set<String> tags = newHashSet();
	    if (response.getField("tags") != null && response.getField("tags").getValues().size() > 0) {
		for (Object tag : response.getField("tags")) {
		    tags.add(String.valueOf(tag));
		}
	    }

	    Document document = new DocumentImpl.Builder().tags(tags).id(id).name(name).attributes(attributes).roles(null).build();
	    return document;
	} catch (Throwable t) {
	    log.error("getMetadata failed", t);
	    throw new ServiceException(t.getLocalizedMessage());
	}
    }

    @RequiresPermissions(CREATE_PERMISSION)
    @Override
    public Document create(Document item) throws ServiceException {
	SimpleDocument sd = getSimpleDocument(item);
	sd.setReadOnlyAttribute(Document.CREATION_DATE, new Date());
	sd.setReadOnlyAttribute(Document.AUTHOR, getCurrentUser());
	return super.create(sd);
    }

    @RequiresPermissions(DELETE_PERMISSION)
    @Override
    public void delete(Document item) throws ServiceException {

	if (!item.hasStatus(DocumentStatus.DELETED)) {
	    throw new ServiceException(String.format("Precondition failure: document %s not marked for deletion!", item.getId()));
	}

	super.delete(item);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.github.richardwilly98.services.BaseService#search(java.lang.String)
     */
    @Override
    public SearchResult<Document> search(String criteria, int first, int pageSize) throws ServiceException {
	try {
	    QueryBuilder query = new QueryStringQueryBuilder(criteria);
	    SearchResponse searchResponse = client.prepareSearch(index).setTypes(type).addPartialField("document", null, "versions")
		    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setFrom(first).setSize(pageSize).setQuery(query).execute().actionGet();
	    return getSearchResult(searchResponse, first, pageSize);
	} catch (Throwable t) {
	    log.error("search failed", t);
	    throw new ServiceException(t.getLocalizedMessage());
	}
    }

    @Override
    protected SearchResult<Document> getSearchResult(SearchResponse searchResponse, int first, int pageSize) throws ServiceException {
	log.trace("*** getSearchResult ***");
	try {
	    // log.debug("searchResponse: " + searchResponse.toString());
//	    Stopwatch watch = new Stopwatch();
//	    watch.start();
	    Set<Document> items = newHashSet();
	    long totalHits = searchResponse.getHits().totalHits();
	    long elapsedTime = searchResponse.getTookInMillis();
	    for (SearchHit hit : searchResponse.getHits().hits()) {
		String json = convertFieldAsString(hit, "document");
		Document item = mapper.readValue(json, Document.class);
		// Version currentVersion = item.getCurrentVersion();
		// if (currentVersion != null) {
		// currentVersion.getFile().setContent(null);
		// }
		items.add(item);
	    }
	    SearchResult<Document> searchResult = new SearchResultImpl.Builder<Document>().totalHits(totalHits).elapsedTime(elapsedTime)
		    .items(items).firstIndex(first).pageSize(pageSize).build();
//	    watch.stop();
//	    log.debug("Elapsed time to build document list " + watch.elapsed(TimeUnit.MILLISECONDS));
	    return searchResult;
	} catch (Throwable t) {
	    log.error("getSearchResult failed", t);
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
    public void checkin(Document document) throws ServiceException {
	if (document.hasStatus(DocumentStatus.LOCKED)) {
	    SimpleDocument sd = getSimpleDocument(document);
	    sd.removeReadOnlyAttribute(Document.STATUS);
	    sd.setReadOnlyAttribute(Document.AUTHOR, getCurrentUser());
	    sd.removeReadOnlyAttribute(Document.LOCKED_BY);
	    document = update(sd);
	} else {
	    throw new ServiceException(String.format("Document %s is not locked.", document.getId()));
	}
    }

    public void markDeleted(Document document) throws ServiceException {
	if (document.hasStatus(DocumentStatus.AVAILABLE)) {
	    SimpleDocument sd = getSimpleDocument(document);
	    sd.setStatus(DocumentStatus.DELETED);
	    document = update(sd);
	} else {
	    throw new ServiceException(String.format("Document %s is not marked as available.", document.getId()));
	}
    }

    public void undelete(Document document) throws ServiceException {
	if (document.hasStatus(DocumentStatus.DELETED)) {
	    SimpleDocument sd = getSimpleDocument(document);
	    sd.setStatus(DocumentStatus.AVAILABLE);

	    document = update(sd);
	} else {
	    throw new ServiceException(String.format("Document %s is not marked for deletion.", document.getId()));
	}
    }

    @Override
    public Document update(Document item) throws ServiceException {
	SimpleDocument document = updateModifiedDate(getSimpleDocument(item));
	return super.update(document);
    }

    @Override
    public void checkout(Document document) throws ServiceException {
	SimpleDocument sd = getSimpleDocument(document);
	if (document.hasStatus(DocumentStatus.LOCKED)) {
	    throw new ServiceException(String.format("Document %s already locked.", document.getId()));
	}
	sd.setStatus(DocumentStatus.LOCKED);
	sd.setReadOnlyAttribute(Document.LOCKED_BY, getCurrentUser());
	update(sd);
    }

    @Override
    public String preview(Document document, /* int versionId, */
	    String criteria, int size) throws ServiceException {
	try {
	    log.trace("*** preview ***");
	    String query = jsonBuilder().startObject().startObject("bool").startArray("must").startObject().startObject("queryString")
		    .field("query", criteria).array("fields", "_all", "file").endObject().endObject().startObject()
		    .startObject("queryString").field("query", document.getId()).field("default_field", "id").endObject().endObject()
		    .endArray().endObject().endObject().string();

	    log.debug("query: " + query);

	    // FieldQueryBuilder x = QueryBuilders.fieldQuery("id",
	    // document.getId());
	    SearchRequestBuilder srb = client.prepareSearch(index).setTypes(type).setSearchType(SearchType.QUERY_AND_FETCH).setQuery(query)
	    // .setQuery(fieldQuery("file", criteria))
		    .setHighlighterPreTags("<span class='highlight-tag'>").setHighlighterPostTags("</span>").setHighlighterOrder("score").addHighlightedField("file", size, 1);
	    log.trace("++ Search request: " + srb);
	    SearchResponse searchResponse = srb.execute().actionGet();
	    if (log.isTraceEnabled()) {
		log.trace("totalHits: " + searchResponse.getHits().totalHits());
	    }
	    String preview = null;
	    for (SearchHit hit : searchResponse.getHits().hits()) {
		for (String key : hit.getHighlightFields().keySet()) {
		    HighlightField field = hit.getHighlightFields().get(key);
//		    log.debug(String.format("Highlight key: %s", key));
//		    log.debug(String.format("Highlight: %s", hit.getHighlightFields().get(key)));
		    for (Text text : field.fragments()) {
			if (preview == null) {
			    preview = text.string();
			}
		    }
		}
	    }
	    return preview;
	} catch (Throwable t) {
	    log.error("preview failed", t);
	    throw new ServiceException(t.getLocalizedMessage());
	}
    }

    private void updateVersions(SimpleDocument document) throws ServiceException {
	for (Version version : document.getVersions().toArray(new Version[0])) {
	    SimpleVersion sv = getSimpleVersion(version);
	    if (!sv.isCurrent()) {
		if (Strings.isNullOrEmpty(sv.getId())) {
		    /* Version newVersion = */versionService.create(sv);
		    // sv.setId(newVersion.getId());
		} else {
		    versionService.update(sv);
		}
		sv.setFile(null);
		document.updateVersion(sv);
	    }
	}
    }

    @Override
    public void addVersion(Document document, Version version) throws ServiceException {
	if (log.isTraceEnabled()) {
	    log.trace(String.format("*** addVersion document: %s - version: %s ***", document, version));
	}
	checkNotNull(document);
	checkNotNull(version);
	SimpleDocument sd = getSimpleDocument(document);
	SimpleVersion sv = getSimpleVersion(version);
	if (document.getCurrentVersion() != null) {
	    // Move current version to archived index
	    SimpleVersion currentVersion = getSimpleVersion(document.getCurrentVersion());
	    currentVersion.setCurrent(false);
	    if (sv.getParentId() == 0) {
		sv.setParentId(currentVersion.getVersionId());
	    }
	    sd.updateVersion(currentVersion);
	}
	sd.addVersion(sv);
	updateVersions(sd);
	update(sd);
    }

    public void deleteVersion(Document document, Version version) throws ServiceException {
	if (log.isTraceEnabled()) {
	    log.trace(String.format("*** deleteVersion document: %s - version: %s ***", document, version));
	}
	checkNotNull(document);
	checkNotNull(version);
	if (document.getVersions().size() == 1) {
	    throw new ServiceException("Cannot delete the last version of a document. Use DocumentService.delete");
	}
	SimpleDocument sd = getSimpleDocument(document);
	SimpleVersion sv;
	if (!version.isCurrent()) {
	    versionService.delete(version);
	} else {
	    if (version.getParentId() > 0) {
		String id = document.getVersion(version.getParentId()).getId();
		if (id != null) {
		    Version lastVersion = versionService.get(id);
		    sv = getSimpleVersion(lastVersion);
		    sv.setCurrent(true);
		    sv.setId(null);
		    sd.updateVersion(sv);
		    versionService.delete(lastVersion);
		}
	    } else {
		throw new ServiceException(String.format("Version %s is current but does not have parent!", version));
	    }
	}

	// Change parent of version where parent is the deleted version
	int parentId = version.getParentId();
	final int versionId = version.getVersionId();
	Set<Version> filteredVersions = Sets.filter(document.getVersions(), new Predicate<Version>() {
	    @Override
	    public boolean apply(Version version) {
		return (version.getParentId() == versionId);
	    }
	});

	for (Version v : filteredVersions.toArray(new Version[0])) {
	    sv = getSimpleVersion(v);
	    sv.setParentId(parentId);
	    sd.updateVersion(sv);
	}

	sd.deleteVersion(version);
	updateVersions(sd);
	update(sd);
    }

    public Version getVersion(Document document, int versionId) throws ServiceException {
	if (log.isTraceEnabled()) {
	    log.trace(String.format("*** getVersion document: %s - versionId: %s ***", document, versionId));
	}
	checkNotNull(document);
	checkNotNull(versionId);
	Version version = document.getVersion(versionId);
	checkNotNull(version);
	if (!version.isCurrent()) {
	    return versionService.get(version.getId());
	} else {
	    return version;
	}
    }

    @Override
    public Set<Version> getVersions(Document document) throws ServiceException {
	if (log.isTraceEnabled()) {
	    log.trace(String.format("*** getVersions document: %s ***", document));
	}
	Set<Version> versions = newHashSet();
	for (Version version : document.getVersions()) {
	    if (version.isCurrent()) {
		versions.add(version);
	    } else {
		versions.add(versionService.get(version.getId()));
	    }
	}
	return versions;
    }

    @Override
    public File getVersionContent(Document document, int versionId) throws ServiceException {
	checkNotNull(document);
	checkArgument(versionId > 0);
	Version version = document.getVersion(versionId);
	if (version == null) {
	    throw new ServiceException(String.format("Version %s not found.", versionId));
	}
	if (version.isCurrent()) {
	    return version.getFile();
	} else {
	    version = versionService.get(version.getId());
	    if (version == null || version.getFile() == null) {
		throw new ServiceException(String.format("Version %s or its contents not found.", versionId));
	    }
	    return version.getFile();
	}
    }

    @Override
    public void setCurrentVersion(Document document, int versionId) throws ServiceException {
	checkNotNull(document);
	checkArgument(versionId > 0);
	Version version = document.getVersion(versionId);
	if (version == null) {
	    throw new ServiceException(String.format("Version %s not found.", versionId));
	}
	SimpleDocument sd = getSimpleDocument(document);
	SimpleVersion sv = getSimpleVersion(document.getCurrentVersion());
	sv.setCurrent(false);
	sd.updateVersion(sv);

	sv = getSimpleVersion(version);
	sv.setCurrent(true);
	sd.updateVersion(sv);
	updateVersions(sd);
	update(sd);
    }

    @Override
    public void setVersionContent(Document document, int versionId, File file) throws ServiceException {
	checkNotNull(document);
	checkArgument(versionId > 0);
	Version version = document.getVersion(versionId);
	if (version == null) {
	    throw new ServiceException(String.format("Version %s not found.", versionId));
	}
	SimpleDocument sd = getSimpleDocument(document);
	SimpleVersion sv = getSimpleVersion(version);
	sv.setFile(file);
	sd.updateVersion(sv);

	updateVersions(sd);
	update(sd);
    }
}
