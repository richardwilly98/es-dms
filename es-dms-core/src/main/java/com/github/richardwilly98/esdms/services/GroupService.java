package com.github.richardwilly98.esdms.services;

import com.github.richardwilly98.esdms.api.Group;

public interface GroupService extends BaseService<Group> {

	public static final String CREATE_PERMISSION = "group:create";
	public static final String EDIT_PERMISSION = "group:edit";
	public static final String DELETE_PERMISSION = "group:delete";
	public static final String ADMINISTRATORS_GROUP = "administrators";

	public static final String ADD_PERMISSION = "group:add";
	public static final String REMOVE_PERMISSION = "group:remove";

}
