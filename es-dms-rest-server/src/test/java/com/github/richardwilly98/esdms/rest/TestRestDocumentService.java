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

import static com.google.common.collect.Sets.newHashSet;
import static org.elasticsearch.common.io.Streams.copyToBytesFromClasspath;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.FileImpl;
import com.github.richardwilly98.esdms.api.AuditEntry;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.Document.DocumentSystemAttributes;
import com.github.richardwilly98.esdms.api.File;
import com.github.richardwilly98.esdms.api.Rating;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.api.Version;
import com.github.richardwilly98.esdms.rest.RestRatingService.RatingRequest;
import com.github.richardwilly98.esdms.search.api.SearchResult;
import com.github.richardwilly98.esdms.services.RoleService;
import com.google.common.collect.ImmutableSet;

public class TestRestDocumentService extends GuiceAndJettyTestBase<Document> {

    private static final String TEST_ATTACHMENT2_HTML = "/test/github/richardwilly98/services/test-attachment2.html";
    public static final String TEST_ATTACHMENT_HTML = "/test/github/richardwilly98/services/test-attachment.html";

    public TestRestDocumentService() throws Exception {
        super();
    }

    @Test
    public void testCreateDeleteDocument() throws Throwable {
        log.debug("*** testCreateDeleteDocument ***");
        String name = "test-attachment.html";
        Document document = createDocument(name, "text/html", TEST_ATTACHMENT_HTML);
        Assert.assertNotNull(document);
        Assert.assertNotNull(document.getCurrentVersion());
        log.debug("New document: " + document);
        Assert.assertEquals(document.getName(), name);
        Document document2 = get(document.getId(), Document.class, RestDocumentService.DOCUMENTS_PATH);
        Assert.assertEquals(document.getId(), document.getId());
        String newName = "document-" + System.currentTimeMillis();
        document2.setName(newName);
        Document document3 = update(document2, Document.class, RestDocumentService.DOCUMENTS_PATH);
        Assert.assertEquals(newName, document3.getName());
        markDeletedDocument(document.getId());
        delete(document.getId(), RestDocumentService.DOCUMENTS_PATH);
        document2 = get(document.getId(), Document.class, RestDocumentService.DOCUMENTS_PATH);
        Assert.assertNull(document2);
    }

    @Test
    public void testCheckoutCheckinDocument() throws Throwable {
        log.debug("*** testCheckoutCheckinDocument ***");
        String name = "test-attachment.html";
        Document document = createDocument(name, "text/html", TEST_ATTACHMENT_HTML);
        Assert.assertNotNull(document);
        log.debug("New document: " + document);

        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(document.getId())
                .path(RestDocumentService.CHECKOUT_PATH).request(MediaType.APPLICATION_JSON).headers(adminAuthenticationHeader).post(null);
        log.debug(String.format("status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.NO_CONTENT.getStatusCode());

        Document document2 = get(document.getId(), Document.class, RestDocumentService.DOCUMENTS_PATH);
        log.debug("Checked-out document: " + document);
        Map<String, Object> attributes = document2.getAttributes();
        Assert.assertNotNull(attributes.get(DocumentSystemAttributes.STATUS.getKey()));
        Assert.assertTrue(attributes.get(DocumentSystemAttributes.STATUS.getKey()).equals(Document.DocumentStatus.LOCKED.getStatusCode()));
        Assert.assertNotNull(attributes.get(DocumentSystemAttributes.LOCKED_BY.getKey()));
        Assert.assertTrue(attributes.get(DocumentSystemAttributes.LOCKED_BY.getKey()).equals(adminCredential.getUsername()));
        Assert.assertNotNull(attributes.get(DocumentSystemAttributes.MODIFIED_DATE.getKey()));

        response = target().path(RestDocumentService.DOCUMENTS_PATH).path(document.getId()).path(RestDocumentService.CHECKOUT_PATH)
                .request(MediaType.APPLICATION_JSON).headers(adminAuthenticationHeader).post(null);
        log.debug(String.format("status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.CONFLICT.getStatusCode());

        response = target().path(RestDocumentService.DOCUMENTS_PATH).path(document.getId()).path(RestDocumentService.CHECKIN_PATH)
                .request(MediaType.APPLICATION_JSON).headers(adminAuthenticationHeader).post(null);
        log.debug(String.format("status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.NO_CONTENT.getStatusCode());

        document2 = get(document.getId(), Document.class, RestDocumentService.DOCUMENTS_PATH);

        attributes = document2.getAttributes();
        Assert.assertTrue(document2.getAttributes().get(DocumentSystemAttributes.STATUS.getKey())
                .equals(Document.DocumentStatus.AVAILABLE.getStatusCode()));
        Assert.assertFalse(attributes.containsKey(DocumentSystemAttributes.LOCKED_BY.getKey()));

        response = target().path(RestDocumentService.DOCUMENTS_PATH).path(document.getId()).path(RestDocumentService.CHECKIN_PATH)
                .request(MediaType.APPLICATION_JSON).headers(adminAuthenticationHeader).post(null);
        log.debug(String.format("status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.CONFLICT.getStatusCode());
    }

    @Test
    public void testDocumentMetadata() throws Throwable {
        log.debug("*** testDocumentMetadata ***");
        String name = "test-document-metadata";
        Document document = createDocument(name, "text/html", TEST_ATTACHMENT_HTML);
        Assert.assertNotNull(document);
        log.info(String.format("New document created %s", document));

        document.addTag("tag1");
        document.setAttribute("attribut1", "value1");
        updateDocument(document);

        document = getDocument(document.getId());
        log.info(String.format("Updated document %s", document));

        document = getMetadata(document.getId());
        Assert.assertNotNull(document);
        Assert.assertEquals(document.getName(), name);
        Assert.assertNotNull(document.getVersions());
        Assert.assertEquals(document.getVersions().size(), 1);
        Version version = document.getCurrentVersion();
        Assert.assertNotNull(version);
        Assert.assertNull(version.getFile());
        Assert.assertNotNull(document.getAttributes());
        Assert.assertTrue(document.getAttributes().containsKey("attribut1"));
        Assert.assertTrue(document.getAttributes().get("attribut1").toString().equals("value1"));
        Assert.assertNotNull(document.getTags());
        Assert.assertTrue(document.getTags().equals(newHashSet(ImmutableSet.of("tag1"))));
    }

    @Test
    public void testCRUDTags() throws Throwable {
        log.debug("*** testCRUDTags ***");
        String name = "test-document-crud-tag";
        Document document = createDocument(name, "text/html", TEST_ATTACHMENT_HTML);
        Assert.assertNotNull(document);
        log.info(String.format("New document created %s", document));

        document.addTag("tag1");
        updateDocument(document);

        Set<String> tags = getTags(document.getId());
        Assert.assertEquals(document.getTags().size(), 1);
        Assert.assertEquals(document.getTags(), tags);

        addTag(document.getId(), "tag2");
        document = getDocument(document.getId());
        tags = getTags(document.getId());
        Assert.assertEquals(document.getTags().size(), 2);
        Assert.assertEquals(document.getTags(), tags);

        removeTag(document.getId(), "tag2");
        tags.remove("tag2");
        document = getDocument(document.getId());
        Assert.assertEquals(document.getTags().size(), 1);
        Assert.assertEquals(document.getTags(), tags);

        removeTags(document.getId());
        tags = getTags(document.getId());
        document = getDocument(document.getId());
        Assert.assertEquals(document.getTags().size(), 0);
        Assert.assertEquals(document.getTags(), tags);

        deleteDocument(document.getId());
    }

    @Test()
    public void testDownloadDocument() throws Throwable {
        log.debug("*** testDownloadDocument ***");
        String name = "test-attachment.html";
        Document document = createDocument(name, "text/html", TEST_ATTACHMENT_HTML);
        Assert.assertNotNull(document);
        log.debug("New document: " + document);

        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(document.getId())
                .path(RestDocumentService.DOWNLOAD_PATH).request().headers(adminAuthenticationHeader).get();

        log.debug(String.format("status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());

        // TODO: Check how binary content is provided in Jersey 2
        // log.debug("Media type: " + response.getMediaType());
        // log.debug("Entity: " + response.getEntity());
        String stream = response.readEntity(String.class);
        Assert.assertNotNull(stream);
        // stream.close();
    }

    @Test
    public void testFindDocuments() throws Throwable {
        log.debug("*** testFindDocuments ***");
        String criteria = "Aliquam";
        Document document = createDocument("test-attachment.html", "text/html", TEST_ATTACHMENT_HTML);
        Assert.assertNotNull(document);
        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(RestItemBaseService.SEARCH_PATH).path(criteria)
                .request().headers(adminAuthenticationHeader).accept(MediaType.APPLICATION_JSON).get();
        log.debug(String.format("status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        SearchResult<Document> documents = response.readEntity(new GenericType<SearchResult<Document>>() {
        });
        Assert.assertNotNull(documents);
        Assert.assertTrue(documents.getTotalHits() >= 1);
    }

    @Test
    public void testAuditDocuments() throws Throwable {
        log.debug("*** testAuditDocuments ***");
        Document document = createDocument("test-attachment.html", "text/html", TEST_ATTACHMENT_HTML);
        Assert.assertNotNull(document);
        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(document.getId()).path(RestDocumentService.AUDIT_PATH)
                .request().headers(adminAuthenticationHeader).accept(MediaType.APPLICATION_JSON).get();
        log.debug(String.format("status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        SearchResult<AuditEntry> auditEntries = response.readEntity(new GenericType<SearchResult<AuditEntry>>() {
        });
        Assert.assertNotNull(auditEntries);
        Assert.assertTrue(auditEntries.getTotalHits() >= 1);
    }

    @Test
    public void testCreateDocumentVersions() throws Throwable {
        log.debug("*** testCreateDocumentVersions ***");
        String name = "Aliquam";
        // String criteria = "Aliquam";

        Document document = createDocument("test-attachment.html", "text/html", TEST_ATTACHMENT_HTML);
        Assert.assertNotNull(document);
        String contentType = "text/plain";
        Version version1 = document.getCurrentVersion();
        log.debug(String.format("testCreateDocumentVersions step 1 obtained document %s having %s versions. Current version %s",
                document.getId(), document.getVersions().size(), version1.getVersionId()));
        Assert.assertEquals(version1.getVersionId(), 1);
        Version currentVersion = getVersion(document.getId(), 1);
        Assert.assertNotNull(currentVersion);

        Version version2 = createVersion(document.getId(), name, contentType, TEST_ATTACHMENT_HTML);
        log.debug(String.format(
                "testCreateDocumentVersions step 2 obtained document %s having %s versions. Current version %s, New version %s",
                document.getId(), document.getVersions().size(), document.getCurrentVersion().getVersionId(), version2.getVersionId()));

        document = get(document.getId(), Document.class, RestDocumentService.DOCUMENTS_PATH);
        // Assert.assertEquals(newV.getVersionId(), 2);
        Assert.assertEquals(document.getCurrentVersion().getVersionId(), 2);

        Version version3 = createFromVersion(document.getId(), version1.getVersionId(), name, contentType, TEST_ATTACHMENT_HTML);
        log.debug("VERSION3: " + version3);
        log.debug(String.format(
                "testCreateDocumentVersions step 3 obtained document %s having %s versions. Current version %s, New version %s",
                document.getId(), document.getVersions().size(), document.getCurrentVersion().getVersionId(), version3.getVersionId()));

        document = get(document.getId(), Document.class, RestDocumentService.DOCUMENTS_PATH);

        // Assert.assertEquals(newV.getVersionId(), 3);
        Assert.assertEquals(document.getCurrentVersion().getVersionId(), 3);

        log.debug(String.format(
                "testCreateDocumentVersions step 4 obtained document %s having %s versions. Current version %s, New version %s",
                document.getId(), document.getVersions().size(), document.getCurrentVersion().getVersionId(), 2));

        if (setCurrentVersion(document.getId(), 2)) {
            log.debug(String.format("testCreateDocumentVersions step 4: Moved current version from 3 to: ", document.getCurrentVersion()
                    .getVersionId()));
        } else {
            log.debug(String.format("testCreateDocumentVersions step 4: Failed to move current version from 3 to 2. current version: ",
                    document.getCurrentVersion().getVersionId()));
        }
        document = get(document.getId(), Document.class, RestDocumentService.DOCUMENTS_PATH);

        Assert.assertEquals(document.getCurrentVersion().getVersionId(), 2);

        log.debug(String.format(
                "testCreateDocumentVersions step 5 obtained document %s having %s versions. Current version %s, New version %s",
                document.getId(), document.getVersions().size(), document.getCurrentVersion().getVersionId(), 1));

        if (setCurrentVersion(document.getId(), 1))
            log.debug(String.format("testCreateDocumentVersions step 5: Moved current version from 2 to: ", document.getCurrentVersion()
                    .getVersionId()));
        else
            log.debug(String.format("testCreateDocumentVersions step 5: Failed to move current version from 2 to 1. current version: ",
                    document.getCurrentVersion().getVersionId()));

        document = get(document.getId(), Document.class, RestDocumentService.DOCUMENTS_PATH);

        Assert.assertEquals(document.getCurrentVersion().getVersionId(), 1);

        log.debug(String.format(
                "testCreateDocumentVersions step 6 obtained document %s having %s versions. Current version %s, New version %s",
                document.getId(), document.getVersions().size(), document.getCurrentVersion().getVersionId(), 3));

        if (setCurrentVersion(document.getId(), 3))
            log.debug(String.format("testCreateDocumentVersions step 6: Moved current version from 1 to: ", document.getCurrentVersion()
                    .getVersionId()));
        else
            log.debug(String.format("testCreateDocumentVersions step 6: Failed to move current version from 1 to 3. current version: ",
                    document.getCurrentVersion().getVersionId()));
        document = get(document.getId(), Document.class, RestDocumentService.DOCUMENTS_PATH);

        Assert.assertEquals(document.getCurrentVersion().getVersionId(), 3);

        log.debug(String.format("testCreateDocumentVersions step 7 obtained document %s having %s versions. Current version %s",
                document.getId(), document.getVersions().size(), document.getCurrentVersion().getVersionId()));

        log.debug("*** testCreateDocumentVersions end ***");
    }

    @Test
    public void testCreateDocumentWithVersions() throws Throwable {
        log.debug("*** testCreateDocumentWithVersions ***");
        String name = "Aliquam";

        Document document = createDocument("test-attachment.html", "text/html", TEST_ATTACHMENT_HTML);
        Assert.assertNotNull(document);
        Assert.assertEquals(document.getVersions().size(), 1);

        String contentType = "text/html";
        Version currentVersion = document.getCurrentVersion();
        Assert.assertEquals(currentVersion.getVersionId(), 1);

        currentVersion = createVersion(document.getId(), name, contentType, TEST_ATTACHMENT_HTML);
        Assert.assertNotNull(currentVersion);

        document = get(document.getId(), Document.class, RestDocumentService.DOCUMENTS_PATH);
        Assert.assertEquals(document.getVersions().size(), 2);
        Assert.assertEquals(document.getCurrentVersion().getVersionId(), currentVersion.getVersionId());

        currentVersion = createVersion(document.getId(), name, contentType, TEST_ATTACHMENT_HTML);
        Assert.assertNotNull(currentVersion);

        document = get(document.getId(), Document.class, RestDocumentService.DOCUMENTS_PATH);
        Assert.assertEquals(document.getVersions().size(), 3);
        Assert.assertEquals(document.getCurrentVersion().getVersionId(), currentVersion.getVersionId());
        deleteDocument(document.getId());
        log.debug("*** testCreateDocumentWithVersions end ***");
    }

    @Test
    public void testCreateUpdateDocumentVersions() throws Throwable {
        log.debug("*** testCreateUpdateDocumentVersions ***");
        String name = "Aliquam";
        String criteria = "Aliquam";

        Document document = createDocument("test-attachment.html", "text/html", TEST_ATTACHMENT_HTML);
        Assert.assertNotNull(document);
        String contentType = "text/plain";
        Version version = document.getCurrentVersion();
        log.debug(String.format("step 1 obtained document %s having %s versions. Current version %s", document.getId(), document
                .getVersions().size(), version.getVersionId()));
        Assert.assertEquals(version.getVersionId(), 1);
        File file = getVersionContent(document.getId(), version.getVersionId());
        Assert.assertNotNull(file);
        Assert.assertNotNull(file.getContent());

        version = updateVersion(document.getId(), "" + version.getVersionId(), name, contentType, TEST_ATTACHMENT2_HTML);
        Assert.assertNotNull(version);

        file = getVersionContent(document.getId(), version.getVersionId());
        log.debug("testCreateUpdateDocumentVersions new content: >>>>>" + new String(file.getContent(), "UTF-8") + "<<<<<<");

        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(RestItemBaseService.SEARCH_PATH).path(criteria)
                .request().headers(adminAuthenticationHeader).accept(MediaType.APPLICATION_JSON).get();
        log.debug(String.format("status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        SearchResult<Document> documents = response.readEntity(new GenericType<SearchResult<Document>>() {
        });
        Assert.assertNotNull(documents);
        Assert.assertTrue(documents.getTotalHits() >= 1);
        log.debug("*** testCreateUpdateDocumentVersions end ***");
    }

    @Test
    public void testCRUDRatingDocuments() throws Throwable {
        log.debug("*** testCRUDRatingDocuments ***");
        Document document = createDocument("test-attachment.html", "text/html", TEST_ATTACHMENT_HTML);
        Assert.assertNotNull(document);
        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(document.getId()).path(RestDocumentService.AUDIT_PATH)
                .request().headers(adminAuthenticationHeader).accept(MediaType.APPLICATION_JSON).get();
        log.debug(String.format("status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        // SearchResult<AuditEntry> auditEntries = response.readEntity(new
        // GenericType<SearchResult<AuditEntry>>() {
        // });
        // Assert.assertNotNull(auditEntries);
        // Assert.assertTrue(auditEntries.getTotalHits() >= 1);
        RestRatingService.RatingRequest ratingRequest = new RatingRequest();
        ratingRequest.setItemId(document.getId());
        ratingRequest.setScore(3);

        // Create
        response = target().path(RestRatingService.RATINGS_PATH).request().headers(adminAuthenticationHeader)
                .post(Entity.entity(ratingRequest, MediaType.APPLICATION_JSON));
        Assert.assertTrue(response.getStatus() == Status.CREATED.getStatusCode());

        // Retrieve
        response = target().path(RestRatingService.RATINGS_PATH).path(document.getId())/*
                                                                                        * .
                                                                                        * path
                                                                                        * (
                                                                                        * adminCredential
                                                                                        * .
                                                                                        * getUsername
                                                                                        * (
                                                                                        * )
                                                                                        * )
                                                                                        */
        .request().headers(adminAuthenticationHeader).accept(MediaType.APPLICATION_JSON).get();
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        Rating rating = response.readEntity(Rating.class);
        Assert.assertNotNull(rating);
        Assert.assertEquals(rating.getScore(), ratingRequest.getScore());
        Assert.assertEquals(rating.getUser(), adminCredential.getUsername());

        // Update
        ratingRequest.setScore(1);
        response = target().path(RestRatingService.RATINGS_PATH).path(document.getId()).request().headers(adminAuthenticationHeader)
                .put(Entity.entity(ratingRequest, MediaType.APPLICATION_JSON));
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        rating = response.readEntity(Rating.class);
        Assert.assertEquals(rating.getScore(), ratingRequest.getScore());
        Assert.assertEquals(rating.getUser(), adminCredential.getUsername());

        // Delete
        response = target().path(RestRatingService.RATINGS_PATH).path(document.getId()).request().headers(adminAuthenticationHeader)
                .delete();
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        response = target().path(RestRatingService.RATINGS_PATH).path(document.getId()).request().headers(adminAuthenticationHeader)
                .accept(MediaType.APPLICATION_JSON).get();
        Assert.assertTrue(response.getStatus() == Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testTotalAverageRatingDocuments() throws Throwable {
        log.debug("*** testTotalAverageRatingDocuments ***");
        Document document = createDocument("test-attachment.html", "text/html", TEST_ATTACHMENT_HTML);
        Assert.assertNotNull(document);
        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(document.getId()).path(RestDocumentService.AUDIT_PATH)
                .request().headers(adminAuthenticationHeader).accept(MediaType.APPLICATION_JSON).get();
        log.debug(String.format("status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());

        // Create
        RestRatingService.RatingRequest ratingRequest = new RatingRequest();
        ratingRequest.setItemId(document.getId());
        ratingRequest.setScore(3);
        response = target().path(RestRatingService.RATINGS_PATH).request().headers(adminAuthenticationHeader)
                .post(Entity.entity(ratingRequest, MediaType.APPLICATION_JSON));
        Assert.assertTrue(response.getStatus() == Status.CREATED.getStatusCode());

        // Retrieve
        response = target().path(RestRatingService.RATINGS_PATH).path(document.getId())/*
                                                                                        * .
                                                                                        * path
                                                                                        * (
                                                                                        * adminCredential
                                                                                        * .
                                                                                        * getUsername
                                                                                        * (
                                                                                        * )
                                                                                        * )
                                                                                        */
        .request().headers(adminAuthenticationHeader).accept(MediaType.APPLICATION_JSON).get();
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        Rating rating = response.readEntity(Rating.class);
        Assert.assertNotNull(rating);
        Assert.assertEquals(rating.getScore(), ratingRequest.getScore());
        Assert.assertEquals(rating.getUser(), adminCredential.getUsername());

        response = target().path(RestRatingService.RATINGS_PATH).path(document.getId()).path(RestRatingService.ALL_PATH).request()
                .headers(adminAuthenticationHeader).accept(MediaType.APPLICATION_JSON).get();
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        Set<Rating> ratings = response.readEntity(new GenericType<Set<Rating>>() {
        });
        Assert.assertNotNull(ratings);
        Assert.assertTrue(ratings.size() == 1);
        Assert.assertTrue(ratings.contains(rating));

        User testRatingUser = createUser("test-rating-user-" + System.currentTimeMillis() + "@gmail.com", "secret",
                ImmutableSet.of(RoleService.DefaultRoles.WRITER.getRole()));
        Assert.assertNotNull(testRatingUser);
        log.debug(String.format("New test rating user created: %s", testRatingUser));
        MultivaluedMap<String, Object> headers = login(new CredentialImpl.Builder().username(testRatingUser.getLogin())
                .password("secret".toCharArray()).build());

        // Create
        RestRatingService.RatingRequest ratingRequest2 = new RatingRequest();
        ratingRequest2.setItemId(document.getId());
        ratingRequest2.setScore(5);
        response = target().path(RestRatingService.RATINGS_PATH).request().headers(headers)
                .post(Entity.entity(ratingRequest2, MediaType.APPLICATION_JSON));
        Assert.assertTrue(response.getStatus() == Status.CREATED.getStatusCode());

        // Retrieve
        response = target().path(RestRatingService.RATINGS_PATH).path(document.getId())/*
                                                                                        * .
                                                                                        * path
                                                                                        * (
                                                                                        * adminCredential
                                                                                        * .
                                                                                        * getUsername
                                                                                        * (
                                                                                        * )
                                                                                        * )
                                                                                        */
        .request().headers(headers).accept(MediaType.APPLICATION_JSON).get();
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        Rating rating2 = response.readEntity(Rating.class);
        Assert.assertNotNull(rating);
        Assert.assertEquals(rating2.getScore(), ratingRequest2.getScore());
        Assert.assertEquals(rating2.getUser(), testRatingUser.getLogin());

        response = target().path(RestRatingService.RATINGS_PATH).path(document.getId()).path(RestRatingService.ALL_PATH).request()
                .headers(adminAuthenticationHeader).accept(MediaType.APPLICATION_JSON).get();
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        ratings = response.readEntity(new GenericType<Set<Rating>>() {
        });
        Assert.assertNotNull(ratings);
        Assert.assertTrue(ratings.size() == 2);
        Assert.assertTrue(ratings.contains(rating2));

        // Total ratings
        response = target().path(RestRatingService.RATINGS_PATH).path(document.getId()).path(RestRatingService.TOTAL_PATH).request()
                .headers(adminAuthenticationHeader).accept(MediaType.APPLICATION_JSON).get();
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        float total = response.readEntity(float.class);
        Assert.assertTrue(total == ratingRequest.getScore() + ratingRequest2.getScore());

        // Average ratings
        response = target().path(RestRatingService.RATINGS_PATH).path(document.getId()).path(RestRatingService.AVERAGE_PATH).request()
                .headers(adminAuthenticationHeader).accept(MediaType.APPLICATION_JSON).get();
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        float average = response.readEntity(float.class);
        Assert.assertTrue(average == (ratingRequest.getScore() + ratingRequest2.getScore()) / ratings.size());

        logout(headers);
    }

    private boolean setCurrentVersion(String documentId, int versionId) throws Throwable {
        log.debug("******* setCurrentVersion  *********");

        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(documentId).path(RestDocumentService.VERSIONS_PATH)
                .path(String.valueOf(versionId)).path(RestDocumentService.CURRENT_PATH).request().headers(adminAuthenticationHeader)
                .post(null);
        Assert.assertEquals(response.getStatus(), Status.OK.getStatusCode());

        log.debug("******* setCurrentVersion end *********");
        return response.getStatus() == Status.OK.getStatusCode();
    }

    private Version updateVersion(String documentId, String versionId, String name, String contentType, String path) throws Throwable {
        log.debug("******* updateVersion  *********");
        byte[] content = copyToBytesFromClasspath(path);
        InputStream is = new ByteArrayInputStream(content);

        FormDataMultiPart form = new FormDataMultiPart();
        form.field("name", name);
        FormDataBodyPart p = new FormDataBodyPart(FormDataContentDisposition.name("file").fileName(name).size(content.length).build(), is,
                MediaType.valueOf(contentType));
        form.bodyPart(p);

        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(documentId).path(RestDocumentService.VERSIONS_PATH)
                .path(versionId).path(RestDocumentService.UPLOAD_PATH).request().headers(adminAuthenticationHeader)
                .put(Entity.entity(form, MediaType.MULTIPART_FORM_DATA_TYPE));
        Assert.assertEquals(response.getStatus(), Status.CREATED.getStatusCode());
        URI uri = response.getLocation();
        Assert.assertNotNull(uri);
        log.debug("******* updateVersion end *********");
        return getVersion(uri);
    }

    private Version createVersion(String documentId, String name, String contentType, String path) throws Throwable {
        return createFromVersion(documentId, 0, name, contentType, path);
    }

    private Version getVersion(String documentId, int versionId) throws Throwable {
        log.trace(String.format("getVersion - %s - %s", documentId, versionId));
        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(documentId).path(RestDocumentService.VERSIONS_PATH)
                .path(String.valueOf(versionId)).request().headers(adminAuthenticationHeader).accept(MediaType.APPLICATION_JSON).get();
        log.trace(String.format("status: %s", response.getStatus()));
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(Version.class);
        }
        return null;
    }

    private File getVersionContent(String documentId, int versionId) throws Throwable {
        log.trace(String.format("getVersionContent - %s - %s", documentId, versionId));
        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(documentId).path(RestDocumentService.VERSIONS_PATH)
                .path(String.valueOf(versionId)).path(RestDocumentService.DOWNLOAD_PATH).request().headers(adminAuthenticationHeader)
                .accept(MediaType.APPLICATION_JSON).get();
        log.trace(String.format("status: %s", response.getStatus()));
        if (response.getStatus() == Status.OK.getStatusCode()) {
            String contentType = response.getStringHeaders().getFirst("Content-Type");
            log.debug("Content-Disposition: " + response.getStringHeaders().getFirst("Content-Disposition"));
            String filename = new ContentDisposition(response.getStringHeaders().getFirst("Content-Disposition")).getFileName();
            log.debug("contentType / filename: " + contentType + " - " + filename);
            FileImpl.Builder builder = new FileImpl.Builder().contentType(contentType).name(filename);
            String stream = response.readEntity(String.class);
            log.debug("stream: " + stream);
            builder.content(stream.getBytes());
            return builder.build();
        }
        return null;
    }

    protected Version getVersion(URI uri) throws Throwable {
        log.trace(String.format("getVersion - %s", uri));
        Response response = client().target(uri).request().headers(adminAuthenticationHeader).accept(MediaType.APPLICATION_JSON).get();
        log.trace(String.format("status: %s", response.getStatus()));
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(Version.class);
        }
        return null;
    }

    private Version createFromVersion(String documentId, int parentId, String name, String contentType, String path) throws Throwable {
        log.debug("******* createFromVersion  *********");
        byte[] content = copyToBytesFromClasspath(path);
        InputStream is = new ByteArrayInputStream(content);

        FormDataMultiPart form = new FormDataMultiPart();
        form.field("name", name);

        FormDataBodyPart p = new FormDataBodyPart(FormDataContentDisposition.name("file").fileName(name).size(content.length).build(), is,
                MediaType.valueOf(contentType));
        form.bodyPart(p);

        WebTarget target = target().path(RestDocumentService.DOCUMENTS_PATH).path(documentId).path(RestDocumentService.VERSIONS_PATH)
                .path(RestDocumentService.UPLOAD_PATH);
        if (parentId != 0) {
            target.queryParam(RestVersionService.VERSION_ID_PARAMETER, parentId);
        }
        Response response = target.request().headers(adminAuthenticationHeader)
                .post(Entity.entity(form, MediaType.MULTIPART_FORM_DATA_TYPE));

        Assert.assertEquals(response.getStatus(), Status.CREATED.getStatusCode());
        URI uri = response.getLocation();
        Assert.assertNotNull(uri);
        log.debug("******* createFromVersion end *********");
        return getVersion(uri);
    }

    private Document createDocument(String name, String contentType, String path) throws Throwable {
        byte[] content = copyToBytesFromClasspath(path);
        InputStream is = new ByteArrayInputStream(content);

        FormDataMultiPart form = new FormDataMultiPart();
        form.field("name", name);
        // FormDataBodyPart p = new FormDataBodyPart("file", is,
        // MediaType.valueOf(contentType));
        FormDataBodyPart p = new FormDataBodyPart(FormDataContentDisposition.name("file").fileName(name).size(content.length).build(), is,
                MediaType.valueOf(contentType));
        form.bodyPart(p);

        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(RestDocumentService.UPLOAD_PATH).request()
                .headers(adminAuthenticationHeader).post(Entity.entity(form, MediaType.MULTIPART_FORM_DATA_TYPE));
        log.debug(String.format("status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.CREATED.getStatusCode());
        URI uri = response.getLocation();
        Assert.assertNotNull(uri);
        return get(uri, Document.class);
    }

    private void updateDocument(Document document) throws Throwable {
        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(RestDocumentService.UPDATE_PATH)
                .request(MediaType.APPLICATION_JSON).headers(adminAuthenticationHeader)
                .put(Entity.entity(document, MediaType.APPLICATION_JSON));
        Assert.assertEquals(response.getStatus(), Status.OK.getStatusCode());
    }

    private void deleteDocument(String id) throws Throwable {
        markDeletedDocument(id);
        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(id).request().headers(adminAuthenticationHeader)
                .delete();
        Assert.assertEquals(response.getStatus(), Status.OK.getStatusCode());
        Assert.assertNull(getDocument(id));
    }

    private Document getDocument(String id) throws Throwable {
        Document document = get(id, Document.class, RestDocumentService.DOCUMENTS_PATH);
        return document;
    }

    private Document getMetadata(String id) throws Throwable {
        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(id).path(RestDocumentService.METADATA_PATH)
                .request(MediaType.APPLICATION_JSON).headers(adminAuthenticationHeader).get();
        log.debug(String.format("status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        Document document = response.readEntity(Document.class);
        Assert.assertNotNull(document);
        return document;
    }

    private void markDeletedDocument(String id) throws Throwable {
        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(id).path(RestDocumentService.MARKDELETE_PATH).request()
                .headers(adminAuthenticationHeader).post(null);
        log.debug(String.format("status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.NO_CONTENT.getStatusCode());
    }

    private Set<String> getTags(String id) throws Throwable {
        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(id).path(RestDocumentService.TAGS_PATH)
                .request(MediaType.APPLICATION_JSON).headers(adminAuthenticationHeader).get();
        Assert.assertEquals(response.getStatus(), Status.OK.getStatusCode());
        Set<String> tags = response.readEntity(new GenericType<Set<String>>() {
        });
        return tags;
    }

    private void addTag(String id, String tag) throws Throwable {
        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(id).path(RestDocumentService.TAGS_PATH).path(tag)
                .request().headers(adminAuthenticationHeader).post(Entity.json(null));
        Assert.assertEquals(response.getStatus(), Status.CREATED.getStatusCode());
    }

    private void removeTag(String id, String tag) throws Throwable {
        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(id).path(RestDocumentService.TAGS_PATH).path(tag)
                .request(MediaType.APPLICATION_JSON).headers(adminAuthenticationHeader).delete();
        Assert.assertEquals(response.getStatus(), Status.NO_CONTENT.getStatusCode());
    }

    private void removeTags(String id) throws Throwable {
        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(id).path(RestDocumentService.TAGS_PATH)
                .request(MediaType.APPLICATION_JSON).headers(adminAuthenticationHeader).delete();
        Assert.assertEquals(response.getStatus(), Status.NO_CONTENT.getStatusCode());
    }
}
