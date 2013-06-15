package com.github.richardwilly98.esdms.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.richardwilly98.esdms.VersionImpl;

@JsonDeserialize(as = VersionImpl.class)
public interface Version extends ItemBase {

	public final static String CREATION_DATE = "creation";
	public final static String MODIFIED_DATE = "modified";

//	public enum VersionStatus {
//		AVAILABLE("A"), LOCKED("L"), DELETED("D");
//
//		private String statusCode;
//
//		private VersionStatus(String status) {
//			statusCode = status;
//		}
//
//		public String getStatusCode() {
//			return statusCode;
//		}
//
//	}

	public abstract File getFile();

	public abstract int getVersionId();
	
	public abstract boolean isCurrent();
	
	public abstract int getParentId();
	
	public abstract String getDocumentId();
}