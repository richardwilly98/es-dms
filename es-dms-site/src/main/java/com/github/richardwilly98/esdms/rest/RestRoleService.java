package com.github.richardwilly98.esdms.rest;

import javax.ws.rs.Path;

import com.github.richardwilly98.esdms.RoleImpl;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.services.AuthenticationService;
import com.github.richardwilly98.esdms.services.RoleService;
import com.google.inject.Inject;

@Path(RestRoleService.ROLES_PATH)
public class RestRoleService extends RestServiceBase<Role> {

	public static final String ROLES_PATH = "roles";

	@Inject
	public RestRoleService(AuthenticationService authenticationService,
			final RoleService roleService) {
		super(authenticationService, roleService);
	}

}
