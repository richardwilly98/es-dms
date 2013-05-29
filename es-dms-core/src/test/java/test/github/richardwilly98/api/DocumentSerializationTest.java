package test.github.richardwilly98.api;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.api.Document;

//@Guice(modules = ProviderModule.class)
public class DocumentSerializationTest {

	private static Logger log = Logger.getLogger(DocumentSerializationTest.class);
	
	private static final ObjectMapper mapper = new ObjectMapper();
//	@Inject
//	HashService service;

	@Test
	public void testHashComputing() throws Throwable {
		String id = "id-" + System.currentTimeMillis();
		String name = "name-" + System.currentTimeMillis();
		Map<String, Object> attributes = newHashMap();
		attributes.put("attribut1", "value1");
		DocumentTest document = new DocumentTest(new Document(id, name, null, attributes));
		document.setReadOnlyAttribute(Document.AUTHOR, "richard");
		log.debug(document);
		String json = mapper.writeValueAsString(document);
		log.debug(json);
		Assert.assertNotNull(json);
		DocumentTest document2 = mapper.readValue(json, DocumentTest.class);
		Assert.assertEquals(document.getId(), document2.getId());
		Assert.assertEquals(document.getName(), document2.getName());
		Assert.assertEquals(document.getAttributes(), document2.getAttributes());
//		String hash1 = service.toBase64("secret".getBytes());
//		Assert.assertNotNull(hash1);
//		log.debug("hash1: " + hash1);
//		String hash2 = service.toBase64("secret1".getBytes());
//		log.debug("hash2: " + hash2);
//		Assert.assertNotNull(hash2);
//		Assert.assertNotSame(hash1, hash2);
	}
}
