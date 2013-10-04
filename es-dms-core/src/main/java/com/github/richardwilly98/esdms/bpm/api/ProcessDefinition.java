package com.github.richardwilly98.esdms.bpm.api;

import java.util.Date;

import com.github.richardwilly98.esdms.api.ItemBase;

public interface ProcessDefinition extends ItemBase {

    public abstract String getVersion();

    public abstract void setVersion(String version);

    public abstract String getCategory();

    public abstract void setCategory(String category);

    public abstract Date getCreation();

    public abstract void setCreation(Date creation);

    public abstract String getStatus();

    public abstract void setStatus(String status);

    public abstract String getOwner();

    public abstract void setOwner(String owner);

}