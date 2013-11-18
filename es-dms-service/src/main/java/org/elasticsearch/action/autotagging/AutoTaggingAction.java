package org.elasticsearch.action.autotagging;

/*
 * #%L
 * es-dms-service
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


import org.elasticsearch.action.Action;
import org.elasticsearch.client.Client;

public class AutoTaggingAction extends Action<AutoTaggingRequest, AutoTaggingResponse, AutoTaggingRequestBuilder> {

    public static final AutoTaggingAction INSTANCE = new AutoTaggingAction();
    public static final String NAME = "autotagging";

    private AutoTaggingAction() {
        super(NAME);
    }

    @Override
    public AutoTaggingRequestBuilder newRequestBuilder(Client client) {
        return new AutoTaggingRequestBuilder(client);
    }

    @Override
    public AutoTaggingResponse newResponse() {
        return new AutoTaggingResponse();
    }

}
