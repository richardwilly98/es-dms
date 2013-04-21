package com.github.richardwilly98.services;

import java.io.IOException;
import java.util.UUID;

import javax.inject.Inject;

import org.elasticsearch.client.Client;

import com.fasterxml.jackson.databind.ObjectMapper;

abstract class ProviderBase {

	static ObjectMapper mapper = new ObjectMapper();

	private Client client;

	protected Client getClient() throws IOException {
		return client;
	}
//		if (client == null) {
//			client = new TransportClient()
//					.addTransportAddress(new InetSocketTransportAddress(
//							"localhost", 9300));
//			createIndex();
//		}
//		return client;
//	}

	@Inject
	ProviderBase(Client client) {
		this.client = client;
	}
	
	protected String generateUniqueId() {
		return UUID.randomUUID().toString();
	}
	
	protected abstract void createIndex() throws IOException;
}
