package com.github.richardwilly98.esdms.bpm.api;

import java.util.Date;

import com.github.richardwilly98.esdms.api.ItemBase;

public interface Task extends ItemBase {

    public abstract int getPriority();

    public abstract void setPriority(int priority);

    public abstract Date getCreation();

    public abstract void setCreation(Date creation);

    public abstract Date getDue();

    public abstract void setDue(Date due);

    public abstract String getStatus();

    public abstract void setStatus(String status);

    public abstract String getOwner();

    public abstract void setOwner(String owner);

    public abstract String getAssignee();

    public abstract void setAssignee(String assignee);

    public abstract String getProcessInstanceId();

    public abstract void setProcessInstanceId(String processInstanceId);

}