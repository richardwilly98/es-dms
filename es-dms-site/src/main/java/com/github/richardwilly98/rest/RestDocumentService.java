package com.github.richardwilly98.rest;

import java.io.FileInputStream;
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

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.common.Base64;
import org.joda.time.DateTime;

import com.github.richardwilly98.api.Document;
import com.github.richardwilly98.api.File;
import com.github.richardwilly98.inject.ProviderModule;
import com.github.richardwilly98.services.DocumentProvider;
import com.github.richardwilly98.api.services.DocumentService;
import com.github.richardwilly98.api.exception.ServiceException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("/documents")
public class RestDocumentService {

	private static Logger log = Logger.getLogger(RestDocumentService.class);

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
			return getProvider().getDocument(id);
		} catch (ServiceException e) {
			log.error("get document failed", e);
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/search/{criteria}")
	public List<Document> search(@PathParam("criteria") String criteria) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("search - %s", criteria));
		}
		try {
			return getProvider().contentSearch(criteria);
		} catch (ServiceException e) {
			log.error("search failed", e);
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/find/{name}")
	public List<Document> find(@PathParam("name") String name) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("find - %s", name));
		}
		try {
			return getProvider().getDocuments(name);
		} catch (ServiceException e) {
			log.error("find failed", e);
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(Document document) {
		if (document == null) {
			throw new IllegalArgumentException("document");
		}
		if (log.isTraceEnabled()) {
			log.trace(String.format("create - %s", document));
		}
		try {
			String id = getProvider().createDocument(document);
			document.setId(id);
			return Response.status(201).entity(document).build();
		} catch (ServiceException e) {
			log.error("create failed", e);
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

	@POST
	@Path("/upload2")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response upload2(@FormDataParam("name") String name,
			@FormDataParam("date") String date,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("upload - %s - %s - %s - %s - %s", name,
					date, fileDetail.getFileName(), fileDetail.getSize(),
					fileDetail.getType()));
		}
		try {
			String filename = System.getProperty("java.io.tmpdir")
					+ fileDetail.getFileName();
			writeToFile(uploadedInputStream, filename);
			byte[] content = IOUtils.toByteArray(new FileInputStream(filename));
			String encodedContent = Base64.encodeBytes(content);
			File file = new File(encodedContent, fileDetail.getFileName(),
					fileDetail.getType());
			Document document = new Document(null, file);
			String id = getProvider().createDocument(document);
			log.debug(String.format("New document uploaded %s", id));
			return Response.status(200).build();
		} catch (Throwable t) {
			log.error("upload2 failed", t);
			throw new RestServiceException(t.getLocalizedMessage());
		}
	}

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response upload(@FormDataParam("name") String name,
			@FormDataParam("date") String date,
			@FormDataParam("file") FormDataBodyPart body) {
		try {
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
			attributes.put(Document.AUTHOR, "rlouapre");
			Document document = new Document(null, file, attributes);

			String id = getProvider().createDocument(document);
			log.debug(String.format("New document uploaded %s", id));
			document.setId(id);
			document.setFile(null);
			return Response.status(200).entity(document).build();
		} catch (Throwable t) {
			log.error("upload failed", t);
			throw new RestServiceException(t.getLocalizedMessage());
		}
	}

	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream,
			String uploadedFileLocation) {
		try {
			OutputStream out = new FileOutputStream(new java.io.File(
					uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new java.io.File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
}
