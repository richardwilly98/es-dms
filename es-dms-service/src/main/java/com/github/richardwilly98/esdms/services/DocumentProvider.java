package com.github.richardwilly98.esdms.services;

import static com.google.common.collect.Sets.newHashSet;
import static org.elasticsearch.common.io.Streams.copyToStringFromClasspath;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.fieldQuery;

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
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightField;
import org.joda.time.DateTime;

import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.google.inject.Inject;

public class DocumentProvider extends ProviderBase<Document> implements
		DocumentService {

	private static final String DOCUMENT_MAPPING_JSON = "/com/github/richardwilly98/esdms/services/document-mapping.json";
	private final static String type = "document";

	@Inject
	DocumentProvider(Client client, BootstrapService bootstrapService)
			throws ServiceException {
		super(client, bootstrapService, null, DocumentProvider.type,
				Document.class);
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

	private SimpleDocument updateModifiedDate(Document document) {
		SimpleDocument sd = new SimpleDocument.Builder().document(document).build();
		DateTime now = new DateTime();
		sd.setReadOnlyAttribute(Document.MODIFIED_DATE, now.toString());
		return sd;
	}

	@RequiresPermissions(CREATE_PERMISSION)
	@Override
	public Document create(Document item) throws ServiceException {
		SimpleDocument sd = new SimpleDocument.Builder().document(item).build();
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
	public Set<Document> search(String criteria, int first, int pageSize) throws ServiceException {
		try {
			Set<Document> documents = newHashSet();

			SearchResponse searchResponse = client.prepareSearch(index)
					.setTypes(type).setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setFrom(first).setSize(pageSize)
					.setQuery(fieldQuery("file", criteria))
					.execute().actionGet();
			log.debug("totalHits: " + searchResponse.getHits().totalHits());
			log.debug(String.format("TotalHits: %s - TookInMillis: %s", searchResponse.getHits().totalHits(), searchResponse.getTookInMillis()));
			Stopwatch watch = new Stopwatch();
			watch.start();
			for (SearchHit hit : searchResponse.getHits().hits()) {
				String json = hit.getSourceAsString();
				Document document = mapper.readValue(json, Document.class);
				document.getFile().setContent(null);
				documents.add(document);
			}
			watch.stop();
			log.debug("Elapsed time to build document list " + watch.elapsed(TimeUnit.MILLISECONDS));

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
			SimpleDocument sd = new SimpleDocument.Builder().document(document).build();
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
		SimpleDocument document = updateModifiedDate(item);
		return super.update(document);
	}

	@Override
	public void checkout(Document document) throws ServiceException {
		SimpleDocument sd = new SimpleDocument.Builder().document(document).build();
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

}
