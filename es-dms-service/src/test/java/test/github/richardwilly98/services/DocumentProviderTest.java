package test.github.richardwilly98.services;

import static org.elasticsearch.common.io.Streams.copyToBytesFromClasspath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.UnauthorizedException;
import org.elasticsearch.common.Base64;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.api.Credential;
import com.github.richardwilly98.api.Document;
import com.github.richardwilly98.api.File;
import com.github.richardwilly98.api.Permission;
import com.github.richardwilly98.api.Role;
import com.github.richardwilly98.api.User;

/*
 * https://github.com/shairontoledo/elasticsearch-attachment-tests/blob/master/src/test/java/net/hashcode/esattach/AttachmentTest.java
 */
public class DocumentProviderTest extends ProviderTestBase {

	private void testCreateDocument(String name, String contentType,
			String path, String contentSearch) throws Throwable {
		String id = String.valueOf(System.currentTimeMillis());
		byte[] content = copyToBytesFromClasspath(path);
		String encodedContent = Base64.encodeBytes(content);
		int startCount = 0;
		List<Document> documents = documentService.getList(contentSearch);
		startCount = documents.size();
		log.info(String.format("startCount: %s", startCount));
		Document document = new Document();
		File file = new File(encodedContent, name, contentType);
		document.setFile(file);
		document.setId(id);
		Document newDocument = documentService.create(document);
		Assert.assertNotNull(newDocument);
		log.info(String.format("New document created #%s", newDocument.getId()));
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
		authenticationService.login(new Credential(user.getLogin(), user
				.getPassword()));
		// TODO: Not sure the reason PDF parsing does not work anymore. To be investigated...
//		testCreateDocument("lorem.pdf", "application/pdf",
//				"/test/github/richardwilly98/services/lorem.pdf",
//				"Lorem ipsum dolor");
		testCreateDocument("test-attachment.html", "text/html",
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
		authenticationService.login(new Credential(user.getLogin(), user
				.getPassword()));
		try {
			testCreateDocument(
					"test-attachment.html",
					"text/html",
					"/test/github/richardwilly98/services/test-attachment.html",
					"Aliquam");
			Assert.fail("Should not be authorized to create document");
		} catch (UnauthorizedException uEx) {
		}
	}

	// @Test
	// public void testHighlightDocument() throws Throwable {
	// log.info("Start testHighlightDocument");
	// String id = String.valueOf(System.currentTimeMillis());
	// String name = "lorem.pdf";
	// String contentType = "application/pdf";
	// byte[] content =
	// copyToBytesFromClasspath("/test/github/richardwilly98/services/lorem.pdf");
	// String encodedContent = Base64.encodeBytes(content);
	// DocumentProvider provider = new DocumentProvider();
	// int startCount = 0;
	// List<Document> documents = provider.getDocuments("Lorem ipsum dolor");
	// startCount = documents.size();
	// Document document = new Document();
	// File file = new File(encodedContent, name, contentType);
	// document.setFile(file);
	// document.setId(id);
	// String newId = provider.createDocument(document);
	// log.info(String.format("New document created #%s", newId));
	// documents = provider.getDocuments("Lorem ipsum dolor");
	// Assert.assertEquals(documents.size() - startCount, 1);
	// }

	@Test
	public void testCreateDocumentWithAuthor() throws Throwable {
		log.info("*** testCreateDocumentWithAuthor ***");
		loginAdminUser();
		String id = String.valueOf(System.currentTimeMillis());
		String name = "document-" + id;
		Map<String, Object> attributes = new HashMap<String, Object>();
		Document document = new Document(id, name, null, attributes);
		document.setId(id);
		Document newDocument = documentService.create(document);
		Assert.assertNotNull(newDocument);
		log.info(String.format("New document created %s", newDocument));
		attributes = newDocument.getAttributes();
		Assert.assertTrue(attributes != null && attributes.size() > 1);
		Assert.assertTrue(attributes.containsKey(Document.AUTHOR));
		String author = attributes.get(Document.AUTHOR).toString();
		Assert.assertTrue(!author.isEmpty());
		// Test ignore set read-only attribute
		newDocument.setAttribute(Document.AUTHOR, author + "-" + System.currentTimeMillis());
		Document updatedDocument = documentService.update(newDocument);
		Assert.assertTrue(updatedDocument.getAttributes().get(Document.AUTHOR).toString().equals(author));
	}

	@Test
	public void testCreateDocumentWithCreationDate() throws Throwable {
		log.info("*** testCreateDocumentWithCreationDate ***");
		loginAdminUser();
		String id = String.valueOf(System.currentTimeMillis());
		String name = "document-" + id;
		Map<String, Object> attributes = new HashMap<String, Object>();
		Document document = new Document(id, name, null, attributes);
		document.setId(id);
		Document newDocument = documentService.create(document);
		Assert.assertNotNull(newDocument);
		log.info(String.format("New document created %s", newDocument));
		attributes = newDocument.getAttributes();

		Assert.assertTrue(attributes != null && attributes.size() > 1);
		Assert.assertTrue(attributes.containsKey(Document.CREATION_DATE));
		log.info(attributes.get(Document.CREATION_DATE));
		DateTimeFormatter formatter = ISODateTimeFormat
				.dateOptionalTimeParser();
		DateTime newDate = formatter.parseDateTime(attributes.get(
				Document.CREATION_DATE).toString());
		log.info(String.format("Attribute %s - %s", Document.CREATION_DATE, newDate));
	}

	@Test
	public void testCheckinCheckoutDocument() throws Throwable {
		log.info("*** testCheckinCheckoutDocument ***");
		loginAdminUser();
		String id = String.valueOf(System.currentTimeMillis());
		String name = "document-" + id;
		Map<String, Object> attributes = new HashMap<String, Object>();
		Document document = new Document(id, name, null, attributes);
		document.setId(id);
		Document newDocument = documentService.create(document);
		Assert.assertNotNull(newDocument);
		log.info(String.format("New document created %s", newDocument));
		log.debug(String.format("Checkout document %s", newDocument.getId()));
		documentService.checkout(newDocument);
		newDocument = documentService.get(newDocument.getId());
		log.trace(String.format("Document checked-out %s", newDocument));
		
		attributes = newDocument.getAttributes();
		Assert.assertNotNull(attributes.get(Document.STATUS));
		Assert.assertTrue(attributes.get(Document.STATUS).equals(Document.DocumentStatus.LOCKED.getStatusCode()));
		Assert.assertNotNull(attributes.get(Document.LOCKED_BY));
		Assert.assertTrue(attributes.get(Document.LOCKED_BY).equals(adminCredential.getUsername()));
		Assert.assertNotNull(attributes.get(Document.MODIFIED_DATE));
		
		log.debug(String.format("Checkin document %s", newDocument.getId()));
		documentService.checkin(newDocument);
		newDocument = documentService.get(newDocument.getId());
		log.trace(String.format("Document checked-in %s", newDocument));
		Assert.assertFalse(attributes.containsKey(Document.STATUS));
		Assert.assertFalse(attributes.containsKey(Document.LOCKED_BY));
	}
}
