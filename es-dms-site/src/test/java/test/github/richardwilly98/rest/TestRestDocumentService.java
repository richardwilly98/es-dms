package test.github.richardwilly98.rest;

import static org.elasticsearch.common.io.Streams.copyToBytesFromClasspath;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.elasticsearch.common.Base64;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.api.Document;
import com.github.richardwilly98.api.File;
import com.github.richardwilly98.rest.RestDocumentService;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.StreamDataBodyPart;

public class TestRestDocumentService extends GuiceAndJettyTestBase<Document> {

	private static final String UPLOAD_PATH = "upload";
	private static final String DOCUMENTS_PATH = RestDocumentService.DOCUMENTS_PATH;

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
			// User user2 = getUser(user1.getId());
			// Assert.assertEquals(user1.getId(), user2.getId());
			// String newName = "user-2-" + System.currentTimeMillis();
			// user2.setName(newName);
			// User user3 = updateUser(user2);
			// Assert.assertEquals(newName, user3.getName());
			// deleteUser(user1.getId());
			// user2 = getUser(user1.getId());
			// Assert.assertNull(user2);
		} catch (Throwable t) {
			log.error("testCreateDocument fail", t);
			Assert.fail();
		}
	}

	private Document getDocument(String name, String contentType, String path)
			throws Throwable {
		String id = String.valueOf(System.currentTimeMillis());
		byte[] content = copyToBytesFromClasspath(path);
		String encodedContent = Base64.encodeBytes(content);
		Document document = new Document();
		File file = new File(encodedContent, name, contentType);
		document.setFile(file);
		document.setId(id);
		return document;
	}

	protected Document createDocument(String name, String contentType,
			String path) throws Throwable {
		String id = String.valueOf(System.currentTimeMillis());
		byte[] content = copyToBytesFromClasspath(path);
		InputStream is = new ByteArrayInputStream(content);

		// Filename of the sent stream is not relevant for this test.
		StreamDataBodyPart streamData = new StreamDataBodyPart("file", is, name);
		FormDataMultiPart mp = new FormDataMultiPart();
		FormDataBodyPart p = new FormDataBodyPart(FormDataContentDisposition
				.name("name").build(), name);
		mp.bodyPart(p);
		mp.bodyPart(streamData);

		ClientResponse response = resource().path(DOCUMENTS_PATH)
				.path(UPLOAD_PATH).cookie(adminCookie)
				.type(MediaType.MULTIPART_FORM_DATA)
				.post(ClientResponse.class, mp);
		log.debug(String.format("status: %s", response.getStatus()));
		Assert.assertTrue(response.getStatus() == Status.CREATED
				.getStatusCode());
		URI uri = response.getLocation();
		Assert.assertNotNull(uri);
		return getItem(uri, Document.class);
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
