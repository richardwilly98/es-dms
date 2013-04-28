package com.github.richardwilly98.rest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.elasticsearch.common.Base64;
import org.joda.time.DateTime;

import com.github.richardwilly98.api.Document;
import com.github.richardwilly98.api.File;
import com.github.richardwilly98.api.exception.ServiceException;
import com.github.richardwilly98.api.services.DocumentService;
import com.github.richardwilly98.inject.ProviderModule;
import com.github.richardwilly98.rest.exception.RestServiceException;
import com.github.richardwilly98.services.DocumentProvider;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("/documents")
public class RestDocumentService extends RestServiceBase {

	private DocumentService provider;

	private DocumentService getProvider() {
		if (provider == null) {
			Injector injector = Guice.createInjector(new ProviderModule());
			provider = injector.getInstance(DocumentProvider.class);
		}
		return provider;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{id}")
	public Document get(@PathParam("id") String id) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("get - %s", id));
		}
		try {
			return getProvider().get(id);
		} catch (ServiceException e) {
			log.error("get document failed", e);
			throw new RestServiceException(e.getLocalizedMessage());
		}
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
			List<Document> documents = getProvider().search(criteria);
			return Response.status(Status.OK).entity(documents).build();
		} catch (ServiceException e) {
			log.error("search failed", e);
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/find/{name}")
	public Response find(@PathParam("name") String name) {
		isAuthenticated();
		if (log.isTraceEnabled()) {
			log.trace(String.format("find - %s", name));
		}
		try {
			List<Document> documents = getProvider().getList(name);
			return Response.status(Status.OK).entity(documents).build();
		} catch (ServiceException e) {
			log.error("find failed", e);
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(Document document) {
		isAuthenticated();
		if (document == null) {
			throw new IllegalArgumentException("document");
		}
		if (log.isTraceEnabled()) {
			log.trace(String.format("create - %s", document));
		}
		try {
			String id = getProvider().create(document);
			document.setId(id);
			return Response.status(Status.CREATED).entity(document).build();
		} catch (ServiceException e) {
			log.error("create failed", e);
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}

	@RequiresPermissions("document:delete")
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{id}")
	public Response delete(@PathParam("id") String id) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("get - %s", id));
		}
		try {
			Document document = getProvider().get(id);
			getProvider().delete(document);
			return Response.ok().build();
		} catch (ServiceException e) {
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}

	// @POST
	// @Path("/upload")
	// @Consumes(MediaType.MULTIPART_FORM_DATA)
	// public Response upload(FormDataMultiPart multiPart) {
	// if (log.isTraceEnabled()) {
	// log.trace(String.format("upload - %s", multiPart));
	// }
	// try {
	// for (String key : multiPart.getFields().keySet()) {
	// // log.debug(String.format("Key: %s - class: %s", key, multiPart
	// // .getField(key).getValue()));
	// try {
	// log.debug(String.format("Key: %s - %s", key,
	// multiPart.getField(key).getEntityAs(String.class)));
	// }
	// catch (Throwable t) {
	// log.error("", t);
	// }
	// }
	// } catch (Throwable t) {
	// log.error("upload failed", t);
	// }
	// return Response.status(200).build();
	//
	// }

	@RequiresPermissions("document:create")
	@POST
	@Path("/upload")
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
			writeToFile(uploadedInputStream, filename);
			String encodedContent = Base64.encodeFromFile(filename);
			File file = new File(encodedContent, fileDetail.getFileName(),
					fileDetail.getType());
			Map<String, Object> attributes = new HashMap<String, Object>();
			DateTime now = new DateTime();
			attributes.put(Document.CREATION_DATE, now.toString());
			attributes.put(Document.AUTHOR, getCurrentUser());
			Document document = new Document(null, file, attributes);
			String id = getProvider().create(document);
			log.debug(String.format("New document uploaded %s", id));
			document.setId(id);
			document.setFile(null);
			return Response.ok(document).build();
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
			// log.debug("Principal: " +
			// SecurityUtils.getSubject().getPrincipal());
			// if (! SecurityUtils.getSubject().hasRole("writer")) {
			// return Response.status(Status.UNAUTHORIZED).build();
			// }

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
			Document document = new Document(null, file, attributes);

			String id = getProvider().create(document);
			log.debug(String.format("New document uploaded %s", id));
			document.setId(id);
			document.setFile(null);
			return Response.status(200).entity(document).build();
		} catch (Throwable t) {
			log.error("upload failed", t);
			throw new RestServiceException(t.getLocalizedMessage());
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

//			out = new FileOutputStream(new java.io.File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException ex) {
			log.error("writeToFile failed", ex);
		}
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
