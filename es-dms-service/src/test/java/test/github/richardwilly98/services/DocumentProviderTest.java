package test.github.richardwilly98.services;

import static org.elasticsearch.common.io.Streams.copyToBytesFromClasspath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.elasticsearch.common.Base64;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.github.richardwilly98.Document;
import com.github.richardwilly98.File;
import com.github.richardwilly98.services.DocumentProvider;
import com.google.inject.Guice;
import com.google.inject.Injector;

/*
 * https://github.com/shairontoledo/elasticsearch-attachment-tests/blob/master/src/test/java/net/hashcode/esattach/AttachmentTest.java
 */
@Test
public class DocumentProviderTest {

	private static Logger log = Logger.getLogger(DocumentProviderTest.class);

	@BeforeSuite
	public void beforeSuite() throws Exception {
	}

	@BeforeClass
	public void setupServer() {
	}

	@AfterClass
	public void closeServer() {
	}

	protected DocumentProvider getDocumentProvider() {
		Injector injector = Guice.createInjector(new ProviderModule());
		return injector.getInstance(DocumentProvider.class);
	}
	
	private void testCreateDocument(String name, String contentType, String path, String contentSearch) throws Throwable {
		DocumentProvider provider = getDocumentProvider();
		String id = String.valueOf(System.currentTimeMillis());
		byte[] content = copyToBytesFromClasspath(path);
		String encodedContent = Base64.encodeBytes(content);
		int startCount = 0;
		List<Document> documents = provider.getDocuments(contentSearch);
		startCount = documents.size();
		log.info(String.format("startCount: %s", startCount));
		Document document = new Document();
		File file = new File(encodedContent, name, contentType);
		document.setFile(file);
		document.setId(id);
		String newId = provider.createDocument(document);
		log.info(String.format("New document created #%s", newId));
		document = provider.getDocument(newId);
		Assert.assertNotNull(document);
		// TODO: How to load mapper-attachments plugin for unit test?
//		documents = provider.getDocuments(contentSearch);
//		documents = provider.contentSearch(contentSearch);
//		log.info(String.format("Documents count: %s", documents.size()));
//		Assert.assertEquals(documents.size() - startCount, 1);
	}

	@Test
	public void testCreateDocument() throws Throwable {
		log.info("Start testCreateDocument");
		testCreateDocument("lorem.pdf", "application/pdf", "/test/github/richardwilly98/services/lorem.pdf", "Lorem ipsum dolor");
		testCreateDocument("test-attachment.html", "text/html", "/test/github/richardwilly98/services/test-attachment.html", "Aliquam");
	}
	
//	@Test
//	public void testHighlightDocument() throws Throwable {
//		log.info("Start testHighlightDocument");
//		String id = String.valueOf(System.currentTimeMillis());
//		String name = "lorem.pdf";
//		String contentType = "application/pdf";
//		byte[] content = copyToBytesFromClasspath("/test/github/richardwilly98/services/lorem.pdf");
//		String encodedContent = Base64.encodeBytes(content);
//		DocumentProvider provider = new DocumentProvider();
//		int startCount = 0;
//		List<Document> documents = provider.getDocuments("Lorem ipsum dolor");
//		startCount = documents.size();
//		Document document = new Document();
//		File file = new File(encodedContent, name, contentType);
//		document.setFile(file);
//		document.setId(id);
//		String newId = provider.createDocument(document);
//		log.info(String.format("New document created #%s", newId));
//		documents = provider.getDocuments("Lorem ipsum dolor");
//		Assert.assertEquals(documents.size() - startCount, 1);
//	}
	
	@Test
	public void testCreateDocumentWithAuthor() throws Throwable {
		DocumentProvider provider = getDocumentProvider();
		String id = String.valueOf(System.currentTimeMillis());
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put(Document.AUTHOR, "richard");
		Document document = new Document(id, null, attributes);
		document.setId(id);
		String newId = provider.createDocument(document);
		log.info(String.format("New document created #%s", newId));
		document = provider.getDocument(newId);
		Assert.assertNotNull(document);
		attributes = document.getAttributes();
		Assert.assertTrue(attributes != null && attributes.size() == 1);
		Assert.assertTrue(attributes.containsKey(Document.AUTHOR) && attributes.get(Document.AUTHOR).equals("richard"));
	}
	
	@Test
	public void testCreateDocumentWithCreationDate() throws Throwable {
		DocumentProvider provider = getDocumentProvider();
		String id = String.valueOf(System.currentTimeMillis());
		Map<String, Object> attributes = new HashMap<String, Object>();
		DateTime now = new DateTime();
		attributes.put(Document.CREATION_DATE, now.toString());
		Document document = new Document(id, null, attributes);
		document.setId(id);
		String newId = provider.createDocument(document);
		log.info(String.format("New document created #%s", newId));
		document = provider.getDocument(newId);
		Assert.assertNotNull(document);
		attributes = document.getAttributes();
		Assert.assertTrue(attributes != null && attributes.size() == 1);
		Assert.assertTrue(attributes.containsKey(Document.CREATION_DATE));
		log.info(attributes.get(Document.CREATION_DATE));
		DateTimeFormatter formatter = ISODateTimeFormat.dateOptionalTimeParser();
		DateTime newDate = formatter.parseDateTime(attributes.get(Document.CREATION_DATE).toString()); 
		Assert.assertEquals(now, newDate);
	}

}
