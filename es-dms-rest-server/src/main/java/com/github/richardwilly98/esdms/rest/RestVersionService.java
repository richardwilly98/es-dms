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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.ContentDisposition.ContentDispositionBuilder;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.github.richardwilly98.esdms.FileImpl;
import com.github.richardwilly98.esdms.VersionImpl;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.File;
import com.github.richardwilly98.esdms.api.Version;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.rest.exception.PreconditionException;
import com.github.richardwilly98.esdms.rest.exception.RestServiceException;
import com.github.richardwilly98.esdms.services.AuthenticationService;
import com.github.richardwilly98.esdms.services.DocumentService;
import com.google.common.base.Strings;

public class RestVersionService extends RestServiceBase {

    public static final String VERSION_ID_PARAMETER = "vi";

    private final DocumentService documentService;

    @Inject
    public RestVersionService(final AuthenticationService authenticationService, final DocumentService documentService) {
        super(authenticationService);
        this.documentService = documentService;
    }

    protected URI getItemUri(UriInfo uriInfo, Version version) {
        checkNotNull(uriInfo);
        checkNotNull(version);
        return uriInfo.getBaseUriBuilder().path(RestDocumentService.DOCUMENTS_PATH).path(version.getDocumentId())
                .path(RestDocumentService.VERSIONS_PATH).path(String.valueOf(version.getVersionId())).build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response get(@PathParam("id") String id) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("versions from document - %s", id));
        }
        try {
            Document document = documentService.getMetadata(id);
            if (document == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            return Response.ok(document.getVersions()).build();
        } catch (ServiceException e) {
            throw new RestServiceException(e.getLocalizedMessage());
        }
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{vid}")
    public Response get(@PathParam("id") String id, @PathParam("vid") int vid) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("getVersion - %s - %s", id, vid));
        }
        try {
            Document document = documentService.getMetadata(id);
            if (document == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            Version version = documentService.getVersion(document, vid);
            if (version == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            return Response.ok(version).build();
        } catch (ServiceException e) {
            throw new RestServiceException(e.getLocalizedMessage());
        }
    }

    @POST
    @Path(RestDocumentService.UPLOAD_PATH)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({ MediaType.APPLICATION_JSON })
    public Response upload(@Context UriInfo uriInfo, @PathParam("id") String id, @FormDataParam("name") String name,
            @FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataBodyPart body,
            @QueryParam(VERSION_ID_PARAMETER) @DefaultValue("0") int vid) {
        log.info(String.format("uploadVersion to document %s from version %s ", id, vid));
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
            log.trace(String.format("uploadVersion for document - %s - %s - %s - %s", name, filename, size, contentType));
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

            Document document = documentService.get(id);
            checkNotNull(document);

            if (!document.hasStatus(Document.DocumentStatus.AVAILABLE))
                throw new PreconditionException(String.format("uploadVersion: Document %s is not available for uploading a version", id));

            Version parentVersion;
            if (vid == 0) {
                parentVersion = document.getCurrentVersion();
            } else {
                parentVersion = document.getVersion(vid);
            }
            if (parentVersion == null) {
                throw new RestServiceException(String.format("Cannot find current version or version %s", vid));
            }

            Version version = new VersionImpl.Builder().documentId(document.getId()).current(true).file(file)
                    .parentId(parentVersion.getVersionId()).versionId(document.getVersions().size() + 1).name(name).roles(null).build();
            documentService.addVersion(document, version);
            // documentService.setCurrentVersion(document,
            // version.getVersionId());
            log.debug("New version updloaded: " + version);
            // Response response = Response.created(getItemUri(document,
            // VERSIONS_PATH, String.valueOf(version.getVersionId()))).build();
            Response response = Response.created(getItemUri(uriInfo, version)).build();

            return response;
        } catch (Throwable t) {
            log.error("uploadVersion: upload failed", t);
            throw new RestServiceException(t.getLocalizedMessage());
        } finally {
            if (path != null) {
                deleteFile(path);
            }
        }
    }

    // @POST
    // @Path("{vid}/" + UPLOAD_PATH)
    // @Consumes(MediaType.MULTIPART_FORM_DATA)
    // @Produces({ MediaType.APPLICATION_JSON })
    // public Response uploadVersion(@Context UriInfo uriInfo, @PathParam("id")
    // String id, @PathParam("vid") String vid,
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
    // Document document = documentService.get(id);
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
    // // Response response =
    // // Response.created(getItemUri(document)).build();
    // Version version = new
    // VersionImpl.Builder().documentId(document.getId()).current(true).file(file)
    // .parentId(Integer.parseInt(vid)).versionId(document.getVersions().size()
    // + 1).build();
    // documentService.addVersion(document, version);
    // Response response = Response.created(getItemUri(uriInfo,
    // version)).build();
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

    @PUT
    @Path("{vid}/" + RestDocumentService.UPLOAD_PATH)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({ MediaType.APPLICATION_JSON })
    public Response update(@Context UriInfo uriInfo, @PathParam("id") String id, @PathParam("vid") int vid,
            @FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataBodyPart body) {
        log.info(String.format("uploadVersion %s for document id %s ", vid, id));
        checkNotNull(body);
        checkNotNull(body.getContentDisposition());
        String filename = body.getContentDisposition().getFileName();

        String path = null;
        long size = body.getContentDisposition().getSize();
        String contentType = body.getMediaType().toString();
        if (log.isTraceEnabled()) {
            log.trace(String.format("uploadVersion %s for document %s - %s - %s - %s", vid, id, filename, size, contentType));
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

            Document document = documentService.get(id);
            checkNotNull(document);

            if (!document.hasStatus(Document.DocumentStatus.AVAILABLE))
                throw new PreconditionException(String.format("uploadVersion: Document %s is not available for uploading a version", id));

            Version version = document.getVersion(vid);
            checkNotNull(version);

            log.info(String.format("uploadVersion updating content to: [[[[" + new String(content, "UTF-8") + "]]]]"));
            documentService.setVersionContent(document, version.getVersionId(), file);
            // Response response =
            // Response.created(getItemUri(document)).build();
            Response response = Response.created(getItemUri(uriInfo, version)).build();

            return response;
        } catch (Throwable t) {
            log.error("uploadVersion: upload failed", t);
            throw new RestServiceException(t.getLocalizedMessage());
        } finally {
            if (path != null) {
                deleteFile(path);
            }
        }
    }

    // @GET
    // @Produces({ MediaType.APPLICATION_JSON })
    // @Path("{vid}/" + PREVIEW_PATH)
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
    // Document document = documentService.get(id);
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

    @POST
    @Path("{vid}/" + RestDocumentService.CURRENT_PATH)
    @Produces({ MediaType.APPLICATION_JSON })
    public Response setCurrentVersion(@PathParam("id") String id, @PathParam("vid") int vid) {
        try {
            isAuthenticated();
            if (log.isTraceEnabled()) {
                log.trace(String.format("setCurrentVersion for document %s to version %s", id, vid));
            }
            Document document = documentService.getMetadata(id);
            if (document == null) {
                throw new RestServiceException(String.format("Cannot found document %s", id));
            }
            if (!document.hasStatus(Document.DocumentStatus.AVAILABLE))
                throw new PreconditionException(String.format("RestDocumentService: Document %s is not available for uploading a version",
                        id));
            Version version = document.getVersion(vid);
            if (version == null) {
                throw new RestServiceException(String.format("Cannot found version %s", id));
            }
            documentService.setCurrentVersion(document, version.getVersionId());
            return Response.ok().build();
        } catch (Throwable t) {
            log.error("RestDocumentService: setCurrentVersion failed", t);
            throw new RestServiceException(t.getLocalizedMessage());
        }
    }

    @GET
    @Path("{vid}/" + RestDocumentService.DOWNLOAD_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response download(@PathParam("id") String id, @PathParam("vid") String vid) {
        try {
            Document document = documentService.get(id);
            checkNotNull(document);

            /*
             * Danilo forse si puo usare un default per il vid per poi usare il
             * getCurrentVersion e usare una sola version del metodo.
             */

            Version version = document.getVersion(Integer.parseInt(vid));
            checkNotNull(version);
            checkNotNull(version.getFile());
            ContentDispositionBuilder<?, ?> contentDisposition = ContentDisposition.type("attachment");

            log.debug("DOWNLOAD VERSION: " + version.getFile().getName());
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

    // @POST
    // @Path("{vid}/" + MARKDELETE_PATH)
    // @Consumes(MediaType.APPLICATION_JSON)
    // @Produces({ MediaType.APPLICATION_JSON })
    // public Response queueDelete(@PathParam("id") String id, @PathParam("vid")
    // String vid) {
    // try {
    // Document document = documentService.get(id);
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

    // @DELETE
    // @Path("{vid}")
    // @Consumes(MediaType.APPLICATION_JSON)
    // @Produces({ MediaType.APPLICATION_JSON })
    // public Response delete(@PathParam("id") String id, @PathParam("vid")
    // String vid) {
    // try {
    // Document document = documentService.get(id);
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

}
