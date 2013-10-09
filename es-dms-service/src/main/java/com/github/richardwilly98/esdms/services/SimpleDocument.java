package com.github.richardwilly98.esdms.services;

/*
 * #%L
 * es-dms-service
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.richardwilly98.esdms.DocumentImpl;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.Version;

class SimpleDocument extends DocumentImpl {

    private static final long serialVersionUID = 1L;

    public static class Builder extends DocumentImpl.Builder {

        private Document document;

        public Builder document(Document document) {
            this.document = document;
            return this;
        }

        public SimpleDocument build() {
            this.versions(document.getVersions()).tags(document.getTags()).ratings(document.getRatings()).id(document.getId())
                    .name(document.getName()).disabled(document.isDisabled()).description(document.getDescription())
                    .attributes(document.getAttributes());
            return new SimpleDocument(this);
        }
    }

    SimpleDocument() {
        this(null);
    }

    SimpleDocument(final Builder builder) {
        super(builder);
    }

    protected void setReadOnlyAttribute(String name, Object value) {
        super.setReadOnlyAttribute(name, value);
    }

    protected void removeReadOnlyAttribute(String name) {
        setReadOnlyAttribute(name, null);
    }

    protected void setStatus(DocumentStatus status) {
        checkNotNull(status);
        setReadOnlyAttribute(DocumentSystemAttributes.STATUS.getKey(), status.getStatusCode());
    }

    /*
     * addVersion set the version added to current
     */
    protected void addVersion(Version version) {
        Version currentVersion = getCurrentVersion();
        SimpleVersion sv = new SimpleVersion.Builder().version(version).build();
        if (currentVersion != null) {
            sv.setParentId(currentVersion.getVersionId());
        }
        sv.setCurrent(true);
        getVersions().add(sv);
    }

    protected void updateVersion(Version version) {
        checkNotNull(version);
        checkArgument(version.getVersionId() > 0);
        getVersions().remove(getVersion(version.getVersionId()));
        getVersions().add(version);
    }

    protected void deleteVersion(Version version) {
        checkNotNull(version);
        getVersions().remove(getVersion(version.getVersionId()));
    }

    @JsonProperty("versions")
    private Set<Version> serializeVersions() throws JsonProcessingException {
        Set<Version> versions = getVersions();
        if (versions == null) {
            return null;
        } else {
            Set<Version> serializedVersions = newHashSet();
            for (Version version : versions) {
                if (version.isCurrent()) {
                    serializedVersions.add(new SimpleVersion.Builder().version(version).build());
                } else {
                    SimpleVersion sv = new SimpleVersion.Builder().version(version).build();
                    sv.setCurrent(false);
                    sv.setFile(null);
                    serializedVersions.add(sv);
                }
            }
            return serializedVersions;
        }
    }

    @Override
    @JsonIgnore
    public DocumentStatus getStatus() {
        DocumentStatus status = super.getStatus();
        if (status == null) {
            return DocumentStatus.AVAILABLE;
        }
        return status;
    }

}
