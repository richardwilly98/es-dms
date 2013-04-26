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
	
	Map<String, Object> properties = new HashMap<String, Object>();

	protected ItemBase() {
		disabled = false;
	}
	
	public ItemBase(String id) {
		disabled = false;
		this.id = id;
	}
	
	public ItemBase(String id, Map<String, Object> properties) {
		disabled = false;
		this.id = id;
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
		if (this.properties == null) this.properties = new HashMap<String, Object>();
		this.properties.put(name, property);
	}
}
