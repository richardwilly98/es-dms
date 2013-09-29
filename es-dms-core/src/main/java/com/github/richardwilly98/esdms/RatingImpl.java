package com.github.richardwilly98.esdms;

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

import java.util.Date;

import com.github.richardwilly98.esdms.api.Rating;
import com.google.common.base.Objects;

public class RatingImpl implements Rating {

    // private final String itemId;
    private String user;
    private Date date;
    private int score;

    public static class Builder {

	// private String itemId;
	private String user;
	private Date date;
	private int score;

	// public Builder itemId(String itemId) {
	// this.itemId = itemId;
	// return this;
	// }

	public Builder user(String user) {
	    this.user = user;
	    return this;
	}

	public Builder date(Date date) {
	    this.date = date;
	    return this;
	}

	public Builder score(int score) {
	    this.score = score;
	    return this;
	}

	public RatingImpl build() {
	    return new RatingImpl(this);
	}
    }

    RatingImpl() {
	this(null);
    }

    public RatingImpl(Builder builder) {
	// checkNotNull(builder);
	// this.itemId = builder.itemId;
	if (builder != null) {
	    this.user = builder.user;
	    this.date = builder.date;
	    this.score = builder.score;
	}
    }

    // @Override
    // public String getItemId() {
    // return itemId;
    // }

    @Override
    public String getUser() {
	return user;
    }

    @Override
    public Date getDate() {
	return date;
    }

    @Override
    public int getScore() {
	return score;
    }

    @Override
    public String toString() {
	return Objects.toStringHelper(this)
	// .add("item", itemId)
	        .add("user", user).add("date", date).add("score", score).toString();
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == this) {
	    return true;
	}
	if (obj == null || obj.getClass() != this.getClass()) {
	    return false;
	}

	RatingImpl obj2 = (RatingImpl) obj;
	return (/* (itemId.equals(obj2.getItemId())) && */(user.equals(obj2.getUser())) && (date.equals(obj2.getDate()) || (score == obj2
	        .getScore())));
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + score;
	// result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
	result = prime * result + ((user == null) ? 0 : user.hashCode());
	result = prime * result + ((date == null) ? 0 : date.hashCode());
	return result;
    }
}
