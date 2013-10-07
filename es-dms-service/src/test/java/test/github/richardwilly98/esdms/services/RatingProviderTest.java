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

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.Rating;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.services.RatingService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

public class RatingProviderTest extends ProviderTestBase {

    @Inject
    RatingService ratingService;
    private User ratingUser;

    private Rating createRating(String itemId, int score) throws Throwable {
        Rating rating = ratingService.create(itemId, score);
        Assert.assertNotNull(rating);
        Rating newRating = ratingService.get(itemId);
        Assert.assertNotNull(newRating);
        Assert.assertEquals(rating, newRating);
        return newRating;
    }

    private String createDocument() throws Throwable {
        return createDocument("test-attachment.html", "text/html", "/test/github/richardwilly98/services/test-attachment.html");
    }

    @Test
    public void testCreateRating() throws Throwable {
        log.info("Start testCreateRating");
        // Make sure to be login with user having sufficient permission
        loginAdminUser();
        String id = createDocument();
        Rating rating = createRating(id, 9);
        log.debug(rating);
        Document document = documentService.get(id);
        Assert.assertTrue(document.getRatings().size() == 1);
        Assert.assertTrue(document.getRatings().contains(rating));
    }

    @Test
    public void testDeleteRating() throws Throwable {
        log.info("Start testDeleteRating");
        String id = createDocument();
        Rating rating = createRating(id, 9);
        log.debug(rating);
        Document document = documentService.get(id);
        Assert.assertTrue(document.getRatings().size() == 1);
        Assert.assertTrue(document.getRatings().contains(rating));

        ratingService.delete(id, rating);
        document = documentService.get(id);
        Assert.assertTrue(document.getRatings().size() == 0);
        Assert.assertTrue(!document.getRatings().contains(rating));
    }

    @Test
    public void testUpdateRating() throws Throwable {
        log.info("Start testUpdateRating");
        // Make sure to be login with user having sufficient permission
        loginAdminUser();
        String id = createDocument();
        Rating rating = createRating(id, 9);
        log.debug(rating);
        Document document = documentService.get(id);
        Assert.assertTrue(document.getRatings().size() == 1);
        Assert.assertTrue(document.getRatings().contains(rating));

        Rating rating2 = ratingService.update(id, 5);
        Assert.assertTrue(rating.getUser().equals(rating2.getUser()));
        Assert.assertTrue(rating.getDate().before(rating2.getDate()));
        document = documentService.get(id);
        Assert.assertTrue(!document.getRatings().contains(rating));
        Assert.assertTrue(document.getRatings().contains(rating2));
    }

    @Test
    public void testTotalRating() throws Throwable {
        log.info("Start testTotalRating");
        loginAdminUser();
        String id = createDocument();
        Rating rating = createRating(id, 9);
        log.debug(rating);
        Document document = documentService.get(id);
        Assert.assertTrue(document.getRatings().size() == 1);
        Assert.assertTrue(document.getRatings().contains(rating));

        // Make sure to have a user with enough permission
        login(createUserWithWriterRole());
        Rating rating2 = createRating(id, 5);
        document = documentService.get(id);
        Assert.assertTrue(document.getRatings().size() == 2);
        Assert.assertTrue(document.getRatings().contains(rating));
        Assert.assertTrue(document.getRatings().contains(rating2));
        float total = ratingService.getTotalRatings(id);
        Assert.assertTrue(total == rating.getScore() + rating2.getScore());
    }

    private User createUserWithWriterRole() throws ServiceException {
        if (ratingUser == null) {
            String id = "rating-ruser-" + System.currentTimeMillis() + "@gmail.com";
            ratingUser = createUser(id, "", false, id, id, "secret".toCharArray(), ImmutableSet.of(writerRole));
            Assert.assertNotNull(ratingUser);
        }
        return ratingUser;
    }

    @Test
    public void testAverageRating() throws Throwable {
        log.info("Start testAverageRating");
        loginAdminUser();
        String id = createDocument();
        Rating rating = createRating(id, 9);
        log.debug(rating);
        Document document = documentService.get(id);
        Assert.assertTrue(document.getRatings().size() == 1);
        Assert.assertTrue(document.getRatings().contains(rating));

        // Make sure to have a user with enough permission
        login(createUserWithWriterRole());
        Rating rating2 = createRating(id, 5);
        document = documentService.get(id);
        Assert.assertTrue(document.getRatings().size() == 2);
        Assert.assertTrue(document.getRatings().contains(rating));
        Assert.assertTrue(document.getRatings().contains(rating2));
        float average = ratingService.getAverageRatings(id);
        Assert.assertTrue(average == (rating.getScore() + rating2.getScore()) / 2);
    }

}
