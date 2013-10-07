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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.Rating;
import com.github.richardwilly98.esdms.api.Version;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class DocumentImpl extends SecuredItemImpl implements Document {

    private static final long serialVersionUID = 1L;
    private static final Set<String> readOnlyAttributes = ImmutableSet.of(DocumentSystemAttributes.AUTHOR.getKey(),
            DocumentSystemAttributes.CREATION_DATE.getKey(), DocumentSystemAttributes.MODIFIED_DATE.getKey(),
            DocumentSystemAttributes.STATUS.getKey(), DocumentSystemAttributes.LOCKED_BY.getKey());

    private final Set<String> tags = newHashSet();
    private final Set<Version> versions = newHashSet();
    private final Set<Rating> ratings = newHashSet();

    public static class Builder extends SecuredItemImpl.Builder<DocumentImpl.Builder> {

        private Set<String> tags;
        private Set<Version> versions;
        private Set<Rating> ratings;

        public Builder tags(Set<String> tags) {
            this.tags = tags;
            return getThis();
        }

        public Builder versions(Set<Version> versions) {
            this.versions = versions;
            return getThis();
        }

        public Builder ratings(Set<Rating> ratings) {
            this.ratings = ratings;
            return getThis();
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        public DocumentImpl build() {
            return new DocumentImpl(this);
        }
    }

    DocumentImpl() {
        this(null);
    }

    protected DocumentImpl(Builder builder) {
        super(builder);
        if (builder != null) {
            if (builder.tags != null) {
                this.tags.addAll(builder.tags);
            }
            if (builder.versions != null) {
                this.versions.addAll(builder.versions);
            }
            if (builder.ratings != null) {
                this.ratings.addAll(builder.ratings);
            }
        }
        readOnlyAttributeKeys = readOnlyAttributes;
    }

    /*
     * Method used to deserialize attributes Map
     */
    @JsonProperty("attributes")
    private void deserialize(Map<String, Object> attributes) {
        if (!attributes.containsKey(DocumentSystemAttributes.STATUS.getKey())) {
            attributes.put(DocumentSystemAttributes.STATUS.getKey(), DocumentImpl.DocumentStatus.AVAILABLE.getStatusCode());
        }
        getAttributes().putAll(attributes);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.IDocument#getCurrentVersion()
     */
    @Override
    @JsonIgnore
    public Version getCurrentVersion() {
        if (versions == null || versions.size() == 0) {
            return null;
        } else {
            try {
                return Iterables.find(versions, new Predicate<Version>() {
                    @Override
                    public boolean apply(Version version) {
                        return version.isCurrent();
                    }
                });
            } catch (NoSuchElementException ex) {
                return null;
            }
        }
    }

    @Override
    @JsonIgnore
    public Version getVersion(final int versionId) {
        checkArgument(versionId > 0);
        try {
            return Iterables.find(versions, new Predicate<Version>() {
                @Override
                public boolean apply(Version version) {
                    return (version.getVersionId() == versionId);
                }
            });
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    @Override
    @JsonIgnore
    public DocumentStatus getStatus() {
        if (this.getAttributes().containsKey(DocumentSystemAttributes.STATUS.getKey())) {
            String status = this.getAttributes().get(DocumentSystemAttributes.STATUS.getKey()).toString();
            return DocumentStatus.getDocumentStatus(status);
        }
        return null;
    }

    @Override
    @JsonIgnore
    public boolean hasStatus(DocumentStatus status) {

        if (!this.getAttributes().containsKey(DocumentSystemAttributes.STATUS.getKey())) {
            return false;
        }

        return this.getAttributes().get(DocumentSystemAttributes.STATUS.getKey()).equals(status.getStatusCode());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.IDocument#getVersions()
     */
    @Override
    public Set<Version> getVersions() {
        return versions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.IDocument#getTags()
     */
    @Override
    public Set<String> getTags() {
        return tags;
    }

    @Override
    public void addTag(String tag) {
        tags.add(tag);
    }

    @Override
    public void removeTag(String tag) {
        checkNotNull(tag);
        if (tags != null) {
            if (tags.contains(tag)) {
                tags.remove(tag);
            }
        }
    }

    @Override
    public Set<Rating> getRatings() {
        return ratings;
    }

    @Override
    public void addRating(Rating rating) {
        ratings.add(rating);
    }

    @Override
    public void removeRating(Rating rating) {
        checkNotNull(rating);
        if (ratings.contains(rating)) {
            ratings.remove(rating);
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("id", id).add("name", name).add("versions", versions).add("tags", tags)
                .add("ratings", ratings).add("description", description).add("attributes", getAttributes()).toString();
    }

}
