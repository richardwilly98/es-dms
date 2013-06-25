package com.github.richardwilly98.esdms.services;

import static com.google.common.collect.Sets.newHashSet;
import static org.elasticsearch.common.io.Streams.copyToStringFromClasspath;
import static org.elasticsearch.index.query.QueryBuilders.fieldQuery;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.base.Stopwatch;
import org.elasticsearch.search.SearchHit;

import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.api.Version;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.google.inject.Inject;

public class VersionProvider extends ProviderBase<Version> implements
		VersionService {

	private static final String VERSION_MAPPING_JSON = "/com/github/richardwilly98/esdms/services/version-mapping.json";
	private final static String type = "version";

	@Inject
	VersionProvider(Client client, BootstrapService bootstrapService)
			throws ServiceException {
		super(client, bootstrapService, bootstrapService.loadSettings().getLibrary() + "-archive", VersionProvider.type,
				Version.class);
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

//	private SimpleDocument updateModifiedDate(Document document) {
//		SimpleDocument sd = new SimpleDocument.Builder().document(document).build();
//		DateTime now = new DateTime();
//		sd.setReadOnlyAttribute(Document.MODIFIED_DATE, now.toString());
//		return sd;
//	}

//	@RequiresPermissions(CREATE_PERMISSION)
//	@Override
//	public Document create(Document item) throws ServiceException {
//		SimpleDocument sd = new SimpleDocument.Builder().document(item).build();
//		DateTime now = new DateTime();
//		sd.setReadOnlyAttribute(Document.CREATION_DATE, now.toString());
//		sd.setReadOnlyAttribute(Document.AUTHOR, getCurrentUser());
//		return super.create(sd);
//	}
//
//	@RequiresPermissions(DELETE_PERMISSION)
//	@Override
//	public void delete(Document item) throws ServiceException {
//		super.delete(item);
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.richardwilly98.services.BaseService#search(java.lang.String)
	 */
	@Override
	public SearchResult<Version> search(String criteria, int first, int pageSize) throws ServiceException {
		try {
//			Set<Version> versions = newHashSet();

			SearchResponse searchResponse = client.prepareSearch(index)
					.setTypes(type).setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setFrom(first).setSize(pageSize)
					.setQuery(fieldQuery("file", criteria))
					.execute().actionGet();
//			log.debug("totalHits: " + searchResponse.getHits().totalHits());
//			log.debug(String.format("TotalHits: %s - TookInMillis: %s", searchResponse.getHits().totalHits(), searchResponse.getTookInMillis()));
//			Stopwatch watch = new Stopwatch();
//			watch.start();
//			for (SearchHit hit : searchResponse.getHits().hits()) {
//				String json = hit.getSourceAsString();
//				Version version = mapper.readValue(json, Version.class);
//				versions.add(version);
//			}
//			watch.stop();
//			log.debug("Elapsed time to build version list " + watch.elapsed(TimeUnit.MILLISECONDS));

//			return versions;
			return getSearchResult(searchResponse, first, pageSize);
		} catch (Throwable t) {
			log.error("search failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

//	@Override
//	public Document update(Document item) throws ServiceException {
//		SimpleDocument document = updateModifiedDate(item);
//		return super.update(document);
//	}

//	private String getStatus(Document document) {
//		Map<String, Object> attributes = document.getAttributes();
//		if (attributes == null || !attributes.containsKey(Document.STATUS)) {
//			return null;
//		} else {
//			return attributes.get(Document.STATUS).toString();
//		}
//	}

}