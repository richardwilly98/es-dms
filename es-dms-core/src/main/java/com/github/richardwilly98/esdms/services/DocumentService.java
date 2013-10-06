package com.github.richardwilly98.esdms.services;

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

import java.util.Set;

import com.github.richardwilly98.esdms.PermissionImpl;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.File;
import com.github.richardwilly98.esdms.api.Permission;
import com.github.richardwilly98.esdms.api.Version;
import com.github.richardwilly98.esdms.exception.ServiceException;

public interface DocumentService extends BaseService<Document> {

    public enum DocumentPermissions {
        READ_PERMISSION(new PermissionImpl.Builder().id(Constants.DOCUMENT_READ).build()), CREATE_PERMISSION(new PermissionImpl.Builder().id(
                Constants.DOCUMENT_CREATE).build()), EDIT_PERMISSION(new PermissionImpl.Builder().id(Constants.DOCUMENT_EDIT).build()), DELETE_PERMISSION(
                new PermissionImpl.Builder().id(Constants.DOCUMENT_DELETE).build()), PROFILE_READ_PERMISSION(new PermissionImpl.Builder().id(
                Constants.PROFILE_READ).build()), CONTENT_READ_PERMISSION(new PermissionImpl.Builder().id(Constants.CONTENT_READ).build()), ANNOTATION_READ_PERMISSION(
                new PermissionImpl.Builder().id(Constants.ANNOTATION_READ).build()), ANNOTATION_WRITE_PERMISSION(new PermissionImpl.Builder().id(
                Constants.ANNOTATION_WRITE).build()), COMMENT_READ_PERMISSION(new PermissionImpl.Builder().id(Constants.COMMENT_READ).build()), COMMENT_WRITE_PERMISSION(
                new PermissionImpl.Builder().id(Constants.COMMENT_WRITE).build()), CONTENT_TODELETE_PERMISSION(new PermissionImpl.Builder().id(
                Constants.CONTENT_TODELETE).build()), PROFILE_WRITE_PERMISSION(new PermissionImpl.Builder().id(Constants.PROFILE_WRITE).build()), CONTENT_WRITE_PERMISSION(
                new PermissionImpl.Builder().id(Constants.CONTENT_WRITE).build()), CONTENT_ADD_PERMISSION(new PermissionImpl.Builder().id(
                Constants.CONTENT_ADD).build()), CONTENT_REMOVE_PERMISSION(new PermissionImpl.Builder().id("content:remove").build()), PROFILE_TODELETE_PERMISSION(
                new PermissionImpl.Builder().id(Constants.PROFILE_TODELETE).build());
        private Permission permission;

        DocumentPermissions(Permission permission) {
            this.permission = permission;
        }

        public Permission getPermission() {
            return permission;
        }

        public static class Constants {
            public static final String DOCUMENT_READ = "document:read";
            public static final String DOCUMENT_CREATE = "document:create";
            public static final String DOCUMENT_EDIT = "document:edit";
            public static final String DOCUMENT_DELETE = "document:delete";
            public static final String PROFILE_READ = "profile:read";
            public static final String CONTENT_READ = "content:read";
            public static final String ANNOTATION_READ = "annotation:read";
            public static final String ANNOTATION_WRITE = "annotation:write";
            public static final String COMMENT_READ = "comment:read";
            public static final String COMMENT_WRITE = "comment:write";
            public static final String CONTENT_TODELETE = "content:todelete";
            public static final String PROFILE_WRITE = "profile:write";
            public static final String CONTENT_WRITE = "content:write";
            public static final String CONTENT_ADD = "content:add";
            public static final String CONTENT_REMOVE = "content:remove";
            public static final String PROFILE_TODELETE = "profile:todelete";
        }
    }

    public abstract Document getMetadata(String id) throws ServiceException;

    public abstract void checkin(Document document) throws ServiceException;

    public abstract void checkout(Document document) throws ServiceException;

    public abstract void markDeleted(Document document) throws ServiceException;

    public abstract void undelete(Document document) throws ServiceException;

    public abstract String preview(Document document, String criteria, int size) throws ServiceException;

    public abstract void addVersion(Document document, Version version) throws ServiceException;

    public abstract void deleteVersion(Document document, Version version) throws ServiceException;

    public abstract Set<Version> getVersions(Document document) throws ServiceException;

    public abstract Version getVersion(Document document, int versionId) throws ServiceException;

    public abstract File getVersionContent(Document document, int versionId) throws ServiceException;

    public abstract void setCurrentVersion(Document document, int versionId) throws ServiceException;

    public abstract void setVersionContent(Document document, int versionId, File file) throws ServiceException;
}