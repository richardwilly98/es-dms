package com.github.richardwilly98.esdms.api;

import java.util.Map;
import java.util.Set;

public interface ItemBase {

	public abstract String getId();

	public abstract void setId(String id);

	public abstract boolean isDisabled();

	public abstract void setDisabled(boolean value);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getDescription();

	public abstract void setDescription(String description);

	public abstract Map<String, Object> getAttributes();

	public abstract Map<String, Object> getAttributes(Set<String> keys);

	public abstract void setAttribute(String name, Object value);

	public abstract void setAttributes(Map<String, Object> attributes);

	public abstract void removeAttribute(String name);

	public abstract Set<String> getReadOnlyAttributeKeys();

}