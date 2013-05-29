package com.github.richardwilly98.api;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

@JsonInclude(Include.NON_NULL)
public class ItemBase implements Serializable {

	final protected Logger log = Logger.getLogger(getClass());
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private boolean disabled;
	private String description;

	Map<String, Object> attributes;

	@JsonIgnore
	protected Set<String> readOnlyAttributeKeys;

	ItemBase() {
		this(null);
	}

	ItemBase(String id) {
		this(id, null);
	}

	ItemBase(String id, Map<String, Object> attributes) {
		this.disabled = false;
		this.id = id;
		this.attributes = attributes;
	}

	@JsonProperty("attributes")
	private void deserialize(Map<String, Object> attributes) {
		log.debug("*** deserialize *** -> " + attributes);
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

	public Map<String, Object> getAttributes(Set<String> keys) {
		Map<String, Object> attributes = getAttributes();
		if (keys != null) {
			for (String key : attributes.keySet()) {
				if (!keys.contains(key)) {
					attributes.remove(key);
				}
			}
		}
		return attributes;
	}

	public void setAttribute(String name, Object value) {
		updateAttribute(name, value, false);
	}

	public void setAttributes(Map<String, Object> attributes) {
		if (attributes != null) {
			for (String key : attributes.keySet()) {
				setAttribute(key, attributes.get(key));
			}
		}
	}

	public void removeAttribute(String name) {
//		if (attributes != null) {
//			this.attributes.remove(name);
//		}
		updateAttribute(name, null, false);
	}

	public Set<String> getReadOnlyAttributeKeys() {
		return readOnlyAttributeKeys;
	}

	protected void setReadOnlyAttribute(String name, Object value) {
		updateAttribute(name, value, true);
	}

	private void updateAttribute(String name, Object value, boolean readOnly) {
		checkNotNull(name);
		if (!readOnly
				&& (readOnlyAttributeKeys != null && readOnlyAttributeKeys
						.contains(name))) {
			log.warn(String
					.format("Cannot update read-only attribute %s", name));
			return;
		}
		if (readOnly
				&& (readOnlyAttributeKeys != null && readOnlyAttributeKeys
						.contains(name)) || !readOnly) {
			if (name != null && !name.isEmpty()) {
				if (this.attributes == null) {
					this.attributes = newHashMap();
				}
				if (value != null) {
					this.attributes.put(name, value);
				} else {
					this.attributes.remove(name);
				}
			}
		}
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("id", id).add("name", name)
				.add("description", description).add("attributes", attributes)
				.toString();
	}
}
