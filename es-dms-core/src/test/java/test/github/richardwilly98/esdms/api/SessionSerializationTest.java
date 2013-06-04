package test.github.richardwilly98.esdms.api;

import java.util.Date;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.esdms.SessionImpl;

public class SessionSerializationTest {

	private static Logger log = Logger.getLogger(SessionSerializationTest.class);
	
	private static final ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testSerializeDeserializeSession() throws Throwable {
		log.debug("*** testSerializeDeserializeSession ***");
		String id = "session-" + System.currentTimeMillis();
		Date createTime = new Date();
		Date lastAccessTime = new Date();
		boolean active = true;
		SessionImpl session = new SessionImpl.Builder().id(id).createTime(createTime).lastAccessTime(lastAccessTime).active(active).build();
		log.debug(session);
		String json = mapper.writeValueAsString(session);
		log.debug(json);
		Assert.assertNotNull(json);
		SessionImpl session2 = mapper.readValue(json, SessionImpl.class);
		log.debug(session2);
		Assert.assertEquals(session.getId(), session2.getId());
	}
}
