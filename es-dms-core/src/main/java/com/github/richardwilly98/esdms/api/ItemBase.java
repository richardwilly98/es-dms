package com.github.richardwilly98.esdms.api;

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

import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

public interface ItemBase {

    @NotNull(message = "id is required")
    public abstract String getId();

    public abstract void setId(String id);

    public abstract boolean isDisabled();

    public abstract void setDisabled(boolean value);

    @NotNull(message = "name is required")
    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getDescription();

    public abstract void setDescription(String description);

    public abstract Map<String, Object> getAttributes();

    public abstract Map<String, Object> getAttributes(Set<String> keys);

    public abstract void setAttribute(String name, Object value);

    public abstract void setAttributes(Map<String, Object> attributes);

    public abstract void removeAttribute(String name);

    public abstract Set<String> getReadOnlyAttributeKeys();

    public abstract byte[] getHashCode();

    public abstract void setHashCode(byte[] id);
}