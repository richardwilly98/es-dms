package com.github.richardwilly98.esdms.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.net.URI;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;

import com.github.richardwilly98.activiti.rest.api.RestDeployment;
import com.github.richardwilly98.activiti.rest.api.RestExternalResource;
import com.github.richardwilly98.activiti.rest.api.RestProcessDefinition;
import com.github.richardwilly98.activiti.rest.api.RestProcessInstance;
import com.github.richardwilly98.activiti.rest.api.RestSearchResult;
import com.github.richardwilly98.activiti.rest.api.RestTask;
import com.github.richardwilly98.activiti.rest.client.RestDeploymentServiceClient;
import com.github.richardwilly98.activiti.rest.client.RestProcessDefinitionServiceClient;
import com.github.richardwilly98.activiti.rest.client.RestProcessInstanceServiceClient;
import com.github.richardwilly98.activiti.rest.client.RestTaskServiceClient;
import com.github.richardwilly98.esdms.api.ItemBase;
import com.github.richardwilly98.esdms.bpm.api.ProcessDefinition;
import com.github.richardwilly98.esdms.bpm.api.ProcessInstance;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.rest.ProcessServiceUtils;

public class ProcessServiceProvider implements ProcessService {

    public static final String ACTIVITI_REST_URL = "http://localhost:18080/activiti-rest/service";
    public static final String ES_DMS_CATEGORY = "es-dms";

    private final static Logger log = Logger.getLogger(ProcessServiceProvider.class);

    private final RestProcessDefinitionServiceClient processDefinitionService;
    private final RestProcessInstanceServiceClient processInstanceService;
    private final RestDeploymentServiceClient deploymentService;
    private final RestTaskServiceClient taskService;

    ProcessServiceProvider() {
        processDefinitionService = new RestProcessDefinitionServiceClient(URI.create(ACTIVITI_REST_URL));
        processInstanceService = new RestProcessInstanceServiceClient(URI.create(ACTIVITI_REST_URL));
        deploymentService = new RestDeploymentServiceClient(URI.create(ACTIVITI_REST_URL));
        taskService = new RestTaskServiceClient(URI.create(ACTIVITI_REST_URL));
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
        String token = getCurrentToken();
        String deploymentId = getFirstDeployment(token);
        log.debug("First deployment it: " + deploymentId);
        processDefinitionService.setToken(token);
        RestSearchResult<RestProcessDefinition> processDefinitions = processDefinitionService
                .getProcessDefinitionsByCategory(ES_DMS_CATEGORY);
        return convertRestProcessDefinitions(processDefinitions);
    }

    @Override
    public ProcessDefinition getProcessDefinition(String id) throws ServiceException {
        log.debug(String.format("*** getProcessDefinition - %s ***", id));
        String token = getCurrentToken();
        processDefinitionService.setToken(token);
        RestProcessDefinition processDefinition = processDefinitionService.getProcessDefinition(id);
        if (processDefinition != null) {
            return ProcessServiceUtils.convertToProcessDefinition(processDefinition);
        }
        throw new ServiceException(String.format("No process definition found for %s.", id));
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

    private String getFirstDeployment(String token) throws ServiceException {
        deploymentService.setToken(token);
        RestSearchResult<RestDeployment> deployments = deploymentService.getDeployments();
        if (deployments.getSize() > 0) {
            return deployments.getData().get(0).getId();
        }
        throw new ServiceException("No deployment found.");
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
            taskService.addExternalResource(task.getId(), resource);
        }
        throw new ServiceException(String.format("No task found for instance %s.", processInstance.getId()));
    }

}
