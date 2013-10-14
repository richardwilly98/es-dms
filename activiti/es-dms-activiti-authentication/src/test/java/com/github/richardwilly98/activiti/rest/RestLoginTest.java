package com.github.richardwilly98.activiti.rest;

import java.io.IOException;

import org.activiti.rest.api.RestUrls;
import org.activiti.rest.api.identity.UserResponse;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.services.UserService;

public class RestLoginTest extends TestRestActivitiServerBase {

    public RestLoginTest() throws Exception {
        super();
    }

    @Test
    public void testLoginValidate() throws IOException {
        log.debug("*** testLoginValidate ***");
        ClientResource client = getAuthenticatedClient(RestUrls.createRelativeResourceUrl(RestUrls.URL_USER,
                UserService.DEFAULT_ADMIN_LOGIN));
        Representation response = client.get();
        Assert.assertEquals(Status.SUCCESS_OK, client.getResponse().getStatus());
        UserResponse user = mapper.readValue(response.getStream(), UserResponse.class);
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getId(), UserService.DEFAULT_ADMIN_LOGIN);
    }

}
