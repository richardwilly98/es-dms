package com.github.richardwilly98.api.services;

import com.github.richardwilly98.api.User;

public interface UserService extends BaseService <User>{

	public static final String CREATE_PERMISSION = "user:create";
	public static final String EDIT_PERMISSION = "user:edit";
	public static final String DELETE_PERMISSION = "user:delete";
}
