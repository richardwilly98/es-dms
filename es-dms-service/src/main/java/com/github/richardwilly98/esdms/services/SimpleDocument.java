package com.github.richardwilly98.esdms.services;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.richardwilly98.esdms.DocumentImpl;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.Version;

class SimpleDocument extends DocumentImpl {

	private static final long serialVersionUID = 1L;

	public static class Builder extends DocumentImpl.Builder {

		private Document document;

		public Builder document(Document document) {
			this.document = document;
			return this;
		}

		public SimpleDocument build() {
			this.versions(document.getVersions()).tags(document.getTags())
					.id(document.getId()).name(document.getName())
					.disabled(document.isDisabled())
					.description(document.getDescription())
					.attributes(document.getAttributes());
			return new SimpleDocument(this);
		}
	}

	SimpleDocument() {
		this(null);
	}

	SimpleDocument(final Builder builder) {
		super(builder);
	}

	protected void setReadOnlyAttribute(String name, Object value) {
		super.setReadOnlyAttribute(name, value);
	}

	protected void removeReadOnlyAttribute(String name) {
		setReadOnlyAttribute(name, null);
	}

	/*
	 * addVersion set the version added to current
	 */
	protected void addVersion(Version version) {
		Version currentVersion = getCurrentVersion();
		SimpleVersion sv = new SimpleVersion.Builder().version(version).build();
		if (currentVersion != null) {
			sv.setParentId(currentVersion.getVersionId());
		}
		sv.setCurrent(true);
		// Set<Version> versions = getVersions();
		// versions.add(sv);
		// setVersion(versions);
		getVersions().add(sv);
	}

	protected void updateVersion(Version version) {
		checkNotNull(version);
		checkArgument(version.getVersionId() > 0);
//		Set<Version> versions = getVersions();
//		versions.remove(getVersion(version.getVersionId()));
//		versions.add(version);
//		setVersion(versions);
		getVersions().remove(getVersion(version.getVersionId()));
		getVersions().add(version);
	}

	protected void deleteVersion(Version version) {
		checkNotNull(version);
//		Set<Version> versions = getVersions();
//		versions.remove(getVersion(version.getVersionId()));
//		setVersion(versions);
		getVersions().remove(getVersion(version.getVersionId()));
	}

	@JsonProperty("versions")
	private Set<Version> serializeVersions() throws JsonProcessingException {
		Set<Version> versions = getVersions();
		if (versions == null) {
			return null;
		} else {
			Set<Version> serializedVersions = newHashSet();
			for (Version version : versions) {
				if (version.isCurrent()) {
					serializedVersions.add(new SimpleVersion.Builder().version(
							version).build());
				} else {
					SimpleVersion sv = new SimpleVersion.Builder().version(
							version).build();
					sv.setCurrent(false);
					sv.setFile(null);
					serializedVersions.add(sv);
				}
			}
			return serializedVersions;
		}
	}

}
