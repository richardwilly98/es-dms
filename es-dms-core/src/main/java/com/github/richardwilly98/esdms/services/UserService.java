package com.github.richardwilly98.esdms.services;

import com.github.richardwilly98.esdms.api.User;

public interface UserService extends BaseService <User>{

	public static final String CREATE_PERMISSION = "user:create";
	public static final String EDIT_PERMISSION = "user:edit";
	public static final String DELETE_PERMISSION = "user:delete";

	public static final String ADD_PERMISSION = "user:add";
	public static final String REMOVE_PERMISSION = "user:remove";
	
	public static final String DEFAULT_ADMIN_LOGIN = "admin";
	public static final String DEFAULT_ADMIN_PASSWORD = "secret";
}
