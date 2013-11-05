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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.richardwilly98.esdms.api.File;
import com.google.common.base.Objects;

@JsonInclude(Include.NON_NULL)
public class FileImpl implements Serializable, File {

    private static final long serialVersionUID = 1L;
    @JsonProperty("content")
    private byte[] content;

    @JsonProperty("_name")
    private String name;

    @JsonProperty("_content_type")
    private String contentType;

    @JsonProperty("date")
    private Date date;

    @JsonProperty("title")
    private String title;

    @JsonProperty("author")
    private String author;

    @JsonProperty("language")
    private String language;

    public static class Builder {

        private String author;
        private String contentType;
        private byte[] content;
        private Date date;
        private String name;
        private String title;
        private String language;

        public Builder content(byte[] content) {
            this.content = content;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public File build() {
            return new FileImpl(this);
        }
    }

    FileImpl() {
        this(null);
    }

    protected FileImpl(Builder builder) {
        if (builder != null) {
            this.author = builder.author;
            this.content = builder.content;
            this.contentType = builder.contentType;
            this.date = builder.date;
            this.name = builder.name;
            this.title = builder.title;
            this.language = builder.language;
        }
        if (this.content == null) {
            this.content = new byte[0];
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.File#getContent()
     */
    @Override
    public byte[] getContent() {
        return content;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.File#setContent(byte[])
     */
    @Override
    public void setContent(byte[] content) {
        this.content = content;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.File#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.File#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.File#getContentType()
     */
    @Override
    public String getContentType() {
        return contentType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.File#setContentType(java.lang.String)
     */
    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.File#getDate()
     */
    @Override
    public Date getDate() {
        return date;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.File#setDate(org.joda.time.DateTime)
     */
    @Override
    public void setDate(Date date) {
        this.date = date;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.File#getTitle()
     */
    @Override
    public String getTitle() {
        return title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.File#setTitle(java.lang.String)
     */
    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.File#getAuthor()
     */
    @Override
    public String getAuthor() {
        return author;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.File#setAuthor(java.lang.String)
     */
    @Override
    public void setAuthor(String author) {
        this.author = author;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.File#getLanguage()
     */
    @Override
    public String getLanguage() {
        return language;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.File#setLanguage(java.lang.String)
     */
    @Override
    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((author == null) ? 0 : author.hashCode());
        result = prime * result + Arrays.hashCode(content);
        result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((language == null) ? 0 : language.hashCode());
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
        FileImpl other = (FileImpl) obj;
        if (author == null) {
            if (other.author != null)
                return false;
        } else if (!author.equals(other.author))
            return false;
        if (!Arrays.equals(content, other.content))
            return false;
        if (contentType == null) {
            if (other.contentType != null)
                return false;
        } else if (!contentType.equals(other.contentType))
            return false;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (language == null) {
            if (other.language != null)
                return false;
        } else if (!language.equals(other.language))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("author", author).add("contentType", contentType).add("date", date).add("name", name)
                .add("title", title).add("language", language).toString();
    }
}
