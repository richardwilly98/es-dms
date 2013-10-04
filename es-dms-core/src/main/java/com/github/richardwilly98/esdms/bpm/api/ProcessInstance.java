package com.github.richardwilly98.esdms.bpm.api;

import java.util.Date;
import java.util.Set;

import com.github.richardwilly98.esdms.api.ItemBase;

public interface ProcessInstance extends ItemBase {

    public abstract Date getStart();

    public abstract void setStart(Date start);

    public abstract Date getModified();

    public abstract void setModified(Date modified);

    public abstract Date getEnd();

    public abstract void setEnd(Date end);

    public abstract String getStatus();

    public abstract void setStatus(String status);

    public abstract String getProcessDefinitionId();

    public abstract void setProcessDefinitionId(String processDefinitionId);

    public abstract String getInitiator();

    public abstract void setInitiator(String initiator);

    public abstract String getOwner();

    public abstract void setOwner(String owner);

    public abstract Set<Object> getVariables();

}