package com.github.richardwilly98.esdms.services;

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


import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import org.apache.log4j.Logger;
import org.elasticsearch.common.settings.ImmutableSettings;

import com.github.richardwilly98.esdms.SettingsImpl;
import com.github.richardwilly98.esdms.api.Settings;

@Singleton
public class BootstrapProvider implements BootstrapService {

	private static Logger log = Logger.getLogger(BootstrapProvider.class);
	private Settings settings;

	@Override
	public Settings loadSettings() {
		if (this.settings == null) {
			org.elasticsearch.common.settings.Settings settings = ImmutableSettings.settingsBuilder()
					.loadFromClasspath("es-dms-settings.yml").build();
			checkNotNull(settings);
			checkNotNull(settings.get("library"));
			checkNotNull(settings.get("es.host"));
			checkNotNull(settings.get("es.port"));
			this.settings = new SettingsImpl();
			this.settings.setLibrary(settings.get("library"));
			this.settings.setEsHost(settings.get("es.host"));
			this.settings.setEsPort(settings.getAsInt("es.port", 9300));
			this.settings.setIndexRefresh(settings.getAsBoolean("es.index.refresh", false));
			log.debug("settings: " + this.settings);
		}
		return settings;
	}

}
