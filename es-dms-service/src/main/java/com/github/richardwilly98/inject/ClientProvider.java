package com.github.richardwilly98.inject;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.network.NetworkUtils;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.google.inject.Provider;

public class ClientProvider implements Provider<Client> {

	@Override
	public Client get() {
//		Builder builder = ImmutableSettings.settingsBuilder().loadFromClasspath("elasticsearch.yml");
//		TransportClient client = new TransportClient(builder.build());
		TransportClient client = new TransportClient();
		client.addTransportAddress(new InetSocketTransportAddress(
				NetworkUtils.getLocalAddress().getHostName(), 9300));
		return client;
	}

}
