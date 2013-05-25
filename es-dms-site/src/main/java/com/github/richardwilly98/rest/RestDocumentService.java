package com.github.richardwilly98.rest;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.elasticsearch.common.Base64;
import org.joda.time.DateTime;

import com.github.richardwilly98.api.Document;
import com.github.richardwilly98.api.File;
import com.github.richardwilly98.api.exception.ServiceException;
import com.github.richardwilly98.api.services.AuthenticationService;
import com.github.richardwilly98.api.services.DocumentService;
import com.github.richardwilly98.rest.exception.RestServiceException;
import com.google.inject.Inject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path(RestDocumentService.DOCUMENTS_PATH)
public class RestDocumentService extends RestServiceBase<Document> {

	public static final String UPLOAD_PATH = "upload";
	public static final String DOCUMENTS_PATH = "documents";
	private final DocumentService documentService;

	@Inject
	public RestDocumentService(
			final AuthenticationService authenticationService,
			final DocumentService documentService) {
		super(authenticationService, documentService);
		this.documentService = documentService;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/search/{criteria}")
	public Response search(@PathParam("criteria") String criteria) {
		// public List<Document> search(@PathParam("criteria") String criteria)
		// {
		try {
			isAuthenticated();
			// log.debug("Principal: " +
			// SecurityUtils.getSubject().getPrincipal());
			// if (! SecurityUtils.getSubject().hasRole("writer")) {
			// return Response.status(Status.UNAUTHORIZED).build();
			// }
			if (log.isTraceEnabled()) {
				log.trace(String.format("search - %s", criteria));
			}
			List<Document> documents = documentService.search(criteria);
			return Response.status(Status.OK).entity(documents).build();
		} catch (ServiceException e) {
			log.error("search failed", e);
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}

	@POST
	@Path(UPLOAD_PATH)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response upload(@FormDataParam("name") String name,
			@FormDataParam("date") String date,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("upload - %s - %s - %s - %s - %s", name,
					date, fileDetail.getFileName(), fileDetail.getSize(),
					fileDetail.getType()));
		}
		String filename = null;
		try {
			isAuthenticated();
			filename = System.getProperty("java.io.tmpdir")
					+ System.currentTimeMillis() + fileDetail.getFileName();
			String encodedContent;
			if (fileDetail.getSize() > 16 * 1024 * 1024) {
				writeToFile(uploadedInputStream, filename);
				encodedContent = Base64.encodeFromFile(filename);
			} else {
				encodedContent = Base64.encodeBytes(toByteArray(uploadedInputStream));
			}
			File file = new File(encodedContent, fileDetail.getFileName(),
					fileDetail.getType());
			Map<String, Object> attributes = new HashMap<String, Object>();
			DateTime now = new DateTime();
			attributes.put(Document.CREATION_DATE, now.toString());
			attributes.put(Document.AUTHOR, getCurrentUser());
			Document document = new Document(null, name, file, attributes);
			return create(document);
		} catch (Throwable t) {
			log.error("upload failed", t);
			throw new RestServiceException(t.getLocalizedMessage());
		} finally {
			if (filename != null) {
				deleteFile(filename);
			}
		}
	}

	@POST
	@Path("/upload-old")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response uploadOld(@FormDataParam("name") String name,
			@FormDataParam("date") String date,
			@FormDataParam("file") FormDataBodyPart body) {
		try {
			isAuthenticated();
			FormDataContentDisposition fileDetail = body
					.getFormDataContentDisposition();
			if (log.isTraceEnabled()) {
				log.trace(String.format("upload - %s - %s - %s - %s - %s",
						name, date, fileDetail.getFileName(),
						fileDetail.getSize(), fileDetail.getType()));
			}
			byte[] content = body.getEntityAs(byte[].class);
			String contentType = body.getMediaType().toString();
			String encodedContent = Base64.encodeBytes(content);
			File file = new File(encodedContent, fileDetail.getFileName(),
					contentType);
			Map<String, Object> attributes = new HashMap<String, Object>();
			DateTime now = new DateTime();
			attributes.put(Document.CREATION_DATE, now.toString());
			attributes.put(Document.AUTHOR, getCurrentUser());
			Document document = new Document(null, name, file, attributes);
			return create(document);
		} catch (Throwable t) {
			log.error("upload failed", t);
			throw new RestServiceException(t.getLocalizedMessage());
		}
	}

	@POST
	@Path("{id}/checkout")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response checkout(@PathParam("id") String id) {
		try {
			Document document = service.get(id);
			documentService.checkout(document);
			return Response.noContent().build();
		} catch (Throwable t) {
			log.error("checkout failed", t);
			return Response.status(Status.CONFLICT).build();
			// throw new RestServiceException(t.getLocalizedMessage());
		}
	}

	@POST
	@Path("{id}/checkin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response checkin(@PathParam("id") String id) {
		try {
			Document document = service.get(id);
			documentService.checkin(document);
			return Response.noContent().build();
		} catch (Throwable t) {
			log.error("checkin failed", t);
			return Response.status(Status.CONFLICT).build();
			// throw new RestServiceException(t.getLocalizedMessage());
		}
	}

	/*
	 * Save uploaded file to temp location
	 */
	private void writeToFile(InputStream uploadedInputStream,
			String uploadedFileLocation) {
		try {
			log.debug(String.format("writeToFile - %s", uploadedFileLocation));
			OutputStream out = new FileOutputStream(new java.io.File(
					uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			// out = new FileOutputStream(new
			// java.io.File(uploadedFileLocation));
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
