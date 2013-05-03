package com.github.richardwilly98.api.services;

import com.github.richardwilly98.api.Role;

public interface RoleService extends BaseService <Role>{

	public static final String CREATE_PERMISSION = "role:create";
	public static final String EDIT_PERMISSION = "role:edit";
	public static final String DELETE_PERMISSION = "role:delete";
	public static final String ADMINISTRATOR_ROLE = "administrator";

}
