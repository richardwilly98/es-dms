package test.github.richardwilly98.esdms.api;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.DocumentImpl;
import com.github.richardwilly98.esdms.FileImpl;
import com.github.richardwilly98.esdms.SearchResultImpl;
import com.github.richardwilly98.esdms.VersionImpl;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.api.Version;

public class DocumentTest extends DocumentImpl {

	private static final long serialVersionUID = 1L;

	DocumentTest() {
		this(null);
	}
	
	public DocumentTest(Builder builder) {
		super(builder);
	}
	
	@Override
	protected void setReadOnlyAttribute(String name, Object value) {
		super.setReadOnlyAttribute(name, value);
	}
	
	private Map<String, DocumentTest> getTestDocuments(int number){
		
		Map<String, DocumentTest> docs = new HashMap<String, DocumentTest>();
		
		for (int i = 0; i < number; i++){
			String attributeKey = "attribute: " + i;
			String attributeValue = "value: " + i;
			String id = "id-" + i + " @time: "+ System.currentTimeMillis();
			String name = "name-" + i + " @time: " + System.currentTimeMillis();
			String html = "<html><body><h1>This is document number: " + i + "</h1></body></html>";
			byte[] content = html.getBytes();
			Map<String, Object> attributes = newHashMap();
			attributes.put(attributeKey, attributeValue);
//			DocumentTest document = new DocumentTest(new DocumentImpl(id, name, new FileImpl(content, "test.html", "text/html"), attributes));
			Set<Version> versions = newHashSet();
			versions.add(new VersionImpl.Builder().documentId(id).file(new FileImpl.Builder().content(content).name("test" + i + ".html").contentType("text/html").build()).current(true).versionId(1).build());
			DocumentTest document = new DocumentTest(new DocumentImpl.Builder().versions(versions).id(id).name(name).attributes(attributes).roles(null));
//			DocumentTest document = new DocumentTest(new DocumentImpl.Builder().file(new FileImpl.Builder().content(content).name("test" + i + ".html").contentType("text/html").build()).id(id).name(name).attributes(attributes).roles(null));
			docs.put("" + i, document);
		}
		return docs;
	}
	
	private void setStatus( DocumentStatus status){
		super.setReadOnlyAttribute(STATUS, status);
	}
	
	@Test
	public void testDocumentStatus() throws Throwable {
		log.debug("*** testDocumentStatus tests: starting ***");
		log.debug("*** ********************************** ***");
		
		Map<String, DocumentTest> documents = getTestDocuments(10);				//documentService.search("*", 0, 10);
		log.info(String.format("Document count: %s", documents.size()));
		Assert.assertNotNull(documents);
		
		DocumentTest doc = documents.get("" + 1);
		Assert.assertNotNull(doc);
		Assert.assertTrue(doc.hasStatus(Document.DocumentStatus.AVAILABLE));
		
		doc.setStatus(Document.DocumentStatus.LOCKED);
		Assert.assertTrue(doc.hasStatus(Document.DocumentStatus.LOCKED));
		
		doc.setStatus(Document.DocumentStatus.AVAILABLE);
		Assert.assertTrue(doc.hasStatus(Document.DocumentStatus.AVAILABLE));
		
		doc.setStatus(Document.DocumentStatus.DELETED);
		Assert.assertTrue(doc.hasStatus(Document.DocumentStatus.DELETED));
		
		log.debug("*** ************************************************ ***");
		log.debug("*** testDocumentStatus tests: completed successfully ***");
	}
}
