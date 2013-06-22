package com.github.richardwilly98.esdms.services;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.log4j.Logger;
import org.elasticsearch.common.settings.ImmutableSettings;

import com.github.richardwilly98.esdms.SettingsImpl;
import com.github.richardwilly98.esdms.api.Settings;

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
			log.debug("settings: " + settings);
		}
		return settings;
	}

}
