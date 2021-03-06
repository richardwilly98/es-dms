package com.github.richardwilly98.activiti.rest;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.activiti.engine.ProcessEngines;
import org.activiti.engine.impl.ProcessEngineImpl;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.test.TestHelper;
import org.apache.log4j.Logger;
import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.resource.ClientResource;

import com.github.richardwilly98.activiti.EsDmsServerWithActivitiBase;

/*
 * Inspire from https://github.com/Activiti/Activiti/blob/master/modules/activiti-rest/src/test/java/org/activiti/rest/service/BaseRestTestCase.java
 */
public class EsDmsServerWithRestActivitiServerBase extends EsDmsServerWithActivitiBase {

    protected final Logger log = Logger.getLogger(getClass());
    private static Component component;
    public static int REST_PORT = 8182;

    protected ProcessEngineConfigurationImpl processEngineConfiguration;

    public EsDmsServerWithRestActivitiServerBase() throws Exception {
        super();
    }

    @Override
    public void startActiviti() throws Exception {
        log.info("*** startActiviti ***");
        initializeRestServer();
        initializeProcessEngine();
        initializeServices();
    }

    private void initializeRestServer() throws Exception {
        log.info("*** initializeRestServer ***");
        component = new Component();
        // Add a new HTTP server listening on port 8182.
        component.getServers().add(Protocol.HTTP, REST_PORT);
        // component.getDefaultHost().attach(new
        // ActivitiRestServicesApplication());
        component.getDefaultHost().attach(new EsDmsActivitiRestServicesApplication());
        component.start();
    }

    private void initializeProcessEngine() {
        processEngine = TestHelper.getProcessEngine(configurationResource);
        ProcessEnginesRest.init();
        ProcessEngines.registerProcessEngine(processEngine);
    }

    private void initializeServices() {
        processEngineConfiguration = ((ProcessEngineImpl) processEngine).getProcessEngineConfiguration();
        repositoryService = processEngine.getRepositoryService();
        runtimeService = processEngine.getRuntimeService();
        taskService = processEngine.getTaskService();
        formService = processEngine.getFormService();
        historyService = processEngine.getHistoryService();
        identityService = processEngine.getIdentityService();
        managementService = processEngine.getManagementService();
    }

    @Override
    public void stopActiviti() throws Exception {
        log.info("*** stopActiviti ***");
        stopProcessEngine();
        stopRestServer();
    }

    private void stopProcessEngine() {
        log.info("*** stopProcessEngine ***");
        if (processEngine != null) {
            ProcessEngines.unregister(processEngine);
            processEngine.close();
            processEngine = null;
        }
    }

    private void stopRestServer() throws Exception {
        log.info("*** stopRestServer ***");
        component.stop();
    }

    protected URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost:" + REST_PORT + "/").build();
    }

    protected ClientResource getAuthenticatedClient(String uri) {
        ClientResource client = new ClientResource(getBaseURI() + uri);
        client.getCookies().add("ES_DMS_TICKET", adminToken);
        // client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, "kermit",
        // "kermit");
        return client;
    }
}
