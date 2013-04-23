package com.github.richardwilly98.api;

import java.util.Map;

public class SecuredItem extends ItemBase {
	
	Map<String, String> permissions;
	
	public Map<String, String> getPermissions() {
		return permissions;
	}
	public void setPermissions(Map<String, String> permissions) {
		this.permissions = permissions;
	}

}
