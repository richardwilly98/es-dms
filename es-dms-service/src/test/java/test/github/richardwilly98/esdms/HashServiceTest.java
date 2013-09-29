package test.github.richardwilly98.esdms;

/*
 * #%L
 * es-dms-service
 * %%
 * Copyright (C) 2013 es-dms
 * %%
 * Copyright 2012-2013 Richard Louapre
 * 
 * This file is part of ES-DMS.
 * 
 * The current version of ES-DMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * ES-DMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import test.github.richardwilly98.esdms.inject.ProviderModule;

import com.github.richardwilly98.esdms.services.HashService;
import com.google.inject.Inject;

@Guice(modules = ProviderModule.class)
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
