package test.github.richardwilly98.esdms.api;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.esdms.DocumentImpl;
import com.github.richardwilly98.esdms.FileImpl;
import com.github.richardwilly98.esdms.VersionImpl;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.Version;

public class DocumentSerializationTest {

	private static Logger log = Logger.getLogger(DocumentSerializationTest.class);
	
	private static final ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testSerializeDeserializeDocument() throws Throwable {
		log.debug("*** testSerializeDeserializeDocument ***");
		String attributeKey = "attribut1";
		String attributeValue = "value1";
		String id = "id-" + System.currentTimeMillis();
		String name = "name-" + System.currentTimeMillis();
		Map<String, Object> attributes = newHashMap();
		attributes.put(attributeKey, attributeValue);
		DocumentTest document = new DocumentTest(new DocumentImpl.Builder().id(id).name(name).attributes(attributes).roles(null));
		log.debug(document);
		Assert.assertTrue(document.getAttributes().get(attributeKey).equals(attributeValue));
		document.setReadOnlyAttribute(DocumentImpl.AUTHOR, "richard");
		log.debug(document);
		String json = mapper.writeValueAsString(document);
		log.debug(json);
		Assert.assertNotNull(json);
		Document document2 = mapper.readValue(json, Document.class);
		Assert.assertEquals(document.getId(), document2.getId());
		Assert.assertEquals(document.getName(), document2.getName());
		Assert.assertTrue(document2.getAttributes().get(attributeKey).equals(attributeValue));
	}

	@Test
	public void testTagsDocument() throws Throwable {
		log.debug("*** testTagsDocument ***");
		String id = "id-" + System.currentTimeMillis();
		String name = "name-" + System.currentTimeMillis();
		String html = "<html><body><h1>Hello World</h1></body></html>";
		byte[] content = html.getBytes();
		Map<String, Object> attributes = newHashMap();
		Set<Version> versions = newHashSet();
		versions.add(new VersionImpl.Builder().documentId(id).file(new FileImpl.Builder().content(content).name("test.html").contentType("text/html").build()).current(true).versionId(1).build());
		DocumentTest document = new DocumentTest(new DocumentImpl.Builder().versions(versions).id(id).name(name).attributes(attributes).roles(null));
		document.addTag("java");
		Assert.assertTrue(document.getTags().contains("java"));
		document.addTag("c#");
		Assert.assertTrue(document.getTags().contains("c#"));
		document.removeTag("c#");
		Assert.assertTrue(! document.getTags().contains("c#"));
		log.debug(document);
		String json = mapper.writeValueAsString(document);
		log.debug(json);
		Assert.assertNotNull(json);
		Document document2 = mapper.readValue(json, Document.class);
		Assert.assertEquals(document.getId(), document2.getId());
		Assert.assertEquals(document.getName(), document2.getName());
		Assert.assertTrue(document2.getTags().size() == 1);
		Assert.assertTrue(document2.getTags().contains("java"));
	}

	@Test
	public void testSerializeDeserializeDocumentWithVersion() throws Throwable {
		log.debug("*** testSerializeDeserializeDocumentWithVersion ***");
		String id = "id-" + System.currentTimeMillis();
		String name = "name-" + System.currentTimeMillis();
		String html = "<html><body><h1>Hello World</h1></body></html>";
		byte[] content = html.getBytes();
		Map<String, Object> attributes = newHashMap();
		Set<Version> versions = newHashSet();
		versions.add(new VersionImpl.Builder().documentId(id).file(new FileImpl.Builder().content(content).name("test.html").contentType("text/html").build()).current(true).versionId(1).build());
		versions.add(new VersionImpl.Builder().documentId(id).file(new FileImpl.Builder().content(content).name("test.html").contentType("text/html").build()).current(false).versionId(2).parentId(1).build());
		DocumentTest document = new DocumentTest(new DocumentImpl.Builder().versions(versions).id(id).name(name).attributes(attributes).roles(null));
		document.setReadOnlyAttribute(DocumentImpl.AUTHOR, "richard");
		log.debug(document);
		String json = mapper.writeValueAsString(document);
		log.debug(json);
		Assert.assertNotNull(json);
		Document document2 = mapper.readValue(json, Document.class);
		Assert.assertNotNull(document2.getCurrentVersion());
		Assert.assertNotNull(document2.getVersions());
		// There are 2 versions
		Assert.assertEquals(document2.getVersions().size(), 2);
		// Version #1 is current
		Assert.assertEquals(document2.getCurrentVersion().getVersionId(), 1);
		// Version #1 does not have parent (ie equals 0)
		Version version = document2.getVersion(1);
		Assert.assertEquals(version.getParentId(), 0);
		// Version.documentId matches document.id
		Assert.assertEquals(version.getDocumentId(), id);
		// Version #1 is parent of Version #2
		version = document2.getVersion(2);
		Assert.assertEquals(version.getParentId(), 1);
		Assert.assertEquals(document.getId(), document2.getId());
		Assert.assertEquals(document.getName(), document2.getName());
		Assert.assertEquals(html, new String(document2.getCurrentVersion().getFile().getContent()));
	}
	
}
