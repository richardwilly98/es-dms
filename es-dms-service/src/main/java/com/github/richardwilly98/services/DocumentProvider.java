package com.github.richardwilly98.services;

import static org.elasticsearch.common.io.Streams.copyToStringFromClasspath;
import static org.elasticsearch.index.query.QueryBuilders.fieldQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.github.richardwilly98.api.Document;
import com.github.richardwilly98.api.services.DocumentService;
import com.github.richardwilly98.api.exception.ServiceException;
import com.google.inject.Inject;

public class DocumentProvider extends ProviderBase implements DocumentService {

	private static Logger log = Logger.getLogger(DocumentProvider.class);
	private static final String DOCUMENT_MAPPING_JSON = "/com/github/richardwilly98/services/document-mapping.json";
	private final static String index = "test-documents";
	private final static String type = "document";

	@Inject
	DocumentProvider(Client client) {
		super(client);
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
			log.error("getDocument failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.services.IDocumentService#getDocuments(java.lang.String)
	 */
	@Override
	public List<Document> getDocuments(String name) throws ServiceException {
		try {
			List<Document> documents = new ArrayList<Document>();

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
			log.error("getDocuments failed", t);
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
			log.error("getDocuments failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}


	/* (non-Javadoc)
	 * @see com.github.richardwilly98.services.IDocumentService#createDocument(com.github.richardwilly98.Document)
	 */
	@Override
	public String create(Document document) throws ServiceException {
		try {
			if (document.getId() == null) {
				document.setId(generateUniqueId(document));
			}
			String json;
			json = mapper.writeValueAsString(document);
//			log.trace(json);
			IndexResponse response = client.prepareIndex(index, type)
					.setId(document.getId()).setSource(json).execute()
					.actionGet();
			log.trace(String.format("Index: %s - Type: %s - Id: %s",
					response.getIndex(), response.getType(), response.getId()));
			client.admin().indices().refresh(new RefreshRequest(index))
					.actionGet();
			return response.getId();
		} catch (Throwable t) {
			log.error("createDocument failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	private String generateUniqueId(Document document) {
		return super.generateUniqueId();
	}

//	private XContentBuilder getMapping() throws IOException {
//		XContentBuilder mapping = jsonBuilder()
//				.startObject()
//					.startObject(type)
//						.startObject("properties")
//							.startObject("file")
//								.field("type", "attachment")
//								.startObject("fields")
//									.startObject("file")
//										.field("term_vector", "with_positions_offsets")
//										.field("store", "yes")
//									.endObject()
//								.endObject()
//					// .startObject("filename").field("type", "string").endObject()
//					// .startObject("contentType").field("type",
//					// "string").endObject()
//							.endObject()
//						.endObject()
//					.endObject()
//				.endObject();
//		log.info(String.format("Mapping: %s", mapping.string()));
//		return mapping;
//	}
	
	private String getMapping() {
		try {
			return copyToStringFromClasspath(DOCUMENT_MAPPING_JSON);
		} catch (IOException ioEx) {
			log.error("getMapping failed", ioEx);
			return null;
		}
	}

	protected void createIndex() {
		if (!client.admin().indices().prepareExists(index).execute()
				.actionGet().exists()) {
			client.admin().indices().prepareCreate(index).execute()
					.actionGet();
			PutMappingResponse mappingResponse = client.admin().indices().preparePutMapping(index)
					.setType(type).setSource(getMapping()).execute()
					.actionGet();
			log.debug(String.format("Mapping response acknowledged: %s", mappingResponse.acknowledged()));
			// Force index to be refreshed.
			client.admin().indices().refresh(new RefreshRequest(index))
					.actionGet();
		}

	}

	@Override
	public void delete(Document document) throws ServiceException {
		try {
		if (document == null) {
			throw new IllegalArgumentException("document is null");
		}
			client.prepareDelete(index, type, document.getId());
		} catch (Throwable t) {
			log.error("getDocuments failed", t);
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
}
