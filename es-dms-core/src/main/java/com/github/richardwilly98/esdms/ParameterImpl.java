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

import javax.validation.constraints.NotNull;

import com.github.richardwilly98.esdms.api.Parameter;
import com.google.common.base.Objects;

public class ParameterImpl extends ItemBaseImpl implements Parameter {

    private static final long serialVersionUID = 1L;
    private ParameterType type;

    public static class Builder extends BuilderBase<Builder> {

        private ParameterType type;

        public Builder type(ParameterType type) {
            this.type = type;
            return getThis();
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        public ParameterImpl build() {
            return new ParameterImpl(this);
        }
    }

    ParameterImpl() {
        this(null);
    }

    protected ParameterImpl(Builder builder) {
        super(builder);
        if (builder != null) {
            this.type = builder.type;
        }
    }

    @Override
    @NotNull(message = "type is required")
    public ParameterType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        ParameterImpl other = (ParameterImpl) obj;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("id", id).add("name", name).add("type", type).add("description", description)
                .add("attributes", getAttributes()).toString();
    }

}
