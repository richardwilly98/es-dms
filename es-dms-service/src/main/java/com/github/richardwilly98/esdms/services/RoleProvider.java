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
		
		Role role = new RoleImpl.Builder().id("reader").name("Reader").build();
		role.addPermission(new PermissionImpl.Builder().id(DocumentService.READ_PERMISSION).name(DocumentService.READ_PERMISSION).build());
//		Role role = new RoleImpl();
//		role.setId("reader");
//		role.setName("Reader");
//		role.addPermission(new PermissionImpl(DocumentService.READ_PERMISSION));
		super.create(role);
		role = new RoleImpl.Builder().id("writer").name("Writer").build();
		role.addPermission(new PermissionImpl.Builder().id(DocumentService.READ_PERMISSION).name(DocumentService.READ_PERMISSION).build());
		role.addPermission(new PermissionImpl.Builder().id(DocumentService.EDIT_PERMISSION).name(DocumentService.EDIT_PERMISSION).build());
		role.addPermission(new PermissionImpl.Builder().id(DocumentService.CREATE_PERMISSION).name(DocumentService.CREATE_PERMISSION).build());
		role.addPermission(new PermissionImpl.Builder().id(DocumentService.DELETE_PERMISSION).name(DocumentService.DELETE_PERMISSION).build());
//		role = new RoleImpl();
//		role.setId("writer");
//		role.setName("Writer");
//		role.addPermission(new PermissionImpl(DocumentService.READ_PERMISSION));
//		role.addPermission(new PermissionImpl(DocumentService.EDIT_PERMISSION));
//		role.addPermission(new PermissionImpl(DocumentService.CREATE_PERMISSION));
//		role.addPermission(new PermissionImpl(DocumentService.DELETE_PERMISSION));
		super.create(role);
		role = new RoleImpl.Builder().id(ADMINISTRATOR_ROLE).name("Administrator").build();
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
//		role = new RoleImpl();
//		role.setId(ADMINISTRATOR_ROLE);
//		role.setName("Administrator");
//		role.addPermission(new PermissionImpl(DocumentService.READ_PERMISSION));
//		role.addPermission(new PermissionImpl(DocumentService.EDIT_PERMISSION));
//		role.addPermission(new PermissionImpl(DocumentService.CREATE_PERMISSION));
//		role.addPermission(new PermissionImpl(DocumentService.DELETE_PERMISSION));
//		role.addPermission(new PermissionImpl(UserService.EDIT_PERMISSION));
//		role.addPermission(new PermissionImpl(UserService.CREATE_PERMISSION));
//		role.addPermission(new PermissionImpl(UserService.DELETE_PERMISSION));
//		role.addPermission(new PermissionImpl(RoleService.EDIT_PERMISSION));
//		role.addPermission(new PermissionImpl(RoleService.CREATE_PERMISSION));
//		role.addPermission(new PermissionImpl(RoleService.DELETE_PERMISSION));
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
