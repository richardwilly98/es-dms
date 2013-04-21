package test.github.richardwilly98.services;

import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.io.IOException;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.io.FileSystemUtils;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.internal.InternalSettingsPerparer;
import org.elasticsearch.plugins.PluginManager;

import com.google.inject.Provider;

public class LocalClientProvider implements Provider<Client> {

	/*
	 * Provides embedded ES instance for unit testing
	 * (non-Javadoc)
	 * @see com.google.inject.Provider#get()
	 */
	@Override
	public Client get() {
		Settings settings = getSettings();
//		Tuple<Settings, Environment> initialSettings = InternalSettingsPerparer
//				.prepareSettings(settings, true);
//		PluginManager pluginManager = new PluginManager(
//				initialSettings.v2(), null);
//
//		if (!initialSettings.v2().configFile().exists()) {
//			FileSystemUtils.mkdirs(initialSettings.v2().configFile());
//		}
//
//		if (!initialSettings.v2().logsFile().exists()) {
//			FileSystemUtils.mkdirs(initialSettings.v2().logsFile());
//		}
//
//		if (!initialSettings.v2().pluginsFile().exists()) {
//			FileSystemUtils.mkdirs(initialSettings.v2().pluginsFile());
//			try {
//				pluginManager.downloadAndExtract(
//						settings.get("plugins.mapper-attachments"), false);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		Node node = nodeBuilder().local(true).settings(settings).node();
		Client client = node.client();
		return client;
	}
	
	private Settings getSettings() {
		Settings settings = settingsBuilder()
				.loadFromStream(
						"settings.yml",
						ClassLoader
								.getSystemResourceAsStream("settings.yml"))
				.build();
		return settings;
	}

}
