package com.github.richardwilly98.api;

import java.util.HashMap;
import java.util.Map;

public class SecuredItem extends ItemBase {
	
	Map<String, Role> roles;
	
	Map<String, SecuredItem> attachments;
	Map<String, Annotation> annotations;
	Map<String, Comment> comments;
	
	public Map<String, Role> getRoles() {
		return roles;
	}
	
	public void setPermissions(Map<String, Role> roles) {
		this.roles = roles;
	}
	
	public void setRole(String name, Role role){
		if (roles == null) roles = new HashMap <String, Role>();
		this.roles.put(name, role);
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
