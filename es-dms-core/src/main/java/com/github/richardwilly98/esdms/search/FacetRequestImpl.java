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

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import com.github.richardwilly98.esdms.search.api.FacetRequest;
import com.github.richardwilly98.esdms.search.api.TermRequest;
import com.google.common.base.Objects;

public class FacetRequestImpl implements FacetRequest {

    private String name;
    private final Set<TermRequest> terms = newHashSet();

    public static class Builder {

        private String name;
        private final Set<TermRequest> terms = newHashSet();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder terms(Set<TermRequest> terms) {
            if (terms != null) {
                this.terms.addAll(terms);
            }
            return this;
        }

        public FacetRequestImpl build() {
            return new FacetRequestImpl(this);
        }
    }

    FacetRequestImpl() {
        this(null);
    }

    protected FacetRequestImpl(Builder builder) {
        if (builder != null) {
            this.name = builder.name;
            this.terms.addAll(builder.terms);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<TermRequest> getTerms() {
        return terms;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((terms == null) ? 0 : terms.hashCode());
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
        FacetRequestImpl other = (FacetRequestImpl) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (terms == null) {
            if (other.terms != null)
                return false;
        } else if (!terms.equals(other.terms))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("name", name).add("terms", terms).toString();
    }

}
