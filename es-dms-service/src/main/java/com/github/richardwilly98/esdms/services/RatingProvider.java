package com.github.richardwilly98.esdms.services;

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


import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.Set;

import javax.inject.Inject;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.github.richardwilly98.esdms.RatingImpl;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.Rating;
import com.github.richardwilly98.esdms.exception.ServiceException;

public class RatingProvider extends AuthenticatedServiceBase implements RatingService {

    final DocumentService documentService;

    @Inject
    RatingProvider(final DocumentService documentService) throws ServiceException {
	checkNotNull(documentService);
	this.documentService = documentService;
    }

    @Override
    public Rating get(String itemId) throws ServiceException {
	try {
	    if (log.isTraceEnabled()) {
		log.trace(String.format("get - %s", itemId));
	    }
	    Document document = documentService.getMetadata(itemId);
	    if (document == null) {
		log.warn(String.format("Cannot find item %s", itemId));
		return null;
	    }
	    Set<Rating> ratings = document.getRatings();
	    if (ratings.size() == 0) {
		log.info(String.format("No rating for item %s", itemId));
		return null;
	    }
	    for (Rating rating : ratings) {
		if (getCurrentUser().equals(rating.getUser())) {
		    return rating;
		}
	    }
	    log.debug(String.format("No rating for user %s in item %s", getCurrentUser(), itemId));
	    return null;
	} catch (Throwable t) {
	    log.error("get failed", t);
	    throw new ServiceException(t.getLocalizedMessage());
	}
    }

    @RequiresPermissions(DocumentService.DocumentPermissions.Constants.DOCUMENT_EDIT)
    @Override
    public Rating create(String itemId, int score) throws ServiceException {
	try {
	    if (log.isTraceEnabled()) {
		log.trace(String.format("create rating for user %s in item %s", getCurrentUser(), itemId));
	    }
	    Document document = documentService.getMetadata(itemId);
	    if (document == null) {
		log.warn(String.format("Cannot find item %s", itemId));
		return null;
	    }
	    Set<Rating> ratings = document.getRatings();
	    Rating rating = null;
	    for (Rating rt : ratings) {
		if (getCurrentUser().equals(rt.getUser())) {
		    rating = rt;
		    break;
		}
	    }
	    if (rating != null) {
		document.removeRating(rating);
	    } else {
		    log.debug(String.format("No rating for user %s in item %s", getCurrentUser(), itemId));
	    }
	    rating = new RatingImpl.Builder().user(getCurrentUser()).score(score).date(new Date()).build();
	    document.addRating(rating);
	    log.debug(String.format("About to update document with rating: %s", document));
	    documentService.updateMetadata(document);
	    return rating;
	} catch (Throwable t) {
	    log.error("create failed", t);
	    throw new ServiceException(t.getLocalizedMessage());
	}
    }

    @RequiresPermissions(DocumentService.DocumentPermissions.Constants.DOCUMENT_EDIT)
    @Override
    public Rating update(String itemId, int score) throws ServiceException {
	if (log.isTraceEnabled()) {
	    log.trace(String.format("update - %s - %s", itemId, score));
	}
	return create(itemId, score);
    }

    @RequiresPermissions(DocumentService.DocumentPermissions.Constants.DOCUMENT_EDIT)
    @Override
    public void delete(String itemId, Rating rating) throws ServiceException {
	try {
	    if (log.isTraceEnabled()) {
		log.trace(String.format("delete - %s", itemId));
	    }
	    Document document = documentService.getMetadata(itemId);
	    if (document == null) {
		log.warn(String.format("Cannot find item %s", itemId));
		return;
	    }
	    Set<Rating> ratings = document.getRatings();
	    if (ratings.contains(rating)) {
		document.removeRating(rating);
		documentService.updateMetadata(document);
	    } else {
		log.info(String.format("Could not find rating %s in item %s", rating, itemId));
	    }
	} catch (Throwable t) {
	    log.error("delete failed", t);
	    throw new ServiceException(t.getLocalizedMessage());
	}
    }

    @Override
    public Set<Rating> getRatings(String itemId) throws ServiceException {
	try {
	    if (log.isTraceEnabled()) {
		log.trace(String.format("getRatings - %s", itemId));
	    }
	    Document document = documentService.getMetadata(itemId);
	    if (document == null) {
		log.warn(String.format("Cannot find item %s", itemId));
		return null;
	    }
	    Set<Rating> ratings = document.getRatings();
	    return ratings;
	} catch (Throwable t) {
	    log.error("getRatings failed", t);
	    throw new ServiceException(t.getLocalizedMessage());
	}
    }

    @Override
    public float getTotalRatings(String itemId) throws ServiceException {
	try {
	    if (log.isTraceEnabled()) {
		log.trace(String.format("getTotalRatings - %s", itemId));
	    }
	    float total = 0;
	    Set<Rating> ratings = getRatings(itemId);
	    for (Rating rating : ratings) {
		total += rating.getScore();
	    }
	    return total;
	} catch (Throwable t) {
	    log.error("getTotalRatings failed", t);
	    throw new ServiceException(t.getLocalizedMessage());
	}
    }

    @Override
    public float getAverageRatings(String itemId) throws ServiceException {
	try {
	    if (log.isTraceEnabled()) {
		log.trace(String.format("getAverageRatings - %s", itemId));
	    }
	    float average = 0;
	    Set<Rating> ratings = getRatings(itemId);
	    if (ratings.size() == 0) {
		return 0;
	    }
	    for (Rating rating : ratings) {
		average += rating.getScore();
	    }
	    return average / ratings.size();
	} catch (Throwable t) {
	    log.error("getAverageRatings failed", t);
	    throw new ServiceException(t.getLocalizedMessage());
	}
    }

}
