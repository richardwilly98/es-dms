package com.github.richardwilly98.esdms.api;

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

import java.util.EnumSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.richardwilly98.esdms.DocumentImpl;

@JsonDeserialize(as = DocumentImpl.class)
public interface Document extends ItemBase {

    public enum DocumentSystemAttributes {
        CREATION_DATE("creation"), MODIFIED_DATE("modified"), AUTHOR("author"), STATUS("status"), LOCKED_BY("lockedBy");
        private String key;
        private DocumentSystemAttributes(String key) {
            this.key = key;
        }
        
        public String getKey() {
            return key;
        }
    }
    public enum DocumentStatus {
	AVAILABLE(Constants.AVAILABLE), LOCKED(Constants.LOCKED), DELETED(Constants.DELETED);

	private String statusCode;

	private DocumentStatus(String status) {
	    statusCode = status;
	}

	public String getStatusCode() {
	    return statusCode;
	}

	public static DocumentStatus getDocumentStatus(String status) {
	    for (DocumentStatus documentStatus : EnumSet.allOf(DocumentStatus.class)) {
	        if (status.equals(documentStatus.getStatusCode())) {
	            return documentStatus;
	        }
	    }
	    return null;
	}
	public static class Constants {
	    public static final String AVAILABLE = "A";
            public static final String LOCKED = "L";
            public static final String DELETED = "D";
	}
    }

    public abstract Version getCurrentVersion();

    public abstract Version getVersion(final int versionId);

    public abstract Set<Version> getVersions();

    public abstract Set<String> getTags();

    public abstract void addTag(String tag);

    public abstract void removeTag(String tag);

    public abstract Set<Rating> getRatings();

    public abstract void addRating(Rating rating);

    public abstract void removeRating(Rating rating);

    @NotNull(message= "status is required")
    public abstract DocumentStatus getStatus();
    
    public abstract boolean hasStatus(DocumentStatus status);
}