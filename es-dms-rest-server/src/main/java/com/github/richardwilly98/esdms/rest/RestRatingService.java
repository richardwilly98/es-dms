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

import java.net.URI;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.github.richardwilly98.esdms.api.Rating;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.rest.exception.RestServiceException;
import com.github.richardwilly98.esdms.services.AuthenticationService;
import com.github.richardwilly98.esdms.services.RatingService;

@Path(RestRatingService.RATINGS_PATH)
public class RestRatingService extends RestServiceBase {

    public static final String RATINGS_PATH = "ratings";
    public static final String ALL_PATH = "_all";
    public static final String TOTAL_PATH = "_total";
    public static final String AVERAGE_PATH = "_average";

    private final RatingService ratingService;

    @Inject
    public RestRatingService(final AuthenticationService authenticationService, final RatingService ratingService) {
        super(authenticationService);
        this.ratingService = ratingService;
    }

    protected URI getItemUri(String itemId, Rating rating) {
        checkNotNull(itemId);
        checkNotNull(rating);
        return url.getBaseUriBuilder().path(getClass()).path(itemId).path(rating.getUser()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(RatingRequest item) {
        checkNotNull(item);
        if (log.isTraceEnabled()) {
            log.trace(String.format("create - %s", item));
        }
        try {
            Rating rating = ratingService.create(item.getItemId(), item.getScore());
            return Response.created(getItemUri(item.getItemId(), rating)).build();
        } catch (ServiceException e) {
            throw new RestServiceException(e.getLocalizedMessage());
        }
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{id}")
    public Response get(@PathParam("id") String id) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("get - %s", id));
        }
        try {
            Rating rating = ratingService.get(id);
            if (rating == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            return Response.ok(rating).build();
        } catch (ServiceException e) {
            throw new RestServiceException(e.getLocalizedMessage());
        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_JSON })
    public Response update(@PathParam("id") String id, RatingRequest item) {
        checkNotNull(item);
        if (log.isTraceEnabled()) {
            log.trace(String.format("update - %s", item));
        }
        try {
            Rating rating = ratingService.update(id, item.getScore());
            return Response.ok(rating).build();
        } catch (ServiceException e) {
            throw new RestServiceException(e.getLocalizedMessage());
        }
    }

    @DELETE
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{id}")
    public Response delete(@PathParam("id") String id) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("delete - %s", id));
        }
        try {
            Rating rating = ratingService.get(id);
            if (rating != null) {
                ratingService.delete(id, rating);
            } else {
                return Response.status(Status.NOT_FOUND).build();
            }
            return Response.ok().build();
        } catch (ServiceException e) {
            throw new RestServiceException(e.getLocalizedMessage());
        }
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{id}/" + ALL_PATH)
    public Response getRatings(@PathParam("id") String id) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("getRatings - %s", id));
        }
        try {
            Set<Rating> ratings = ratingService.getRatings(id);
            if (ratings == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            return Response.ok(ratings).build();
        } catch (ServiceException e) {
            throw new RestServiceException(e.getLocalizedMessage());
        }
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{id}/" + TOTAL_PATH)
    public Response getTotalRatings(@PathParam("id") String id) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("getTotalRatings - %s", id));
        }
        try {
            float total = ratingService.getTotalRatings(id);
            return Response.ok(total).build();
        } catch (ServiceException e) {
            throw new RestServiceException(e.getLocalizedMessage());
        }
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{id}/" + AVERAGE_PATH)
    public Response getAverageRatings(@PathParam("id") String id) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("getAverageRatings - %s", id));
        }
        try {
            float total = ratingService.getAverageRatings(id);
            return Response.ok(total).build();
        } catch (ServiceException e) {
            throw new RestServiceException(e.getLocalizedMessage());
        }
    }

    public static class RatingRequest {
        private String itemId;
        private int score;

        public RatingRequest() {
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

    }
}
