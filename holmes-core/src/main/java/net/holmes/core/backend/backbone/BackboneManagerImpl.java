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

import java.io.File;
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

import com.google.common.base.Strings;
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
        for (ConfigurationNode node : configNodes) {
            if (node.getId().equals(id)) return new ConfigurationFolder(node.getId(), node.getLabel(), node.getPath());
        }
        if (podcast) throw new IllegalArgumentException(bundle.getString("backend.podcast.unknown.error"));
        else throw new IllegalArgumentException(bundle.getString("backend.folder.unknown.error"));
    }

    @Override
    public void addFolder(ConfigurationFolder folder, List<ConfigurationNode> configNodes, boolean podcast) {
        validateFolder(folder, podcast);
        validateDuplicatedFolder(null, folder, configNodes, podcast);
        folder.setId(UUID.randomUUID().toString());
        configNodes.add(new ConfigurationNode(folder.getId(), folder.getName(), folder.getPath()));
        configuration.saveConfig();
    }

    @Override
    public void editFolder(String id, ConfigurationFolder folder, List<ConfigurationNode> configNodes, boolean podcast) {
        validateFolder(folder, podcast);
        validateDuplicatedFolder(id, folder, configNodes, podcast);
        ConfigurationNode currentNode = null;
        for (ConfigurationNode node : configNodes) {
            if (node.getId().equals(id)) {
                currentNode = node;
                break;
            }
        }

        if (currentNode == null) {
            if (podcast) throw new IllegalArgumentException(bundle.getString("backend.podcast.unknown.error"));
            else throw new IllegalArgumentException(bundle.getString("backend.folder.unknown.error"));
        }

        currentNode.setLabel(folder.getName());
        currentNode.setPath(folder.getPath());
        configuration.saveConfig();
    }

    @Override
    public void removeFolder(String id, List<ConfigurationNode> configNodes, boolean podcast) {
        ConfigurationNode currentNode = null;
        for (ConfigurationNode node : configNodes) {
            if (node.getId().equals(id)) {
                currentNode = node;
                break;
            }
        }
        if (currentNode == null) {
            if (podcast) throw new IllegalArgumentException(bundle.getString("backend.podcast.unknown.error"));
            else throw new IllegalArgumentException(bundle.getString("backend.folder.unknown.error"));
        }
        configNodes.remove(currentNode);
        configuration.saveConfig();

    }

    @Override
    public Settings getSettings() {
        return new Settings(configuration.getUpnpServerName(), configuration.getHttpServerPort(),
                configuration.getParameter(Parameter.PREPEND_PODCAST_ENTRY_NAME), configuration.getParameter(Parameter.ENABLE_EXTERNAL_SUBTITLES));
    }

    @Override
    public void updateSettings(Settings settings) {
        if (Strings.isNullOrEmpty(settings.getServerName())) throw new IllegalArgumentException(bundle.getString("backend.settings.server.name.error"));
        if (settings.getHttpServerPort() == null || settings.getHttpServerPort() < 1024 || settings.getHttpServerPort() > 9999)
            throw new IllegalArgumentException(bundle.getString("backend.settings.http.port.error"));

        configuration.setUpnpServerName(settings.getServerName());
        configuration.setHttpServerPort(settings.getHttpServerPort());
        configuration.setParameter(Parameter.PREPEND_PODCAST_ENTRY_NAME, settings.getPrependPodcastItem());
        configuration.setParameter(Parameter.ENABLE_EXTERNAL_SUBTITLES, settings.getEnableExternalSubtitles());
        configuration.saveConfig();
    }

    private void validateFolder(ConfigurationFolder folder, boolean podcast) {
        // Check folder's name and path are not empty
        if (podcast && Strings.isNullOrEmpty(folder.getName())) throw new IllegalArgumentException(bundle.getString("backend.podcast.name.error"));
        else if (podcast && Strings.isNullOrEmpty(folder.getPath())) throw new IllegalArgumentException(bundle.getString("backend.podcast.url.error"));
        else if (Strings.isNullOrEmpty(folder.getName())) throw new IllegalArgumentException(bundle.getString("backend.folder.name.error"));
        else if (Strings.isNullOrEmpty(folder.getPath())) throw new IllegalArgumentException(bundle.getString("backend.folder.path.error"));

        // Check folder's path is correct
        if (podcast) {
            if (!folder.getPath().toLowerCase().startsWith("http://"))
                throw new IllegalArgumentException(bundle.getString("backend.podcast.url.malformatted.error"));
        } else {
            File file = new File(folder.getPath());
            if (!file.exists() || !file.canRead() || file.isHidden() || !file.isDirectory())
                throw new IllegalArgumentException(bundle.getString("backend.folder.path.unknown.error"));

        }
    }

    // Check folder does not already exist
    private void validateDuplicatedFolder(String excludedId, ConfigurationFolder folder, List<ConfigurationNode> configNodes, boolean podcast) {
        for (ConfigurationNode node : configNodes) {
            if (excludedId != null && node.getId().equals(folder.getId())) continue;

            if (podcast && node.getLabel().equals(folder.getName())) throw new IllegalArgumentException(bundle.getString("backend.podcast.already.exist.error"));
            else if (podcast && node.getPath().equals(folder.getPath())) throw new IllegalArgumentException(
                    bundle.getString("backend.podcast.already.exist.error"));
            else if (node.getLabel().equals(folder.getName())) throw new IllegalArgumentException(bundle.getString("backend.folder.already.exist.error"));
            else if (node.getPath().equals(folder.getPath())) throw new IllegalArgumentException(bundle.getString("backend.folder.already.exist.error"));
        }
    }
}
