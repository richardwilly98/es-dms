package com.github.richardwilly98.esdms.services;

import org.elasticsearch.client.Client;

import com.github.richardwilly98.esdms.PermissionImpl;
import com.github.richardwilly98.esdms.api.Permission;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.services.BootstrapService;
import com.github.richardwilly98.esdms.services.DocumentService;
import com.github.richardwilly98.esdms.services.PermissionService;
import com.github.richardwilly98.esdms.services.RoleService;
import com.github.richardwilly98.esdms.services.UserService;
import com.google.inject.Inject;

public class PermissionProvider extends ProviderBase<Permission> implements PermissionService {

	private final static String type = "permission";

	@Inject
	PermissionProvider(Client client, BootstrapService bootstrapService) throws ServiceException {
		super(client, bootstrapService, null, PermissionProvider.type, Permission.class);
	}

	@Override
	protected void loadInitialData() throws ServiceException {
		Permission permission = new PermissionImpl.Builder().name(DocumentService.CREATE_PERMISSION).build();
//		Permission permission = new PermissionImpl(DocumentService.CREATE_PERMISSION);
		super.create(permission);
//		permission = new PermissionImpl(DocumentService.DELETE_PERMISSION);
		permission = new PermissionImpl.Builder().name(DocumentService.DELETE_PERMISSION).build();
		create(permission);
//		permission = new PermissionImpl(DocumentService.EDIT_PERMISSION);
		permission = new PermissionImpl.Builder().name(DocumentService.EDIT_PERMISSION).build();
		create(permission);
//		permission = new PermissionImpl(DocumentService.READ_PERMISSION);
		permission = new PermissionImpl.Builder().name(DocumentService.READ_PERMISSION).build();
		create(permission);
//		permission = new PermissionImpl(UserService.CREATE_PERMISSION);
		permission = new PermissionImpl.Builder().name(UserService.CREATE_PERMISSION).build();
		create(permission);
//		permission = new PermissionImpl(UserService.EDIT_PERMISSION);
		permission = new PermissionImpl.Builder().name(UserService.EDIT_PERMISSION).build();
		create(permission);
//		permission = new PermissionImpl(UserService.DELETE_PERMISSION);
		permission = new PermissionImpl.Builder().name(UserService.DELETE_PERMISSION).build();
		create(permission);
//		permission = new PermissionImpl(RoleService.CREATE_PERMISSION);
		permission = new PermissionImpl.Builder().name(RoleService.CREATE_PERMISSION).build();
		create(permission);
//		permission = new PermissionImpl(RoleService.EDIT_PERMISSION);
		permission = new PermissionImpl.Builder().name(RoleService.EDIT_PERMISSION).build();
		create(permission);
//		permission = new PermissionImpl(RoleService.DELETE_PERMISSION);
		permission = new PermissionImpl.Builder().name(RoleService.DELETE_PERMISSION).build();
		create(permission);
	}

	@Override
	protected String getMapping() {
		return null;
	}

}
