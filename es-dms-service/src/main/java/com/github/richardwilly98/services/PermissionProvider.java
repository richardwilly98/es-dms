package com.github.richardwilly98.services;

import org.elasticsearch.client.Client;

import com.github.richardwilly98.api.Permission;
import com.github.richardwilly98.api.exception.ServiceException;
import com.github.richardwilly98.api.services.DocumentService;
import com.github.richardwilly98.api.services.PermissionService;
import com.github.richardwilly98.api.services.RoleService;
import com.github.richardwilly98.api.services.UserService;
import com.google.inject.Inject;

public class PermissionProvider extends ProviderBase<Permission> implements PermissionService {

	private final static String index = "system";
	private final static String type = "permission";

	@Inject
	PermissionProvider(Client client) throws ServiceException {
		super(client, PermissionProvider.index, PermissionProvider.type, Permission.class);
	}

	@Override
	protected void loadInitialData() throws ServiceException {
		Permission permission = new Permission(DocumentService.CREATE_PERMISSION);
		super.create(permission);
		permission = new Permission(DocumentService.DELETE_PERMISSION);
		create(permission);
		permission = new Permission(DocumentService.EDIT_PERMISSION);
		create(permission);
		permission = new Permission(DocumentService.READ_PERMISSION);
		create(permission);
		permission = new Permission(UserService.CREATE_PERMISSION);
		create(permission);
		permission = new Permission(UserService.EDIT_PERMISSION);
		create(permission);
		permission = new Permission(UserService.DELETE_PERMISSION);
		create(permission);
		permission = new Permission(RoleService.CREATE_PERMISSION);
		create(permission);
		permission = new Permission(RoleService.EDIT_PERMISSION);
		create(permission);
		permission = new Permission(RoleService.DELETE_PERMISSION);
		create(permission);
	}

	@Override
	protected String getMapping() {
		return null;
	}

}
