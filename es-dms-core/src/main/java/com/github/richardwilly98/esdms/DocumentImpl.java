package com.github.richardwilly98.esdms;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.Version;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class DocumentImpl extends SecuredItemImpl implements Document {

	private static final long serialVersionUID = 1L;
	private static final Set<String> readOnlyAttributes = ImmutableSet.of(
			AUTHOR, CREATION_DATE, MODIFIED_DATE, STATUS, LOCKED_BY);

	private final Set<String> tags = newHashSet();
	private final Set<Version> versions = newHashSet();

	public static class Builder extends
			SecuredItemImpl.Builder<DocumentImpl.Builder> {

		private Set<String> tags;
		private Set<Version> versions;

		public Builder tags(Set<String> tags) {
			this.tags = tags;
			return getThis();
		}

		public Builder versions(Set<Version> versions) {
			this.versions = versions;
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
		}
		readOnlyAttributeKeys = readOnlyAttributes;
	}

	/*
	 * Method used to deserialize attributes Map
	 */
	@JsonProperty("attributes")
	private void deserialize(Map<String, Object> attributes) {
		if (!attributes.containsKey(DocumentImpl.STATUS)) {
			attributes.put(DocumentImpl.STATUS,
					DocumentImpl.DocumentStatus.AVAILABLE.getStatusCode());
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
	public boolean hasStatus(DocumentStatus status) {

		if (!this.getAttributes().containsKey(Document.STATUS)) {
			return false;
		}

		return this.getAttributes().get(Document.STATUS)
				.equals(status.getStatusCode());
	}

	// @JsonProperty("current_version")
	// private Version serializeCurrentVersion() {
	// Version version = getCurrentVersion();
	// return version;
	// }
	//
	// @JsonProperty("current_version")
	// private void deserializeCurrentVersions(Version version) {
	// if (versions != null) {
	//
	// }
	// }

	// @JsonProperty("versions")
	// private Set<Version> serializeVersions() throws JsonProcessingException {
	// if (versions == null) {
	// return null;
	// } else {
	// Set<Version> _versions = newHashSet();
	// for (Version version : versions) {
	// _versions.add(new
	// VersionImpl.Builder().versionId(version.getVersionId()).current(version.isCurrent()).id(version.getId()).roles(null).build());
	// }
	// return _versions;
	// }
	// }

	// @JsonProperty("versions")
	// private void deserializeVersions(Set<Version> versions) {
	// if (versions != null) {
	//
	// }
	// }
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
	public String toString() {
		return Objects.toStringHelper(this).add("id", id).add("name", name)
				.add("versions", versions).add("tags", tags)
				.add("description", description)
				.add("attributes", getAttributes()).toString();
	}
}
