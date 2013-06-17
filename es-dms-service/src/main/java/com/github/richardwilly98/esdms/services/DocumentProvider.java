package com.github.richardwilly98.esdms.services;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static org.elasticsearch.common.io.Streams.copyToStringFromClasspath;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.base.Stopwatch;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightField;
import org.joda.time.DateTime;

import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.File;
import com.github.richardwilly98.esdms.api.Version;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class DocumentProvider extends ProviderBase<Document> implements
		DocumentService {

	private static final String DOCUMENT_MAPPING_JSON = "/com/github/richardwilly98/esdms/services/document-mapping.json";
	private final static String type = "document";
	private final VersionService versionService;

	@Inject
	DocumentProvider(Client client, BootstrapService bootstrapService,
			VersionService versionService) throws ServiceException {
		super(client, bootstrapService, null, DocumentProvider.type,
				Document.class);
		this.versionService = versionService;
	}

	
	private SimpleDocument getSimpleDocument(Document document) {
		checkNotNull(document);
		return new SimpleDocument.Builder().document(document)
				.build();
	}

	private SimpleVersion getSimpleVersion(Version version) {
		checkNotNull(version);
		return new SimpleVersion.Builder().version(
				version).build();
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
		DateTime now = new DateTime();
		document.setReadOnlyAttribute(Document.MODIFIED_DATE, now.toString());
		return document;
	}

	@RequiresPermissions(CREATE_PERMISSION)
	@Override
	public Document create(Document item) throws ServiceException {
		SimpleDocument sd = getSimpleDocument(item);
		DateTime now = new DateTime();
		sd.setReadOnlyAttribute(Document.CREATION_DATE, now.toString());
		sd.setReadOnlyAttribute(Document.AUTHOR, getCurrentUser());
		return super.create(sd);
	}

	@RequiresPermissions(DELETE_PERMISSION)
	@Override
	public void delete(Document item) throws ServiceException {
		super.delete(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.richardwilly98.services.BaseService#search(java.lang.String)
	 */
	@Override
	public Set<Document> search(String criteria, int first, int pageSize)
			throws ServiceException {
		try {
			Set<Document> documents = newHashSet();

			QueryBuilder query = new MultiMatchQueryBuilder(criteria, "file",
					"name");
			// QueryBuilder query = fieldQuery("file", criteria);
			SearchResponse searchResponse = client.prepareSearch(index)
					.setTypes(type)
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setFrom(first).setSize(pageSize).setQuery(query).execute()
					.actionGet();
			log.debug("totalHits: " + searchResponse.getHits().totalHits());
			log.debug(String.format("TotalHits: %s - TookInMillis: %s",
					searchResponse.getHits().totalHits(),
					searchResponse.getTookInMillis()));
			Stopwatch watch = new Stopwatch();
			watch.start();
			for (SearchHit hit : searchResponse.getHits().hits()) {
				String json = hit.getSourceAsString();
				Document document = mapper.readValue(json, Document.class);
				Version currentVersion = document.getCurrentVersion();
				if (currentVersion != null) {
					currentVersion.getFile().setContent(null);
				}
				// document.getFile().setContent(null);
				documents.add(document);
			}
			watch.stop();
			log.debug("Elapsed time to build document list "
					+ watch.elapsed(TimeUnit.MILLISECONDS));

			return documents;
		} catch (Throwable t) {
			log.error("search failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	@Override
	public void checkin(Document document) throws ServiceException {
		String status = getStatus(document);
		if (status.equals(Document.DocumentStatus.LOCKED.getStatusCode())) {
			SimpleDocument sd = getSimpleDocument(document);
			sd.removeReadOnlyAttribute(Document.STATUS);
			sd.setReadOnlyAttribute(Document.AUTHOR, getCurrentUser());
			sd.removeReadOnlyAttribute(Document.LOCKED_BY);
			document = update(sd);
		} else {
			throw new ServiceException(String.format(
					"Document %s is not locked.", document.getId()));
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
		if (document.getAttributes() != null
				&& document.getAttributes().containsKey(Document.STATUS)) {
			if (document.getAttributes().get(Document.STATUS)
					.equals(Document.DocumentStatus.LOCKED.getStatusCode())) {
				throw new ServiceException(String.format(
						"Document %s already locked.", document.getId()));
			}
		}
		sd.setReadOnlyAttribute(Document.STATUS,
				Document.DocumentStatus.LOCKED.getStatusCode());
		sd.setReadOnlyAttribute(Document.LOCKED_BY, getCurrentUser());
		update(sd);
	}

	@Override
	public String preview(Document document, String criteria, int size)
			throws ServiceException {
		try {
			log.trace("*** preview ***");
			String query = jsonBuilder().startObject().startObject("bool")
					.startArray("must").startObject()
					.startObject("queryString").field("query", criteria)
					.array("fields", "_all", "file").endObject().endObject()
					.startObject().startObject("queryString")
					.field("query", document.getId())
					.field("default_field", "id").endObject().endObject()
					.endArray().endObject().endObject().string();

			log.debug("query: " + query);

			// FieldQueryBuilder x = QueryBuilders.fieldQuery("id",
			// document.getId());
			SearchRequestBuilder srb = client.prepareSearch(index)
					.setTypes(type).setSearchType(SearchType.QUERY_AND_FETCH)
					.setQuery(query)
					// .setQuery(fieldQuery("file", criteria))
					.setHighlighterOrder("score")
					.addHighlightedField("file", size, 1);
			log.trace("Search request: " + srb);
			SearchResponse searchResponse = srb.execute().actionGet();
			log.debug("totalHits: " + searchResponse.getHits().totalHits());
			String preview = null;
			for (SearchHit hit : searchResponse.getHits().hits()) {
				log.debug(String.format("HighlightFields: %s", hit
						.getHighlightFields().size()));
				for (String key : hit.getHighlightFields().keySet()) {
					HighlightField field = hit.getHighlightFields().get(key);
					log.debug(String.format("Highlight key: %s", key));
					log.debug(String.format("Highlight: %s", hit
							.getHighlightFields().get(key)));
					for (Text text : field.fragments()) {
						if (preview == null) {
							preview = text.string();
						}
					}
				}
			}
			return preview;
		} catch (Throwable t) {
			log.error("getItems failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	private String getStatus(Document document) {
		Map<String, Object> attributes = document.getAttributes();
		if (attributes == null || !attributes.containsKey(Document.STATUS)) {
			return null;
		} else {
			return attributes.get(Document.STATUS).toString();
		}
	}

	private void updateVersions(SimpleDocument document) throws ServiceException {
		for (Version version : document.getVersions().toArray(new Version[0])) {
			SimpleVersion sv = getSimpleVersion(version);
			if (!sv.isCurrent()) {
				if (Strings.isNullOrEmpty(sv.getId())) {
					/*Version newVersion =*/ versionService.create(sv);
//					sv.setId(newVersion.getId());
				} else {
					versionService.update(sv);
				}
				sv.setFile(null);
				document.updateVersion(sv);
			}
			log.debug(String.format("updateVersions - Version updated: %s", sv));
		}
		log.debug(String.format("updateVersions - 2. Document updated: %s", document));
	}

	@Override
	public void addVersion(Document document, Version version)
			throws ServiceException {
		if (log.isTraceEnabled()) {
			log.trace(String.format(
					"*** addVersion document: %s - version: %s ***", document,
					version));
		}
		checkNotNull(document);
		checkNotNull(version);
		SimpleDocument sd = getSimpleDocument(document);
		SimpleVersion sv = getSimpleVersion(version);
		if (document.getCurrentVersion() != null) {
			// Move current version to archived index
			SimpleVersion currentVersion = getSimpleVersion(
					document.getCurrentVersion());
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

	public void deleteVersion(Document document, Version version)
			throws ServiceException {
		if (log.isTraceEnabled()) {
			log.trace(String.format(
					"*** deleteVersion document: %s - version: %s ***",
					document, version));
		}
		checkNotNull(document);
		checkNotNull(version);
		if (document.getVersions().size() == 1) {
			throw new ServiceException(
					"Cannot delete the last version of a document. Use DocumentService.delete");
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
				throw new ServiceException(String.format(
						"Version %s is current but does not have parent!",
						version));
			}
		}

		// Change parent of version where parent is the deleted version
		int parentId = version.getParentId();
		final int versionId = version.getVersionId();
		Set<Version> filteredVersions = Sets.filter(document.getVersions(),
				new Predicate<Version>() {
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

	public Version getVersion(Document document, int versionId)
			throws ServiceException {
		if (log.isTraceEnabled()) {
			log.trace(String.format(
					"*** getVersion document: %s - versionId: %s ***",
					document, versionId));
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
			log.trace(String.format("*** getVersions document: %s ***",
					document));
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
	public File getVersionContent(Document document, int versionId)
			throws ServiceException {
		checkNotNull(document);
		checkArgument(versionId > 0);
		Version version = document.getVersion(versionId);
		if (version == null) {
			throw new ServiceException(String.format("Version %s not found.",
					versionId));
		}
		if (version.isCurrent()) {
			return version.getFile();
		} else {
			version = versionService.get(version.getId());
			if (version == null || version.getFile() == null) {
				throw new ServiceException(String.format(
						"Version %s or its contents not found.", versionId));
			}
			return version.getFile();
		}
	}

	@Override
	public void setCurrentVersion(Document document, int versionId)
			throws ServiceException {
		checkNotNull(document);
		checkArgument(versionId > 0);
		Version version = document.getVersion(versionId);
		if (version == null) {
			throw new ServiceException(String.format("Version %s not found.",
					versionId));
		}
		SimpleDocument sd = getSimpleDocument(document);
		SimpleVersion sv = getSimpleVersion(
				document.getCurrentVersion());
		sv.setCurrent(false);
		sd.updateVersion(sv);

		sv = getSimpleVersion(version);
		sv.setCurrent(true);
		sd.updateVersion(sv);
		updateVersions(sd);
		update(sd);
	}
}
