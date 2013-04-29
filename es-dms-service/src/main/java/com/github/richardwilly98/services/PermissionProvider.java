package com.github.richardwilly98.services;

import org.elasticsearch.client.Client;

import com.github.richardwilly98.api.Permission;
import com.github.richardwilly98.api.services.PermissionService;
import com.google.inject.Inject;

public class PermissionProvider extends ProviderBase<Permission> implements PermissionService {

	private final static String index = "system";
	private final static String type = "permission";

	@Inject
	PermissionProvider(Client client) {
		super(client, PermissionProvider.index, PermissionProvider.type, Permission.class);
	}

}
