package test.github.richardwilly98.esdms.services;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.elasticsearch.common.io.Streams.copyToBytesFromClasspath;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.util.Map;
import java.util.Set;

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
import com.github.richardwilly98.esdms.VersionImpl;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.Document.DocumentStatus;
import com.github.richardwilly98.esdms.api.File;
import com.github.richardwilly98.esdms.api.Permission;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.api.Version;
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
		Set<Document> documents = documentService.search(contentSearch, 0, 10);
		startCount = documents.size();
		log.info(String.format("startCount: %s", startCount));
		File file = new FileImpl.Builder().content(content).name(name)
				.contentType(contentType).build();
		Set<Version> versions = newHashSet();
		versions.add(new VersionImpl.Builder().documentId(id).file(file)
				.current(true).versionId(1).build());

		Document document = new DocumentImpl.Builder().versions(versions)
				.id(id).name(name).roles(null).build();
		Document newDocument = documentService.create(document);
		Assert.assertNotNull(newDocument);
		Assert.assertEquals(id, newDocument.getId());
		Assert.assertTrue(newDocument.hasStatus(DocumentStatus.AVAILABLE));
		log.info(String.format("New document created #%s", newDocument.getId()));
		return id;
	}

	private Document addVersion(Document document, int versionId, File file,
			int parentId) throws ServiceException {
		try {
			if (parentId == 0) {
				parentId = document.getCurrentVersion().getVersionId();
			}
			Version version = new VersionImpl.Builder()
					.documentId(document.getId()).file(file)
					.versionId(versionId).parentId(parentId).build();
			documentService.addVersion(document, version);
			Document updatedDocument = documentService.get(document.getId());
			Assert.assertNotNull(updatedDocument);
			return updatedDocument;
		} catch (ServiceException sEx) {
			Assert.fail("addVersion failed", sEx);
			throw sEx;
		}
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
		authenticationService
				.login(new CredentialImpl.Builder().username(user.getLogin())
						.password(user.getPassword()).build());
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
	public void testCreateDeleteDocument() throws Throwable {
		log.info("Start testCreateDeleteDocument ************************************");
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
		log.info("testCreateDeleteDocument: step 1");
		Assert.assertNotNull(user);
		authenticationService
				.login(new CredentialImpl.Builder().username(user.getLogin())
						.password(user.getPassword()).build());

		log.info("testCreateDeleteDocument: step 2");
		String id = createDocument("test-attachment.html", "text/html",
				"/test/github/richardwilly98/services/test-attachment.html",
				"Aliquam");
		Document document = documentService.get(id);
		log.info("testCreateDeleteDocument: step 3");
		Assert.assertNotNull(document);
		try {
			documentService.delete(document);
			Assert.fail("Cannot delete document if it has not been marked for deletion.");
		} catch (Exception e) {
			log.info(String
					.format("Document %s not deleted without having been marked for deletion. Exception raised!",
							id));
			log.info(e.getLocalizedMessage());
		}
		log.info("testCreateDeleteDocument: step 4");
		documentService.markDeleted(document);
		
		document = documentService.get(id);
		Assert.assertTrue(document.hasStatus(DocumentStatus.DELETED));

		log.info("testCreateDeleteDocument: step 5");
		documentService.delete(document);
		log.info("End testCreateDeleteDocument successfully*****************************");
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
		authenticationService
				.login(new CredentialImpl.Builder().username(user.getLogin())
						.password(user.getPassword()).build());
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
		Map<String, Object> attributes = newHashMap();
		// Document document = new DocumentImpl(id, name, null, attributes);
		Document document = new DocumentImpl.Builder().attributes(attributes)
				.id(id).name(name).roles(null).build();
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
		Assert.assertTrue(updatedDocument.getAttributes()
				.get(DocumentImpl.AUTHOR).toString().equals(author));
	}

	@Test
	public void testCreateDocumentWithCreationDate() throws Throwable {
		log.info("*** testCreateDocumentWithCreationDate ***");
		loginAdminUser();
		String id = String.valueOf(System.currentTimeMillis());
		String name = "document-" + id;
		Map<String, Object> attributes = newHashMap();
		// Document document = new DocumentImpl(id, name, null, attributes);
		Document document = new DocumentImpl.Builder().attributes(attributes)
				.id(id).name(name).roles(null).build();
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
		Map<String, Object> attributes = newHashMap();
		// Document document = new DocumentImpl(id, name, null, attributes);
		Document document = new DocumentImpl.Builder().attributes(attributes)
				.id(id).name(name).roles(null).build();
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

	@Test
	public void testCreateDocumentWithVersions() throws Throwable {
		log.info("*** testCreateDocumentWithVersions ***");
		loginAdminUser();
		String id = String.valueOf(System.currentTimeMillis());
		String name = "document-" + id;
		Map<String, Object> attributes = newHashMap();
		String html = "<html><body><h1>Hello World</h1></body></html>";
		String html2 = "<html><body><h1>Version 2</h1></body></html>";
		byte[] content = html.getBytes();
		byte[] content2 = html2.getBytes();
		Set<Version> versions = newHashSet();
		versions.add(new VersionImpl.Builder()
				.documentId(id)
				.file(new FileImpl.Builder().content(content).name("test.html")
						.contentType("text/html").build()).current(true)
				.versionId(1).build());
		Document document = new DocumentImpl.Builder().versions(versions)
				.attributes(attributes).id(id).name(name).roles(null).build();
		document.setId(id);
		Document newDocument = documentService.create(document);
		Assert.assertNotNull(newDocument);
		log.info(String.format("New document created %s", newDocument));
		Assert.assertNotNull(newDocument.getCurrentVersion());
		Assert.assertNotNull(newDocument.getVersions());
		Assert.assertTrue(newDocument.getVersions().size() == 1);
		Assert.assertTrue(newDocument.getCurrentVersion().getVersionId() == 1);

		newDocument = addVersion(newDocument, 2, new FileImpl.Builder()
				.content(content2).name("test.html").contentType("text/html")
				.build(), 0);
		// Version version2 = new VersionImpl.Builder()
		// .documentId(id)
		// .file(new FileImpl.Builder().content(content2)
		// .name("test.html").contentType("text/html").build())
		// .current(false).versionId(2).parentId(1).build();
		// documentService.addVersion(newDocument, version2);
		//
		// newDocument = documentService.get(id);
		log.info(String.format("Version #2 added to document %s", newDocument));
		Assert.assertTrue(newDocument.getVersions().size() == 2);
		Assert.assertTrue(newDocument.getCurrentVersion().getVersionId() == 2);

		// Version #1 is not current anymore
		Version v = newDocument.getVersion(1);
		Assert.assertNotNull(v);
		Assert.assertFalse(v.isCurrent());
		Assert.assertNull(v.getFile());

		// Version #2 is current and has content
		v = newDocument.getVersion(2);
		Assert.assertNotNull(v);
		Assert.assertTrue(v.isCurrent());
		Assert.assertNotNull(v.getFile());
		Assert.assertEquals(v.getFile().getContent(), content2);

		// Test retrieve archived version (not current)
		v = documentService.getVersion(newDocument, newDocument.getVersion(1)
				.getVersionId());
		Assert.assertNotNull(v);
		Assert.assertNotNull(v.getFile());
		Assert.assertEquals(v.getFile().getContent(), content);

		// Test delete version
		documentService.deleteVersion(newDocument, v);
		newDocument = documentService.get(id);
		Assert.assertTrue(newDocument.getVersions().size() == 1);
		Assert.assertTrue(newDocument.getCurrentVersion().getVersionId() == 2);
	}

	@Test
	public void testSerializeDeserializeVersions() throws Throwable {
		log.info("*** testSerializeDeserializeVersions ***");
		loginAdminUser();
		String id = String.valueOf(System.currentTimeMillis());
		String name = "document-" + id;
		Map<String, Object> attributes = newHashMap();
		String html = "<html><body><h1>Hello World</h1></body></html>";
		byte[] content = html.getBytes();
		Set<Version> versions = newHashSet();

		Version version1 = new VersionImpl.Builder()
				.documentId(id)
				.file(new FileImpl.Builder().content(content).name("test.html")
						.contentType("text/html").build()).current(true)
				.versionId(1).build();

		Version version2 = new VersionImpl.Builder()
				.documentId(id)
				.file(new FileImpl.Builder().content(content).name("test.html")
						.contentType("text/html").build()).versionId(2)
				.parentId(1).build();

		Version version3 = new VersionImpl.Builder()
				.documentId(id)
				.file(new FileImpl.Builder().content(content).name("test.html")
						.contentType("text/html").build()).versionId(3)
				.parentId(2).build();

		versions.add(version1);
		Document document = new DocumentImpl.Builder().versions(versions)
				.attributes(attributes).id(id).name(name).roles(null).build();
		document.setId(id);

		Document newDocument = documentService.create(document);
		Assert.assertNotNull(newDocument);

		newDocument = addVersion(newDocument, 2, version2.getFile(), 0);

		// documentService.addVersion(newDocument, version2);
		// newDocument = documentService.get(id);
		log.info(String.format("Document updated - %s", newDocument));

		Version v2 = newDocument.getVersion(2);
		Assert.assertNotNull(v2);
		Assert.assertTrue(v2.isCurrent());
		Assert.assertEquals(v2.getFile().getContentType(), version2.getFile()
				.getContentType());
		Assert.assertEquals(v2.getFile().getContent(), version2.getFile()
				.getContent());
		Assert.assertEquals(v2.getParentId(), version2.getParentId());
		Assert.assertEquals(v2.getDocumentId(), newDocument.getId());

		newDocument = addVersion(newDocument, 3, version3.getFile(), 0);

		// documentService.addVersion(newDocument, version3);
		// newDocument = documentService.get(id);
		log.info(String.format("Document updated - %s", newDocument));

		newDocument = documentService.get(id);
		log.info(String.format("Document updated - %s", newDocument));

		Version v3 = newDocument.getVersion(3);
		Assert.assertNotNull(v3);
		Assert.assertTrue(v3.isCurrent());
		Assert.assertEquals(v3.getFile().getContentType(), version3.getFile()
				.getContentType());
		Assert.assertEquals(v3.getFile().getContent(), version3.getFile()
				.getContent());
		Assert.assertEquals(v3.getParentId(), version3.getParentId());
		Assert.assertEquals(v3.getDocumentId(), newDocument.getId());

		v2 = newDocument.getVersion(2);
		Assert.assertNotNull(v2);
		Assert.assertFalse(v2.isCurrent());
		Assert.assertNull(v2.getFile());
		Assert.assertEquals(v2.getParentId(), version2.getParentId());
		Assert.assertEquals(v2.getDocumentId(), newDocument.getId());
	}

	@Test
	public void testAddVersion() throws Throwable {
		log.info("*** testAddVersion ***");
		loginAdminUser();
		String id = String.valueOf(System.currentTimeMillis());
		String name = "document-" + id;
		Map<String, Object> attributes = newHashMap();
		String html = "<html><body><h1>Hello World</h1></body></html>";
		byte[] content = html.getBytes();
		Set<Version> versions = newHashSet();
		versions.add(new VersionImpl.Builder()
				.documentId(id)
				.file(new FileImpl.Builder().content(content).name("test.html")
						.contentType("text/html").build()).current(true)
				.versionId(1).build());
		Document document = new DocumentImpl.Builder().versions(versions)
				.attributes(attributes).id(id).name(name).roles(null).build();
		document.setId(id);

		Document newDocument = documentService.create(document);
		Assert.assertNotNull(newDocument);

		Version version2 = new VersionImpl.Builder()
				.documentId(id)
				.file(new FileImpl.Builder().content(content).name("test.html")
						.contentType("text/html").build()).versionId(2).build();
		documentService.addVersion(newDocument, version2);
		newDocument = documentService.get(id);
		log.info(String.format("Document updated - %s", newDocument));

		Version v1 = newDocument.getVersion(1);
		Assert.assertNotNull(v1);
		Assert.assertNotNull(v1.getId());
		Assert.assertFalse(v1.isCurrent());

		Version v2 = newDocument.getVersion(2);
		Assert.assertNotNull(v2);
		Assert.assertNull(v2.getId());
		Assert.assertTrue(v2.isCurrent());
		Assert.assertEquals(v2.getParentId(), v1.getVersionId());
	}

	@Test
	public void testDeleteVersion() throws Throwable {
		log.info("*** testDeleteVersion ***");
		loginAdminUser();
		String id = String.valueOf(System.currentTimeMillis());
		String name = "document-" + id;
		Map<String, Object> attributes = newHashMap();
		String html = "<html><body><h1>Hello World</h1></body></html>";
		byte[] content = html.getBytes();
		Set<Version> versions = newHashSet();
		versions.add(new VersionImpl.Builder()
				.documentId(id)
				.file(new FileImpl.Builder().content(content).name("test.html")
						.contentType("text/html").build()).current(true)
				.versionId(1).build());
		Document document = new DocumentImpl.Builder().versions(versions)
				.attributes(attributes).id(id).name(name).roles(null).build();
		document.setId(id);

		Document newDocument = documentService.create(document);
		Assert.assertNotNull(newDocument);

		Version version2 = new VersionImpl.Builder()
				.documentId(id)
				.file(new FileImpl.Builder().content(content).name("test.html")
						.contentType("text/html").build()).versionId(2)
				.parentId(1).build();
		documentService.addVersion(newDocument, version2);
		newDocument = documentService.get(id);
		log.info(String.format("Document updated - %s", newDocument));

		Version v2 = newDocument.getVersion(2);
		Assert.assertNotNull(v2);
		Assert.assertTrue(v2.isCurrent());
		Assert.assertEquals(v2.getParentId(), 1);

		Version version3 = new VersionImpl.Builder()
				.documentId(id)
				.file(new FileImpl.Builder().content(content).name("test.html")
						.contentType("text/html").build()).versionId(3)
				.parentId(2).build();
		documentService.addVersion(newDocument, version3);
		newDocument = documentService.get(id);
		log.info(String.format("Document updated - %s", newDocument));

		// Version #3 is current and has content
		Version v3 = newDocument.getCurrentVersion();
		log.info(String.format("Version #3 - %s", v3));
		Assert.assertNotNull(v3);
		Assert.assertTrue(v3.isCurrent());
		Assert.assertEquals(v3.getParentId(), 2);

		v2 = newDocument.getVersion(2);
		log.info(String.format("Version #2 - %s", v2));
		Assert.assertNotNull(v2);
		Assert.assertFalse(v2.isCurrent());

		Assert.assertTrue(newDocument.getVersions().size() == 3);

		// Test delete version #2
		documentService.deleteVersion(newDocument, v2);
		newDocument = documentService.get(id);
		Assert.assertTrue(newDocument.getVersions().size() == 2);
		log.info(String.format(
				"Document after version #2 has been deleted - %s", newDocument));

		// Test delete current version #2
		v3 = newDocument.getVersion(3);
		documentService.deleteVersion(newDocument, v3);
		newDocument = documentService.get(id);
		log.info(String.format(
				"Document after version #3 has been deleted - %s", newDocument));
		Assert.assertTrue(newDocument.getVersions().size() == 1);
		Assert.assertTrue(newDocument.getCurrentVersion().getVersionId() == 1);
	}

	@Test
	public void testChangeCurrentVersion() throws Throwable {
		log.info("*** testChangeCurrentVersion ***");
		loginAdminUser();
		String id = String.valueOf(System.currentTimeMillis());
		String name = "document-" + id;
		Map<String, Object> attributes = newHashMap();
		String html = "<html><body><h1>Hello World</h1></body></html>";
		byte[] content = html.getBytes();
		Set<Version> versions = newHashSet();
		versions.add(new VersionImpl.Builder()
				.documentId(id)
				.file(new FileImpl.Builder().content(content).name("test.html")
						.contentType("text/html").build()).current(true)
				.versionId(1).build());
		Document document = new DocumentImpl.Builder().versions(versions)
				.attributes(attributes).id(id).name(name).roles(null).build();
		document.setId(id);

		Document newDocument = documentService.create(document);
		Assert.assertNotNull(newDocument);

		Version version2 = new VersionImpl.Builder()
				.documentId(id)
				.file(new FileImpl.Builder().content(content).name("test.html")
						.contentType("text/html").build()).versionId(2)
				.parentId(1).build();
		documentService.addVersion(newDocument, version2);
		newDocument = documentService.get(id);
		log.info(String.format("Document updated - %s", newDocument));
		Version v2 = newDocument.getVersion(2);
		Assert.assertTrue(v2.isCurrent());

		documentService.setCurrentVersion(newDocument, 1);
		newDocument = documentService.get(id);
		log.info(String.format(
				"Document updated (set current version #1) - %s", newDocument));
		Version v1 = newDocument.getVersion(1);
		Assert.assertTrue(v1.isCurrent());
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
					.startObject("bool").startArray("must").startObject()
					.startObject("queryString").field("query", "XXXX")
					.array("fields", "_all", "file").endObject().endObject()
					.startObject().startObject("queryString")
					.field("query", "id").field("default_field", "id")
					.endObject().endObject().endArray().endObject().endObject()
					.endObject().string();
			log.debug("query: " + query);
		} catch (Throwable t) {
			log.error("testJson failed", t);
			Assert.fail();
		}
	}
}
