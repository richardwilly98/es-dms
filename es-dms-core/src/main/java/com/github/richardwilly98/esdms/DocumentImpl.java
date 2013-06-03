package com.github.richardwilly98.esdms;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.File;
import com.google.common.collect.ImmutableSet;

public class DocumentImpl extends SecuredItemImpl implements Document {

	private static final long serialVersionUID = 1L;
	private static final Set<String> readOnlyAttributes = ImmutableSet.of(
			AUTHOR, CREATION_DATE, MODIFIED_DATE, STATUS, LOCKED_BY);
	
	private String versionId;
	
	@JsonProperty("file")
	private File file;
	
	private Set<String> tags;

	public DocumentImpl() {
		super();
		readOnlyAttributeKeys = readOnlyAttributes;
	}

	public DocumentImpl(String id, File file) {
		this(id, null, file, null);
	}

	public DocumentImpl(String id, String name, File file,
			Map<String, Object> attributes) {
		this();
		if (file == null) {
			file = new FileImpl();
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
		if (! attributes.containsKey(DocumentImpl.STATUS)) {
			attributes.put(DocumentImpl.STATUS, DocumentImpl.DocumentStatus.AVAILABLE.getStatusCode());
		}
		this.attributes = attributes;
	}

	public DocumentImpl(Document document) {
		this(document.getId(), document.getName(), document.getFile(), null);
		// Override behavior for read-only attribute.
		this.attributes = document.getAttributes();
	}
	
	/* (non-Javadoc)
	 * @see com.github.richardwilly98.api.IDocument#getFile()
	 */
	@Override
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.api.IDocument#getVersionId()
	 */
	@Override
	public String getVersionId() {
		return versionId;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.api.IDocument#getTags()
	 */
	@Override
	public Set<String> getTags() {
		return tags;
	}

	void setTags(Set<String> tags) {
		this.tags = tags;
	}
	
	@Override
	public void addTag(String tag) {
		if (tags == null) {
			tags = newHashSet();
		}
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

}
