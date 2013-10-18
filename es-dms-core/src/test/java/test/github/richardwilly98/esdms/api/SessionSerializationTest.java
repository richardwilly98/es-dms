package test.github.richardwilly98.esdms.api;

/*
 * #%L
 * es-dms-core
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

import static com.google.common.collect.Maps.newHashMap;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.esdms.ParameterImpl;
import com.github.richardwilly98.esdms.SessionImpl;
import com.github.richardwilly98.esdms.api.Parameter;
import com.github.richardwilly98.esdms.api.Parameter.ParameterType;

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

    @Test
    public void testSerializeDeserializeParameter() throws Throwable {
        log.debug("*** testSerializeDeserializeParameter ***");
        String id = "user-" + System.currentTimeMillis();
        String name = id;
        Map<String, Object> attributes = newHashMap();
        attributes.put("key-1", "value-1");
        attributes.put("key-2", new Date().getTime());
        Parameter parameter1 = new ParameterImpl.Builder().id(id).name(name).attributes(attributes).type(ParameterType.USER).build();
        log.debug(parameter1);
        String json = mapper.writeValueAsString(parameter1);
        log.debug(json);
        Assert.assertNotNull(json);
        Parameter parameter2 = mapper.readValue(json, Parameter.class);
        log.debug(parameter2);
        Assert.assertEquals(parameter1, parameter2);
    }
}
