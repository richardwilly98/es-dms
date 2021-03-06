package com.github.richardwilly98.esdms.rest;

/*
 * #%L
 * es-dms-site
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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.ContentDisposition.ContentDispositionBuilder;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.github.richardwilly98.esdms.DocumentImpl;
import com.github.richardwilly98.esdms.FileImpl;
import com.github.richardwilly98.esdms.VersionImpl;
import com.github.richardwilly98.esdms.api.AuditEntry;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.File;
import com.github.richardwilly98.esdms.api.Version;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.rest.exception.PreconditionException;
import com.github.richardwilly98.esdms.rest.exception.RestServiceException;
import com.github.richardwilly98.esdms.search.api.SearchResult;
import com.github.richardwilly98.esdms.services.AuditService;
import com.github.richardwilly98.esdms.services.AuthenticationService;
import com.github.richardwilly98.esdms.services.DocumentService;
import com.github.richardwilly98.esdms.web.Audit;
import com.google.common.base.Strings;

@Path(RestDocumentService.DOCUMENTS_PATH)
public class RestDocumentService extends RestItemBaseService<Document> {

    public static final String PREVIEW_FRAGMENT_SIZE_PARAMETER = "fs";
    public static final String PREVIEW_CRITERIA_PARAMETER = "cr";
    public static final String AUDIT_PATH = "_audit";
    public static final String UPLOAD_PATH = "_upload";
    public static final String UPDATE_PATH = "_update";
    public static final String DOCUMENTS_PATH = "documents";
    public static final String CHECKOUT_PATH = "_checkout";
    public static final String CHECKIN_PATH = "_checkin";
    public static final String CURRENT_PATH = "_current";
    public static final String DOWNLOAD_PATH = "_download";
    public static final String VIEW_PATH = "_view";
    public static final String EDIT_PATH = "_edit";
    public static final String METADATA_PATH = "_metadata";
    public static final String PREVIEW_PATH = "_preview";
    public static final String VERSIONS_PATH = "versions";
    public static final String MARKDELETE_PATH = "_delete";
    public static final String UNDELETE_PATH = "_undelete";
    public static final String TAGS_PATH = "tags";

    private final DocumentService documentService;
    private final AuditService auditService;

    @Inject
    public RestDocumentService(final AuthenticationService authenticationService, final DocumentService documentService,
            final AuditService auditService) {
        super(authenticationService, documentService);
        this.auditService = auditService;
        this.documentService = documentService;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{id}/" + METADATA_PATH)
    public Response getMetadata(@PathParam("id") String id) {
        try {
            isAuthenticated();
            if (log.isTraceEnabled()) {
                log.trace(String.format("getMetadata - %s", id));
            }
            Document document = documentService.getMetadata(id);

            return Response.status(Status.OK).entity(document).build();
        } catch (ServiceException e) {
            log.error("getMetadata failed", e);
            throw new RestServiceException(e.getLocalizedMessage());
        }
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{id}/" + AUDIT_PATH)
    public Response audit(@PathParam("id") String id, @QueryParam(SEARCH_FIRST_PARAMETER) @DefaultValue("0") int first,
            @QueryParam(SEARCH_PAGE_SIZE_PARAMETER) @DefaultValue("20") int pageSize) {
        try {
            isAuthenticated();
            if (log.isTraceEnabled()) {
                log.trace(String.format("audit - %s - %s - %s", id, first, pageSize));
            }
            String criteria = "item_id:" + id;
            SearchResult<AuditEntry> auditEntries = auditService.search(criteria, first, pageSize);
            checkNotNull(auditEntries);
            return Response.ok(auditEntries).build();
        } catch (ServiceException e) {
            log.error("audit failed", e);
            throw new RestServiceException(e.getLocalizedMessage());
        }
    }

    @POST
    @Path("{id}/" + CHECKOUT_PATH)
    @Produces({ MediaType.APPLICATION_JSON })
    @Audit(AuditEntry.Event.CHECKOUT)
    public Response checkout(@PathParam("id") String id) {
        try {
            Document document = service.get(id);
            documentService.checkout(document);
            return includeItemIdInHeaders(Response.noContent(), document).build();
        } catch (Throwable t) {
            log.error(String.format("checkout failed - %s", t.getLocalizedMessage()));
            return Response.status(Status.CONFLICT).build();
        }
    }

    @POST
    @Path("{id}/" + CHECKIN_PATH)
    @Produces({ MediaType.APPLICATION_JSON })
    @Audit(AuditEntry.Event.CHECKIN)
    public Response checkin(@PathParam("id") String id) {
        try {
            Document document = service.get(id);
            checkNotNull(document);
            documentService.checkin(document);
            return includeItemIdInHeaders(Response.noContent(), document).build();
        } catch (Throwable t) {
            log.error(String.format("checkin failed - %s", t.getLocalizedMessage()));
            return Response.status(Status.CONFLICT).build();
        }
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{id}/" + PREVIEW_PATH)
    public Response preview(@PathParam("id") String id, @QueryParam(PREVIEW_CRITERIA_PARAMETER) String criteria,
            @QueryParam(PREVIEW_FRAGMENT_SIZE_PARAMETER) @DefaultValue("1024") int fragmentSize) {
        try {
            isAuthenticated();
            if (log.isTraceEnabled()) {
                log.trace(String.format("preview - %s - %s", id, criteria));
            }
            if (criteria == null) {
                criteria = "*";
            }
            Document document = service.get(id);
            checkNotNull(document);

            /*
             * Danilo preview requires a version id parameter
             */

            final String content = documentService.preview(document, /*
                                                                      * document.
                                                                      * getCurrentVersion
                                                                      * (
                                                                      * ).getId(
                                                                      * ),
                                                                      */criteria, fragmentSize);
            Preview preview = new Preview() {

                @Override
                public String getContent() {
                    return content;
                }
            };
            return Response.status(Status.OK).entity(preview).build();
        } catch (ServiceException e) {
            log.error("preview failed", e);
            throw new RestServiceException(e.getLocalizedMessage());
        }
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{id}/" + VIEW_PATH)
    public Response view(@PathParam("id") String id) {
        try {
            Document document = service.get(id);
            checkNotNull(document);
            Version version = document.getCurrentVersion();
            checkNotNull(version);
            checkNotNull(version.getFile());
            ContentDispositionBuilder<?, ?> contentDisposition = ContentDisposition.type("inline");

            contentDisposition.fileName(version.getFile().getName());
            if (version.getFile().getDate() != null) {
                contentDisposition.creationDate(version.getFile().getDate());
            }
            ResponseBuilder rb = Response.ok();
            rb.type(version.getFile().getContentType());
            if (log.isTraceEnabled()) {
                log.debug(String.format("Document: %s Content type: %s", id, version.getFile().getContentType()));
            }
            rb.type(version.getFile().getContentType());
            InputStream stream = new ByteArrayInputStream(version.getFile().getContent());
            rb.entity(stream);
            rb.status(Status.OK);
            rb.header("Content-Disposition", contentDisposition.build());
            return rb.build();
        } catch (Throwable t) {
            log.error("view failed", t);
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("{id}/" + DOWNLOAD_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response download(@PathParam("id") String id) {
        try {
            checkNotNull(id);
            Document document = service.get(id);
            if (document == null) {
                throw new ServiceException(String.format("Cannot find document %s", id));
            }

            Version version = document.getCurrentVersion();
            if (version == null) {
                throw new ServiceException(String.format("Cannot find current version for document %s", id));
            }
            if (version.getFile() == null) {
                throw new ServiceException(String.format("Current version file is null for document %s", id));
            }
            ContentDispositionBuilder<?, ?> contentDisposition = ContentDisposition.type("attachment");

            contentDisposition.fileName(version.getFile().getName());
            if (version.getFile().getDate() != null) {
                contentDisposition.creationDate(version.getFile().getDate());
            }
            ResponseBuilder rb = Response.ok();
            rb.type(version.getFile().getContentType());
            InputStream stream = new ByteArrayInputStream(version.getFile().getContent());
            rb.entity(stream);
            rb.status(Status.OK);
            rb.header("Content-Disposition", contentDisposition.build());
            return rb.build();
        } catch (Throwable t) {
            log.error("download failed", t);
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path(UPLOAD_PATH)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({ MediaType.APPLICATION_JSON })
    @Audit(AuditEntry.Event.UPLOAD)
    public Response upload(@FormDataParam("name") String name, @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataBodyPart body, @FormDataParam("tagging") int autoTagging) {
        checkNotNull(body);
        checkNotNull(body.getContentDisposition());
        String filename = body.getContentDisposition().getFileName();
        if (Strings.isNullOrEmpty(name)) {
            name = filename;
        }
        String path = null;
        long size = body.getContentDisposition().getSize();
        String contentType = body.getMediaType().toString();
        if (log.isTraceEnabled()) {
            log.trace(String.format("upload - %s - %s - %s - %s - %s", name, filename, size, contentType, autoTagging));
        }
        try {
            isAuthenticated();
            byte[] content;
            if (size > 16 * 1024 * 1024) {
                path = System.getProperty("java.io.tmpdir") + System.currentTimeMillis() + filename;
                writeToFile(uploadedInputStream, path);
                content = Files.readAllBytes(Paths.get(path));
            } else {
                content = toByteArray(uploadedInputStream);
            }
            File file = new FileImpl.Builder().content(content).name(filename).contentType(contentType).build();
            Map<String, Object> attributes = newHashMap();
            Document document = new DocumentImpl.Builder().versions(new HashSet<Version>()).name(name).attributes(attributes).roles(null)
                    .build();
            document = service.create(document);
            Response response = includeItemIdInHeaders(Response.created(getItemUri(document)), document).build();
            Version version = new VersionImpl.Builder().documentId(document.getId()).current(true).file(file).versionId(1).build();
            documentService.addVersion(document, version);
            if (autoTagging > 0) {
                documentService.detectTags(document, autoTagging);
            }
            return response;
        } catch (Throwable t) {
            log.error("upload failed", t);
            throw new RestServiceException(t.getLocalizedMessage());
        } finally {
            if (path != null) {
                deleteFile(path);
            }
        }
    }

    @POST
    @Path("{id}/" + MARKDELETE_PATH)
    // @Consumes(MediaType.APPLICATION_JSON)
    // @Produces({ MediaType.APPLICATION_JSON })
    public Response queueDelete(@PathParam("id") String id) {
        try {
            Document document = service.get(id);
            checkNotNull(document);

            documentService.markDeleted(document);

            return Response.noContent().build();
        } catch (ServiceException t) {
            log.error(String.format("Document %s is not marked as available", id), t);
            return Response.status(Status.PRECONDITION_FAILED).build();
        } catch (Throwable t) {
            log.error(String.format("Check if document %s exists", id), t);
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("{id}/" + UNDELETE_PATH)
    // @Consumes(MediaType.APPLICATION_JSON)
    // @Produces({ MediaType.APPLICATION_JSON })
    public Response undelete(@PathParam("id") String id) {
        try {
            Document document = service.get(id);
            checkNotNull(document);

            documentService.undelete(document);

            return Response.noContent().build();
        } catch (ServiceException t) {
            log.error(String.format("Document %s is not marked as deleted", id), t);
            return Response.status(Status.PRECONDITION_FAILED).build();
        } catch (Throwable t) {
            log.error(String.format("Check if document %s exists", id), t);
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{id}")
    public Response delete(@PathParam("id") String id) {
        try {
            Document document = service.get(id);
            checkNotNull(document);

            if (!document.hasStatus(Document.DocumentStatus.DELETED))
                throw new PreconditionException(String.format("Document %s is not marked as deleted", id));

            return super.delete(id);
        } catch (ServiceException t) {
            String message = String.format("Document %s is not marked as deleted", id);
            log.error(message, t);
            return Response.status(Status.PRECONDITION_FAILED).entity(message).build();
        } catch (NullPointerException npEx) {
            log.error(String.format("Check if document %s exists", id), npEx);
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @Path("{id}/" + VERSIONS_PATH)
    public RestVersionService getVersionResource() {
        return new RestVersionService(authenticationService, documentService);
    }

    // // Versions methods
    // @GET
    // @Produces({ MediaType.APPLICATION_JSON })
    // @Path("{id}" + VERSIONS_PATH + "/{vid}/" + PREVIEW_PATH)
    // public Response preview(@PathParam("id") String id, @PathParam("vid")
    // String vid,
    // @QueryParam(PREVIEW_CRITERIA_PARAMETER) String criteria,
    // @QueryParam(PREVIEW_FRAGMENT_SIZE_PARAMETER) @DefaultValue("1024") int
    // fragmentSize) {
    // try {
    // isAuthenticated();
    // if (log.isTraceEnabled()) {
    // log.trace(String.format("preview - %s - %s", id, criteria));
    // }
    // if (criteria == null) {
    // criteria = "*";
    // }
    // Document document = service.get(id);
    // checkNotNull(document);
    //
    // /*
    // * Danilo preview requires a version id parameter
    // */
    //
    // final String content = documentService.preview(document, /*
    // * Integer.
    // * parseInt
    // * (vid),
    // */criteria, fragmentSize);
    // Preview preview = new Preview() {
    //
    // @Override
    // public String getContent() {
    // return content;
    // }
    // };
    // return Response.status(Status.OK).entity(preview).build();
    // } catch (ServiceException e) {
    // log.error("preview failed", e);
    // throw new RestServiceException(e.getLocalizedMessage());
    // }
    // }
    //
    // @GET
    // @Produces({ MediaType.APPLICATION_JSON })
    // @Path("{id}/" + VERSIONS_PATH + "/{vid}")
    // public Response getVersion(@PathParam("id") String id, @PathParam("vid")
    // int vid) {
    // if (log.isTraceEnabled()) {
    // log.trace(String.format("getVersion - %s - %s", id, vid));
    // }
    // try {
    // Document document = documentService.get(id);
    // if (document == null) {
    // return Response.status(Status.NOT_FOUND).build();
    // }
    // Version version = documentService.getVersion(document, vid);
    // if (version == null) {
    // return Response.status(Status.NOT_FOUND).build();
    // }
    // return Response.ok(version).build();
    // } catch (ServiceException e) {
    // throw new RestServiceException(e.getLocalizedMessage());
    // }
    // }
    //
    // @POST
    // // @Path("{id}/" + UPLOAD_PATH)
    // @Path("{id}/" + VERSIONS_PATH)
    // @Consumes(MediaType.MULTIPART_FORM_DATA)
    // @Produces({ MediaType.APPLICATION_JSON })
    // public Response uploadVersion(@PathParam("id") String id,
    // @FormDataParam("file") InputStream uploadedInputStream,
    // @FormDataParam("file") FormDataBodyPart body) {
    // log.info(String.format("uploadVersion to document id %s ", id));
    // checkNotNull(body);
    // checkNotNull(body.getContentDisposition());
    // String filename = body.getContentDisposition().getFileName();
    //
    // String path = null;
    // long size = body.getContentDisposition().getSize();
    // String contentType = body.getMediaType().toString();
    // if (log.isTraceEnabled()) {
    // log.trace(String.format("uploadVersion for document %s - %s - %s - %s",
    // id, filename, size, contentType));
    // }
    // try {
    // isAuthenticated();
    // byte[] content;
    // if (size > 16 * 1024 * 1024) {
    // path = System.getProperty("java.io.tmpdir") + System.currentTimeMillis()
    // + filename;
    // writeToFile(uploadedInputStream, path);
    // content = Files.readAllBytes(Paths.get(path));
    // } else {
    // content = toByteArray(uploadedInputStream);
    // }
    // File file = new
    // FileImpl.Builder().content(content).name(filename).contentType(contentType).build();
    //
    // Document document = service.get(id);
    // checkNotNull(document);
    //
    // if (!document.hasStatus(Document.DocumentStatus.AVAILABLE))
    // throw new
    // PreconditionException(String.format("uploadVersion: Document %s is not available for uploading a version",
    // id));
    //
    // Version currentVersion = document.getCurrentVersion();
    // checkNotNull(currentVersion);
    //
    // Version version = new
    // VersionImpl.Builder().documentId(document.getId()).current(true).file(file)
    // .parentId(currentVersion.getVersionId()).versionId(document.getVersions().size()
    // + 1).build();
    // documentService.addVersion(document, version);
    // // documentService.setCurrentVersion(document,
    // // version.getVersionId());
    // log.debug("new version updloaded: " + VERSIONS_PATH + "/" +
    // version.getVersionId());
    // Response response = Response.created(getItemUri(document, VERSIONS_PATH,
    // String.valueOf(version.getVersionId()))).build();
    //
    // return response;
    // } catch (Throwable t) {
    // log.error("uploadVersion: upload failed", t);
    // throw new RestServiceException(t.getLocalizedMessage());
    // } finally {
    // if (path != null) {
    // deleteFile(path);
    // }
    // }
    // }
    //
    // @POST
    // @Path("{id}/" + VERSIONS_PATH + "/{vid}/" + UPLOAD_PATH)
    // @Consumes(MediaType.MULTIPART_FORM_DATA)
    // @Produces({ MediaType.APPLICATION_JSON })
    // public Response uploadVersion(@PathParam("id") String id,
    // @PathParam("vid") String vid,
    // @FormDataParam("file") InputStream uploadedInputStream,
    // @FormDataParam("file") FormDataBodyPart body) {
    // checkNotNull(body);
    // checkNotNull(body.getContentDisposition());
    // String filename = body.getContentDisposition().getFileName();
    //
    // String path = null;
    // long size = body.getContentDisposition().getSize();
    // String contentType = body.getMediaType().toString();
    // if (log.isTraceEnabled()) {
    // log.trace(String.format("uploadVersion - %s - %s - %s - %s - %s", id,
    // vid, filename, size, contentType));
    // }
    // try {
    // isAuthenticated();
    // byte[] content;
    // if (size > 16 * 1024 * 1024) {
    // path = System.getProperty("java.io.tmpdir") + System.currentTimeMillis()
    // + filename;
    // writeToFile(uploadedInputStream, path);
    // content = Files.readAllBytes(Paths.get(path));
    // } else {
    // content = toByteArray(uploadedInputStream);
    // }
    // File file = new
    // FileImpl.Builder().content(content).name(filename).contentType(contentType).build();
    //
    // Document document = service.get(id);
    // checkNotNull(document);
    //
    // if (!document.hasStatus(Document.DocumentStatus.AVAILABLE))
    // throw new
    // PreconditionException(String.format("Document %s is not available for uploading a version",
    // id));
    //
    // Version parentVersion = document.getVersion(Integer.parseInt(vid));
    // checkNotNull(parentVersion);
    //
    // Response response = Response.created(getItemUri(document)).build();
    // Version version = new
    // VersionImpl.Builder().documentId(document.getId()).current(true).file(file)
    // .parentId(Integer.parseInt(vid)).versionId(document.getVersions().size()
    // + 1).build();
    // documentService.addVersion(document, version);
    // // documentService.setCurrentVersion(document,
    // // version.getVersionId());
    //
    // return response;
    // } catch (Throwable t) {
    // log.error("upload failed", t);
    // throw new RestServiceException(t.getLocalizedMessage());
    // } finally {
    // if (path != null) {
    // deleteFile(path);
    // }
    // }
    // }
    //
    // @POST
    // @Path("{id}/" + VERSIONS_PATH + "/{vid}/" + UPDATE_PATH)
    // @Consumes(MediaType.MULTIPART_FORM_DATA)
    // @Produces({ MediaType.APPLICATION_JSON })
    // public Response updateVersion(@PathParam("id") String id,
    // @PathParam("vid") String vid,
    // @FormDataParam("file") InputStream uploadedInputStream,
    // @FormDataParam("file") FormDataBodyPart body) {
    // log.info(String.format("uploadVersion %s for document id %s ", vid, id));
    // checkNotNull(body);
    // checkNotNull(body.getContentDisposition());
    // String filename = body.getContentDisposition().getFileName();
    //
    // String path = null;
    // long size = body.getContentDisposition().getSize();
    // String contentType = body.getMediaType().toString();
    // if (log.isTraceEnabled()) {
    // log.trace(String.format("uploadVersion %s for document %s - %s - %s - %s",
    // vid, id, filename, size, contentType));
    // }
    // try {
    // isAuthenticated();
    // byte[] content;
    // if (size > 16 * 1024 * 1024) {
    // path = System.getProperty("java.io.tmpdir") + System.currentTimeMillis()
    // + filename;
    // writeToFile(uploadedInputStream, path);
    // content = Files.readAllBytes(Paths.get(path));
    // } else {
    // content = toByteArray(uploadedInputStream);
    // }
    // File file = new
    // FileImpl.Builder().content(content).name(filename).contentType(contentType).build();
    //
    // Document document = service.get(id);
    // checkNotNull(document);
    //
    // if (!document.hasStatus(Document.DocumentStatus.AVAILABLE))
    // throw new
    // PreconditionException(String.format("uploadVersion: Document %s is not available for uploading a version",
    // id));
    //
    // Version version = document.getVersion(Integer.parseInt(vid));
    // checkNotNull(version);
    //
    // log.info(String.format("uploadVersion updating content to: [[[[" + new
    // String(content, "UTF-8") + "]]]]"));
    // documentService.setVersionContent(document, version.getVersionId(),
    // file);
    // Response response = Response.created(getItemUri(document)).build();
    //
    // return response;
    // } catch (Throwable t) {
    // log.error("uploadVersion: upload failed", t);
    // throw new RestServiceException(t.getLocalizedMessage());
    // } finally {
    // if (path != null) {
    // deleteFile(path);
    // }
    // }
    // }
    //
    // @POST
    // @Path("{id}/" + VERSIONS_PATH + "/{vid}/" + CURRENT_PATH)
    // // @Consumes(MediaType.APPLICATION_JSON)
    // @Produces({ MediaType.APPLICATION_JSON })
    // public Response setCurrentVersion(@PathParam("id") String id,
    // @PathParam("vid") String vid) {
    //
    // log.debug(String.format("RestDocumentService: setCurrentVersion for document %s to version %s",
    // id, vid));
    // try {
    // isAuthenticated();
    //
    // Document document = service.get(id);
    // checkNotNull(document);
    //
    // if (!document.hasStatus(Document.DocumentStatus.AVAILABLE))
    // throw new
    // PreconditionException(String.format("RestDocumentService: Document %s is not available for uploading a version",
    // id));
    //
    // Version version = document.getVersion(Integer.parseInt(vid));
    // checkNotNull(version);
    //
    // documentService.setCurrentVersion(document, version.getVersionId());
    //
    // Response response = Response.ok(
    // // getItemUri(document)
    // ).build();
    //
    // log.debug(String.format("RestDocumentService: document - %s, current version - %s, ",
    // document.getId(), version.getVersionId()));
    // log.debug("RestDocumentService: setCurrentVersion end ok");
    // return response;
    // } catch (Throwable t) {
    // log.error("RestDocumentService: setCurrentVersion failed", t);
    // throw new RestServiceException(t.getLocalizedMessage());
    // }
    // }
    //
    // @GET
    // @Path("{id}/{vid}/" + DOWNLOAD_PATH)
    // @Consumes(MediaType.APPLICATION_JSON)
    // public Response download(@PathParam("id") String id, @PathParam("vid")
    // String vid) {
    // try {
    // Document document = service.get(id);
    // checkNotNull(document);
    //
    // /*
    // * Danilo forse si puo usare un default per il vid per poi usare il
    // * getCurrentVersion e usare una sola version del metodo.
    // */
    //
    // Version version = document.getVersion(Integer.parseInt(vid));
    // checkNotNull(version);
    // checkNotNull(version.getFile());
    // ContentDispositionBuilder<?, ?> contentDisposition =
    // ContentDisposition.type("attachment");
    //
    // contentDisposition.fileName(version.getFile().getName());
    // if (version.getFile().getDate() != null) {
    // contentDisposition.creationDate(version.getFile().getDate());
    // }
    // ResponseBuilder rb = Response.ok();
    // rb.type(version.getFile().getContentType());
    // InputStream stream = new
    // ByteArrayInputStream(version.getFile().getContent());
    // rb.entity(stream);
    // rb.status(Status.OK);
    // rb.header("Content-Disposition", contentDisposition.build());
    // return rb.build();
    // } catch (Throwable t) {
    // log.error("download failed", t);
    // return Response.status(Status.NOT_FOUND).build();
    // }
    // }
    //
    // @GET
    // @Produces({ MediaType.APPLICATION_JSON })
    // @Path("{id}/" + VERSIONS_PATH)
    // public Response getVersions(@PathParam("id") String id) {
    // if (log.isTraceEnabled()) {
    // log.trace(String.format("versions - %s", id));
    // }
    // try {
    // Document document = service.get(id);
    // if (document == null) {
    // return Response.status(Status.NOT_FOUND).build();
    // }
    // return Response.ok(document.getVersions()).build();
    // } catch (ServiceException e) {
    // throw new RestServiceException(e.getLocalizedMessage());
    // }
    // }
    //
    // @POST
    // @Path("{id}/" + VERSIONS_PATH + "/{vid}/" + MARKDELETE_PATH)
    // @Consumes(MediaType.APPLICATION_JSON)
    // @Produces({ MediaType.APPLICATION_JSON })
    // public Response queueDelete(@PathParam("id") String id, @PathParam("vid")
    // String vid) {
    // try {
    // Document document = service.get(id);
    // checkNotNull(document);
    //
    // documentService.markDeleted(document);
    //
    // return Response.noContent().build();
    // } catch (ServiceException t) {
    // log.error(String.format("Document %s is not marked as available", id),
    // t);
    // return Response.status(Status.PRECONDITION_FAILED).build();
    // } catch (Throwable t) {
    // log.error(String.format("Check if document %s exists", id), t);
    // return Response.status(Status.NOT_FOUND).build();
    // }
    // }
    //
    // @DELETE
    // @Path("{id}/" + VERSIONS_PATH + "/{vid}/")
    // @Consumes(MediaType.APPLICATION_JSON)
    // @Produces({ MediaType.APPLICATION_JSON })
    // public Response delete(@PathParam("id") String id, @PathParam("vid")
    // String vid) {
    // try {
    // Document document = service.get(id);
    // checkNotNull(document);
    //
    // if (!document.hasStatus(Document.DocumentStatus.DELETED))
    // throw new
    // PreconditionException(String.format("Document %s is not marked as deleted",
    // id));
    //
    // Version version = document.getVersion(Integer.parseInt(vid));
    // checkNotNull(version);
    //
    // documentService.deleteVersion(document, version);
    //
    // return Response.noContent().build();
    // } catch (ServiceException t) {
    // log.error(String.format("Error processing deletion of version %s for document %s",
    // vid, id), t);
    // return Response.status(Status.PRECONDITION_FAILED).build();
    // } catch (Throwable t) {
    // log.error(String.format("Check if document %s exists", id), t);
    // return Response.status(Status.NOT_FOUND).build();
    // }
    // }

    @GET
    @Path("{id}/" + TAGS_PATH)
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getTags(@PathParam("id") String id) {
        try {
            Document document = documentService.getMetadata(id);
            checkNotNull(document);
            return Response.ok(document.getTags()).build();
        } catch (ServiceException t) {
            String message = String.format("Error getting tags for document %s", id);
            log.error(message, t);
            throw new RestServiceException(message);
        }
    }

    @POST
    @Path("{id}/" + TAGS_PATH + "/{tag}/")
    public Response addTag(@PathParam("id") String id, @PathParam("tag") String tag) {
        try {
            Document document = documentService.getMetadata(id);
            checkNotNull(document);

            document.getTags().add(tag);
            documentService.updateMetadata(document);
            return Response.created(getItemUri(document, TAGS_PATH, tag)).build();
            // return Response.ok(super.update(id, document)).build();
        } catch (ServiceException t) {
            String message = String.format("Error adding tag %s for document %s", tag, id);
            log.error(message, t);
            throw new RestServiceException(message);
        }
    }

    @DELETE
    @Path("{id}/" + TAGS_PATH + "/{tag}/")
    public Response deleteTag(@PathParam("id") String id, @PathParam("tag") String tag) {
        try {
            Document document = documentService.getMetadata(id);
            checkNotNull(document);

            if (document.getTags().contains(tag)) {
                document.getTags().remove(tag);
                document = documentService.updateMetadata(document);
                return Response.noContent().build();
            } else {
                return Response.status(Status.NOT_FOUND).build();
            }
        } catch (ServiceException t) {
            String message = String.format("Error deleting tag %s for document %s", tag, id);
            log.error(message, t);
            throw new RestServiceException(message);
        }
    }

    @DELETE
    @Path("{id}/" + TAGS_PATH)
    public Response deleteTags(@PathParam("id") String id) {
        try {
            Document document = documentService.getMetadata(id);
            checkNotNull(document);

            document.getTags().clear();
            documentService.updateMetadata(document);
            return Response.noContent().build();
        } catch (ServiceException t) {
            String message = String.format("Error deleting tags for document %s", id);
            log.error(message, t);
            throw new RestServiceException(message);
        }
    }

    /*
     * Save uploaded file to temp location
     */
    private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {
        try {
            log.debug(String.format("writeToFile - %s", uploadedFileLocation));
            OutputStream out = new FileOutputStream(new java.io.File(uploadedFileLocation));
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException ex) {
            log.error("writeToFile failed", ex);
        }
    }

    private byte[] toByteArray(InputStream is) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();

            return buffer.toByteArray();
        } catch (IOException ex) {
            log.error("toByteArray failed", ex);
        }
        return null;
    }

    private void deleteFile(String name) {
        try {
            java.io.File file = new java.io.File(name);
            if (!file.delete()) {
                log.warn(String.format("Could not delete file %s", name));
            }
        } catch (Throwable t) {
            log.error("deleteFile failed", t);
        }
    }

    private interface Preview {
        String getContent();
    }

}
