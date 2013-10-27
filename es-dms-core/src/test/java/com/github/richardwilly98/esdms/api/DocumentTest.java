package com.github.richardwilly98.esdms.api;

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

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.DocumentImpl;
import com.github.richardwilly98.esdms.FileImpl;
import com.github.richardwilly98.esdms.VersionImpl;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.Version;

public class DocumentTest extends DocumentImpl {

    private static final long serialVersionUID = 1L;

    DocumentTest() {
	this(null);
    }

    public DocumentTest(Builder builder) {
	super(builder);
    }

    @Override
    protected void setReadOnlyAttribute(String name, Object value) {
	super.setReadOnlyAttribute(name, value);
    }

    private Map<String, DocumentTest> getTestDocuments(int number) {

	Map<String, DocumentTest> docs = new HashMap<String, DocumentTest>();

	for (int i = 0; i < number; i++) {
	    String attributeKey = "attribute: " + i;
	    String attributeValue = "value: " + i;
	    String id = "id-" + i + " @time: " + System.currentTimeMillis();
	    String name = "name-" + i + " @time: " + System.currentTimeMillis();
	    String html = "<html><body><h1>This is document number: " + i + "</h1></body></html>";
	    byte[] content = html.getBytes();
	    Map<String, Object> attributes = newHashMap();
	    attributes.put(attributeKey, attributeValue);
	    // DocumentTest document = new DocumentTest(new DocumentImpl(id,
	    // name, new FileImpl(content, "test.html", "text/html"),
	    // attributes));
	    Set<Version> versions = newHashSet();
	    versions.add(new VersionImpl.Builder().documentId(id)
		    .file(new FileImpl.Builder().content(content).name("test" + i + ".html").contentType("text/html").build())
		    .current(true).versionId(1).build());
	    DocumentTest document = new DocumentTest(new DocumentImpl.Builder().versions(versions).id(id).name(name).attributes(attributes)
		    .roles(null));
	    // RLO - document status is not set at this point because it has not
	    // been serialized. Work-around force status = AVAILABLE.
	    document.setStatus(DocumentStatus.AVAILABLE);
	    // DocumentTest document = new DocumentTest(new
	    // DocumentImpl.Builder().file(new
	    // FileImpl.Builder().content(content).name("test" + i +
	    // ".html").contentType("text/html").build()).id(id).name(name).attributes(attributes).roles(null));
	    docs.put("" + i, document);
	}
	return docs;
    }

    void setStatus(DocumentStatus status) {
	super.setReadOnlyAttribute(DocumentSystemAttributes.STATUS.getKey(), status.getStatusCode());
    }

    @Test
    public void testDocumentStatus() throws Throwable {
	log.debug("*** testDocumentStatus tests: starting ***");
	log.debug("*** ********************************** ***");

	Map<String, DocumentTest> documents = getTestDocuments(10); // documentService.search("*",
								    // 0, 10);
	log.info(String.format("Document count: %s", documents.size()));
	Assert.assertNotNull(documents);

	DocumentTest doc = documents.get("" + 1);
	Assert.assertNotNull(doc);
	Assert.assertTrue(doc.hasStatus(Document.DocumentStatus.AVAILABLE));

	doc.setStatus(Document.DocumentStatus.LOCKED);
	Assert.assertTrue(doc.hasStatus(Document.DocumentStatus.LOCKED));

	doc.setStatus(Document.DocumentStatus.AVAILABLE);
	Assert.assertTrue(doc.hasStatus(Document.DocumentStatus.AVAILABLE));

	doc.setStatus(Document.DocumentStatus.DELETED);
	Assert.assertTrue(doc.hasStatus(Document.DocumentStatus.DELETED));

	log.debug("*** ************************************************ ***");
	log.debug("*** testDocumentStatus tests: completed successfully ***");
    }
}
