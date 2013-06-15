package com.github.richardwilly98.esdms.services;

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

	protected void setCurrent(boolean current) {
		super.setCurrent(current);
	}

	protected void setFile(File file) {
		super.setFile(file);
	}
}
