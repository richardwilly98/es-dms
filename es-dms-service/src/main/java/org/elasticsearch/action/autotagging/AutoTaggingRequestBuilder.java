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

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.single.custom.SingleCustomOperationRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.internal.InternalClient;
import org.elasticsearch.common.Nullable;

public class AutoTaggingRequestBuilder extends
        SingleCustomOperationRequestBuilder<AutoTaggingRequest, AutoTaggingResponse, AutoTaggingRequestBuilder> {

    protected AutoTaggingRequestBuilder(Client client) {
        super((InternalClient) client, new AutoTaggingRequest());
    }

    public AutoTaggingRequestBuilder(Client client, @Nullable String index) {
        super((InternalClient) client, new AutoTaggingRequest(index));
    }

    /**
     * Sets the type to index the document to.
     */
    public AutoTaggingRequestBuilder setIndex(String index) {
        request.index(index);
        return this;
    }

    /**
     * Sets the type to index the document to.
     */
    public AutoTaggingRequestBuilder setType(String type) {
        request.type(type);
        return this;
    }

    /**
     * Sets the id to index the document under. Optional, and if not set, one
     * will be automatically generated.
     */
    public AutoTaggingRequestBuilder setId(String id) {
        request.id(id);
        return this;
    }

    public AutoTaggingRequestBuilder setField(String field) {
        request.field(field);
        return this;
    }

    public AutoTaggingRequestBuilder setContent(String content) {
        request.content(content);
        return this;
    }

    public AutoTaggingRequestBuilder setMax(Integer max) {
        request.max(max);
        return this;
    }

    @Override
    protected void doExecute(ActionListener<AutoTaggingResponse> listener) {
        ((Client) client).execute(AutoTaggingAction.INSTANCE, request, listener);
    }
}
