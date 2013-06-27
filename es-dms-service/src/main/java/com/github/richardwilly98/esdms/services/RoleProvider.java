package com.github.richardwilly98.esdms.services;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.elasticsearch.client.Client;

import com.github.richardwilly98.esdms.PermissionImpl;
import com.github.richardwilly98.esdms.RoleImpl;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.services.BootstrapService;
import com.github.richardwilly98.esdms.services.DocumentService;
import com.github.richardwilly98.esdms.services.RoleService;
import com.github.richardwilly98.esdms.services.UserService;
import com.google.inject.Inject;

public class RoleProvider extends ProviderBase<Role> implements RoleService {

	private final static String type = "role";

	@Inject
	RoleProvider(Client client, BootstrapService bootstrapService) throws ServiceException {
		super(client, bootstrapService, null, RoleProvider.type, Role.class);
	}

	@Override
	protected void loadInitialData() throws ServiceException {
		
		Role role = new RoleImpl.Builder().id(READER_ROLE_ID).name(READER_ROLE_NAME).build();
		role.addPermission(new PermissionImpl.Builder().id(DocumentService.READ_PERMISSION).name(DocumentService.READ_PERMISSION).build());
		super.create(role);

		role = new RoleImpl.Builder().id(WRITER_ROLE_ID).name(WRITER_ROLE_NAME).build();
		role.addPermission(new PermissionImpl.Builder().id(DocumentService.READ_PERMISSION).name(DocumentService.READ_PERMISSION).build());
		role.addPermission(new PermissionImpl.Builder().id(DocumentService.EDIT_PERMISSION).name(DocumentService.EDIT_PERMISSION).build());
		role.addPermission(new PermissionImpl.Builder().id(DocumentService.CREATE_PERMISSION).name(DocumentService.CREATE_PERMISSION).build());
		role.addPermission(new PermissionImpl.Builder().id(DocumentService.DELETE_PERMISSION).name(DocumentService.DELETE_PERMISSION).build());
		super.create(role);

		role = new RoleImpl.Builder().id(ADMINISTRATOR_ROLE_ID).name(ADMINISTRATOR_ROLE_NAME).build();
		role.addPermission(new PermissionImpl.Builder().id(DocumentService.READ_PERMISSION).name(DocumentService.READ_PERMISSION).build());
		role.addPermission(new PermissionImpl.Builder().id(DocumentService.EDIT_PERMISSION).name(DocumentService.EDIT_PERMISSION).build());
		role.addPermission(new PermissionImpl.Builder().id(DocumentService.CREATE_PERMISSION).name(DocumentService.CREATE_PERMISSION).build());
		role.addPermission(new PermissionImpl.Builder().id(DocumentService.DELETE_PERMISSION).name(DocumentService.DELETE_PERMISSION).build());
		role.addPermission(new PermissionImpl.Builder().id(UserService.EDIT_PERMISSION).name(UserService.EDIT_PERMISSION).build());
		role.addPermission(new PermissionImpl.Builder().id(UserService.CREATE_PERMISSION).name(UserService.CREATE_PERMISSION).build());
		role.addPermission(new PermissionImpl.Builder().id(UserService.DELETE_PERMISSION).name(UserService.DELETE_PERMISSION).build());
		role.addPermission(new PermissionImpl.Builder().id(RoleService.EDIT_PERMISSION).name(RoleService.EDIT_PERMISSION).build());
		role.addPermission(new PermissionImpl.Builder().id(RoleService.CREATE_PERMISSION).name(RoleService.CREATE_PERMISSION).build());
		role.addPermission(new PermissionImpl.Builder().id(RoleService.DELETE_PERMISSION).name(RoleService.DELETE_PERMISSION).build());
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
