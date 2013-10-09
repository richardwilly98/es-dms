package test.github.richardwilly98.esdms.rest;

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
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.api.AuditEntry;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.Rating;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.api.Version;
import com.github.richardwilly98.esdms.api.Document.DocumentSystemAttributes;
import com.github.richardwilly98.esdms.rest.RestDocumentService;
import com.github.richardwilly98.esdms.rest.RestItemBaseService;
import com.github.richardwilly98.esdms.rest.RestRatingService;
import com.github.richardwilly98.esdms.rest.RestRatingService.RatingRequest;
import com.github.richardwilly98.esdms.services.RoleService;
import com.google.common.collect.ImmutableSet;

public class TestRestDocumentService extends GuiceAndJettyTestBase<Document> {

    public TestRestDocumentService() throws Exception {
        super();
    }

    @Test
    public void testCreateDeleteDocument() throws Throwable {
        log.debug("*** testCreateDeleteDocument ***");
        try {
            String name = "test-attachment.html";
            Document document = createDocument(name, "text/html", "/test/github/richardwilly98/services/test-attachment.html");
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
        } catch (Throwable t) {
            log.error("testCreateDeleteDocument fail", t);
            Assert.fail();
        }
    }

    @Test
    public void testCheckoutCheckinDocument() throws Throwable {
        log.debug("*** testCheckoutCheckinDocument ***");
        try {
            String name = "test-attachment.html";
            Document document = createDocument(name, "text/html", "/test/github/richardwilly98/services/test-attachment.html");
            Assert.assertNotNull(document);
            log.debug("New document: " + document);

            Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(document.getId()).path("checkout")
                    .request(MediaType.APPLICATION_JSON).cookie(adminCookie).post(null);
            log.debug(String.format("status: %s", response.getStatus()));
            Assert.assertTrue(response.getStatus() == Status.NO_CONTENT.getStatusCode());

            Document document2 = get(document.getId(), Document.class, RestDocumentService.DOCUMENTS_PATH);
            log.debug("Checked-out document: " + document);
            Map<String, Object> attributes = document2.getAttributes();
            Assert.assertNotNull(attributes.get(DocumentSystemAttributes.STATUS.getKey()));
            Assert.assertTrue(attributes.get(DocumentSystemAttributes.STATUS.getKey()).equals(
                    Document.DocumentStatus.LOCKED.getStatusCode()));
            Assert.assertNotNull(attributes.get(DocumentSystemAttributes.LOCKED_BY.getKey()));
            Assert.assertTrue(attributes.get(DocumentSystemAttributes.LOCKED_BY.getKey()).equals(adminCredential.getUsername()));
            Assert.assertNotNull(attributes.get(DocumentSystemAttributes.MODIFIED_DATE.getKey()));

            response = target().path(RestDocumentService.DOCUMENTS_PATH).path(document.getId()).path("checkout")
                    .request(MediaType.APPLICATION_JSON).cookie(adminCookie).post(null);
            log.debug(String.format("status: %s", response.getStatus()));
            Assert.assertTrue(response.getStatus() == Status.CONFLICT.getStatusCode());

            response = target().path(RestDocumentService.DOCUMENTS_PATH).path(document.getId()).path("checkin")
                    .request(MediaType.APPLICATION_JSON).cookie(adminCookie).post(null);
            log.debug(String.format("status: %s", response.getStatus()));
            Assert.assertTrue(response.getStatus() == Status.NO_CONTENT.getStatusCode());

            document2 = get(document.getId(), Document.class, RestDocumentService.DOCUMENTS_PATH);

            attributes = document2.getAttributes();
            Assert.assertTrue(document2.getAttributes().get(DocumentSystemAttributes.STATUS.getKey())
                    .equals(Document.DocumentStatus.AVAILABLE.getStatusCode()));
            Assert.assertFalse(attributes.containsKey(DocumentSystemAttributes.LOCKED_BY.getKey()));

            response = target().path(RestDocumentService.DOCUMENTS_PATH).path(document.getId()).path("checkin")
                    .request(MediaType.APPLICATION_JSON).cookie(adminCookie).post(null);
            log.debug(String.format("status: %s", response.getStatus()));
            Assert.assertTrue(response.getStatus() == Status.CONFLICT.getStatusCode());

        } catch (Throwable t) {
            log.error("testCheckoutCheckinDocument fail", t);
            Assert.fail();
        }
    }

    @Test
    public void testDocumentMetadata() throws Throwable {
        log.debug("*** testDocumentMetadata ***");
        try {
            String name = "test-document-metadata";
            Document document = createDocument(name, "text/html", "/test/github/richardwilly98/services/test-attachment.html");
            Assert.assertNotNull(document);
            log.info(String.format("New document created %s", document));

            document.addTag("tag1");
            document.setAttribute("attribut1", "value1");
            updateDocument(document);

            document = getDocument(document.getId());
            log.info(String.format("Updated document %s", document));

            document = getMetadata(document.getId());
            Assert.assertNotNull(document);
            // Assert.assertEquals(document.getId(), id);
            Assert.assertEquals(document.getName(), name);
            Assert.assertNotNull(document.getVersions());
            Assert.assertTrue(document.getVersions().size() == 0);
            Assert.assertNotNull(document.getAttributes());
            Assert.assertTrue(document.getAttributes().containsKey("attribut1"));
            Assert.assertTrue(document.getAttributes().get("attribut1").toString().equals("value1"));
            Assert.assertNotNull(document.getTags());
            Assert.assertTrue(document.getTags().equals(newHashSet(ImmutableSet.of("tag1"))));
        } catch (Throwable t) {
            log.error("testDocumentMetadata fail", t);
            Assert.fail();
        }
    }

    @Test()
    public void testDownloadDocument() throws Throwable {
        log.debug("*** testDownloadDocument ***");
        try {
            String name = "test-attachment.html";
            Document document = createDocument(name, "text/html", "/test/github/richardwilly98/services/test-attachment.html");
            Assert.assertNotNull(document);
            log.debug("New document: " + document);

            Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(document.getId()).path("download").request()
                    .cookie(adminCookie).get();

            log.debug(String.format("status: %s", response.getStatus()));
            Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());

            // TODO: Check how binary content is provided in Jersey 2
            // log.debug("Media type: " + response.getMediaType());
            // log.debug("Entity: " + response.getEntity());
            String stream = response.readEntity(String.class);
            Assert.assertNotNull(stream);
            // stream.close();
        } catch (Throwable t) {
            log.error("testDownloadDocument fail", t);
            Assert.fail();
        }
    }

    @Test
    public void testFindDocuments() throws Throwable {
        log.debug("*** testFindDocuments ***");
        try {
            String criteria = "Aliquam";
            Document document = createDocument("test-attachment.html", "text/html",
                    "/test/github/richardwilly98/services/test-attachment.html");
            Assert.assertNotNull(document);
            Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(RestItemBaseService.SEARCH_PATH).path(criteria)
                    .request().cookie(adminCookie).accept(MediaType.APPLICATION_JSON).get();
            log.debug(String.format("status: %s", response.getStatus()));
            Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
            SearchResult<Document> documents = response.readEntity(new GenericType<SearchResult<Document>>() {
            });
            Assert.assertNotNull(documents);
            Assert.assertTrue(documents.getTotalHits() >= 1);
        } catch (Throwable t) {
            log.error("testFindDocuments fail", t);
            Assert.fail();
        }
    }

    @Test
    public void testAuditDocuments() throws Throwable {
        log.debug("*** testAuditDocuments ***");
        try {
            Document document = createDocument("test-attachment.html", "text/html",
                    "/test/github/richardwilly98/services/test-attachment.html");
            Assert.assertNotNull(document);
            Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(document.getId())
                    .path(RestDocumentService.AUDIT_PATH).request().cookie(adminCookie).accept(MediaType.APPLICATION_JSON).get();
            log.debug(String.format("status: %s", response.getStatus()));
            Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
            SearchResult<AuditEntry> auditEntries = response.readEntity(new GenericType<SearchResult<AuditEntry>>() {
            });
            Assert.assertNotNull(auditEntries);
            Assert.assertTrue(auditEntries.getTotalHits() >= 1);
        } catch (Throwable t) {
            log.error("testAuditDocuments fail", t);
            Assert.fail();
        }
    }

    @Test
    public void testCreateDocumentVersions() throws Throwable {
        log.debug("*** testCreateDocumentVersions ***");
        try {
            String name = "Aliquam";
            // String criteria = "Aliquam";

            Document document = createDocument("test-attachment.html", "text/html",
                    "/test/github/richardwilly98/services/test-attachment.html");
            Assert.assertNotNull(document);
            String contentType = "text/plain";
            Version oldV = document.getCurrentVersion();
            log.debug(String.format("testCreateDocumentVersions step 1 obtained document %s having %s versions. Current version %s",
                    document.getId(), document.getVersions().size(), oldV.getVersionId()));
            Assert.assertEquals(oldV.getVersionId(), 1);
            Version newV = createVersion(document.getId(), name, contentType, "/test/github/richardwilly98/services/test-attachment.html");
            log.debug(String.format(
                    "testCreateDocumentVersions step 2 obtained document %s having %s versions. Current version %s, New version %s",
                    document.getId(), document.getVersions().size(), document.getCurrentVersion().getVersionId(), newV.getVersionId()));

            document = get(document.getId(), Document.class, RestDocumentService.DOCUMENTS_PATH);
            // Assert.assertEquals(newV.getVersionId(), 2);
            Assert.assertEquals(document.getCurrentVersion().getVersionId(), 2);

            newV = createFromVersion(document.getId(), "" + oldV.getVersionId(), name, contentType,
                    "/test/github/richardwilly98/services/test-attachment.html");
            log.debug(String.format(
                    "testCreateDocumentVersions step 3 obtained document %s having %s versions. Current version %s, New version %s",
                    document.getId(), document.getVersions().size(), document.getCurrentVersion().getVersionId(), newV.getVersionId()));

            document = get(document.getId(), Document.class, RestDocumentService.DOCUMENTS_PATH);

            // Assert.assertEquals(newV.getVersionId(), 3);
            Assert.assertEquals(document.getCurrentVersion().getVersionId(), 3);

            log.debug(String.format(
                    "testCreateDocumentVersions step 4 obtained document %s having %s versions. Current version %s, New version %s",
                    document.getId(), document.getVersions().size(), document.getCurrentVersion().getVersionId(), 2));

            if (setCurrentVersion(document.getId(), "" + 2)) {
                log.debug(String.format("testCreateDocumentVersions step 4: Moved current version from 3 to: ", document
                        .getCurrentVersion().getVersionId()));
            } else {
                log.debug(String.format("testCreateDocumentVersions step 4: Failed to move current version from 3 to 2. current version: ",
                        document.getCurrentVersion().getVersionId()));
            }
            document = get(document.getId(), Document.class, RestDocumentService.DOCUMENTS_PATH);

            Assert.assertEquals(document.getCurrentVersion().getVersionId(), 2);

            log.debug(String.format(
                    "testCreateDocumentVersions step 5 obtained document %s having %s versions. Current version %s, New version %s",
                    document.getId(), document.getVersions().size(), document.getCurrentVersion().getVersionId(), 1));

            if (setCurrentVersion(document.getId(), "" + 1))
                log.debug(String.format("testCreateDocumentVersions step 5: Moved current version from 2 to: ", document
                        .getCurrentVersion().getVersionId()));
            else
                log.debug(String.format("testCreateDocumentVersions step 5: Failed to move current version from 2 to 1. current version: ",
                        document.getCurrentVersion().getVersionId()));

            document = get(document.getId(), Document.class, RestDocumentService.DOCUMENTS_PATH);

            Assert.assertEquals(document.getCurrentVersion().getVersionId(), 1);

            log.debug(String.format(
                    "testCreateDocumentVersions step 6 obtained document %s having %s versions. Current version %s, New version %s",
                    document.getId(), document.getVersions().size(), document.getCurrentVersion().getVersionId(), 3));

            if (setCurrentVersion(document.getId(), "" + 3))
                log.debug(String.format("testCreateDocumentVersions step 6: Moved current version from 1 to: ", document
                        .getCurrentVersion().getVersionId()));
            else
                log.debug(String.format("testCreateDocumentVersions step 6: Failed to move current version from 1 to 3. current version: ",
                        document.getCurrentVersion().getVersionId()));
            document = get(document.getId(), Document.class, RestDocumentService.DOCUMENTS_PATH);

            Assert.assertEquals(document.getCurrentVersion().getVersionId(), 3);

            log.debug(String.format("testCreateDocumentVersions step 7 obtained document %s having %s versions. Current version %s",
                    document.getId(), document.getVersions().size(), document.getCurrentVersion().getVersionId()));

            // ClientResponse response = resource()
            // .path(RestDocumentService.DOCUMENTS_PATH)
            // .path(RestServiceBase.SEARCH_PATH).path(criteria)
            // .cookie(adminCookie).accept(MediaType.APPLICATION_JSON)
            // .get(ClientResponse.class);
            // log.debug(String.format("status: %s", response.getStatus()));
            // Assert.assertTrue(response.getStatus() ==
            // Status.OK.getStatusCode());
            // List<Document> documents = response
            // .getEntity(new GenericType<List<Document>>() {
            // });
            // Assert.assertNotNull(documents);
            // Assert.assertTrue(documents.size() >= 1);
        } catch (Throwable t) {
            log.error("testCreateDocumentVersions fail", t);
            Assert.fail();
        }
        log.debug("*** testCreateDocumentVersions end ***");
    }

    @Test
    public void testCreateUpdateDocumentVersions() throws Throwable {
        log.debug("*** testCreateUpdateDocumentVersions ***");
        try {
            String name = "Aliquam";
            String criteria = "Aliquam";

            Document document = createDocument("test-attachment.html", "text/html",
                    "/test/github/richardwilly98/services/test-attachment.html");
            Assert.assertNotNull(document);
            String contentType = "text/plain";
            Version version = document.getCurrentVersion();
            log.debug(String.format("step 1 obtained document %s having %s versions. Current version %s", document.getId(), document
                    .getVersions().size(), version.getVersionId()));
            Assert.assertEquals(version.getVersionId(), 1);

            version = updateVersion(document.getId(), "" + version.getVersionId(), name, contentType,
                    "/test/github/richardwilly98/services/test-attachment2.html");
            log.debug("testCreateUpdateDocumentVersions new content: >>>>>" + new String(version.getFile().getContent(), "UTF-8")
                    + "<<<<<<");

            Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(RestItemBaseService.SEARCH_PATH).path(criteria)
                    .request().cookie(adminCookie).accept(MediaType.APPLICATION_JSON).get();
            log.debug(String.format("status: %s", response.getStatus()));
            Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
            SearchResult<Document> documents = response.readEntity(new GenericType<SearchResult<Document>>() {
            });
            Assert.assertNotNull(documents);
            Assert.assertTrue(documents.getTotalHits() >= 1);
        } catch (Throwable t) {
            log.error("testCreateUpdateDocumentVersions fail", t);
            Assert.fail();
        }
        log.debug("*** testCreateUpdateDocumentVersions end ***");
    }

    @Test
    public void testCRUDRatingDocuments() throws Throwable {
        log.debug("*** testCRUDRatingDocuments ***");
        try {
            Document document = createDocument("test-attachment.html", "text/html",
                    "/test/github/richardwilly98/services/test-attachment.html");
            Assert.assertNotNull(document);
            Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(document.getId())
                    .path(RestDocumentService.AUDIT_PATH).request().cookie(adminCookie).accept(MediaType.APPLICATION_JSON).get();
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
            response = target().path(RestRatingService.RATINGS_PATH).request().cookie(adminCookie)
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
            .request().cookie(adminCookie).accept(MediaType.APPLICATION_JSON).get();
            Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
            Rating rating = response.readEntity(Rating.class);
            Assert.assertNotNull(rating);
            Assert.assertEquals(rating.getScore(), ratingRequest.getScore());
            Assert.assertEquals(rating.getUser(), adminCredential.getUsername());

            // Update
            ratingRequest.setScore(1);
            response = target().path(RestRatingService.RATINGS_PATH).path(document.getId()).request().cookie(adminCookie)
                    .put(Entity.entity(ratingRequest, MediaType.APPLICATION_JSON));
            Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
            rating = response.readEntity(Rating.class);
            Assert.assertEquals(rating.getScore(), ratingRequest.getScore());
            Assert.assertEquals(rating.getUser(), adminCredential.getUsername());

            // Delete
            response = target().path(RestRatingService.RATINGS_PATH).path(document.getId()).request().cookie(adminCookie).delete();
            Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
            response = target().path(RestRatingService.RATINGS_PATH).path(document.getId()).request().cookie(adminCookie)
                    .accept(MediaType.APPLICATION_JSON).get();
            Assert.assertTrue(response.getStatus() == Status.NOT_FOUND.getStatusCode());

        } catch (Throwable t) {
            log.error("testCRUDRatingDocuments fail", t);
            Assert.fail();
        }
    }

    @Test
    public void testTotalAverageRatingDocuments() throws Throwable {
        log.debug("*** testTotalAverageRatingDocuments ***");
        try {
            Document document = createDocument("test-attachment.html", "text/html",
                    "/test/github/richardwilly98/services/test-attachment.html");
            Assert.assertNotNull(document);
            Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(document.getId())
                    .path(RestDocumentService.AUDIT_PATH).request().cookie(adminCookie).accept(MediaType.APPLICATION_JSON).get();
            log.debug(String.format("status: %s", response.getStatus()));
            Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());

            // Create
            RestRatingService.RatingRequest ratingRequest = new RatingRequest();
            ratingRequest.setItemId(document.getId());
            ratingRequest.setScore(3);
            response = target().path(RestRatingService.RATINGS_PATH).request().cookie(adminCookie)
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
            .request().cookie(adminCookie).accept(MediaType.APPLICATION_JSON).get();
            Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
            Rating rating = response.readEntity(Rating.class);
            Assert.assertNotNull(rating);
            Assert.assertEquals(rating.getScore(), ratingRequest.getScore());
            Assert.assertEquals(rating.getUser(), adminCredential.getUsername());

            response = target().path(RestRatingService.RATINGS_PATH).path(document.getId()).path(RestRatingService.ALL_PATH).request()
                    .cookie(adminCookie).accept(MediaType.APPLICATION_JSON).get();
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
            Cookie cookie = login(new CredentialImpl.Builder().username(testRatingUser.getLogin()).password("secret".toCharArray()).build());

            // Create
            RestRatingService.RatingRequest ratingRequest2 = new RatingRequest();
            ratingRequest2.setItemId(document.getId());
            ratingRequest2.setScore(5);
            response = target().path(RestRatingService.RATINGS_PATH).request().cookie(cookie)
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
            .request().cookie(cookie).accept(MediaType.APPLICATION_JSON).get();
            Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
            Rating rating2 = response.readEntity(Rating.class);
            Assert.assertNotNull(rating);
            Assert.assertEquals(rating2.getScore(), ratingRequest2.getScore());
            Assert.assertEquals(rating2.getUser(), testRatingUser.getLogin());

            response = target().path(RestRatingService.RATINGS_PATH).path(document.getId()).path(RestRatingService.ALL_PATH).request()
                    .cookie(adminCookie).accept(MediaType.APPLICATION_JSON).get();
            Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
            ratings = response.readEntity(new GenericType<Set<Rating>>() {
            });
            Assert.assertNotNull(ratings);
            Assert.assertTrue(ratings.size() == 2);
            Assert.assertTrue(ratings.contains(rating2));

            // Total ratings
            response = target().path(RestRatingService.RATINGS_PATH).path(document.getId()).path(RestRatingService.TOTAL_PATH).request()
                    .cookie(adminCookie).accept(MediaType.APPLICATION_JSON).get();
            Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
            float total = response.readEntity(float.class);
            Assert.assertTrue(total == ratingRequest.getScore() + ratingRequest2.getScore());

            // Average ratings
            response = target().path(RestRatingService.RATINGS_PATH).path(document.getId()).path(RestRatingService.AVERAGE_PATH).request()
                    .cookie(adminCookie).accept(MediaType.APPLICATION_JSON).get();
            Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
            float average = response.readEntity(float.class);
            Assert.assertTrue(average == (ratingRequest.getScore() + ratingRequest2.getScore()) / ratings.size());

            logout(cookie);

        } catch (Throwable t) {
            log.error("testTotalAverageRatingDocuments fail", t);
            Assert.fail();
        }
    }

    private boolean setCurrentVersion(String documentId, String versionId) throws Throwable {
        log.debug("******* setCurrentVersion  *********");

        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(documentId).path(RestDocumentService.VERSIONS_PATH)
                .path(versionId).path(RestDocumentService.CURRENT_PATH).request().cookie(adminCookie).post(null);
        log.debug(String.format("setCurrentVersion clientResponse location: %s", response.getLocation()));
        log.debug(String.format("setCurrentVersion clientResponse cookie: %s", response.getCookies()));
        log.debug(String.format("setCurrentVersion clientResponse toString: %s", response.toString()));
        log.debug(String.format("setCurrentVersion clientResponse status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());

        log.debug("******* setCurrentVersion end *********");
        return response.getStatus() == Status.OK.getStatusCode();
    }

    private Version updateVersion(String documentId, String versionId, String name, String contentType, String path) throws Throwable {
        log.debug("******* updateVersion  *********");
        byte[] content = copyToBytesFromClasspath(path);
        InputStream is = new ByteArrayInputStream(content);

        FormDataMultiPart form = new FormDataMultiPart();
        form.field("name", name);

        FormDataBodyPart p = new FormDataBodyPart("file", is, MediaType.valueOf(contentType));
        form.bodyPart(p);

        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(documentId).path(RestDocumentService.VERSIONS_PATH)
                .path(versionId).path(RestDocumentService.UPDATE_PATH).request().cookie(adminCookie)
                .post(Entity.entity(form, MediaType.MULTIPART_FORM_DATA_TYPE));
        log.debug(String.format("updateVersion clientResponse location: %s", response.getLocation()));
        log.debug(String.format("updateVersion clientResponse cookie: %s", response.getCookies()));
        log.debug(String.format("updateVersion clientResponse toString: %s", response.toString()));
        log.debug(String.format("updateVersion clientResponse status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.CREATED.getStatusCode());
        URI uri = response.getLocation();
        Assert.assertNotNull(uri);
        log.debug("******* updateVersion end *********");
        return get(uri, Document.class).getCurrentVersion();
    }

    private Version createVersion(String documentId, String name, String contentType, String path) throws Throwable {
        log.debug("******* createVersion  *********");
        byte[] content = copyToBytesFromClasspath(path);
        InputStream is = new ByteArrayInputStream(content);

        FormDataMultiPart form = new FormDataMultiPart();
        form.field("name", name);

        FormDataBodyPart p = new FormDataBodyPart("file", is, MediaType.valueOf(contentType));
        form.bodyPart(p);

        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(documentId).path(RestDocumentService.UPLOAD_PATH)
                .request().cookie(adminCookie).post(Entity.entity(form, MediaType.MULTIPART_FORM_DATA));
        log.debug(String.format("createVersion clientResponse location: %s", response.getLocation()));
        log.debug(String.format("createVersion clientResponse cookie: %s", response.getCookies()));
        log.debug(String.format("createVersion clientResponse toString: %s", response.toString()));
        log.debug(String.format("createVersion clientResponse status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.CREATED.getStatusCode());
        URI uri = response.getLocation();
        Assert.assertNotNull(uri);
        log.debug("******* createVersion end *********");
        return get(uri, Document.class).getCurrentVersion();
    }

    private Version createFromVersion(String documentId, String versionId, String name, String contentType, String path) throws Throwable {
        log.debug("******* createFromVersion  *********");
        byte[] content = copyToBytesFromClasspath(path);
        InputStream is = new ByteArrayInputStream(content);

        FormDataMultiPart form = new FormDataMultiPart();
        form.field("name", name);

        FormDataBodyPart p = new FormDataBodyPart("file", is, MediaType.valueOf(contentType));
        form.bodyPart(p);

        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(documentId).path(RestDocumentService.VERSIONS_PATH)
                .path(versionId).path(RestDocumentService.UPLOAD_PATH).request().cookie(adminCookie)
                .post(Entity.entity(form, MediaType.MULTIPART_FORM_DATA_TYPE));
        log.debug(String.format("createFromVersion clientResponse location: %s", response.getLocation()));
        log.debug(String.format("createFromVersion clientResponse cookie: %s", response.getCookies()));
        log.debug(String.format("createFromVersion clientResponse toString: %s", response.toString()));
        log.debug(String.format("createFromVersion clientResponse status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.CREATED.getStatusCode());
        URI uri = response.getLocation();
        Assert.assertNotNull(uri);
        log.debug("******* createFromVersion end *********");
        return get(uri, Document.class).getCurrentVersion();
    }

    private Document createDocument(String name, String contentType, String path) throws Throwable {
        byte[] content = copyToBytesFromClasspath(path);
        InputStream is = new ByteArrayInputStream(content);

        FormDataMultiPart form = new FormDataMultiPart();
        form.field("name", name);
        FormDataBodyPart p = new FormDataBodyPart("file", is, MediaType.valueOf(contentType));
        form.bodyPart(p);

        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(RestDocumentService.UPLOAD_PATH).request()
                .cookie(adminCookie).post(Entity.entity(form, MediaType.MULTIPART_FORM_DATA_TYPE));
        log.debug(String.format("status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.CREATED.getStatusCode());
        URI uri = response.getLocation();
        Assert.assertNotNull(uri);
        return get(uri, Document.class);
    }

    private void updateDocument(Document document) throws Throwable {
        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(RestDocumentService.UPDATE_PATH)
                .request(MediaType.APPLICATION_JSON).cookie(adminCookie).put(Entity.entity(document, MediaType.APPLICATION_JSON));
        log.debug(String.format("status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
    }

    private Document getDocument(String id) throws Throwable {
        Document document = get(id, Document.class, RestDocumentService.DOCUMENTS_PATH);
        Assert.assertNotNull(document);
        return document;
    }

    private Document getMetadata(String id) throws Throwable {
        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(id).path(RestDocumentService.METADATA_PATH)
                .request(MediaType.APPLICATION_JSON).cookie(adminCookie).get();
        log.debug(String.format("status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        Document document = response.readEntity(Document.class);
        Assert.assertNotNull(document);
        return document;
    }

    private void markDeletedDocument(String id) throws Throwable {
        Response response = target().path(RestDocumentService.DOCUMENTS_PATH).path(id).path(RestDocumentService.MARKDELETED_PATH).request()
                .cookie(adminCookie).post(null);
        log.debug(String.format("status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.NO_CONTENT.getStatusCode());
    }
}
