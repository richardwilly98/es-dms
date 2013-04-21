package com.github.richardwilly98.services;

import java.io.IOException;
import java.util.UUID;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.fasterxml.jackson.databind.ObjectMapper;

abstract class ProviderBase {

	static ObjectMapper mapper = new ObjectMapper();

	private Client client;

	protected Client getClient() throws IOException {
		if (client == null) {
			client = new TransportClient()
					.addTransportAddress(new InetSocketTransportAddress(
							"localhost", 9300));
			createIndex();
		}
		return client;
	}
	
	protected String generateUniqueId() {
		return UUID.randomUUID().toString();
	}
	
	protected abstract void createIndex() throws IOException;
}
