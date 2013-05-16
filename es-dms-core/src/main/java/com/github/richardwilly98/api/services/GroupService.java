package com.github.richardwilly98.api.services;

import com.github.richardwilly98.api.Role;

public interface GroupService extends BaseService <Role>{

	public static final String CREATE_PERMISSION = "group:create";
	public static final String EDIT_PERMISSION = "group:edit";
	public static final String DELETE_PERMISSION = "group:delete";
	public static final String ADMINISTRATORS_GROUP = "administrators";
	
	public static final String ADD_PERMISSION = "group:add";
	public static final String REMOVE_PERMISSION = "group:remove";

//	permissions.add(createPermission("milestone:add", "milestone:add", true));
//	permissions.add(createPermission("milestone:remove", "milestone:remove", true));
//	permissions.add(createPermission("task:assign", "task:assign", true));


}
