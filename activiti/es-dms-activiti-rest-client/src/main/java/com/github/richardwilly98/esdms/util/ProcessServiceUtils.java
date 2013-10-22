package com.github.richardwilly98.esdms.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import com.github.richardwilly98.activiti.rest.api.RestItemBase;
import com.github.richardwilly98.activiti.rest.api.RestProcessDefinition;
import com.github.richardwilly98.activiti.rest.api.RestProcessInstance;
import com.github.richardwilly98.activiti.rest.api.RestTask;
import com.github.richardwilly98.esdms.bpm.ProcessDefinitionImpl;
import com.github.richardwilly98.esdms.bpm.ProcessInstanceImpl;
import com.github.richardwilly98.esdms.bpm.TaskImpl;
import com.github.richardwilly98.esdms.bpm.api.ProcessDefinition;
import com.github.richardwilly98.esdms.bpm.api.ProcessInstance;
import com.github.richardwilly98.esdms.bpm.api.Task;

public final class ProcessServiceUtils {
    public static ProcessInstance convertToProcessInstance(RestProcessInstance instance) {
        checkNotNull(instance);
        return new ProcessInstanceImpl.Builder().id(instance.getId()).name(instance.getName()).attributes(getAttributes(instance))
                .processDefinitionId(instance.getProcessDefinitionId()).description(instance.getDescription()).build();
    }

    public static ProcessDefinition convertToProcessDefinition(RestProcessDefinition definition) {
        checkNotNull(definition);
        return new ProcessDefinitionImpl.Builder().id(definition.getId()).name(definition.getName()).attributes(getAttributes(definition))
                .version(String.valueOf(definition.getVersion())).category(definition.getCategory()).build();
    }

    public static Task convertToTask(RestTask task) {
        checkNotNull(task);
        return new TaskImpl.Builder().id(task.getId()).name(task.getName()).attributes(getAttributes(task))
                .processInstanceId(task.getProcessInstanceId()).description(task.getDescription()).build();
    }

    private static Map<String, Object> getAttributes(RestItemBase item) {
        Map<String, Object> attributes = newHashMap();
        attributes.put("url", item.getUrl());
        return attributes;
    }
}
