package com.github.richardwilly98.api;

import java.util.Map;

abstract class ItemBase {

	String id;
	
	Map<String, String> permissions;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Map<String, String> getPermissions() {
		return permissions;
	}
	public void setPermissions(Map<String, String> permissions) {
		this.permissions = permissions;
	}
}
