package com.github.richardwilly98.services;

import static org.elasticsearch.common.io.Streams.copyToStringFromClasspath;
import static org.elasticsearch.index.query.QueryBuilders.fieldQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.github.richardwilly98.api.Document;
import com.github.richardwilly98.api.exception.ServiceException;
import com.github.richardwilly98.api.services.BootstrapService;
import com.github.richardwilly98.api.services.DocumentService;
import com.google.inject.Inject;

public class DocumentProvider extends ProviderBase<Document> implements DocumentService {

	private static final String DOCUMENT_MAPPING_JSON = "/com/github/richardwilly98/services/document-mapping.json";
	private final static String type = "document";

	@Inject
	DocumentProvider(Client client, BootstrapService bootstrapService) throws ServiceException {
		super(client, bootstrapService, null, DocumentProvider.type, Document.class);
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

//	@Override
//	protected void createIndex() {
//		if (!client.admin().indices().prepareExists(index).execute()
//				.actionGet().isExists()) {
//			client.admin().indices().prepareCreate(index).execute()
//					.actionGet();
//			PutMappingResponse mappingResponse = client.admin().indices().preparePutMapping(index)
//					.setType(type).setSource(getMapping()).execute()
//					.actionGet();
//			log.debug(String.format("Mapping response acknowledged: %s", mappingResponse.isAcknowledged()));
//		}
//
//	}

	@RequiresPermissions(CREATE_PERMISSION)
	@Override
	public Document create(Document item) throws ServiceException {
		return super.create(item);
	}

	@RequiresPermissions(DELETE_PERMISSION)
	@Override
	public void delete(Document item) throws ServiceException {
		super.delete(item);
	}
	
	/* (non-Javadoc)
	 * @see com.github.richardwilly98.services.IDocumentService#getDocument(java.lang.String)
	 */
	@Override
	public Document get(String id) throws ServiceException {
		try {
			GetResponse response = client.prepareGet(index, type, id)
					.execute().actionGet();
			String json = response.getSourceAsString();
			Document document = mapper.readValue(json, Document.class);
			return document;
		} catch (Throwable t) {
			log.error("get failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
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
	
	/* (non-Javadoc)
	 * @see com.github.richardwilly98.services.IDocumentService#contentSearch(java.lang.String)
	 */
	@Override
	public List<Document> search(String criteria) throws ServiceException {
		try {
			List<Document> documents = new ArrayList<Document>();

			SearchResponse searchResponse = client.prepareSearch(index)
					.setTypes(type).setSearchType(SearchType.QUERY_AND_FETCH).setQuery(fieldQuery("file", criteria))
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
					for(Text text : hit.getHighlightFields().get(key).fragments()) {
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkout(Document document) throws ServiceException {
		// TODO Auto-generated method stub
		
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
