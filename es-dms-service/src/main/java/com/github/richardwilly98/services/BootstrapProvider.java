package com.github.richardwilly98.services;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.log4j.Logger;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;

import com.github.richardwilly98.api.Settings;
import com.github.richardwilly98.api.services.BootstrapService;

public class BootstrapProvider implements BootstrapService {

	private static Logger log = Logger.getLogger(BootstrapProvider.class);
	
	@Override
	public Settings loadSettings() {
		Builder builder = ImmutableSettings.settingsBuilder().loadFromClasspath("es-dms-settings.yml");
		checkNotNull(builder);
		checkNotNull(builder.get("library"));
		checkNotNull(builder.get("es.host"));
		checkNotNull(builder.get("es.port"));
		log.debug(builder.toString());
		log.debug("library: " + builder.get("library"));
		Settings settings = new Settings();
		settings.setLibrary(builder.get("library"));
		settings.setEsHost(builder.get("es.host"));
		settings.setEsPort(Integer.parseInt(builder.get("es.port")));
		return settings;
	}

}
