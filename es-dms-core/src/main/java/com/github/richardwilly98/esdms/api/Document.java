package com.github.richardwilly98.esdms.api;

import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.richardwilly98.esdms.DocumentImpl;

@JsonDeserialize(as = DocumentImpl.class)
public interface Document extends ItemBase {

	public final static String CREATION_DATE = "creation";
	public final static String MODIFIED_DATE = "modified";
	public final static String AUTHOR = "author";
	public final static String STATUS = "status";
	public final static String LOCKED_BY = "lockedBy";

	public enum DocumentStatus {
		AVAILABLE("A"), LOCKED("L"), DELETED("D");

		private String statusCode;

		private DocumentStatus(String status) {
			statusCode = status;
		}

		public String getStatusCode() {
			return statusCode;
		}

	}

	public abstract Version getCurrentVersion();
	
	public abstract Version getVersion(final int versionId);
	
	public abstract Set<Version> getVersions();

	public abstract Set<String> getTags();

	public abstract void addTag(String tag);
	
	public abstract void removeTag(String tag);
	
	public abstract boolean hasStatus(DocumentStatus status);
}