package com.github.richardwilly98.api;

import java.util.Map;

public class SecuredItem extends ItemBase {
	
	Map<String, Permission> permissions;
	
	public Map<String, Permission> getPermissions() {
		return permissions;
	}
	public void setPermissions(Map<String, Permission> permissions) {
		this.permissions = permissions;
	}

}
