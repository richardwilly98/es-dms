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
		Role role = new RoleImpl();
		role.setId("reader");
		role.setName("Reader");
		role.addPermission(new PermissionImpl(DocumentService.READ_PERMISSION));
		super.create(role);
		role = new RoleImpl();
		role.setId("writer");
		role.setName("Writer");
		role.addPermission(new PermissionImpl(DocumentService.READ_PERMISSION));
		role.addPermission(new PermissionImpl(DocumentService.EDIT_PERMISSION));
		role.addPermission(new PermissionImpl(DocumentService.CREATE_PERMISSION));
		role.addPermission(new PermissionImpl(DocumentService.DELETE_PERMISSION));
		super.create(role);
		role = new RoleImpl();
		role.setId(ADMINISTRATOR_ROLE);
		role.setName("Administrator");
		role.addPermission(new PermissionImpl(DocumentService.READ_PERMISSION));
		role.addPermission(new PermissionImpl(DocumentService.EDIT_PERMISSION));
		role.addPermission(new PermissionImpl(DocumentService.CREATE_PERMISSION));
		role.addPermission(new PermissionImpl(DocumentService.DELETE_PERMISSION));
		role.addPermission(new PermissionImpl(UserService.EDIT_PERMISSION));
		role.addPermission(new PermissionImpl(UserService.CREATE_PERMISSION));
		role.addPermission(new PermissionImpl(UserService.DELETE_PERMISSION));
		role.addPermission(new PermissionImpl(RoleService.EDIT_PERMISSION));
		role.addPermission(new PermissionImpl(RoleService.CREATE_PERMISSION));
		role.addPermission(new PermissionImpl(RoleService.DELETE_PERMISSION));
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
