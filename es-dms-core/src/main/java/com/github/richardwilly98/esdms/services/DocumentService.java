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

import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.File;
import com.github.richardwilly98.esdms.api.Version;
import com.github.richardwilly98.esdms.exception.ServiceException;

public interface DocumentService extends BaseService<Document> {

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