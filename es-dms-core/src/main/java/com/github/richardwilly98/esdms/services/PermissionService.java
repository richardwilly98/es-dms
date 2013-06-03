package com.github.richardwilly98.esdms.services;

import com.github.richardwilly98.esdms.api.Permission;

public interface PermissionService extends BaseService <Permission>{

	public static final String CREATE_PERMISSION = "permission:create";
	public static final String EDIT_PERMISSION = "permission:edit";
	public static final String DELETE_PERMISSION = "permission:delete";
	
	public static final String ADD_PERMISSION = "permission:add";
	public static final String REMOVE_PERMISSION = "permission:remove";
}
