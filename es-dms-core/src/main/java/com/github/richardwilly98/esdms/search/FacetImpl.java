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

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.github.richardwilly98.esdms.search.api.Facet;
import com.github.richardwilly98.esdms.search.api.Term;
import com.google.common.base.Objects;

public class FacetImpl implements Facet {

    private String name;
    private String type;
    private final List<Term> terms = newArrayList();
    private long missingCount;
    private long otherCount;
    private long totalCount;

    public static class Builder {

        private String name;
        private String type;
	private final List<Term> terms = newArrayList();
	private long missingCount;
	private long otherCount;
	private long totalCount;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder terms(List<Term> terms) {
	    if (terms != null) {
		this.terms.addAll(terms);
	    }
	    return this;
	}

	public Builder missingCount(long missingCount) {
	    this.missingCount = missingCount;
	    return this;
	}

	public Builder otherCount(long otherCount) {
	    this.otherCount = otherCount;
	    return this;
	}

	public Builder totalCount(long totalCount) {
	    this.totalCount = totalCount;
	    return this;
	}

	public FacetImpl build() {
	    return new FacetImpl(this);
	}
    }

    FacetImpl() {
	this(null);
    }

    protected FacetImpl(Builder builder) {
	if (builder != null) {
	    this.name = builder.name;
	    this.type = builder.type;
	    this.terms.addAll(builder.terms);
	    this.missingCount = builder.missingCount;
	    this.otherCount = builder.otherCount;
	    this.totalCount = builder.totalCount;
	}
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getType() {
        return type;
    }

    @Override
    public List<Term> getTerms() {
	return terms;
    }

    @Override
    public long getMissingCount() {
	return missingCount;
    }

    @Override
    public long getOtherCount() {
	return otherCount;
    }

    @Override
    public long getTotalCount() {
	return totalCount;
    }

    void setMissingCount(long missingCount) {
	this.missingCount = missingCount;
    }

    void setOtherCount(long otherCount) {
	this.otherCount = otherCount;
    }

    void setTotalCount(long totalCount) {
	this.totalCount = totalCount;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + (int) (missingCount ^ (missingCount >>> 32));
        result = prime * result + (int) (otherCount ^ (otherCount >>> 32));
        result = prime * result + ((terms == null) ? 0 : terms.hashCode());
        result = prime * result + (int) (totalCount ^ (totalCount >>> 32));
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
        FacetImpl other = (FacetImpl) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (missingCount != other.missingCount)
            return false;
        if (otherCount != other.otherCount)
            return false;
        if (terms == null) {
            if (other.terms != null)
                return false;
        } else if (!terms.equals(other.terms))
            return false;
        if (totalCount != other.totalCount)
            return false;
        return true;
    }

    @Override
    public String toString() {
	return Objects.toStringHelper(this).add("name", name).add("type", type).add("terms", terms).add("missingCount", missingCount).add("otherCount", otherCount)
	        .add("totalCount", totalCount).toString();
    }

}
