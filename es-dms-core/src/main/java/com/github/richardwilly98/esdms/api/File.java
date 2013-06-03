package com.github.richardwilly98.esdms.api;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.richardwilly98.esdms.FileImpl;

@JsonDeserialize(as = FileImpl.class)
public interface File {

	public abstract byte[] getContent();

	public abstract void setContent(byte[] content);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getContentType();

	public abstract void setContentType(String contentType);

	public abstract String getHighlight();

	public abstract void setHighlight(String highlight);

	public abstract DateTime getDate();

	public abstract void setDate(DateTime date);

	public abstract String getTitle();

	public abstract void setTitle(String title);

	public abstract String getAuthor();

	public abstract void setAuthor(String author);

}