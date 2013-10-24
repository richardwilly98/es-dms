package test.github.richardwilly98.esdms.services;

/*
 * #%L
 * es-dms-service
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
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.shiro.authz.UnauthorizedException;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.DocumentImpl;
import com.github.richardwilly98.esdms.FileImpl;
import com.github.richardwilly98.esdms.VersionImpl;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.Document.DocumentStatus;
import com.github.richardwilly98.esdms.api.Document.DocumentSystemAttributes;
import com.github.richardwilly98.esdms.api.File;
import com.github.richardwilly98.esdms.api.Permission;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.api.Version;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.inject.SystemParametersModule;
import com.github.richardwilly98.esdms.services.DocumentProvider;
import com.github.richardwilly98.esdms.services.UserService;
import com.google.common.collect.ImmutableSet;

/*
 * https://github.com/shairontoledo/elasticsearch-attachment-tests/blob/master/src/test/java/net/hashcode/esattach/AttachmentTest.java
 */
public class DocumentProviderTest extends ProviderTestBase {

    private SearchResult<Document> searchDocument(String criteria, int first, int pageSize) throws Throwable {
        SearchResult<Document> searchResult = documentService.search(criteria, first, pageSize);
        Assert.assertNotNull(searchResult);
        return searchResult;
    }

    private Document addVersion(Document document, int versionId, File file, int parentId) throws ServiceException {
        try {
            if (parentId == 0) {
                parentId = document.getCurrentVersion().getVersionId();
            }
            Version version = new VersionImpl.Builder().documentId(document.getId()).file(file).versionId(versionId).parentId(parentId)
                    .build();
            documentService.addVersion(document, version);
            Document updatedDocument = documentService.get(document.getId());
            Assert.assertNotNull(updatedDocument);
            return updatedDocument;
        } catch (ServiceException sEx) {
            Assert.fail("addVersion failed", sEx);
            throw sEx;
        }
    }

    private void deleteDocument(Document document) throws ServiceException {
        documentService.markDeleted(document);
        document = documentService.get(document.getId());
        documentService.delete(document);
    }

    @Test
    public void testCreateDocument() throws Throwable {
        log.info("Start testCreateDocument");
        User user = null;
        for (User u : users) {
            for (Role role : u.getRoles()) {
                for (Permission permission : role.getPermissions()) {
                    if ("document:create".equals(permission.getId())) {
                        user = u;
                        break;
                    }
                }
            }
        }
        Assert.assertNotNull(user);
        login(user);
        // TODO: Not sure the reason PDF parsing does not work anymore. To be
        // investigated...
        // testCreateDocument("lorem.pdf", "application/pdf",
        // "/test/github/richardwilly98/services/lorem.pdf",
        // "Lorem ipsum dolor");
        createDocument("test-attachment.html", "text/html", "/test/github/richardwilly98/services/test-attachment.html"/*
                                                                                                                        * ,
                                                                                                                        * "Aliquam"
                                                                                                                        */);
    }

    @Test
    public void testCreateDeleteDocument() throws Throwable {
        log.info("Start testCreateDeleteDocument ************************************");
        User user = null;
        for (User u : users) {
            for (Role role : u.getRoles()) {
                for (Permission permission : role.getPermissions()) {
                    if ("document:create".equals(permission.getId())) {
                        user = u;
                        break;
                    }
                }
            }
        }
        log.info("testCreateDeleteDocument: step 1");
        Assert.assertNotNull(user);
        login(user);

        log.info("testCreateDeleteDocument: step 2");
        String id = createDocument("test-attachment.html", "text/html", "/test/github/richardwilly98/services/test-attachment.html");
        Document document = documentService.get(id);
        log.info("testCreateDeleteDocument: step 3");
        Assert.assertNotNull(document);
        try {
            documentService.delete(document);
            Assert.fail("Cannot delete document if it has not been marked for deletion.");
        } catch (Exception e) {
            log.info(String.format("Document %s not deleted without having been marked for deletion. Exception raised!", id));
            log.info(e.getLocalizedMessage());
        }
        log.info("testCreateDeleteDocument: step 4");
        documentService.markDeleted(document);

        document = documentService.get(id);
        Assert.assertTrue(document.hasStatus(DocumentStatus.DELETED));

        log.info("testCreateDeleteDocument: step 5");
        documentService.delete(document);
        log.info("End testCreateDeleteDocument successfully*****************************");
    }

    @Test
    public void testCannotCreateDocument() throws Throwable {
        log.info("*** testCannotCreateDocument ***");
        User user = createUser("read-user-", "Reader", false, "reader@reader", "secret".toCharArray(), ImmutableSet.of(readerRole));
        login(user);
        try {
            createDocument("test-attachment.html", "text/html", "/test/github/richardwilly98/services/test-attachment.html");
            Assert.fail("Should not be authorized to create document");
        } catch (UnauthorizedException uEx) {
        }
    }

    // TODO: hightlighting does not seem to work in unit test.
    @Test(enabled = false)
    public void testHighlightDocument() throws Throwable {
        log.info("Start testHighlightDocument");
        loginAdminUser();
        String id = createDocument("test-attachment.html", "text/html", "/test/github/richardwilly98/services/test-attachment.html");

        Document document = documentService.get(id);
        String preview = documentService.preview(document, "Aliquam", 0);
        Assert.assertNotNull(preview);
        Assert.assertTrue(preview.contains("Aliquam"));
        log.info(String.format("document preview: %s", preview));
    }

    @Test
    public void testSearchDocument() throws Throwable {

        log.info("Start testSearchDocument");
        loginAdminUser();
        int max = 15;
        int i = 0;
        while (i++ < max) {
            createDocument("test-attachment.html", "text/html", "/test/github/richardwilly98/services/test-attachment.html");
        }
        i = 0;
        while (i++ < max) {
            createDocument("Gingerbread", "text/plain", "/test/github/richardwilly98/services/test-attachment.txt");
        }
        SearchResult<Document> result = searchDocument("Gingerbread", 0, max);
        log.debug(String.format("Total hits: %s", result.getTotalHits()));
        Assert.assertTrue(result.getTotalHits() >= max);
        for (Document document : result.getItems()) {
            Assert.assertNotNull(document);
            Assert.assertNull(document.getCurrentVersion());
        }
    }

    @Test
    public void testCreateDocumentWithAuthor() throws Throwable {
        log.info("*** testCreateDocumentWithAuthor ***");
        loginAdminUser();
        String id = String.valueOf(System.currentTimeMillis());
        String name = "document-" + id;
        Map<String, Object> attributes = newHashMap();
        Document document = new DocumentImpl.Builder().attributes(attributes).id(id).name(name).roles(null).build();
        document.setId(id);
        Document newDocument = documentService.create(document);
        Assert.assertNotNull(newDocument);
        log.info(String.format("New document created %s", newDocument));
        attributes = newDocument.getAttributes();
        Assert.assertTrue(attributes != null && attributes.size() > 1);
        Assert.assertTrue(attributes.containsKey(DocumentSystemAttributes.AUTHOR.getKey()));
        String author = attributes.get(DocumentSystemAttributes.AUTHOR.getKey()).toString();
        Assert.assertTrue(!author.isEmpty());
        // Test ignore set read-only attribute
        newDocument.setAttribute(DocumentSystemAttributes.AUTHOR.getKey(), author + "-" + System.currentTimeMillis());
        Document updatedDocument = documentService.update(newDocument);
        Assert.assertTrue(updatedDocument.getAttributes().get(DocumentSystemAttributes.AUTHOR.getKey()).toString().equals(author));
    }

    @Test
    public void testCreateDocumentWithCreationDate() throws Throwable {
        log.info("*** testCreateDocumentWithCreationDate ***");
        loginAdminUser();
        String id = String.valueOf(System.currentTimeMillis());
        String name = "document-" + id;
        Map<String, Object> attributes = newHashMap();
        Document document = new DocumentImpl.Builder().attributes(attributes).id(id).name(name).roles(null).build();
        document.setId(id);
        Document newDocument = documentService.create(document);
        Assert.assertNotNull(newDocument);
        log.info(String.format("New document created %s", newDocument));
        attributes = newDocument.getAttributes();

        Assert.assertTrue(attributes != null && attributes.size() > 1);
        Assert.assertTrue(attributes.containsKey(DocumentSystemAttributes.CREATION_DATE.getKey()));
        log.info(attributes.get(DocumentSystemAttributes.CREATION_DATE.getKey()));
        Date newDate = new Date(Long.valueOf(attributes.get(DocumentSystemAttributes.CREATION_DATE.getKey()).toString()));
        log.info(String.format("Attribute %s - %s", DocumentSystemAttributes.CREATION_DATE.getKey(), newDate));
    }

    @Test
    public void testCheckinCheckoutDocument() throws Throwable {
        log.info("*** testCheckinCheckoutDocument ***");
        loginAdminUser();
        String id = String.valueOf(System.currentTimeMillis());
        String name = "document-" + id;
        Map<String, Object> attributes = newHashMap();
        Document document = new DocumentImpl.Builder().attributes(attributes).id(id).name(name).roles(null).build();
        document.setId(id);
        Document newDocument = documentService.create(document);
        Assert.assertNotNull(newDocument);
        log.info(String.format("New document created %s", newDocument));
        log.debug(String.format("Checkout document %s", newDocument.getId()));
        documentService.checkout(newDocument);
        newDocument = documentService.get(newDocument.getId());
        log.trace(String.format("Document checked-out %s", newDocument));
        try {
            documentService.checkout(newDocument);
            Assert.fail("Should not be authorized to check-out document twice");
        } catch (ServiceException sEx) {
        }

        attributes = newDocument.getAttributes();
        Assert.assertNotNull(attributes.get(DocumentSystemAttributes.STATUS.getKey()));
        Assert.assertTrue(attributes.get(DocumentSystemAttributes.STATUS.getKey()).equals(
                DocumentImpl.DocumentStatus.LOCKED.getStatusCode()));
        Assert.assertNotNull(attributes.get(DocumentSystemAttributes.LOCKED_BY.getKey()));
        Assert.assertTrue(attributes.get(DocumentSystemAttributes.LOCKED_BY.getKey()).equals(UserService.DEFAULT_ADMIN_LOGIN));
        Assert.assertNotNull(attributes.get(DocumentSystemAttributes.MODIFIED_DATE.getKey()));

        log.debug(String.format("Checkin document %s", newDocument.getId()));
        documentService.checkin(newDocument);
        newDocument = documentService.get(newDocument.getId());
        log.trace(String.format("Document checked-in %s", newDocument));
        Assert.assertTrue(newDocument.getAttributes().get(DocumentSystemAttributes.STATUS.getKey())
                .equals(DocumentImpl.DocumentStatus.AVAILABLE.getStatusCode()));
        Assert.assertFalse(newDocument.getAttributes().containsKey(DocumentSystemAttributes.LOCKED_BY.getKey()));

        try {
            documentService.checkin(newDocument);
            Assert.fail("Should not be authorized to check-out document twice");
        } catch (ServiceException sEx) {
        }
    }

    @Test
    public void testCreateDocumentWithVersions() throws Throwable {
        log.info("*** testCreateDocumentWithVersions ***");
        loginAdminUser();
        String id = String.valueOf(System.currentTimeMillis());
        String name = "document-" + id;
        Map<String, Object> attributes = newHashMap();
        String html = "<html><body><h1>Hello World</h1></body></html>";
        String html2 = "<html><body><h1>Version 2</h1></body></html>";
        byte[] content = html.getBytes();
        byte[] content2 = html2.getBytes();
        Set<Version> versions = newHashSet();
        versions.add(new VersionImpl.Builder().documentId(id)
                .file(new FileImpl.Builder().content(content).name("test.html").contentType("text/html").build()).current(true)
                .versionId(1).build());
        Document document = new DocumentImpl.Builder().versions(versions).attributes(attributes).id(id).name(name).roles(null).build();
        document.setId(id);
        Document newDocument = documentService.create(document);
        Assert.assertNotNull(newDocument);
        log.info(String.format("New document created %s", newDocument));
        Assert.assertNotNull(newDocument.getCurrentVersion());
        Assert.assertNotNull(newDocument.getVersions());
        Assert.assertTrue(newDocument.getVersions().size() == 1);
        Assert.assertTrue(newDocument.getCurrentVersion().getVersionId() == 1);

        newDocument = addVersion(newDocument, 2, new FileImpl.Builder().content(content2).name("test.html").contentType("text/html")
                .build(), 0);
        // Version version2 = new VersionImpl.Builder()
        // .documentId(id)
        // .file(new FileImpl.Builder().content(content2)
        // .name("test.html").contentType("text/html").build())
        // .current(false).versionId(2).parentId(1).build();
        // documentService.addVersion(newDocument, version2);
        //
        // newDocument = documentService.get(id);
        log.info(String.format("Version #2 added to document %s", newDocument));
        Assert.assertTrue(newDocument.getVersions().size() == 2);
        Assert.assertTrue(newDocument.getCurrentVersion().getVersionId() == 2);

        // Version #1 is not current anymore
        Version v = newDocument.getVersion(1);
        Assert.assertNotNull(v);
        Assert.assertFalse(v.isCurrent());
        Assert.assertNull(v.getFile());

        // Version #2 is current and has content
        v = newDocument.getVersion(2);
        Assert.assertNotNull(v);
        Assert.assertTrue(v.isCurrent());
        Assert.assertNotNull(v.getFile());
        Assert.assertEquals(v.getFile().getContent(), content2);

        // Test retrieve archived version (not current)
        v = documentService.getVersion(newDocument, newDocument.getVersion(1).getVersionId());
        Assert.assertNotNull(v);
        Assert.assertNotNull(v.getFile());
        Assert.assertEquals(v.getFile().getContent(), content);

        // Test delete version
        documentService.deleteVersion(newDocument, v);
        newDocument = documentService.get(id);
        Assert.assertTrue(newDocument.getVersions().size() == 1);
        Assert.assertTrue(newDocument.getCurrentVersion().getVersionId() == 2);
    }

    @Test
    public void testSerializeDeserializeVersions() throws Throwable {
        log.info("*** testSerializeDeserializeVersions ***");
        loginAdminUser();
        String id = String.valueOf(System.currentTimeMillis());
        String name = "document-" + id;
        Map<String, Object> attributes = newHashMap();
        String html = "<html><body><h1>Hello World</h1></body></html>";
        byte[] content = html.getBytes();
        Set<Version> versions = newHashSet();

        Version version1 = new VersionImpl.Builder().documentId(id)
                .file(new FileImpl.Builder().content(content).name("test.html").contentType("text/html").build()).current(true)
                .versionId(1).build();

        Version version2 = new VersionImpl.Builder().documentId(id)
                .file(new FileImpl.Builder().content(content).name("test.html").contentType("text/html").build()).versionId(2).parentId(1)
                .build();

        Version version3 = new VersionImpl.Builder().documentId(id)
                .file(new FileImpl.Builder().content(content).name("test.html").contentType("text/html").build()).versionId(3).parentId(2)
                .build();

        versions.add(version1);
        Document document = new DocumentImpl.Builder().versions(versions).attributes(attributes).id(id).name(name).roles(null).build();
        document.setId(id);

        Document newDocument = documentService.create(document);
        Assert.assertNotNull(newDocument);

        newDocument = addVersion(newDocument, 2, version2.getFile(), 0);

        // documentService.addVersion(newDocument, version2);
        // newDocument = documentService.get(id);
        log.info(String.format("Document updated - %s", newDocument));

        Version v2 = newDocument.getVersion(2);
        Assert.assertNotNull(v2);
        Assert.assertTrue(v2.isCurrent());
        Assert.assertEquals(v2.getFile().getContentType(), version2.getFile().getContentType());
        Assert.assertEquals(v2.getFile().getContent(), version2.getFile().getContent());
        Assert.assertEquals(v2.getParentId(), version2.getParentId());
        Assert.assertEquals(v2.getDocumentId(), newDocument.getId());

        newDocument = addVersion(newDocument, 3, version3.getFile(), 0);

        // documentService.addVersion(newDocument, version3);
        // newDocument = documentService.get(id);
        log.info(String.format("Document updated - %s", newDocument));

        newDocument = documentService.get(id);
        log.info(String.format("Document updated - %s", newDocument));

        Version v3 = newDocument.getVersion(3);
        Assert.assertNotNull(v3);
        Assert.assertTrue(v3.isCurrent());
        Assert.assertEquals(v3.getFile().getContentType(), version3.getFile().getContentType());
        Assert.assertEquals(v3.getFile().getContent(), version3.getFile().getContent());
        Assert.assertEquals(v3.getParentId(), version3.getParentId());
        Assert.assertEquals(v3.getDocumentId(), newDocument.getId());

        v2 = newDocument.getVersion(2);
        Assert.assertNotNull(v2);
        Assert.assertFalse(v2.isCurrent());
        Assert.assertNull(v2.getFile());
        Assert.assertEquals(v2.getParentId(), version2.getParentId());
        Assert.assertEquals(v2.getDocumentId(), newDocument.getId());
    }

    @Test
    public void testAddVersion() throws Throwable {
        log.info("*** testAddVersion ***");
        loginAdminUser();
        String id = String.valueOf(System.currentTimeMillis());
        String name = "document-" + id;
        Map<String, Object> attributes = newHashMap();
        String html = "<html><body><h1>Hello World</h1></body></html>";
        byte[] content = html.getBytes();
        Set<Version> versions = newHashSet();
        versions.add(new VersionImpl.Builder().documentId(id)
                .file(new FileImpl.Builder().content(content).name("test.html").contentType("text/html").build()).current(true)
                .versionId(1).build());
        Document document = new DocumentImpl.Builder().versions(versions).attributes(attributes).id(id).name(name).roles(null).build();
        document.setId(id);

        Document newDocument = documentService.create(document);
        Assert.assertNotNull(newDocument);

        Version version2 = new VersionImpl.Builder().documentId(id)
                .file(new FileImpl.Builder().content(content).name("test.html").contentType("text/html").build()).versionId(2).build();
        documentService.addVersion(newDocument, version2);
        newDocument = documentService.get(id);
        log.info(String.format("Document updated - %s", newDocument));

        Version v1 = newDocument.getVersion(1);
        Assert.assertNotNull(v1);
        Assert.assertNotNull(v1.getId());
        Assert.assertFalse(v1.isCurrent());

        Version v2 = newDocument.getVersion(2);
        Assert.assertNotNull(v2);
        Assert.assertNull(v2.getId());
        Assert.assertTrue(v2.isCurrent());
        Assert.assertEquals(v2.getParentId(), v1.getVersionId());
    }

    @Test
    public void testDeleteVersion() throws Throwable {
        log.info("*** testDeleteVersion ***");
        loginAdminUser();
        String id = String.valueOf(System.currentTimeMillis());
        String name = "document-" + id;
        Map<String, Object> attributes = newHashMap();
        String html = "<html><body><h1>Hello World</h1></body></html>";
        byte[] content = html.getBytes();
        Set<Version> versions = newHashSet();
        versions.add(new VersionImpl.Builder().documentId(id)
                .file(new FileImpl.Builder().content(content).name("test.html").contentType("text/html").build()).current(true)
                .versionId(1).build());
        Document document = new DocumentImpl.Builder().versions(versions).attributes(attributes).id(id).name(name).roles(null).build();
        document.setId(id);

        Document newDocument = documentService.create(document);
        Assert.assertNotNull(newDocument);

        Version version2 = new VersionImpl.Builder().documentId(id)
                .file(new FileImpl.Builder().content(content).name("test.html").contentType("text/html").build()).versionId(2).parentId(1)
                .build();
        documentService.addVersion(newDocument, version2);
        newDocument = documentService.get(id);
        log.info(String.format("Document updated - %s", newDocument));

        Version v2 = newDocument.getVersion(2);
        Assert.assertNotNull(v2);
        Assert.assertTrue(v2.isCurrent());
        Assert.assertEquals(v2.getParentId(), 1);

        Version version3 = new VersionImpl.Builder().documentId(id)
                .file(new FileImpl.Builder().content(content).name("test.html").contentType("text/html").build()).versionId(3).parentId(2)
                .build();
        documentService.addVersion(newDocument, version3);
        newDocument = documentService.get(id);
        log.info(String.format("Document updated - %s", newDocument));

        // Version #3 is current and has content
        Version v3 = newDocument.getCurrentVersion();
        log.info(String.format("Version #3 - %s", v3));
        Assert.assertNotNull(v3);
        Assert.assertTrue(v3.isCurrent());
        Assert.assertEquals(v3.getParentId(), 2);

        v2 = newDocument.getVersion(2);
        log.info(String.format("Version #2 - %s", v2));
        Assert.assertNotNull(v2);
        Assert.assertFalse(v2.isCurrent());

        Assert.assertTrue(newDocument.getVersions().size() == 3);

        // Test delete version #2
        documentService.deleteVersion(newDocument, v2);
        newDocument = documentService.get(id);
        Assert.assertTrue(newDocument.getVersions().size() == 2);
        log.info(String.format("Document after version #2 has been deleted - %s", newDocument));

        // Test delete current version #2
        v3 = newDocument.getVersion(3);
        documentService.deleteVersion(newDocument, v3);
        newDocument = documentService.get(id);
        log.info(String.format("Document after version #3 has been deleted - %s", newDocument));
        Assert.assertTrue(newDocument.getVersions().size() == 1);
        Assert.assertTrue(newDocument.getCurrentVersion().getVersionId() == 1);
    }

    @Test
    public void testChangeCurrentVersion() throws Throwable {
        log.info("*** testChangeCurrentVersion ***");
        loginAdminUser();
        String id = String.valueOf(System.currentTimeMillis());
        String name = "document-" + id;
        Map<String, Object> attributes = newHashMap();
        String html = "<html><body><h1>Hello World</h1></body></html>";
        byte[] content = html.getBytes();
        Set<Version> versions = newHashSet();
        versions.add(new VersionImpl.Builder().documentId(id)
                .file(new FileImpl.Builder().content(content).name("test.html").contentType("text/html").build()).current(true)
                .versionId(1).build());
        Document document = new DocumentImpl.Builder().versions(versions).attributes(attributes).id(id).name(name).roles(null).build();
        document.setId(id);

        Document newDocument = documentService.create(document);
        Assert.assertNotNull(newDocument);

        Version version2 = new VersionImpl.Builder().documentId(id)
                .file(new FileImpl.Builder().content(content).name("test.html").contentType("text/html").build()).versionId(2).parentId(1)
                .build();
        documentService.addVersion(newDocument, version2);
        newDocument = documentService.get(id);
        log.info(String.format("Document updated - %s", newDocument));
        Version v2 = newDocument.getVersion(2);
        Assert.assertTrue(v2.isCurrent());

        documentService.setCurrentVersion(newDocument, 1);
        newDocument = documentService.get(id);
        log.info(String.format("Document updated (set current version #1) - %s", newDocument));
        Version v1 = newDocument.getVersion(1);
        Assert.assertTrue(v1.isCurrent());
    }

    @Test
    public void testDocumentWithTags() throws Throwable {
        log.info("*** testDocumentWithTags ***");
        loginAdminUser();
        String id = String.valueOf(System.currentTimeMillis());
        String name = "document-" + id;
        Map<String, Object> attributes = newHashMap();
        Document document = new DocumentImpl.Builder().attributes(attributes).id(id).name(name).roles(null).build();
        document.setId(id);
        document = documentService.create(document);
        Assert.assertNotNull(document);
        log.info(String.format("New document created %s", document));

        document.addTag("tag1");
        documentService.update(document);
        document = documentService.get(document.getId());
        log.debug(String.format("Add 'tag1' to %s", document));
        Assert.assertTrue(document.getTags() != null && document.getTags().size() == 1);
        Assert.assertTrue(document.getTags().iterator().next().equals("tag1"));

        document.addTag("tag2");
        documentService.update(document);
        document = documentService.get(document.getId());
        log.debug(String.format("Add 'tag2' to %s", document));
        Assert.assertTrue(document.getTags() != null && document.getTags().size() == 2);
        Assert.assertTrue(document.getTags().equals(newHashSet(ImmutableSet.of("tag1", "tag2"))));

        document.removeTag("tag2");
        documentService.update(document);
        document = documentService.get(document.getId());
        log.debug(String.format("Remove 'tag2' to %s", document));
        Assert.assertTrue(document.getTags() != null && document.getTags().size() == 1);
        Assert.assertTrue(document.getTags().iterator().next().equals("tag1"));

        document.removeTag("tag1");
        documentService.update(document);
        document = documentService.get(document.getId());
        log.debug(String.format("Remove 'tag1' to %s", document));
        Assert.assertTrue(document.getTags() != null && document.getTags().size() == 0);

        deleteDocument(document);
    }

    @Test
    public void testDocumentMetadata() throws Throwable {
        log.info("*** testDocumentMetadata ***");
        loginAdminUser();
        String name = "test-document-metadata";
        String id = createDocument(name, "text/html", "/test/github/richardwilly98/services/test-attachment.html");
        Document document = documentService.get(id);
        Assert.assertNotNull(document);
        log.info(String.format("New document created %s", document));

        document.addTag("tag1");
        document.setAttribute("attribut1", "value1");
        documentService.update(document);

        document = documentService.get(id);
        log.info(String.format("Update document %s", document));

        document = documentService.getMetadata(document.getId());
        Assert.assertNotNull(document);
        Assert.assertEquals(document.getId(), id);
        Assert.assertEquals(document.getName(), name);
        Assert.assertNotNull(document.getVersions());
        Assert.assertTrue(document.getVersions().size() == 0);
        Assert.assertNotNull(document.getAttributes());
        Assert.assertTrue(document.getAttributes().containsKey("attribut1"));
        Assert.assertTrue(document.getAttributes().get("attribut1").toString().equals("value1"));
        Assert.assertNotNull(document.getTags());
        Assert.assertTrue(document.getTags().equals(newHashSet(ImmutableSet.of("tag1"))));

        deleteDocument(document);
    }
    
    @Test
    public void testSystemParameter() throws Throwable {
        DocumentProvider provider = (DocumentProvider)documentService;
        Assert.assertNotNull(provider);
        Assert.assertEquals(provider.previewLength, SystemParametersModule.DEFAULT_PREVIEW_LENGTH);
    }

    @Test(enabled = false)
    public void testJson() {
        try {
            XContentBuilder mapping = jsonBuilder().startObject().startObject("XXXXXXX").startObject("properties").startObject("content")
                    .field("type", "attachment").endObject().startObject("filename").field("type", "string").endObject()
                    .startObject("contentType").field("type", "string").endObject().startObject("md5").field("type", "string").endObject()
                    .startObject("length").field("type", "long").endObject().startObject("chunkSize").field("type", "long").endObject()
                    .endObject().endObject().endObject();
            log.info("Mapping: " + mapping.string());
            String query = jsonBuilder().startObject().startObject("query").startObject("bool").startArray("must").startObject()
                    .startObject("queryString").field("query", "XXXX").array("fields", "_all", "file").endObject().endObject()
                    .startObject().startObject("queryString").field("query", "id").field("default_field", "id").endObject().endObject()
                    .endArray().endObject().endObject().endObject().string();
            log.debug("query: " + query);
        } catch (Throwable t) {
            log.error("testJson failed", t);
            Assert.fail();
        }
    }
}
