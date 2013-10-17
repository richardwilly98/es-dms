package com.github.richardwilly98.esdms.rest;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.github.richardwilly98.activiti.rest.api.RestProcessInstance;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.bpm.api.ProcessDefinition;
import com.github.richardwilly98.esdms.bpm.api.ProcessInstance;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.services.DocumentService;
import com.github.richardwilly98.esdms.services.ProcessService;
import com.google.common.base.Strings;

@Path(RestProcessService.PROCESS_PATH)
public class RestProcessService {

    private final static Logger log = Logger.getLogger(RestProcessService.class);

    public static final String PROCESS_PATH = "process";
    public static final String PROCESS_DEFINITIONS_PATH = "process-definitions";
    public static final String PROCESS_INSTANCES_PATH = "process-instances";
    private final ProcessService processService;
    private final DocumentService documentService;

    @Inject
    public RestProcessService(final ProcessService processService, final DocumentService documentService) {
        this.processService = processService;
        this.documentService = documentService;
    }

    @GET
    @Path(PROCESS_DEFINITIONS_PATH)
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getProcessDefinitions() {
        if (log.isTraceEnabled()) {
            log.trace(String.format("getProcessDefinitions"));
        }
        try {
            Set<ProcessDefinition> processDefinitions = processService.getProcessDefinitions();
            return Response.ok().entity(processDefinitions).build();
        } catch (ServiceException e) {
            log.error("getProcessDefinitions failed", e);
            throw new WebApplicationException(e.getLocalizedMessage());
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(PROCESS_INSTANCES_PATH)
    public Response startProcessInstance(RestProcessInstance instance) {
        try {
            if (log.isTraceEnabled()) {
                log.trace(String.format("startProcessInstance - %s", instance));
            }
            checkNotNull(instance, "instance cannot be null.");
            checkArgument(!Strings.isNullOrEmpty(instance.getProcessDefinitionId()), "Missing field processDefinitionId");
            ProcessDefinition definition = processService.getProcessDefinition(instance.getProcessDefinitionId());
            ProcessInstance processInstance = processService.startProcessInstance(definition);
            instance.setUrl("http://localhost:18080/activiti-rest/service/runtime/process-instances/" + processInstance.getId());
            return Response.created(URI.create(instance.getUrl())).build();
        } catch (ServiceException e) {
            log.error("startProcessInstance failed", e);
            throw new WebApplicationException(e.getLocalizedMessage());
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(PROCESS_INSTANCES_PATH + "/{instanceId}/attachments/{itemId}")
    public Response attach(@PathParam("instanceId") String instanceId, @PathParam("itemId") String itemId) {
        try {
            if (log.isTraceEnabled()) {
                log.trace(String.format("attach - %s - %s", instanceId, itemId));
            }
            checkArgument(!Strings.isNullOrEmpty(instanceId), "Parameter instanceId is required");
            checkArgument(!Strings.isNullOrEmpty(itemId), "Parameter itemId is required");
            ProcessInstance instance = processService.getProcessInstance(instanceId);
            Document document = documentService.get(itemId);
            processService.attach(instance, document);
            return Response.ok().build();
        } catch (ServiceException e) {
            log.error("attach failed", e);
            throw new WebApplicationException(e.getLocalizedMessage());
        }
    }
}
