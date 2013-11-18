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

import java.io.IOException;

import org.elasticsearch.action.support.single.custom.SingleCustomOperationRequest;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;

public class AutoTaggingRequest extends SingleCustomOperationRequest<AutoTaggingRequest> {

    private String index;
    private String type;
    private String id;
    private String field;
    private String content;
    private Integer max;

    public AutoTaggingRequest() {
    }

    public AutoTaggingRequest(String index) {
        this.index = index;
    }

    public AutoTaggingRequest index(String index) {
        this.index = index;
        return this;
    }

    public AutoTaggingRequest type(String type) {
        this.type = type;
        return this;
    }

    public AutoTaggingRequest id(String id) {
        this.id = id;
        return this;
    }

    public AutoTaggingRequest field(String field) {
        this.field = field;
        return this;
    }

    public AutoTaggingRequest content(String content) {
        this.content = content;
        return this;
    }

    public AutoTaggingRequest max(Integer max) {
        this.max = max;
        return this;
    }

    public String getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getField() {
        return field;
    }

    public String getContent() {
        return content;
    }

    public Integer getMax() {
        return max;
    }

    @Override
    public void readFrom(StreamInput in) throws IOException {
        super.readFrom(in);
        index = in.readOptionalString();
        type = in.readOptionalString();
        id = in.readOptionalString();
        field = in.readOptionalString();
        content = in.readOptionalString();
        max = in.readInt();
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeOptionalString(getIndex());
        out.writeOptionalString(getType());
        out.writeOptionalString(getId());
        out.writeOptionalString(getField());
        out.writeOptionalString(getContent());
        out.writeInt(max);
    }

}
