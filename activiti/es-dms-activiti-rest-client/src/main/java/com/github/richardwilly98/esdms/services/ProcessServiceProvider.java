package com.github.richardwilly98.esdms.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;

import com.github.richardwilly98.activiti.rest.api.RestExternalResource;
import com.github.richardwilly98.activiti.rest.api.RestProcessDefinition;
import com.github.richardwilly98.activiti.rest.api.RestProcessInstance;
import com.github.richardwilly98.activiti.rest.api.RestSearchResult;
import com.github.richardwilly98.activiti.rest.api.RestTask;
import com.github.richardwilly98.activiti.rest.client.RestProcessDefinitionServiceClient;
import com.github.richardwilly98.activiti.rest.client.RestProcessInstanceServiceClient;
import com.github.richardwilly98.activiti.rest.client.RestTaskServiceClient;
import com.github.richardwilly98.esdms.ParameterImpl;
import com.github.richardwilly98.esdms.api.ItemBase;
import com.github.richardwilly98.esdms.api.Parameter;
import com.github.richardwilly98.esdms.api.Parameter.ParameterType;
import com.github.richardwilly98.esdms.bpm.api.ProcessDefinition;
import com.github.richardwilly98.esdms.bpm.api.ProcessInstance;
import com.github.richardwilly98.esdms.bpm.api.Task;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.util.ProcessServiceUtils;

public class ProcessServiceProvider implements ProcessService {

    private static final String ACTIVITI_APPLICATION_NAME = "Activiti Settings";
    private static final String ACTIVITI_APPLICATION_ID = "activiti";
    private static final String REST_URL = "rest.url";
    public static final String ACTIVITI_REST_URL = "http://localhost:8080/activiti-rest/service";
    public static final String ES_DMS_CATEGORY = "es-dms";

    private final static Logger log = Logger.getLogger(ProcessServiceProvider.class);

    private final ParameterService parameterService;
    private RestProcessDefinitionServiceClient processDefinitionService;
    private RestProcessInstanceServiceClient processInstanceService;
    private RestTaskServiceClient taskService;

    @Inject
    ProcessServiceProvider(final ParameterService parameterService) {
        this.parameterService = parameterService;
        initializeServices();
    }

    private void initializeServices() {
        Parameter parameter = null;
        String activitiUrl = ACTIVITI_REST_URL;
        try {
            parameter = parameterService.get(ACTIVITI_APPLICATION_ID);
            if (parameter == null) {
                Map<String, Object> attributes = newHashMap();
                attributes.put(REST_URL, ACTIVITI_REST_URL);
                parameter = new ParameterImpl.Builder().id(ACTIVITI_APPLICATION_ID).name(ACTIVITI_APPLICATION_NAME)
                        .type(ParameterType.SYSTEM).attributes(attributes).build();
                parameterService.create(parameter);
            } else {
                Map<String, Object> attributes = parameter.getAttributes();
                if (attributes != null && attributes.containsKey(REST_URL)) {
                    activitiUrl = attributes.get(REST_URL).toString();
                }
            }
        } catch (ServiceException e) {
            log.info("Cannot get activiti parameter.", e);
        }
        log.info(String.format("Using REST Activiti: %s", activitiUrl));
        URI uri = URI.create(activitiUrl);
        processDefinitionService = new RestProcessDefinitionServiceClient(uri);
        processInstanceService = new RestProcessInstanceServiceClient(uri);
        taskService = new RestTaskServiceClient(uri);
    }

    protected String getCurrentToken() {
        try {
            log.trace("*** getCurrentToken ***");
            Subject currentSubject = SecurityUtils.getSubject();
            if (log.isTraceEnabled()) {
                log.trace(String.format("currentSubject.isAuthenticated(): %s", currentSubject.isAuthenticated()));
                log.trace(String.format("Principal: %s", currentSubject.getPrincipal()));
            }
            if (currentSubject.getPrincipal() == null) {
                throw new AuthenticationException("Principal is null. Unauthorize request");
            }
            if (currentSubject.getSession() == null) {
                throw new AuthenticationException("Session is null. Unauthorize request");
            }
            String token = currentSubject.getSession().getId().toString();
            log.trace(String.format("Current token: %s", token));
            return token;
        } catch (Throwable t) {
            throw new AuthenticationException();
        }
    }

    @Override
    public ProcessInstance startProcessInstance(ProcessDefinition processDefinition) throws ServiceException {
        log.debug("*** startProcessInstance ***");
        checkNotNull(processDefinition);
        String token = getCurrentToken();
        processInstanceService.setToken(token);
        RestProcessInstance processInstance = processInstanceService.startProcessInstance(processDefinition.getId());
        checkNotNull(processInstance);
        return ProcessServiceUtils.convertToProcessInstance(processInstance);
    }

    @Override
    public Set<ProcessDefinition> getProcessDefinitions() throws ServiceException {
        log.debug("*** getProcessDefinitions ***");
        try {
            String token = getCurrentToken();
            processDefinitionService.setToken(token);
            RestSearchResult<RestProcessDefinition> processDefinitions = processDefinitionService
                    .getProcessDefinitionsByCategory(ES_DMS_CATEGORY);
            if (processDefinitions == null) {
                throw new ServiceException("No process definitions found for category " + ES_DMS_CATEGORY);
            }
            return convertRestProcessDefinitions(processDefinitions);
        } catch (Throwable t) {
            log.error("getProcessDefinitions failed", t);
            throw new ServiceException(t.getLocalizedMessage());
        }
    }

    @Override
    public ProcessDefinition getProcessDefinition(String id) throws ServiceException {
        log.debug(String.format("*** getProcessDefinition - %s ***", id));
        String token = getCurrentToken();
        processDefinitionService.setToken(token);
        RestProcessDefinition processDefinition = processDefinitionService.getProcessDefinition(id);
        if (processDefinition != null) {
            return ProcessServiceUtils.convertToProcessDefinition(processDefinition);
        } else {
            throw new ServiceException(String.format("No process definition found for %s.", id));
        }
    }

    @Override
    public ProcessInstance getProcessInstance(String id) throws ServiceException {
        log.debug(String.format("*** getProcessInstance - %s ***", id));
        String token = getCurrentToken();
        processInstanceService.setToken(token);
        RestProcessInstance processInstance = processInstanceService.getProcessInstance(id);
        if (processInstance != null) {
            return ProcessServiceUtils.convertToProcessInstance(processInstance);
        }
        throw new ServiceException(String.format("No process instance found for %s.", id));
    }

    private Set<ProcessDefinition> convertRestProcessDefinitions(RestSearchResult<RestProcessDefinition> processDefinitions) {
        checkNotNull(processDefinitions);
        Set<ProcessDefinition> pds = newHashSet();
        for (RestProcessDefinition processDefinition : processDefinitions.getData()) {
            pds.add(ProcessServiceUtils.convertToProcessDefinition(processDefinition));
        }
        return pds;
    }

    @Override
    public void attach(ProcessInstance processInstance, ItemBase item) throws ServiceException {
        checkNotNull(processInstance);
        checkNotNull(item);
        String token = getCurrentToken();
        taskService.setToken(token);
        RestSearchResult<RestTask> tasks = taskService.getTasksByProcessInstance(processInstance.getId());
        if (tasks != null && tasks.getSize() > 0) {
            RestTask task = tasks.getData().get(0);
            RestExternalResource resource = new RestExternalResource();
            resource.setName("Document #" + item.getId());
            resource.setDescription("Document - " + item.getName());
            resource.setType("simpleType");
            resource.setExternalUrl("http://www.google.com?q=activiti");
            resource = taskService.addExternalResource(task.getId(), resource);
            log.debug(String.format("New resource created: %s", resource));
        }
    }

    @Override
    public Set<Task> getTasksByProcessInstance(String id) throws ServiceException {
        log.debug(String.format("*** getTasksByProcessInstance - %s ***", id));
        String token = getCurrentToken();
        taskService.setToken(token);
        RestSearchResult<RestTask> tasks = taskService.getTasksByProcessInstance(id);
        Set<Task> ts = newHashSet();
        for (RestTask task : tasks.getData()) {
            ts.add(ProcessServiceUtils.convertToTask(task));
        }
        return ts;
    }

    @Override
    public void assignTask(String id, String userId) throws ServiceException {
        log.debug(String.format("*** assignTask - %s - %s ***", id, userId));
        String token = getCurrentToken();
        taskService.setToken(token);
        RestTask task = taskService.getTask(id);
        checkNotNull(task);
        task.setAssignee(userId);
        taskService.update(task);
    }

    @Override
    public Task getTask(String id) throws ServiceException {
        log.debug(String.format("*** getTask - %s ***", id));
        String token = getCurrentToken();
        taskService.setToken(token);
        RestTask task = taskService.getTask(id);
        if (task != null) {
            return ProcessServiceUtils.convertToTask(task);
        }
        throw new ServiceException(String.format("No task found for %s.", id));
    }

}
