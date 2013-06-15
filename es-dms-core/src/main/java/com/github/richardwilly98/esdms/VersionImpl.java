package com.github.richardwilly98.esdms;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.richardwilly98.esdms.api.File;
import com.github.richardwilly98.esdms.api.Version;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

public class VersionImpl extends SecuredItemImpl implements Version {

	private static final long serialVersionUID = 1L;
	private static final Set<String> readOnlyAttributes = ImmutableSet.of(
			CREATION_DATE, MODIFIED_DATE);

	@JsonProperty("version_id")
	private int versionId;

	@JsonProperty("document_id")
	private String documentId;

	@JsonProperty("parent_id")
	private int parentId;

	private File file;
	private boolean current;

	/*
	 * versionId and documentId are required.
	 */
	public static class Builder extends
			SecuredItemImpl.Builder<VersionImpl.Builder> {

		private int versionId;
		private String documentId;
		private int parentId;
		private File file;
		private boolean current;

		public Builder versionId(int versionId) {
			this.versionId = versionId;
			return getThis();
		}

		public Builder parentId(int parentId) {
			this.parentId = parentId;
			return getThis();
		}

		public Builder documentId(String documentId) {
			this.documentId = documentId;
			return getThis();
		}

		public Builder file(File file) {
			this.file = file;
			return getThis();
		}

		public Builder current(boolean current) {
			this.current = current;
			return getThis();
		}

		@Override
		protected Builder getThis() {
			return this;
		}

		public VersionImpl build() {
			// name cannot be null
			if (Strings.isNullOrEmpty(name)) {
				name = String.valueOf(versionId);
			}
			return new VersionImpl(this);
		}
	}

	VersionImpl() {
		this(null);
	}

	protected VersionImpl(Builder builder) {
		super(builder);
		if (builder != null) {
			checkNotNull(builder.documentId);
			if (builder.versionId < 1) {
				this.versionId = 1;
			} else {
				this.versionId = builder.versionId;
			}
			if (builder.parentId < 1) {
				this.parentId = 0;
			} else {
				this.parentId = builder.parentId;
			}
			this.documentId = builder.documentId;
			this.file = builder.file;
			this.current = builder.current;
		}
		readOnlyAttributeKeys = readOnlyAttributes;
	}

	/*
	 * Method used to deserialize attributes Map
	 */
//	@JsonProperty("attributes")
//	private void deserialize(Map<String, Object> attributes) {
//		this.attributes = attributes;
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.IVersion#getFile()
	 */
	@Override
	public File getFile() {
		return file;
	}

	protected void setFile(File file) {
		this.file = file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.IVersion#getVersionId()
	 */
	@Override
	public int getVersionId() {
		return versionId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.IVersion#isCurrent()
	 */
	@Override
	public boolean isCurrent() {
		return current;
	}

	protected void setCurrent(boolean current) {
		this.current = current;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.IVersion#getParentVersionId()
	 */
	@Override
	public int getParentId() {
		return parentId;
	}

	protected void setParentId(int parentId) {
		this.parentId = parentId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.IVersion#getParentVersionId()
	 */
	@Override
	public String getDocumentId() {
		return documentId;
	}

	protected void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("id", id).add("name", name)
				.add("documentId", documentId).add("versionId", versionId)
				.add("parentId", parentId).add("current", current)
				.add("description", description).add("attributes", getAttributes())
				.toString();
	}
}
