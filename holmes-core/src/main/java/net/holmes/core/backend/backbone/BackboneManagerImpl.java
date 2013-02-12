/**
* Copyright (C) 2012-2013  Cedric Cheneau
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.holmes.core.backend.backbone;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import net.holmes.core.backend.backbone.response.ConfigurationFolder;
import net.holmes.core.backend.backbone.response.Settings;
import net.holmes.core.configuration.Configuration;
import net.holmes.core.configuration.ConfigurationNode;
import net.holmes.core.configuration.Parameter;
import net.holmes.core.util.bundle.Bundle;

import com.google.common.collect.Lists;

public final class BackboneManagerImpl implements BackboneManager {

    private final Configuration configuration;
    private final Bundle bundle;

    @Inject
    public BackboneManagerImpl(Configuration configuration, Bundle bundle) {
        this.configuration = configuration;
        this.bundle = bundle;
    }

    @Override
    public Collection<ConfigurationFolder> getFolders(List<ConfigurationNode> configNodes) {
        Collection<ConfigurationFolder> folders = Lists.newArrayList();
        for (ConfigurationNode node : configNodes) {
            folders.add(new ConfigurationFolder(node.getId(), node.getLabel(), node.getPath()));
        }
        return folders;
    }

    @Override
    public ConfigurationFolder getFolder(String id, List<ConfigurationNode> configNodes, boolean podcast) {
        //TODO validation
        for (ConfigurationNode node : configNodes) {
            if (node.getId().equals(id)) return new ConfigurationFolder(node.getId(), node.getLabel(), node.getPath());
        }
        return null;
    }

    @Override
    public void addFolder(ConfigurationFolder folder, List<ConfigurationNode> configNodes, boolean podcast) {
        //TODO validation
        folder.setId(UUID.randomUUID().toString());
        configNodes.add(new ConfigurationNode(folder.getId(), folder.getName(), folder.getPath()));
        configuration.saveConfig();
    }

    @Override
    public void editFolder(String id, ConfigurationFolder folder, List<ConfigurationNode> configNodes, boolean podcast) {
        //TODO validation
        ConfigurationNode currentNode = null;
        for (ConfigurationNode node : configNodes) {
            if (node.getId().equals(id)) {
                currentNode = node;
                break;
            }
        }
        if (currentNode != null) {
            currentNode.setLabel(folder.getName());
            currentNode.setPath(folder.getPath());
            configuration.saveConfig();
        }
    }

    @Override
    public void removeFolder(String id, List<ConfigurationNode> configNodes, boolean podcast) {
        //TODO validation
        ConfigurationNode currentNode = null;
        for (ConfigurationNode node : configNodes) {
            if (node.getId().equals(id)) {
                currentNode = node;
                break;
            }
        }
        if (currentNode != null) {
            configNodes.remove(currentNode);
            configuration.saveConfig();
        }
    }

    @Override
    public Settings getSettings() {
        return new Settings(configuration.getUpnpServerName(), configuration.getHttpServerPort(),
                configuration.getParameter(Parameter.PREPEND_PODCAST_ENTRY_NAME));
    }

    @Override
    public void updateSettings(Settings settings) {
        if (settings.getServerName() == null || settings.getServerName().trim().length() == 0)
            throw new IllegalArgumentException(bundle.getString("backend.settings.server.name.error"));
        if (settings.getHttpServerPort() == null || settings.getHttpServerPort() < 1024 || settings.getHttpServerPort() > 9999)
            throw new IllegalArgumentException(bundle.getString("backend.settings.http.port.error"));

        configuration.setUpnpServerName(settings.getServerName());
        configuration.setHttpServerPort(settings.getHttpServerPort());
        configuration.setParameter(Parameter.PREPEND_PODCAST_ENTRY_NAME, settings.getPrependPodcastItem());
        configuration.saveConfig();
    }

}
