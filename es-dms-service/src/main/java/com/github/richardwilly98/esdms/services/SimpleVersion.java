package com.github.richardwilly98.esdms.services;

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


import com.github.richardwilly98.esdms.VersionImpl;
import com.github.richardwilly98.esdms.api.File;
import com.github.richardwilly98.esdms.api.Version;

public class SimpleVersion extends VersionImpl {

	private static final long serialVersionUID = 1L;

	public static class Builder extends VersionImpl.Builder {

		private Version version;

		public Builder version(Version document) {
			this.version = document;
			return this;
		}

		public SimpleVersion build() {
			this.versionId(version.getVersionId())
					.documentId(version.getDocumentId())
					.file(version.getFile()).parentId(version.getParentId())
					.current(version.isCurrent()).id(version.getId())
					.name(version.getName()).disabled(version.isDisabled())
					.description(version.getDescription())
					.attributes(version.getAttributes());
			return new SimpleVersion(this);
		}
	}

	SimpleVersion() {
		this(null);
	}

	SimpleVersion(Builder builder) {
		super(builder);
	}

	public void setId(String id) {
		super.setId(id);
	}

	protected void setParentId(int parentId) {
		super.setParentId(parentId);
	}

	public void setCurrent(boolean current) {
		super.setCurrent(current);
	}

	protected void setFile(File file) {
		super.setFile(file);
	}
}
