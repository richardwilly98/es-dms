package test.github.richardwilly98.esdms.services;

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

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.ParameterImpl;
import com.github.richardwilly98.esdms.api.Parameter;
import com.github.richardwilly98.esdms.api.Parameter.ParameterType;
import com.github.richardwilly98.esdms.search.api.SearchResult;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.services.ParameterService;
import com.google.inject.Inject;

public class ParameterProviderTest extends ProviderTestBase {

    @Inject
    ParameterService parameterService;

    private Parameter createParameter(String id, String name, ParameterType type, Map<String, Object> attributes) throws Throwable {
        Parameter parameter = parameterService.create(new ParameterImpl.Builder().id(id).name(name).type(type).attributes(attributes)
                .build());
        Assert.assertNotNull(parameter);
        Parameter parameter2 = parameterService.get(id);
        Assert.assertNotNull(parameter2);
        Assert.assertEquals(parameter, parameter2);
        return parameter2;
    }

    @Test
    public void testCreateParameter() throws Throwable {
        log.info("Start testCreateParameter");
        loginAdminUser();
        String id = "param-1";
        Map<String, Object> attributes = newHashMap();
        attributes.put("key-1", "value-1");
        Parameter parameter = createParameter(id, id, ParameterType.SYSTEM, attributes);
        Assert.assertEquals(id, parameter.getId());
        Assert.assertEquals(ParameterType.SYSTEM, parameter.getType());

        parameter = createParameter(id, id, ParameterType.USER, attributes);
        Assert.assertEquals(id, parameter.getId());
        Assert.assertEquals(ParameterType.USER, parameter.getType());

        parameter = createParameter(id, id, ParameterType.CUSTOM, attributes);
        Assert.assertEquals(id, parameter.getId());
        Assert.assertEquals(ParameterType.CUSTOM, parameter.getType());
    }

    @Test
    public void testDeleteParameter() throws Throwable {
        log.info("Start testDeleteParameter");
        String id = "param-1";
        Map<String, Object> attributes = newHashMap();
        attributes.put("key-1", "value-1");
        Parameter parameter = createParameter(id, id, ParameterType.SYSTEM, attributes);
        parameterService.delete(parameter);
        parameter = parameterService.get(id);
        Assert.assertNull(parameter);
    }

    @Test
    public void testUpdateParameter() throws Throwable {
        String id = "param-1";
        Map<String, Object> attributes = newHashMap();
        attributes.put("key-1", "value-1");
        createParameter(id, id, ParameterType.SYSTEM, attributes);
        Parameter parameter2 = parameterService.get(id);
        parameter2.setName("new-name");
        parameter2.setAttribute("key-1", "new-value-1");
        parameter2.setAttribute("key-2", "value-2");
        Parameter parameter3 = parameterService.update(parameter2);
        Assert.assertEquals(parameter2, parameter3);
        Assert.assertEquals(parameter3.getAttributes().size(), 2);
        Assert.assertTrue(parameter3.getAttributes().containsKey("key-1"));
        Assert.assertTrue(parameter3.getAttributes().containsKey("key-2"));
    }

    @Test
    public void testFindParametersByType() throws Throwable {
        log.info("Start testFindParametersByType");
        SearchResult<Parameter> parameters = parameterService.findByType(ParameterType.SYSTEM, 0, 20);
        Assert.assertNotNull(parameters);
        long total = parameters.getTotalHits();
        for (Parameter parameter : parameters.getItems()) {
            Assert.assertEquals(parameter.getType(), ParameterType.SYSTEM);
            log.trace(parameter);
        }

        String id = "param-1";
        Map<String, Object> attributes = newHashMap();
        attributes.put("key-1", "value-1");
        Parameter parameter = createParameter(id, id, ParameterType.SYSTEM, attributes);
        parameters = parameterService.findByType(ParameterType.SYSTEM, 0, 20);
        Assert.assertNotNull(parameters);
        Assert.assertEquals(parameters.getTotalHits(), total + 1);
        Assert.assertTrue(parameters.getItems().contains(parameter));

        parameters = parameterService.findByType(ParameterType.CUSTOM, 0, 20);
        Assert.assertNotNull(parameters);
        total = parameters.getTotalHits();
        id = "param-1";
        attributes = newHashMap();
        attributes.put("key-1", "value-1");
        Parameter parameter2 = createParameter(id, id, ParameterType.CUSTOM, attributes);
        parameters = parameterService.findByType(ParameterType.CUSTOM, 0, 20);
        Assert.assertNotNull(parameters);
        Assert.assertEquals(parameters.getTotalHits(), total + 1);
        Assert.assertTrue(parameters.getItems().contains(parameter2));
        Assert.assertFalse(parameters.getItems().contains(parameter));
    }

    @Test
    public void testGetParameterValue() throws Throwable {
        log.info("Start testGetParameterValue");
        String id = "id-" + System.currentTimeMillis();
        String name = "name-" + System.currentTimeMillis();
        ParameterType type = ParameterType.SYSTEM;
        Map<String, Object> attributes = newHashMap();
        attributes.put("key-1", "value-1");
        Parameter parameter = createParameter(id, name, type, attributes);
        Assert.assertNotNull(parameter);
        Object object = parameterService.getParameterValue(id);
        Assert.assertEquals(parameter, object);
        object = parameterService.getParameterValue(id + ".id");
        Assert.assertEquals(id, object);
        object = parameterService.getParameterValue(id + ".name");
        Assert.assertEquals(name, object);
        object = parameterService.getParameterValue(id + ".type");
        Assert.assertEquals(type, object);
        object = parameterService.getParameterValue(id + ".attributes");
        Assert.assertEquals(attributes, object);
        object = parameterService.getParameterValue(id + ".attributes(key-1)");
        Assert.assertEquals(attributes.get("key-1"), object);
        object = parameterService.getParameterValue(id + ".attributes(key-2)");
        Assert.assertNull(object);
        try {
            object = parameterService.getParameterValue(id + ".dummyProperty");
            Assert.fail("This test should not fail - property dummayProperty does not exist.");
        } catch (ServiceException sEx) {

        }
        parameterService.delete(parameter);
        object = parameterService.getParameterValue(id);
        Assert.assertNull(object);
    }
    
    @Test
    public void testSetParameterValue() throws Throwable {
        log.info("Start testSetParameterValue");
        String id = "id-" + System.currentTimeMillis();
        String name = "name-" + System.currentTimeMillis();
        ParameterType type = ParameterType.SYSTEM;
        Map<String, Object> attributes = newHashMap();
        attributes.put("key-1", "value-1");
        Parameter parameter = createParameter(id, name, type, attributes);
        Assert.assertNotNull(parameter);
        name = "name-" + System.currentTimeMillis();
        parameter = parameterService.setParameterValue(parameter, "name", name);
        Assert.assertEquals(parameter.getName(), name);
        parameter = parameterService.setParameterValue(parameter, "attributes(key-2)", "value-2");
        Assert.assertEquals(parameter.getAttributes().get("key-2"), "value-2");
    }
}
