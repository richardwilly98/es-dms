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
		log.info("Start testCannotCreateDocument");
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
		String id = String.valueOf(System.currentTimeMillis());
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put(Document.AUTHOR, "richard");
		Document document = new Document(id, id, null, attributes);
		document.setId(id);
		Document newDocument = documentService.create(document);
		Assert.assertNotNull(newDocument);
		log.info(String.format("New document created #%s", newDocument.getId()));
		// document = documentService.get(newId);
		attributes = document.getAttributes();
		Assert.assertTrue(attributes != null && attributes.size() == 1);
		Assert.assertTrue(attributes.containsKey(Document.AUTHOR)
				&& attributes.get(Document.AUTHOR).equals("richard"));
	}

	@Test
	public void testCreateDocumentWithCreationDate() throws Throwable {
		String id = String.valueOf(System.currentTimeMillis());
		Map<String, Object> attributes = new HashMap<String, Object>();
		DateTime now = new DateTime();
		attributes.put(Document.CREATION_DATE, now.toString());
		Document document = new Document(id, id, null, attributes);
		document.setId(id);
		Document newDocument = documentService.create(document);
		Assert.assertNotNull(newDocument);
		log.info(String.format("New document created #%s", newDocument.getId()));
		attributes = document.getAttributes();
		Assert.assertTrue(attributes != null && attributes.size() == 1);
		Assert.assertTrue(attributes.containsKey(Document.CREATION_DATE));
		log.info(attributes.get(Document.CREATION_DATE));
		DateTimeFormatter formatter = ISODateTimeFormat
				.dateOptionalTimeParser();
		DateTime newDate = formatter.parseDateTime(attributes.get(
				Document.CREATION_DATE).toString());
		Assert.assertEquals(now, newDate);
	}

}
