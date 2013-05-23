package com.github.richardwilly98.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.base.Objects;

@JsonInclude(Include.NON_NULL)
public abstract class ItemBase implements Serializable {

	private static final long serialVersionUID = 1L;

	String id;
	String name;
	Boolean disabled;
	String description;
	Map<String, Object> attributes;

	protected ItemBase() {
		this(null);
	}

	public ItemBase(String id) {
		this(id, null);
	}

	public ItemBase(String id, Map<String, Object> attributes) {
		this.disabled = false;
		this.id = id;
		this.attributes = attributes;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isDisabled() {
		return this.disabled;
	}

	public void setDisabled(boolean value) {
		this.disabled = value;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttribute(String name, Object attribute) {
		if (name != null && !name.isEmpty()) {
			if (this.attributes == null) {
				this.attributes = new HashMap<String, Object>();
			}
			this.attributes.put(name, attribute);
		}
	}

	public void setAttributes(Map<String, Object> attributes) {
		if (attributes != null) {
			if (this.attributes == null) {
				this.attributes = new HashMap<String, Object>();
			}
			this.attributes.putAll(attributes);
		}

	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("id", id).add("name", name)
				.add("description", description).toString();
	}
}
