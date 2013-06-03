package com.github.richardwilly98.esdms.api;

import java.util.Map;

import com.github.richardwilly98.esdms.RoleImpl;

public interface SecuredItem {

	public abstract Map<String, RoleImpl> getRoles();

	public abstract void setPermissions(Map<String, RoleImpl> roles);

	public abstract void setRole(String name, RoleImpl role);

	public abstract Map<String, SecuredItem> getAttachments();

	public abstract void setAttachments(Map<String, SecuredItem> attachments);

	public abstract void setAttachment(String name, SecuredItem attachment);

}