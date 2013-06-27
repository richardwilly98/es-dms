package com.github.richardwilly98.esdms.services;

import com.github.richardwilly98.esdms.api.Role;

public interface RoleService extends BaseService <Role>{

	public static final String CREATE_PERMISSION = "role:create";
	public static final String EDIT_PERMISSION = "role:edit";
	public static final String DELETE_PERMISSION = "role:delete";
	public static final String ADMINISTRATOR_ROLE_ID = "administrator";
	public static final String ADMINISTRATOR_ROLE_NAME = "Administrator";
	public static final String WRITER_ROLE_ID = "writer";
	public static final String WRITER_ROLE_NAME = "Writer";
	public static final String READER_ROLE_ID = "reader";
	public static final String READER_ROLE_NAME = "Reader";
	
	public static final String ADD_PERMISSION = "role:add";
	public static final String REMOVE_PERMISSION = "role:remove";

}
