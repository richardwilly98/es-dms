package test.github.richardwilly98.esdms.services;

import static org.elasticsearch.common.io.Streams.copyToBytesFromClasspath;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.UnauthorizedException;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.DocumentImpl;
import com.github.richardwilly98.esdms.FileImpl;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.Permission;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.exception.ServiceException;

/*
 * https://github.com/shairontoledo/elasticsearch-attachment-tests/blob/master/src/test/java/net/hashcode/esattach/AttachmentTest.java
 */
public class DocumentProviderTest extends ProviderTestBase {

	private String createDocument(String name, String contentType, String path,
			String contentSearch) throws Throwable {
		String id = String.valueOf(System.currentTimeMillis());
		byte[] content = copyToBytesFromClasspath(path);
		int startCount = 0;
		List<Document> documents = documentService.getList(contentSearch);
		startCount = documents.size();
		log.info(String.format("startCount: %s", startCount));
		FileImpl file = new FileImpl(content, name, contentType);
		Document document = new DocumentImpl(id, name, file, null);
		Document newDocument = documentService.create(document);
		Assert.assertNotNull(newDocument);
		Assert.assertEquals(id, newDocument.getId());
		log.info(String.format("New document created #%s", newDocument.getId()));
		return id;
	}

	@Test
	public void testCreateDocument() throws Throwable {
		log.info("Start testCreateDocument");
		User user = null;
		for (User u : users.values()) {
			for (Role role : u.getRoles()) {
				for (Permission permission : role.getPermissions()) {
					if ("document:create".equals(permission.getId())) {
						user = u;
						break;
					}
				}
			}
		}
		Assert.assertNotNull(user);
		authenticationService.login(new CredentialImpl(user.getLogin(), user
				.getPassword()));
		// TODO: Not sure the reason PDF parsing does not work anymore. To be
		// investigated...
		// testCreateDocument("lorem.pdf", "application/pdf",
		// "/test/github/richardwilly98/services/lorem.pdf",
		// "Lorem ipsum dolor");
		createDocument("test-attachment.html", "text/html",
				"/test/github/richardwilly98/services/test-attachment.html",
				"Aliquam");
	}

	@Test
	public void testCannotCreateDocument() throws Throwable {
		log.info("*** testCannotCreateDocument ***");
		User user = null;
		for (User u : users.values()) {
			for (Role role : u.getRoles()) {
				boolean found = false;
				for (Permission permission : role.getPermissions()) {
					if ("document:create".equals(permission.getId())) {
						found = true;
						break;
					}
				}
				if (!found) {
					user = u;
				}
			}
		}
		Assert.assertNotNull(user);
		authenticationService.login(new CredentialImpl(user.getLogin(), user
				.getPassword()));
		try {
			createDocument(
					"test-attachment.html",
					"text/html",
					"/test/github/richardwilly98/services/test-attachment.html",
					"Aliquam");
			Assert.fail("Should not be authorized to create document");
		} catch (UnauthorizedException uEx) {
		}
	}

	@Test(enabled = false)
	public void testHighlightDocument() throws Throwable {
		log.info("Start testHighlightDocument");
		loginAdminUser();
		String id = createDocument("test-attachment.html", "text/html",
				"/test/github/richardwilly98/services/test-attachment.html",
				"Aliquam");

		Document document = documentService.get(id);
		String preview = documentService.preview(document, "Aliquam", 0);
		Assert.assertNotNull(preview);
		Assert.assertTrue(preview.contains("Aliquam"));
		log.info(String.format("document preview: %s", preview));
	}

	@Test
	public void testCreateDocumentWithAuthor() throws Throwable {
		log.info("*** testCreateDocumentWithAuthor ***");
		loginAdminUser();
		String id = String.valueOf(System.currentTimeMillis());
		String name = "document-" + id;
		Map<String, Object> attributes = new HashMap<String, Object>();
		Document document = new DocumentImpl(id, name, null, attributes);
		document.setId(id);
		Document newDocument = documentService.create(document);
		Assert.assertNotNull(newDocument);
		log.info(String.format("New document created %s", newDocument));
		attributes = newDocument.getAttributes();
		Assert.assertTrue(attributes != null && attributes.size() > 1);
		Assert.assertTrue(attributes.containsKey(DocumentImpl.AUTHOR));
		String author = attributes.get(DocumentImpl.AUTHOR).toString();
		Assert.assertTrue(!author.isEmpty());
		// Test ignore set read-only attribute
		newDocument.setAttribute(DocumentImpl.AUTHOR,
				author + "-" + System.currentTimeMillis());
		Document updatedDocument = documentService.update(newDocument);
		Assert.assertTrue(updatedDocument.getAttributes().get(DocumentImpl.AUTHOR)
				.toString().equals(author));
	}

	@Test
	public void testCreateDocumentWithCreationDate() throws Throwable {
		log.info("*** testCreateDocumentWithCreationDate ***");
		loginAdminUser();
		String id = String.valueOf(System.currentTimeMillis());
		String name = "document-" + id;
		Map<String, Object> attributes = new HashMap<String, Object>();
		Document document = new DocumentImpl(id, name, null, attributes);
		document.setId(id);
		Document newDocument = documentService.create(document);
		Assert.assertNotNull(newDocument);
		log.info(String.format("New document created %s", newDocument));
		attributes = newDocument.getAttributes();

		Assert.assertTrue(attributes != null && attributes.size() > 1);
		Assert.assertTrue(attributes.containsKey(DocumentImpl.CREATION_DATE));
		log.info(attributes.get(DocumentImpl.CREATION_DATE));
		DateTimeFormatter formatter = ISODateTimeFormat
				.dateOptionalTimeParser();
		DateTime newDate = formatter.parseDateTime(attributes.get(
				DocumentImpl.CREATION_DATE).toString());
		log.info(String.format("Attribute %s - %s", DocumentImpl.CREATION_DATE,
				newDate));
	}

	@Test
	public void testCheckinCheckoutDocument() throws Throwable {
		log.info("*** testCheckinCheckoutDocument ***");
		loginAdminUser();
		String id = String.valueOf(System.currentTimeMillis());
		String name = "document-" + id;
		Map<String, Object> attributes = new HashMap<String, Object>();
		Document document = new DocumentImpl(id, name, null, attributes);
		document.setId(id);
		Document newDocument = documentService.create(document);
		Assert.assertNotNull(newDocument);
		log.info(String.format("New document created %s", newDocument));
		log.debug(String.format("Checkout document %s", newDocument.getId()));
		documentService.checkout(newDocument);
		newDocument = documentService.get(newDocument.getId());
		log.trace(String.format("Document checked-out %s", newDocument));
		try {
			documentService.checkout(newDocument);
			Assert.fail("Should not be authorized to check-out document twice");
		} catch (ServiceException sEx) {
		}

		attributes = newDocument.getAttributes();
		Assert.assertNotNull(attributes.get(DocumentImpl.STATUS));
		Assert.assertTrue(attributes.get(DocumentImpl.STATUS).equals(
				DocumentImpl.DocumentStatus.LOCKED.getStatusCode()));
		Assert.assertNotNull(attributes.get(DocumentImpl.LOCKED_BY));
		Assert.assertTrue(attributes.get(DocumentImpl.LOCKED_BY).equals(
				adminCredential.getUsername()));
		Assert.assertNotNull(attributes.get(DocumentImpl.MODIFIED_DATE));

		log.debug(String.format("Checkin document %s", newDocument.getId()));
		documentService.checkin(newDocument);
		newDocument = documentService.get(newDocument.getId());
		log.trace(String.format("Document checked-in %s", newDocument));
		Assert.assertTrue(newDocument.getAttributes().get(DocumentImpl.STATUS)
				.equals(DocumentImpl.DocumentStatus.AVAILABLE.getStatusCode()));
		Assert.assertFalse(newDocument.getAttributes().containsKey(
				DocumentImpl.LOCKED_BY));

		try {
			documentService.checkin(newDocument);
			Assert.fail("Should not be authorized to check-out document twice");
		} catch (ServiceException sEx) {
		}
	}

	@Test(enabled = false)
	public void testJson() {
		try {
			XContentBuilder mapping = jsonBuilder().startObject()
					.startObject("XXXXXXX").startObject("properties")
					.startObject("content").field("type", "attachment")
					.endObject().startObject("filename")
					.field("type", "string").endObject()
					.startObject("contentType").field("type", "string")
					.endObject().startObject("md5").field("type", "string")
					.endObject().startObject("length").field("type", "long")
					.endObject().startObject("chunkSize").field("type", "long")
					.endObject().endObject().endObject().endObject();
			log.info("Mapping: " + mapping.string());
			String query = jsonBuilder().startObject().startObject("query")
					.startObject("bool").startArray("must")
					.startObject()
						.startObject("queryString")
							.field("query", "XXXX")
							.array("fields", "_all", "file")
						.endObject()
					.endObject()
					.startObject()
						.startObject("queryString")
							.field("query", "id")
							.field("default_field", "id")
						.endObject()
					.endObject()
					.endArray().endObject()
					// .startArray("must")
					// .startObject()
					// .startObject("queryString")
					// .field("query", criteria)
					// .startArray("fields")
					// .value("_all")
					// .value("file")
					// .endArray()
					// .endObject()
					// .endObject()
					// .startObject()
					// .startObject("queryString")
					// .field("query", document.getId())
					// .field("default_field" , "id")
					// .endObject()
					// .endObject()
					// .endArray()
					.endObject().endObject().string();

			log.debug("query: " + query);
		} catch (Throwable t) {
			log.error("testJson failed", t);
			Assert.fail();
		}
	}
}
