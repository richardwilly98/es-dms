package com.github.richardwilly98.services;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.elasticsearch.client.Client;

import com.github.richardwilly98.api.Permission;
import com.github.richardwilly98.api.Role;
import com.github.richardwilly98.api.exception.ServiceException;
import com.github.richardwilly98.api.services.BootstrapService;
import com.github.richardwilly98.api.services.DocumentService;
import com.github.richardwilly98.api.services.RoleService;
import com.github.richardwilly98.api.services.UserService;
import com.google.inject.Inject;

public class RoleProvider extends ProviderBase<Role> implements RoleService {

	private final static String type = "role";

	@Inject
	RoleProvider(Client client, BootstrapService bootstrapService) throws ServiceException {
		super(client, bootstrapService, null, RoleProvider.type, Role.class);
	}

	@Override
	protected void loadInitialData() throws ServiceException {
		Role role = new Role();
		role.setId("reader");
		role.setName("Reader");
		role.addPermission(new Permission(DocumentService.READ_PERMISSION));
		super.create(role);
		role = new Role();
		role.setId("writer");
		role.setName("Writer");
		role.addPermission(new Permission(DocumentService.READ_PERMISSION));
		role.addPermission(new Permission(DocumentService.EDIT_PERMISSION));
		role.addPermission(new Permission(DocumentService.CREATE_PERMISSION));
		role.addPermission(new Permission(DocumentService.DELETE_PERMISSION));
		super.create(role);
		role = new Role();
		role.setId(ADMINISTRATOR_ROLE);
		role.setName("Administrator");
		role.addPermission(new Permission(DocumentService.READ_PERMISSION));
		role.addPermission(new Permission(DocumentService.EDIT_PERMISSION));
		role.addPermission(new Permission(DocumentService.CREATE_PERMISSION));
		role.addPermission(new Permission(DocumentService.DELETE_PERMISSION));
		role.addPermission(new Permission(UserService.EDIT_PERMISSION));
		role.addPermission(new Permission(UserService.CREATE_PERMISSION));
		role.addPermission(new Permission(UserService.DELETE_PERMISSION));
		role.addPermission(new Permission(RoleService.EDIT_PERMISSION));
		role.addPermission(new Permission(RoleService.CREATE_PERMISSION));
		role.addPermission(new Permission(RoleService.DELETE_PERMISSION));
		super.create(role);
	}

	@RequiresPermissions(CREATE_PERMISSION)
	@Override
	public Role create(Role item) throws ServiceException {
		return super.create(item);
	}

	@RequiresPermissions(DELETE_PERMISSION)
	@Override
	public void delete(Role item) throws ServiceException {
		super.delete(item);
	}

	@Override
	protected String getMapping() {
		return null;
	}
	
}
