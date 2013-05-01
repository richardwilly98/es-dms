package com.github.richardwilly98.api;

public class Permission extends ItemBase {

	private static final long serialVersionUID = 1L;
	String access;

	public Permission() {
	}
	
	public Permission(String name) {
		super(name);
		setName(name);
	}

	public void setAccess(String access) {
		this.access = access;
	}

	public String getAccess() {
		return access;
	}
}
