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

import java.util.EnumSet;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.richardwilly98.esdms.ParameterImpl;

@JsonDeserialize(as = ParameterImpl.class)
public interface Parameter extends ItemBase {

    public enum ParameterType {
        SYSTEM(0, "System"), USER(1, "User"), CUSTOM(2, "Custom");

        private int type;
        private String description;

        ParameterType(int type, String description) {
            this.type = type;
            this.description = description;
        }

        @JsonValue
        public int getType() {
            return this.type;
        }
        
        public String getDescription() {
            return this.description;
        }

        @JsonValue
        public static ParameterType fromValue(int value) {
            for (ParameterType roleType : EnumSet.allOf(ParameterType.class)) {
                if (roleType.getType() == value) {
                    return roleType;
                }
            }
            throw new IllegalArgumentException("Invalid parameter type: " + value);
        }
    }

    @NotNull(message= "type is required")
    public abstract ParameterType getType();
}