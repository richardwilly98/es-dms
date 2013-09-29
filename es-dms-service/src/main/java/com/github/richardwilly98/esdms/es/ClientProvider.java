package com.github.richardwilly98.esdms.es;

/*
 * #%L
 * es-dms-service
 * %%
 * Copyright (C) 2013 es-dms
 * %%
 * Copyright 2012-2013 Richard Louapre
 * 
 * This file is part of ES-DMS.
 * 
 * The current version of ES-DMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * ES-DMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.github.richardwilly98.esdms.api.Settings;
import com.github.richardwilly98.esdms.services.BootstrapService;
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
	// Builder builder =
	// ImmutableSettings.settingsBuilder().loadFromClasspath("elasticsearch.yml");
	// TransportClient client = new TransportClient(builder.build());
	TransportClient client = new TransportClient();
	client.addTransportAddress(new InetSocketTransportAddress(settings.getEsHost(), settings.getEsPort()));
	return client;
    }

}
