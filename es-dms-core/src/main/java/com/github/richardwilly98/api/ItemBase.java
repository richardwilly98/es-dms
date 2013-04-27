package com.github.richardwilly98.api;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonInclude(Include.NON_NULL)
public abstract class ItemBase {

	String id;
	String name;
	Boolean disabled;
	String description;
	
	final Map<String, Object> properties;

	protected ItemBase() {
		this(null);
	}
	
	public ItemBase(String id) {
		this(id, new HashMap<String, Object>());
	}
	
	public ItemBase(String id, Map<String, Object> properties) {
		this.disabled = false;
		this.id = id;
		if (properties == null) {
			properties = new HashMap<String, Object>();
		}
		this.properties = properties;
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
	
	public Map<String, Object> getProperties() {
		return properties;
	}
	
	public void setProperties(String name, Object property){
//		if (this.properties == null) this.properties = new HashMap<String, Object>();
		this.properties.put(name, property);
	}
}
