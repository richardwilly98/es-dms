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
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import com.github.richardwilly98.esdms.rest.TestRestServerBase;

/*
 * Inspire from https://github.com/Activiti/Activiti/blob/master/modules/activiti-engine/src/main/java/org/activiti/engine/test/ActivitiTestCase.java
 */
public class EsDmsServerWithActivitiBase extends TestRestServerBase {

    protected String configurationResource = "activiti.cfg.xml";
    protected String deploymentId = null;

    protected ProcessEngine processEngine;
    protected RepositoryService repositoryService;
    protected RuntimeService runtimeService;
    protected TaskService taskService;
    protected HistoryService historyService;
    protected IdentityService identityService;
    protected ManagementService managementService;
    protected FormService formService;

    // protected ActivitiMockSupport mockSupport;

    public EsDmsServerWithActivitiBase() throws Exception {
        super();
    }

    public EsDmsServerWithActivitiBase(String configurationResource) throws Exception {
        this();
        this.configurationResource = configurationResource;
    }

    @BeforeClass
    public final void setUpActiviti() throws Throwable {
        log.debug("*** setUpActiviti ***");
        startActiviti();
    }

    @AfterClass
    public final void tearDownActiviti() throws Throwable {
        log.debug("*** tearDownActiviti ***");
        try {
             stopActiviti();
        } catch (Throwable t) {
            log.error("tearDownActiviti failed", t);
            throw t;
        }
    }

    private void initializeProcessEngine() {
        log.debug(String.format("*** initializeProcessEngine - %s ***", configurationResource));
        processEngine = TestHelper.getProcessEngine(configurationResource);
    }

    private void initializeServices() {
        log.debug("*** initializeServices ***");
        repositoryService = processEngine.getRepositoryService();
        runtimeService = processEngine.getRuntimeService();
        taskService = processEngine.getTaskService();
        historyService = processEngine.getHistoryService();
        identityService = processEngine.getIdentityService();
        managementService = processEngine.getManagementService();
        formService = processEngine.getFormService();
    }

//    private void initializeMockSupport() {
//         if (ActivitiMockSupport.isMockSupportPossible(processEngine)) {
//         this.mockSupport = new ActivitiMockSupport(processEngine);
//         }
//    }

    protected void configureProcessEngine() {
        /** meant to be overridden */
    }

    public void startActiviti() throws Exception {
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

    public void stopActiviti() throws Exception {
        log.info("*** Stop Activiti Engine ***");
        finished();

    }

    private void finished(/* Description description */) {
        if (processEngine != null) {
            processEngine.close();
            processEngine = null;
        }
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
