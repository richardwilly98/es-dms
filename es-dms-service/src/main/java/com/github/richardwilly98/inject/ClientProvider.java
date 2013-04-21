package com.github.richardwilly98.inject;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.google.inject.Provider;

public class ClientProvider implements Provider<Client> {

	@Override
	public Client get() {
		Client client = new TransportClient()
		.addTransportAddress(new InetSocketTransportAddress(
				"localhost", 9300));
		return client;
	}

}
