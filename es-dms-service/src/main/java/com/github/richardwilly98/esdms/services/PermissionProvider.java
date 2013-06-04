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
		create(new PermissionImpl.Builder().name(DocumentService.CREATE_PERMISSION).build());
		create(new PermissionImpl.Builder().name(DocumentService.DELETE_PERMISSION).build());
		create(new PermissionImpl.Builder().name(DocumentService.EDIT_PERMISSION).build());
		create(new PermissionImpl.Builder().name(DocumentService.READ_PERMISSION).build());
		create(new PermissionImpl.Builder().name(UserService.CREATE_PERMISSION).build());
		create(new PermissionImpl.Builder().name(UserService.EDIT_PERMISSION).build());
		create(new PermissionImpl.Builder().name(UserService.DELETE_PERMISSION).build());
		create(new PermissionImpl.Builder().name(RoleService.CREATE_PERMISSION).build());
		create(new PermissionImpl.Builder().name(RoleService.EDIT_PERMISSION).build());
		create(new PermissionImpl.Builder().name(RoleService.DELETE_PERMISSION).build());
	}

	@Override
	protected String getMapping() {
		return null;
	}

}
