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

//	public abstract String getHighlight();
//
//	public abstract void setHighlight(String highlight);
//
	public abstract DateTime getDate();

	public abstract void setDate(DateTime date);

	public abstract String getTitle();

	public abstract void setTitle(String title);

	public abstract String getAuthor();

	public abstract void setAuthor(String author);

}