package test.github.richardwilly98.rest;

import static org.elasticsearch.common.io.Streams.copyToBytesFromClasspath;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.api.Document;
import com.github.richardwilly98.rest.RestDocumentService;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

public class TestRestDocumentService extends GuiceAndJettyTestBase<Document> {
//public class TestRestDocumentService extends GuiceAndJerseyTestBase<Document> {

	public TestRestDocumentService() throws Exception {
		super();
	}

	@Test
	public void testCreateDocument() throws Throwable {
		log.debug("*** testCreateDocument ***");
		try {
			String name = "test-attachment.html";
			Document document = createDocument(name, "text/html",
					"/test/github/richardwilly98/services/test-attachment.html");
			Assert.assertNotNull(document);
			log.debug("New document: " + document);
			Assert.assertEquals(document.getName(), name);
			Document document2 = get(document.getId(), Document.class,
					RestDocumentService.DOCUMENTS_PATH);
			Assert.assertEquals(document.getId(), document.getId());
			String newName = "document-" + System.currentTimeMillis();
			document2.setName(newName);
			Document document3 = update(document2, Document.class,
					RestDocumentService.DOCUMENTS_PATH);
			Assert.assertEquals(newName, document3.getName());
			delete(document.getId(), RestDocumentService.DOCUMENTS_PATH);
			document2 = get(document.getId(), Document.class,
					RestDocumentService.DOCUMENTS_PATH);
			Assert.assertNull(document2);
		} catch (Throwable t) {
			log.error("testCreateDocument fail", t);
			Assert.fail();
		}
	}

	@Test
	public void testCheckoutCheckinDocument() throws Throwable {
		log.debug("*** testCheckoutCheckinDocument ***");
		try {
			String name = "test-attachment.html";
			Document document = createDocument(name, "text/html",
					"/test/github/richardwilly98/services/test-attachment.html");
			Assert.assertNotNull(document);
			log.debug("New document: " + document);
			
			ClientResponse response = resource()
					.path(RestDocumentService.DOCUMENTS_PATH)
					.path(document.getId()).path("checkout").cookie(adminCookie)
					.type(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class);
			log.debug(String.format("status: %s", response.getStatus()));
			Assert.assertTrue(response.getStatus() == Status.NO_CONTENT
					.getStatusCode());

			Document document2 = get(document.getId(), Document.class,
					RestDocumentService.DOCUMENTS_PATH);
			log.debug("Checked-out document: " + document);
			Map<String, Object> attributes = document2.getAttributes();
			Assert.assertNotNull(attributes.get(Document.STATUS));
			Assert.assertTrue(attributes.get(Document.STATUS).equals(Document.DocumentStatus.LOCKED.getStatusCode()));
			Assert.assertNotNull(attributes.get(Document.LOCKED_BY));
			Assert.assertTrue(attributes.get(Document.LOCKED_BY).equals(adminCredential.getUsername()));
			Assert.assertNotNull(attributes.get(Document.MODIFIED_DATE));

			response = resource()
					.path(RestDocumentService.DOCUMENTS_PATH)
					.path(document.getId()).path("checkout").cookie(adminCookie)
					.type(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class);
			log.debug(String.format("status: %s", response.getStatus()));
			Assert.assertTrue(response.getStatus() == Status.CONFLICT
					.getStatusCode());
			
			response = resource()
					.path(RestDocumentService.DOCUMENTS_PATH)
					.path(document.getId()).path("checkin").cookie(adminCookie)
					.type(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class);
			log.debug(String.format("status: %s", response.getStatus()));
			Assert.assertTrue(response.getStatus() == Status.NO_CONTENT
					.getStatusCode());

			document2 = get(document.getId(), Document.class,
					RestDocumentService.DOCUMENTS_PATH);
			
			attributes = document2.getAttributes();
			Assert.assertTrue(document2.getAttributes().get(Document.STATUS).equals(Document.DocumentStatus.AVAILABLE.getStatusCode()));
			Assert.assertFalse(attributes.containsKey(Document.LOCKED_BY));

			response = resource()
					.path(RestDocumentService.DOCUMENTS_PATH)
					.path(document.getId()).path("checkin").cookie(adminCookie)
					.type(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class);
			log.debug(String.format("status: %s", response.getStatus()));
			Assert.assertTrue(response.getStatus() == Status.CONFLICT
					.getStatusCode());

		} catch (Throwable t) {
			log.error("testCheckoutCheckinDocument fail", t);
			Assert.fail();
		}
	}

	@Test()
	public void testDownloadDocument() throws Throwable {
		log.debug("*** testDownloadDocument ***");
		try {
			String name = "test-attachment.html";
			Document document = createDocument(name, "text/html",
					"/test/github/richardwilly98/services/test-attachment.html");
			Assert.assertNotNull(document);
			log.debug("New document: " + document);
			
			ClientResponse response = resource()
					.path(RestDocumentService.DOCUMENTS_PATH)
					.path(document.getId()).path("download").cookie(adminCookie)
//					.type(MediaType.APPLICATION_JSON)
					.get(ClientResponse.class);
			log.debug(String.format("status: %s", response.getStatus()));
			Assert.assertTrue(response.getStatus() == Status.OK
					.getStatusCode());
			
			log.debug("Content type: " + response.getType().getType());
			InputStream stream = response.getEntityInputStream();
			Assert.assertNotNull(stream);
			
//			Assert.assertFalse(attributes.containsKey(Document.STATUS));
//			Assert.assertFalse(attributes.containsKey(Document.LOCKED_BY));
		} catch (Throwable t) {
			log.error("testDownloadDocument fail", t);
			Assert.fail();
		}
	}

	private Document createDocument(String name, String contentType,
			String path) throws Throwable {
		String id = String.valueOf(System.currentTimeMillis());
		byte[] content = copyToBytesFromClasspath(path);
		InputStream is = new ByteArrayInputStream(content);

		// Filename of the sent stream is not relevant for this test.
//		StreamDataBodyPart streamData = new StreamDataBodyPart("file", is, name);
		FormDataMultiPart form = new FormDataMultiPart();
		form.field("name", name);
//		FormDataBodyPart p = new FormDataBodyPart(FormDataContentDisposition
//				.name("name").build(), name);
//		p.contentDisposition(ContentDisposition.type(contentType).fileName(name).size(content.length).build()); 
//		mp.bodyPart(p);
		FormDataBodyPart p = new FormDataBodyPart("file", is, MediaType.valueOf(contentType));
		form.bodyPart(p);
//		mp.bodyPart(streamData);

		ClientResponse response = resource()
				.path(RestDocumentService.DOCUMENTS_PATH)
				.path(RestDocumentService.UPLOAD_PATH).cookie(adminCookie)
				.type(MediaType.MULTIPART_FORM_DATA)
				.post(ClientResponse.class, form);
		log.debug(String.format("status: %s", response.getStatus()));
		Assert.assertTrue(response.getStatus() == Status.CREATED
				.getStatusCode());
		URI uri = response.getLocation();
		Assert.assertNotNull(uri);
		return get(uri, Document.class);
	}

	// @Test
	// public void testFindDocuments() throws Throwable {
	// log.debug("*** testFindDocuments ***");
	// ClientConfig clientConfig = new DefaultClientConfig();
	// clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
	// Boolean.TRUE);
	// Client client = Client.create(clientConfig);
	// WebResource webResource = client
	// .resource("http://localhost:8080/api/documents/search/*");
	// ClientResponse response = webResource.get(ClientResponse.class);
	// log.debug("body: " + response.getEntity(String.class));
	// log.debug("status: " + response.getStatus());
	// }

}
