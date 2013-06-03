package com.github.richardwilly98.esdms.services;

import com.github.richardwilly98.esdms.api.Person;

public interface PersonService extends BaseService<Person> {

	public static final String CREATE_PERMISSION = "person:create";
	public static final String EDIT_PERMISSION = "person:edit";
	public static final String DELETE_PERMISSION = "person:delete";

	public static final String ADD_PERMISSION = "person:add";
	public static final String REMOVE_PERMISSION = "person:remove";
}
