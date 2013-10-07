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

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.richardwilly98.esdms.AuditEntryImpl;

@JsonDeserialize(as = AuditEntryImpl.class)
public interface AuditEntry extends ItemBase {

    public enum Event {
        UNDEFINED(Constants.UNDEFINED_EVENT_ID), CUSTOM(Constants.CUSTOM_EVENT_ID), UPLOAD(Constants.UPLOAD_EVENT_ID), CHECKOUT(
                Constants.CHECKOUT_EVENT_ID), CHECKIN(Constants.CHECKIN_EVENT_ID);

        private String eventName;

        private Event(String eventName) {
            this.eventName = eventName;
        }

        public String getEventName() {
            return eventName;
        }

        public static class Constants {
            public static final String UNDEFINED_EVENT_ID = "undefined";
            public static final String CUSTOM_EVENT_ID = "custom";
            public static final String UPLOAD_EVENT_ID = "upload";
            public static final String CHECKOUT_EVENT_ID = "checkout";
            public static final String CHECKIN_EVENT_ID = "checkin";
        }
    }

    @NotNull(message = "user is required")
    public abstract String getUser();

    @NotNull(message = "date is required")
    public abstract Date getDate();

    @NotNull(message = "event is required")
    public abstract Event getEvent();

    @NotNull(message = "itemId is required")
    public abstract String getItemId();

}