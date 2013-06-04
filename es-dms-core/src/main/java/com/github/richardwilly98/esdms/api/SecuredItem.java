package com.github.richardwilly98.esdms.api;

import java.util.Map;

public interface SecuredItem {

	public abstract Map<String, Role> getRoles();

	public abstract void setPermissions(Map<String, Role> roles);

	public abstract void setRole(String name, Role role);

	public abstract Map<String, SecuredItem> getAttachments();

	public abstract void setAttachments(Map<String, SecuredItem> attachments);

	public abstract void setAttachment(String name, SecuredItem attachment);

}