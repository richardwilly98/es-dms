package com.github.richardwilly98.services;

import java.util.UUID;

import javax.inject.Inject;

import org.elasticsearch.client.Client;

import com.fasterxml.jackson.databind.ObjectMapper;

abstract class ProviderBase {

	static ObjectMapper mapper = new ObjectMapper();

	private final Client client;
	
	String index;

	protected Client getClient() {
		return client;
	}

	@Inject
	ProviderBase(Client client) {
		this.client = client;
		createIndex();
	}
	
	protected String generateUniqueId() {
		return UUID.randomUUID().toString();
	}
	
	protected abstract void createIndex();
}
