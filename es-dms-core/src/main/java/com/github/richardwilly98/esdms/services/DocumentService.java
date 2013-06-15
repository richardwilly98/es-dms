package com.github.richardwilly98.esdms.services;

import java.util.Set;

import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.File;
import com.github.richardwilly98.esdms.api.Version;
import com.github.richardwilly98.esdms.exception.ServiceException;

public interface DocumentService extends BaseService <Document> {
	
	public static final String READ_PERMISSION = "document:read";
	public static final String CREATE_PERMISSION = "document:create";
	public static final String EDIT_PERMISSION = "document:edit";
	public static final String DELETE_PERMISSION = "document:delete";
	
	public static final String PROFILE_READ_PERMISSION = "profile:read";
	public static final String CONTENT_READ_PERMISSION = "content:read";
	public static final String ANNOTATION_READ_PERMISSION = "annotation:read";
	public static final String ANNOTATION_WRITE_PERMISSION = "annotation:write";
	
	public static final String COMMENT_READ_PERMISSION = "comment:read";
	public static final String COMMENT_WRITE_PERMISSION = "comment:write";
	
	public static final String CONTENT_TODELETE_PERMISSION = "content:todelete";
	
	public static final String PROFILE_WRITE_PERMISSION = "profile:write";
	public static final String CONTENT_WRITE_PERMISSION = "content:write";
	
	public static final String CONTENT_ADD_PERMISSION = "content:add";
	public static final String CONTENT_REMOVE_PERMISSION = "content:remove";
	
	public static final String PROFILE_TODELETE_PERMISSION = "profile:todelete";
	
	public abstract void checkin(Document document)
			throws ServiceException;

	public abstract void checkout(Document document)
			throws ServiceException;
	
	public abstract String preview(Document document, String criteria, int size)
			throws ServiceException;

	public abstract void addVersion(Document document, Version version)
			throws ServiceException;

	public abstract void deleteVersion(Document document, Version version)
			throws ServiceException;

	public abstract Set<Version> getVersions(Document document)
			throws ServiceException;

	public abstract Version getVersion(Document document, int versionId)
			throws ServiceException;

	public abstract File getVersionContent(Document document, int versionId)
			throws ServiceException;

	public abstract void setCurrentVersion(Document document, int versionId)
			throws ServiceException;
}