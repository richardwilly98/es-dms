package com.github.richardwilly98.services;

import static org.elasticsearch.common.io.Streams.copyToStringFromClasspath;
import static org.elasticsearch.index.query.QueryBuilders.fieldQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.joda.time.DateTime;

import com.github.richardwilly98.api.Document;
import com.github.richardwilly98.api.exception.ServiceException;
import com.github.richardwilly98.api.services.BootstrapService;
import com.github.richardwilly98.api.services.DocumentService;
import com.google.inject.Inject;

public class DocumentProvider extends ProviderBase<Document> implements
		DocumentService {

	private static final String DOCUMENT_MAPPING_JSON = "/com/github/richardwilly98/services/document-mapping.json";
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
		SimpleDocument sd = new SimpleDocument(document);
		DateTime now = new DateTime();
		sd.setReadOnlyAttribute(Document.MODIFIED_DATE, now.toString());
		return sd;
	}
	
	@RequiresPermissions(CREATE_PERMISSION)
	@Override
	public Document create(Document item) throws ServiceException {
//		SimpleDocument sd = updateModifiedDate(item);
		SimpleDocument sd = new SimpleDocument(item);
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

	@Override
	public Set<Document> getItems(String name) throws ServiceException {
		try {
			Set<Document> documents = new HashSet<Document>();

			SearchResponse searchResponse = client.prepareSearch(index)
					.setTypes(type).setQuery(QueryBuilders.queryString(name))
					.addHighlightedField("file").execute().actionGet();
			log.debug("totalHits: " + searchResponse.getHits().totalHits());
			for (SearchHit hit : searchResponse.getHits().hits()) {
				log.debug(String.format("HighlightFields: %s", hit
						.getHighlightFields().size()));
				for (String key : hit.getHighlightFields().keySet()) {
					log.debug(String.format("Highlight key: %s", key));
				}
				String json = hit.getSourceAsString();
				Document document = mapper.readValue(json, Document.class);
				documents.add(document);
			}

			return documents;
		} catch (Throwable t) {
			log.error("getItems failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.richardwilly98.services.BaseService#search(java.lang.String)
	 */
	@Override
	public List<Document> search(String criteria) throws ServiceException {
		try {
			List<Document> documents = new ArrayList<Document>();

			SearchResponse searchResponse = client.prepareSearch(index)
					.setTypes(type).setSearchType(SearchType.QUERY_AND_FETCH)
					.setQuery(fieldQuery("file", criteria))
					.addHighlightedField("file").execute().actionGet();
			log.debug("totalHits: " + searchResponse.getHits().totalHits());
			for (SearchHit hit : searchResponse.getHits().hits()) {
				String highlight = null;
				if (log.isTraceEnabled()) {
					log.trace(String.format("HighlightFields: %s", hit
							.getHighlightFields().size()));
				}
				for (String key : hit.getHighlightFields().keySet()) {
					if (log.isTraceEnabled()) {
						log.trace(String.format("Highlight key: %s", key));
					}
					for (Text text : hit.getHighlightFields().get(key)
							.fragments()) {
						log.debug(String.format("Fragment: %s", text));
						highlight = text.toString();
					}
				}
				String json = hit.getSourceAsString();
				Document document = mapper.readValue(json, Document.class);
				document.getFile().setContent(null);
				if (highlight != null) {
					document.getFile().setHighlight(highlight);
				}
				documents.add(document);
			}

			return documents;
		} catch (Throwable t) {
			log.error("search failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	@Override
	public void checkin(Document document) throws ServiceException {
//		SimpleDocument sd = updateModifiedDate(document);
		SimpleDocument sd = new SimpleDocument(document);
//		DateTime now = new DateTime();
		sd.removeReadOnlyAttribute(Document.STATUS);
//		sd.setReadOnlyAttribute(Document.MODIFIED_DATE, now.toString());
		sd.setReadOnlyAttribute(Document.AUTHOR, getCurrentUser());
		sd.removeReadOnlyAttribute(Document.LOCKED_BY);
		//document.removeAttribute(Document.LOCKED_BY);
		document = update(sd);
//		document.removeAttribute(Document.STATUS);
//		DateTime now = new DateTime();
//		document.setAttribute(Document.MODIFIED_DATE, now.toString());
//		document.removeAttribute(Document.LOCKED_BY);
//		document = update(document);
	}

	@Override
	public Document update(Document item) throws ServiceException {
		SimpleDocument document = updateModifiedDate(item);
		return super.update(document);
	}
	
	@Override
	public void checkout(Document document) throws ServiceException {
		SimpleDocument sd = new SimpleDocument(document);
		if (document.getAttributes() != null && document.getAttributes().containsKey(Document.STATUS)) {
			if (document.getAttributes().get(Document.STATUS)
					.equals(Document.DocumentStatus.LOCKED.getStatusCode())) {
				throw new ServiceException(String.format(
						"Document %s already locked.", document.getId()));
			}
		}
		sd.setReadOnlyAttribute(Document.STATUS, Document.DocumentStatus.LOCKED.getStatusCode());
//		document.setAttribute(Document.STATUS,
//				Document.DocumentStatus.LOCKED.getStatusCode());
//		DateTime now = new DateTime();
//		sd.setReadOnlyAttribute(Document.MODIFIED_DATE, now.toString());
//		document.setAttribute(Document.MODIFIED_DATE, now.toString());
		sd.setReadOnlyAttribute(Document.LOCKED_BY, getCurrentUser());
//		document.setAttribute(Document.LOCKED_BY, getCurrentUser());
		update(sd);
//		update(document);
	}

	@Override
	public boolean disabled(Document document) throws ServiceException {
		try {
			if (document == null) {
				throw new IllegalArgumentException("document is null");
			}
			//
		} catch (Throwable t) {
			log.error("getDocuments failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
		return document.isDisabled();
	}

	@Override
	public void disable(Document document, boolean b) throws ServiceException {
		try {
			if (document == null) {
				throw new IllegalArgumentException("document is null");
			}
			//
		} catch (Throwable t) {
			log.error("getDocuments failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
		document.setDisabled(b);
	}

}
