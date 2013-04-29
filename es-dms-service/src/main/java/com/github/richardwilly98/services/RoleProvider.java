package com.github.richardwilly98.services;

import org.elasticsearch.client.Client;

import com.github.richardwilly98.api.Role;
import com.github.richardwilly98.api.services.RoleService;
import com.google.inject.Inject;

public class RoleProvider extends ProviderBase<Role> implements RoleService {

	private final static String index = "test-roles";
	private final static String type = "role";

	@Inject
	RoleProvider(Client client) {
		super(client, RoleProvider.index, RoleProvider.type, Role.class);
	}

}
