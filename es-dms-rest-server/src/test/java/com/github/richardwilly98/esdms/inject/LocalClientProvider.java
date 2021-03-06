package com.github.richardwilly98.esdms.inject;

/*
 * #%L
 * es-dms-site
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

import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.io.FileSystemUtils;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.internal.InternalSettingsPreparer;
import org.elasticsearch.plugins.PluginManager;
import org.elasticsearch.plugins.PluginManager.OutputMode;

import com.google.inject.Provider;

class LocalClientProvider implements Provider<Client> {

    /*
     * Provides embedded ES instance for unit testing (non-Javadoc)
     * 
     * @see com.google.inject.Provider#get()
     */
    @Override
    public Client get() {
        Settings settings = getSettings();
        Tuple<Settings, Environment> initialSettings = InternalSettingsPreparer.prepareSettings(settings, true);

        if (!initialSettings.v2().configFile().exists()) {
            FileSystemUtils.mkdirs(initialSettings.v2().configFile());
        }

        if (!initialSettings.v2().logsFile().exists()) {
            FileSystemUtils.mkdirs(initialSettings.v2().logsFile());
        }

        if (!initialSettings.v2().pluginsFile().exists()) {
            FileSystemUtils.mkdirs(initialSettings.v2().pluginsFile());
            try {
                Map<String, String> plugins = settings.getByPrefix("plugins").getAsMap();
                for (String key : plugins.keySet()) {
                    String url = null;
                    String name = null;
                    if (!plugins.get(key).startsWith("elasticsearch")) {
                        name = key;
                        url = plugins.get(key);
                    } else {
                        name = plugins.get(key);
                    }
                    PluginManager pluginManager = new PluginManager(initialSettings.v2(), url, OutputMode.DEFAULT);
                    pluginManager.downloadAndExtract(name);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Node node = nodeBuilder().local(true).settings(settings).node();
        Client client = node.client();
        return client;
    }

    private Settings getSettings() {
        Settings settings = settingsBuilder().loadFromStream("settings.yml", ClassLoader.getSystemResourceAsStream("settings.yml")).build();
        return settings;
    }

}
