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

import com.github.richardwilly98.esdms.search.api.Term;
import com.google.common.base.Objects;

public class TermImpl implements Term {

    private String term;
    private int count;

    public static class Builder {

	private String term;
	private int count;

	public Builder term(String term) {
	    this.term = term;
	    return this;
	}

	public Builder count(int count) {
	    this.count = count;
	    return this;
	}

	public TermImpl build() {
	    return new TermImpl(this);
	}
    }

    TermImpl() {
	this(null);
    }

    public TermImpl(Builder builder) {
	if (builder != null) {
	    this.term = builder.term;
	    this.count = builder.count;
	}
    }

    @Override
    public String getTerm() {
	return term;
    }

    @Override
    public int getCount() {
	return count;
    }

    void setTerm(String term) {
	this.term = term;
    }

    void setCount(int count) {
	this.count = count;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + count;
        result = prime * result + ((term == null) ? 0 : term.hashCode());
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
        TermImpl other = (TermImpl) obj;
        if (count != other.count)
            return false;
        if (term == null) {
            if (other.term != null)
                return false;
        } else if (!term.equals(other.term))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("term", term).add("count", count).toString();
    }

    @Override
    public int compareTo(Term term) {
        int i = this.count - term.getCount();
        if (i < 0) {
            return -1;
        }
        if (i > 0) {
            return 1;
        }
        return 0;
    }

}
