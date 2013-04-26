package com.github.richardwilly98.api;

import java.util.HashMap;
import java.util.Map;

public class SecuredItem extends ItemBase {
	
	Map<String, Permission> permissions;
	
	Map<String, SecuredItem> attachments;
	Map<String, Annotation> annotations;
	Map<String, Comment> comments;
	
	public Map<String, Permission> getPermissions() {
		return permissions;
	}
	
	public void setPermissions(Map<String, Permission> permissions) {
		this.permissions = permissions;
	}
	
	public void setPermission(String name, Permission permission){
		if (permissions == null) permissions = new HashMap <String, Permission>();
		this.permissions.put(name, permission);
	}
	
	public Map<String, SecuredItem> getAttachments() {
		return attachments;
	}
	
	public void setAttachments(Map<String, SecuredItem> attachments) {
		this.attachments = attachments;
	}
	
	public void setAttachment(String name, SecuredItem attachment){
		if (attachments == null) attachments = new HashMap <String, SecuredItem>();
		this.attachments.put(name, attachment);
	}
}
