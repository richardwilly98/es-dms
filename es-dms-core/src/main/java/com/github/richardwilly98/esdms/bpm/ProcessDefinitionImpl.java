package com.github.richardwilly98.esdms.bpm;

import java.util.Date;

import com.github.richardwilly98.esdms.ItemBaseImpl;
import com.github.richardwilly98.esdms.bpm.api.ProcessDefinition;

public class ProcessDefinitionImpl extends ItemBaseImpl implements ProcessDefinition {

    private static final long serialVersionUID = 1L;
    private String version;
    private String category;
    private Date creation;
    private String status;
    private String owner;

    public static class Builder extends BuilderBase<Builder> {

        private String version;
        private String category;
        private Date creation;
        private String status;
        private String owner;

        public Builder version(String version) {
            this.version = version;
            return getThis();
        }
        public Builder category(String category) {
            this.category = category;
            return getThis();
        }
        public Builder creation(Date creation) {
            this.creation = creation;
            return getThis();
        }
        public Builder status(String status) {
            this.status = status;
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

        public ProcessDefinition build() {
            return new ProcessDefinitionImpl(this);
        }

    }

    ProcessDefinitionImpl() {
        super(null);
    }

    protected ProcessDefinitionImpl(Builder builder) {
        super(builder);
        if (builder != null) {
            this.version = builder.version;
            this.category = builder.category;
            this.creation = builder.creation;
            this.status = builder.status;
            this.owner = builder.owner;
        }
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessDefinition#getVersion()
     */
    @Override
    public String getVersion() {
        return version;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessDefinition#setVersion(java.lang.String)
     */
    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessDefinition#getCategory()
     */
    @Override
    public String getCategory() {
        return category;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessDefinition#setCategory(java.lang.String)
     */
    @Override
    public void setCategory(String category) {
        this.category = category;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessDefinition#getCreation()
     */
    @Override
    public Date getCreation() {
        return creation;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessDefinition#setCreation(java.util.Date)
     */
    @Override
    public void setCreation(Date creation) {
        this.creation = creation;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessDefinition#getStatus()
     */
    @Override
    public String getStatus() {
        return status;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessDefinition#setStatus(java.lang.String)
     */
    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessDefinition#getOwner()
     */
    @Override
    public String getOwner() {
        return owner;
    }

    /* (non-Javadoc)
     * @see com.github.richardwilly98.esdms.ProcessDefinition#setOwner(java.lang.String)
     */
    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }
}
