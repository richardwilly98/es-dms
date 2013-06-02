package com.github.richardwilly98.api;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

public class Document extends SecuredItem {

	private static final long serialVersionUID = 1L;
	public final static String CREATION_DATE = "creation";
	public final static String MODIFIED_DATE = "modified";
	public final static String AUTHOR = "author";
	public final static String STATUS = "status";
	public final static String LOCKED_BY = "lockedBy";
	private static final Set<String> readOnlyAttributes = ImmutableSet.of(
			AUTHOR, CREATION_DATE, MODIFIED_DATE, STATUS, LOCKED_BY);
	
	@JsonProperty("file")
	private File file;
	
	private Set<Version> versions;
	private Set<String> tags;

	public Document() {
		super();
		readOnlyAttributeKeys = readOnlyAttributes;
	}

	public Document(String id, File file) {
		this(id, null, file, null);
	}

	public Document(String id, String name, File file,
			Map<String, Object> attributes) {
		this();
		if (file == null) {
			file = new File();
		}
		setId(id);
		setName(name);
		this.file = file;
		setAttributes(attributes);
	}

	/*
	 * Method used to deserialize attributes Map
	 */
	@JsonProperty("attributes")
	private void deserialize(Map<String, Object> attributes) {
		if (! attributes.containsKey(Document.STATUS)) {
			attributes.put(Document.STATUS, Document.DocumentStatus.AVAILABLE.getStatusCode());
		}
		this.attributes = attributes;
	}

	public Document(Document document) {
		this(document.getId(), document.getName(), document.getFile(), null);
		// Override behavior for read-only attribute.
		this.attributes = document.getAttributes();
	}
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Set<String> getTags() {
		return tags;
	}

	void setTags(Set<String> tags) {
		this.tags = tags;
	}
	
	public void addTag(String tag) {
		if (tags == null) {
			tags = newHashSet();
		}
		tags.add(tag);
	}
	
	public void removeTag(String tag) {
		checkNotNull(tag);
		if (tags != null) {
			if (tags.contains(tag)) {
				tags.remove(tag);
			}
		}
	}

	public enum DocumentStatus {
		AVAILABLE("A"), LOCKED("L"), DELETED("D");

		private String statusCode;

		private DocumentStatus(String status) {
			statusCode = status;
		}

		public String getStatusCode() {
			return statusCode;
		}

	}
}
