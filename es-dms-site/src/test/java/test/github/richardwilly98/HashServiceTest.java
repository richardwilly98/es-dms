package test.github.richardwilly98;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import com.github.richardwilly98.api.services.HashService;
import com.google.inject.Inject;

@Guice( modules = ServiceModule.class)
public class HashServiceTest {

	private static Logger log = Logger.getLogger(HashServiceTest.class);
	
	@Inject
	HashService service;

	@Test
	public void testHashComputing() throws Throwable {
		String hash1 = service.toBase64("secret".getBytes());
		Assert.assertNotNull(hash1);
		log.debug("hash1: " + hash1);
		String hash2 = service.toBase64("secret1".getBytes());
		log.debug("hash2: " + hash2);
		Assert.assertNotNull(hash2);
		Assert.assertNotSame(hash1, hash2);
	}
}
