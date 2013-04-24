package com.github.richardwilly98.api;

abstract class ItemBase {

	String id;
	String name;
	Boolean disabled;
	String description;

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
}
