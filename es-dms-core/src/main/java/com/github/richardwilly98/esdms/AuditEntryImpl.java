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
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.richardwilly98.esdms.api.AuditEntry;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

public class AuditEntryImpl extends ItemBaseImpl implements AuditEntry {

    private static final long serialVersionUID = 1L;
    private String user;
    private AuditEntry.Event event;
    @JsonProperty("item_id")
    private String itemId;
    private Date date;

    public static class Builder extends BuilderBase<Builder> {

	private String user;
	private AuditEntry.Event event;
	private String itemId;
	private Date date;

	public Builder user(String user) {
	    this.user = user;
	    return getThis();
	}

	public Builder date(Date date) {
	    this.date = date;
	    return getThis();
	}

	public Builder event(AuditEntry.Event event) {
	    this.event = event;
	    return getThis();
	}

	public Builder itemId(String itemId) {
	    this.itemId = itemId;
	    return getThis();
	}

	@Override
	protected Builder getThis() {
	    return this;
	}

	public AuditEntryImpl build() {
	    if (Strings.isNullOrEmpty(name)) {
		name = new StringBuilder().append(event).append("-").append(user).append("-").append(itemId).toString();
	    }
	    return new AuditEntryImpl(this);
	}
    }

    AuditEntryImpl() {
	this(null);
    }

    protected AuditEntryImpl(Builder builder) {
	super(builder);
	if (builder != null) {
	    user = builder.user;
	    date = builder.date;
	    event = builder.event;
	    itemId = builder.itemId;
	}
    }

    /*
     * Method used to deserialize attributes Map
     */
    @JsonProperty("attributes")
    private void deserialize(Map<String, Object> attributes) {
	// if (!attributes.containsKey(AuditEntryImpl.STATUS)) {
	// attributes.put(AuditEntryImpl.STATUS,
	// DocumentImpl.DocumentStatus.AVAILABLE.getStatusCode());
	// }
	getAttributes().putAll(attributes);
    }

    @Override
    public String getUser() {
	return user;
    }

    @Override
    public Date getDate() {
	return date;
    }

    @Override
    public AuditEntry.Event getEvent() {
	return event;
    }

    @Override
    public String getItemId() {
	return itemId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((event == null) ? 0 : event.hashCode());
        result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        AuditEntryImpl other = (AuditEntryImpl) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (event != other.event)
            return false;
        if (itemId == null) {
            if (other.itemId != null)
                return false;
        } else if (!itemId.equals(other.itemId))
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        return true;
    }

    @Override
    public String toString() {
	return Objects.toStringHelper(this).add("id", id).add("name", name).add("user", user).add("date", date).add("type", event)
	        .add("itemId", itemId).add("description", description).add("attributes", getAttributes()).toString();
    }
}
