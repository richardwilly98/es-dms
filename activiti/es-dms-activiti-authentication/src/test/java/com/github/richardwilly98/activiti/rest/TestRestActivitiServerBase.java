package com.github.richardwilly98.activiti.rest;

import org.apache.log4j.Logger;
import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.resource.ClientResource;

import com.github.richardwilly98.activiti.TestRestServerWithActivitiBase;

public class TestRestActivitiServerBase extends TestRestServerWithActivitiBase {

    protected final Logger log = Logger.getLogger(getClass());
    private static Component component;
    public static int REST_PORT = 8182;

    public TestRestActivitiServerBase() throws Exception {
        super();
    }

    @Override
    public void startActiviti() throws Exception {
        super.startActiviti();
        initializeRestServer();
    }

    private void initializeRestServer() throws Exception {
        log.debug("*** initializeRestServer ***");
        component = new Component();
        // Add a new HTTP server listening on port 8182.
        component.getServers().add(Protocol.HTTP, REST_PORT);
        // component.getDefaultHost().attach(new
        // ActivitiRestServicesApplication());
        component.getDefaultHost().attach(new EsDmsActivitiRestServicesApplication());
        component.start();
    }

    @Override
    public void stopActiviti() throws Exception {
        super.stopActiviti();
        stopRestServer();
    }

    private void stopRestServer() throws Exception {
        log.debug("*** stopRestServer ***");
        component.stop();
    }

    protected ClientResource getAuthenticatedClient(String uri) {
        ClientResource client = new ClientResource("http://localhost:" + REST_PORT + "/" + uri);
        client.getCookies().add("ES_DMS_TICKET", adminToken);
//        client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, "kermit", "kermit");
        return client;
    }
}
