package com.github.richardwilly98.esdms;

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
import com.github.richardwilly98.esdms.api.ItemBase;
import com.google.common.base.Objects;

@JsonInclude(Include.NON_DEFAULT)
public abstract class ItemBaseImpl implements Serializable, ItemBase {

	final protected Logger log = Logger.getLogger(getClass());
	private static final long serialVersionUID = 1L;

	String id;
	String name;
	boolean disabled;
	String description;

	Map<String, Object> attributes;

	@JsonIgnore
	protected Set<String> readOnlyAttributeKeys;

	static abstract class BuilderBase<T extends BuilderBase<T>> {

		String id;
		String name;
		boolean disabled;
		String description;
		Map<String, Object> attributes;

		public T id(String id) {
			this.id = id;
			return getThis();
		}

		public T name(String name) {
			this.name = name;
			return getThis();
		}

		public T description(String description) {
			this.description = description;
			return getThis();
		}

		public T disabled(boolean disabled) {
			this.disabled = disabled;
			return getThis();
		}

		public T attributes(Map<String, Object> attributes) {
			this.attributes = attributes;
			return getThis();
		}

		protected abstract T getThis();

	}

	protected ItemBaseImpl(BuilderBase<?> builder) {
		if (builder != null) {
//			checkNotNull(builder.id);
			checkNotNull(builder.name);
			this.id = builder.id;
			this.name = builder.name;
			this.disabled = builder.disabled;
			this.description = builder.description;
			this.attributes = builder.attributes;
		}
		// this(null);
	}

	// ItemBaseImpl(String id) {
	// this(id, null);
	// }
	//
	// ItemBaseImpl(String id, Map<String, Object> attributes) {
	// // this.disabled = false;
	// this.id = id;
	// this.attributes = attributes;
	// }

	@JsonProperty("attributes")
	private void deserialize(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.IItemBase#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.IItemBase#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.IItemBase#isDisabled()
	 */
	@Override
	public boolean isDisabled() {
		return this.disabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.IItemBase#setDisabled(boolean)
	 */
	@Override
	public void setDisabled(boolean value) {
		this.disabled = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.IItemBase#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.IItemBase#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.IItemBase#getDescription()
	 */
	@Override
	public String getDescription() {
		return this.description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.richardwilly98.api.IItemBase#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.IItemBase#getAttributes()
	 */
	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.IItemBase#getAttributes(java.util.Set)
	 */
	@Override
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.richardwilly98.api.IItemBase#setAttribute(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setAttribute(String name, Object value) {
		updateAttribute(name, value, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.IItemBase#setAttributes(java.util.Map)
	 */
	@Override
	public void setAttributes(Map<String, Object> attributes) {
		if (attributes != null) {
			for (String key : attributes.keySet()) {
				setAttribute(key, attributes.get(key));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.richardwilly98.api.IItemBase#removeAttribute(java.lang.String)
	 */
	@Override
	public void removeAttribute(String name) {
		updateAttribute(name, null, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.IItemBase#getReadOnlyAttributeKeys()
	 */
	@Override
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
