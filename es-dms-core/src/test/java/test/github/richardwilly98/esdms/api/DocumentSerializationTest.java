package test.github.richardwilly98.esdms.api;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.esdms.DocumentImpl;
import com.github.richardwilly98.esdms.FileImpl;
import com.github.richardwilly98.esdms.api.Document;

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
		String html = "<html><body><h1>Hello World</h1></body></html>";
		byte[] content = html.getBytes();
		Map<String, Object> attributes = newHashMap();
		attributes.put(attributeKey, attributeValue);
		DocumentTest document = new DocumentTest(new DocumentImpl(id, name, new FileImpl(content, "test.html", "text/html"), attributes));
		document.setReadOnlyAttribute(DocumentImpl.AUTHOR, "richard");
		log.debug(document);
		String json = mapper.writeValueAsString(document);
		log.debug(json);
		Assert.assertNotNull(json);
		Document document2 = mapper.readValue(json, Document.class);
		Assert.assertEquals(document.getId(), document2.getId());
		Assert.assertEquals(document.getName(), document2.getName());
		Assert.assertTrue(document2.getAttributes().get(attributeKey).equals(attributeValue));
		Assert.assertEquals(html, new String(document2.getFile().getContent()));
	}

	@Test
	public void testTagsDocument() throws Throwable {
		log.debug("*** testTagsDocument ***");
		String id = "id-" + System.currentTimeMillis();
		String name = "name-" + System.currentTimeMillis();
		String html = "<html><body><h1>Hello World</h1></body></html>";
		byte[] content = html.getBytes();
		Map<String, Object> attributes = newHashMap();
		Document document = new DocumentTest(new DocumentImpl(id, name, new FileImpl(content, "test.html", "text/html"), attributes));
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
		Assert.assertEquals(html, new String(document2.getFile().getContent()));
	}
}
