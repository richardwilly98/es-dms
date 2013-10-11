package com.github.richardwilly98.activiti;

import java.lang.reflect.Method;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.test.TestHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.github.richardwilly98.esdms.rest.TestRestServerBase;

public class TestRestServerWithActivitiBase extends TestRestServerBase {

    protected String configurationResource = "activiti.cfg.xml";
    protected String deploymentId = null;

    protected static ProcessEngine processEngine;
    protected static RepositoryService repositoryService;
    protected static RuntimeService runtimeService;
    protected static TaskService taskService;
    protected static HistoryService historyService;
    protected static IdentityService identityService;
    protected static ManagementService managementService;
    protected static FormService formService;

    // protected ActivitiMockSupport mockSupport;

    public TestRestServerWithActivitiBase() throws Exception {
        super();
    }

    public TestRestServerWithActivitiBase(String configurationResource) throws Exception {
        this();
        this.configurationResource = configurationResource;
    }

    @Override
    public void setUp() throws Throwable {
        super.setUp();
        startActiviti();
    }

    @Override
    public void tearDown() throws Throwable {
        try {
        super.tearDown();
        stopActiviti();
        } catch (Throwable t) {
            log.error("afterClass failed", t);
            throw t;
        }
    }

    protected void initializeProcessEngine() {
        log.debug(String.format("*** initializeProcessEngine - %s ***", configurationResource));
        processEngine = TestHelper.getProcessEngine(configurationResource);
    }

    protected void initializeServices() {
        log.debug("*** initializeServices ***");
        repositoryService = processEngine.getRepositoryService();
        runtimeService = processEngine.getRuntimeService();
        taskService = processEngine.getTaskService();
        historyService = processEngine.getHistoryService();
        identityService = processEngine.getIdentityService();
        managementService = processEngine.getManagementService();
        formService = processEngine.getFormService();
    }

    protected void initializeMockSupport() {
        // if (ActivitiMockSupport.isMockSupportPossible(processEngine)) {
        // this.mockSupport = new ActivitiMockSupport(processEngine);
        // }
    }

    protected void configureProcessEngine() {
        /** meant to be overridden */
    }

    private void startActiviti() {
        log.info("*** Start Activiti Engine ***");
        if (processEngine == null) {
            initializeProcessEngine();
            initializeServices();
        }

        // if (mockSupport == null) {
        // initializeMockSupport();
        // }

        // Allow for mock configuration
        configureProcessEngine();

        // Allow for annotations
        // try {
        // TestHelper.annotationMockSupportSetup(Class.forName(description.getClassName()),
        // description.getMethodName(), mockSupport);
        // } catch (ClassNotFoundException e) {
        // throw new
        // ActivitiException("Programmatic error: could not instantiate " +
        // description.getClassName(), e);
        // }

        // try {
        // deploymentId = TestHelper.annotationDeploymentSetUp(processEngine,
        // Class.forName(description.getClassName()),
        // description.getMethodName());
        // } catch (ClassNotFoundException e) {
        // throw new
        // ActivitiException("Programmatic error: could not instantiate " +
        // description.getClassName(), e);
        // }
    }

    private void stopActiviti() {
        log.info("*** Stop Activiti Engine ***");
        finished();

    }

    protected void finished(/* Description description */) {
        /*
         * // Remove the test deployment try {
         * TestHelper.annotationDeploymentTearDown(processEngine, deploymentId,
         * Class.forName(description.getClassName()),
         * description.getMethodName()); } catch (ClassNotFoundException e) {
         * throw new
         * ActivitiException("Programmatic error: could not instantiate " +
         * description.getClassName(), e); }
         * 
         * // Reset internal clock ClockUtil.reset();
         * 
         * // Rest mocks if (mockSupport != null) {
         * TestHelper.annotationMockSupportTeardown(mockSupport); }
         */
    }

    
    @BeforeMethod
    public void beforeMethod(Method method) {
        log.debug("beforeMethod - " + method.getDeclaringClass() + " - " + method.getName());
        deploymentId = TestHelper.annotationDeploymentSetUp(processEngine, method.getDeclaringClass(), method.getName());
    }
    
    @AfterMethod
    public void afterMethod(Method method) {
        TestHelper.annotationDeploymentTearDown(processEngine, deploymentId, method.getDeclaringClass(), method.getName());
    }

}
