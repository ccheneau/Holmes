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

import static net.holmes.core.configuration.ConfigurationEvent.EventType.ADD;
import static net.holmes.core.configuration.ConfigurationEvent.EventType.DELETE;
import static net.holmes.core.configuration.ConfigurationEvent.EventType.UPDATE;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import javax.inject.Inject;

import net.holmes.core.backend.backbone.response.ConfigurationFolder;
import net.holmes.core.backend.backbone.response.IndexElement;
import net.holmes.core.backend.backbone.response.Settings;
import net.holmes.core.configuration.Configuration;
import net.holmes.core.configuration.ConfigurationEvent;
import net.holmes.core.configuration.ConfigurationNode;
import net.holmes.core.configuration.Parameter;
import net.holmes.core.media.MediaCommand;
import net.holmes.core.media.MediaCommand.CommandType;
import net.holmes.core.media.index.MediaIndexElement;
import net.holmes.core.media.index.MediaIndexManager;
import net.holmes.core.media.node.RootNode;
import net.holmes.core.util.bundle.Bundle;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;

public final class BackboneManagerImpl implements BackboneManager {

    private final Configuration configuration;
    private final EventBus eventBus;
    private final Bundle bundle;
    private final MediaIndexManager mediaIndexManager;

    @Inject
    public BackboneManagerImpl(MediaIndexManager mediaIndexManager, Configuration configuration, EventBus eventBus, Bundle bundle) {
        this.mediaIndexManager = mediaIndexManager;
        this.configuration = configuration;
        this.eventBus = eventBus;
        this.bundle = bundle;
    }

    @Override
    public Collection<ConfigurationFolder> getFolders(RootNode rootNode) {
        List<ConfigurationNode> configNodes = configuration.getFolders(rootNode);
        Collection<ConfigurationFolder> folders = Lists.newArrayList();
        for (ConfigurationNode node : configNodes) {
            folders.add(new ConfigurationFolder(node.getId(), node.getLabel(), node.getPath()));
        }
        return folders;
    }

    @Override
    public ConfigurationFolder getFolder(String id, RootNode rootNode) {
        List<ConfigurationNode> configNodes = configuration.getFolders(rootNode);
        for (ConfigurationNode node : configNodes) {
            if (node.getId().equals(id)) return new ConfigurationFolder(node.getId(), node.getLabel(), node.getPath());
        }
        if (rootNode == RootNode.PODCAST) throw new IllegalArgumentException(bundle.getString("backend.podcast.unknown.error"));
        else throw new IllegalArgumentException(bundle.getString("backend.folder.unknown.error"));
    }

    @Override
    public void addFolder(ConfigurationFolder folder, RootNode rootNode) {
        List<ConfigurationNode> configNodes = configuration.getFolders(rootNode);
        boolean podcast = rootNode == RootNode.PODCAST;

        // Validate
        validateFolder(folder, podcast);
        validateDuplicatedFolder(null, folder, configNodes, podcast);
        folder.setId(UUID.randomUUID().toString());

        // Save config
        ConfigurationNode newNode = new ConfigurationNode(folder.getId(), folder.getName(), folder.getPath());
        configNodes.add(newNode);
        configuration.saveConfig();

        // Post event
        eventBus.post(new ConfigurationEvent(ADD, newNode, rootNode));
    }

    @Override
    public void editFolder(String id, ConfigurationFolder folder, RootNode rootNode) {
        List<ConfigurationNode> configNodes = configuration.getFolders(rootNode);
        boolean podcast = (rootNode == RootNode.PODCAST);

        // Validate
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

        // Save config if name or path has changed
        if (!currentNode.getLabel().equals(folder.getName()) || !currentNode.getPath().equals(folder.getPath())) {
            currentNode.setLabel(folder.getName());
            currentNode.setPath(folder.getPath());
            configuration.saveConfig();

            // Post Event
            eventBus.post(new ConfigurationEvent(UPDATE, currentNode, rootNode));
        }
    }

    @Override
    public void removeFolder(String id, RootNode rootNode) {
        List<ConfigurationNode> configNodes = configuration.getFolders(rootNode);
        boolean podcast = (rootNode == RootNode.PODCAST);

        // Validate
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

        // Save config
        configNodes.remove(currentNode);
        configuration.saveConfig();

        // Post Event
        eventBus.post(new ConfigurationEvent(DELETE, currentNode, rootNode));
    }

    @Override
    public Settings getSettings() {
        return new Settings(configuration.getUpnpServerName(), configuration.getHttpServerPort(),
                configuration.getParameter(Parameter.PREPEND_PODCAST_ENTRY_NAME), configuration.getParameter(Parameter.ENABLE_EXTERNAL_SUBTITLES));
    }

    @Override
    public void saveSettings(Settings settings) {
        if (Strings.isNullOrEmpty(settings.getServerName())) throw new IllegalArgumentException(bundle.getString("backend.settings.server.name.error"));
        if (settings.getHttpServerPort() == null || settings.getHttpServerPort() < 1024 || settings.getHttpServerPort() > 9999)
            throw new IllegalArgumentException(bundle.getString("backend.settings.http.port.error"));

        configuration.setUpnpServerName(settings.getServerName());
        configuration.setHttpServerPort(settings.getHttpServerPort());
        configuration.setParameter(Parameter.PREPEND_PODCAST_ENTRY_NAME, settings.getPrependPodcastItem());
        configuration.setParameter(Parameter.ENABLE_EXTERNAL_SUBTITLES, settings.getEnableExternalSubtitles());
        configuration.saveConfig();
    }

    @Override
    public Collection<IndexElement> getMediaIndexElements() {
        Collection<IndexElement> indexElements = Lists.newArrayList();
        for (Entry<String, MediaIndexElement> elementEntry : mediaIndexManager.getElements()) {
            indexElements.add(new IndexElement(elementEntry.getKey(), elementEntry.getValue().getParentId(), elementEntry.getValue().getMediaType(),
                    elementEntry.getValue().getName(), elementEntry.getValue().getPath()));
        }
        return indexElements;
    }

    @Override
    public void scanAllMedia() {
        eventBus.post(new MediaCommand(CommandType.SCAN_ALL, null));
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
