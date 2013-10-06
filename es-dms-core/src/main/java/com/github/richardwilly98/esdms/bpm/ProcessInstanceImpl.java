package com.github.richardwilly98.esdms.bpm;

/*
 * #%L
 * es-dms-core
 * %%
 * Copyright (C) 2013 es-dms
 * %%
 * Copyright 2012-2013 Richard Louapre
 * 
 * This file is part of ES-DMS.
 * 
 * The current version of ES-DMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * ES-DMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import static com.google.common.collect.Sets.newHashSet;

import java.util.Date;
import java.util.Set;

import com.github.richardwilly98.esdms.ItemBaseImpl;
import com.github.richardwilly98.esdms.bpm.api.ProcessInstance;



public class ProcessInstanceImpl extends ItemBaseImpl implements ProcessInstance {

    private static final long serialVersionUID = 1L;
    private Date start;
    private Date modified;
    private Date end;
    private String status;
    private String processDefinitionId;
    private String initiator;
    private String owner;
    private final Set<Object> variables = newHashSet();

    public static class Builder extends BuilderBase<Builder> {

        private Date start;
        private Date modified;
        private Date end;
        private String status;
        private String processDefinitionId;
        private String initiator;
        private String owner;

        public Builder start(Date start) {
            this.start = start;
            return getThis();
        }
        public Builder modified(Date modified) {
            this.modified = modified;
            return getThis();
        }
        public Builder end(Date end) {
            this.end = end;
            return getThis();
        }
        public Builder status(String status) {
            this.status = status;
            return getThis();
        }
        public Builder processDefinitionId(String processDefinitionId) {
            this.processDefinitionId = processDefinitionId;
            return getThis();
        }
        public Builder initiator(String initiator) {
            this.initiator = initiator;
            return getThis();
        }
        public Builder owner(String owner) {
            this.owner = owner;
            return getThis();
        }
        @Override
        protected Builder getThis() {
            return this;
        }

        public ProcessInstance build() {
            return new ProcessInstanceImpl(this);
        }
        
    }
    
    ProcessInstanceImpl() {
        super(null);
    }

    protected ProcessInstanceImpl(Builder builder) {
        super(builder);
        if (builder != null) {
            this.start = builder.start;
            this.modified = builder.modified;
            this.end = builder.end;
            this.status = builder.status;
            this.processDefinitionId = builder.processDefinitionId;
            this.initiator = builder.initiator;
            this.owner = builder.owner;
        }
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessInstance#getStart()
     */
    @Override
    public Date getStart() {
        return start;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessInstance#setStart(java.util.Date)
     */
    @Override
    public void setStart(Date start) {
        this.start = start;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessInstance#getModified()
     */
    @Override
    public Date getModified() {
        return modified;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessInstance#setModified(java.util.Date)
     */
    @Override
    public void setModified(Date modified) {
        this.modified = modified;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessInstance#getEnd()
     */
    @Override
    public Date getEnd() {
        return end;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessInstance#setEnd(java.util.Date)
     */
    @Override
    public void setEnd(Date end) {
        this.end = end;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessInstance#getStatus()
     */
    @Override
    public String getStatus() {
        return status;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessInstance#setStatus(java.lang.String)
     */
    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessInstance#getProcessDefinitionId()
     */
    @Override
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessInstance#setProcessDefinitionId(java.lang.String)
     */
    @Override
    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessInstance#getInitiator()
     */
    @Override
    public String getInitiator() {
        return initiator;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessInstance#setInitiator(java.lang.String)
     */
    @Override
    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessInstance#getOwner()
     */
    @Override
    public String getOwner() {
        return owner;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessInstance#setOwner(java.lang.String)
     */
    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessInstance#getVariables()
     */
    @Override
    public Set<Object> getVariables() {
        return variables;
    }

}
