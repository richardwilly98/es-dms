package com.github.richardwilly98.es;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.github.richardwilly98.api.Settings;
import com.github.richardwilly98.api.services.BootstrapService;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class ClientProvider implements Provider<Client> {

	final Settings settings;
	
	@Inject
	public ClientProvider(final BootstrapService bootstrapService) {
		this.settings = bootstrapService.loadSettings();
	}
	@Override
	public Client get() {
//		Builder builder = ImmutableSettings.settingsBuilder().loadFromClasspath("elasticsearch.yml");
//		TransportClient client = new TransportClient(builder.build());
		TransportClient client = new TransportClient();
		client.addTransportAddress(new InetSocketTransportAddress(
				settings.getEsHost(), settings.getEsPort()));
		return client;
	}

}
