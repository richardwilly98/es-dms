package com.github.richardwilly98.esdms.search;

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

import com.github.richardwilly98.esdms.search.api.TermRequest;
import com.google.common.base.Objects;

public class TermRequestImpl implements TermRequest {

    private String field;
    private int size;

    public static class Builder {

	private String field;
	private int size;

	public Builder fieldName(String field) {
	    this.field = field;
	    return this;
	}

	public Builder size(int size) {
	    this.size = size;
	    return this;
	}

	public TermRequestImpl build() {
	    return new TermRequestImpl(this);
	}
    }

    TermRequestImpl() {
	this(null);
    }

    public TermRequestImpl(Builder builder) {
	if (builder != null) {
	    this.field = builder.field;
	    this.size = builder.size;
	}
    }

    @Override
    public String getField() {
	return field;
    }

    @Override
    public int getSize() {
	return size;
    }

    void setFieldName(String fieldName) {
	this.field = fieldName;
    }

    void setSize(int size) {
	this.size = size;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + size;
        result = prime * result + ((field == null) ? 0 : field.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TermRequestImpl other = (TermRequestImpl) obj;
        if (size != other.size)
            return false;
        if (field == null) {
            if (other.field != null)
                return false;
        } else if (!field.equals(other.field))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("fieldName", field).add("size", size).toString();
    }

}
