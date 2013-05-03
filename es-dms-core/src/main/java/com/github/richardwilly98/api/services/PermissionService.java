package com.github.richardwilly98.api.services;

import com.github.richardwilly98.api.Permission;

public interface PermissionService extends BaseService <Permission>{

	public static final String CREATE_PERMISSION = "permission:create";
	public static final String EDIT_PERMISSION = "permission:edit";
	public static final String DELETE_PERMISSION = "permission:delete";
}
