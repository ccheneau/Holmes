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

package net.holmes.core.backend;

import static net.holmes.common.event.ConfigurationEvent.EventType.ADD;
import static net.holmes.common.event.ConfigurationEvent.EventType.DELETE;
import static net.holmes.common.event.ConfigurationEvent.EventType.UPDATE;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.inject.Inject;

import net.holmes.common.configuration.Configuration;
import net.holmes.common.configuration.ConfigurationNode;
import net.holmes.common.configuration.Parameter;
import net.holmes.common.event.ConfigurationEvent;
import net.holmes.common.event.MediaEvent;
import net.holmes.common.event.MediaEvent.MediaEventType;
import net.holmes.common.media.RootNode;
import net.holmes.core.backend.exception.ConfigurationException;
import net.holmes.core.backend.response.ConfigurationFolder;
import net.holmes.core.backend.response.IndexElement;
import net.holmes.core.backend.response.Settings;
import net.holmes.core.media.index.MediaIndexElement;
import net.holmes.core.media.index.MediaIndexManager;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;

public final class BackendManagerImpl implements BackendManager {

    private final Configuration configuration;
    private final EventBus eventBus;
    private final ResourceBundle resourceBundle;
    private final MediaIndexManager mediaIndexManager;

    @Inject
    public BackendManagerImpl(MediaIndexManager mediaIndexManager, Configuration configuration, EventBus eventBus, ResourceBundle resourceBundle) {
        this.mediaIndexManager = mediaIndexManager;
        this.configuration = configuration;
        this.eventBus = eventBus;
        this.resourceBundle = resourceBundle;
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
        if (rootNode == RootNode.PODCAST) throw new IllegalArgumentException(resourceBundle.getString("backend.podcast.unknown.error"));
        else throw new IllegalArgumentException(resourceBundle.getString("backend.folder.unknown.error"));
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
        try {
            configuration.saveConfig();
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }

        // Post event
        eventBus.post(new ConfigurationEvent(ADD, newNode, rootNode));
    }

    @Override
    public void editFolder(String id, ConfigurationFolder folder, RootNode rootNode) {
        List<ConfigurationNode> configNodes = configuration.getFolders(rootNode);
        boolean podcast = rootNode == RootNode.PODCAST;

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
            if (podcast) throw new IllegalArgumentException(resourceBundle.getString("backend.podcast.unknown.error"));
            else throw new IllegalArgumentException(resourceBundle.getString("backend.folder.unknown.error"));
        }

        // Save config if name or path has changed
        if (!currentNode.getLabel().equals(folder.getName()) || !currentNode.getPath().equals(folder.getPath())) {
            currentNode.setLabel(folder.getName());
            currentNode.setPath(folder.getPath());
            try {
                configuration.saveConfig();
            } catch (IOException e) {
                throw new ConfigurationException(e);
            }

            // Post Event
            eventBus.post(new ConfigurationEvent(UPDATE, currentNode, rootNode));
        }
    }

    @Override
    public void removeFolder(String id, RootNode rootNode) {
        List<ConfigurationNode> configNodes = configuration.getFolders(rootNode);
        boolean podcast = rootNode == RootNode.PODCAST;

        // Validate
        ConfigurationNode currentNode = null;
        for (ConfigurationNode node : configNodes) {
            if (node.getId().equals(id)) {
                currentNode = node;
                break;
            }
        }
        if (currentNode == null) {
            if (podcast) throw new IllegalArgumentException(resourceBundle.getString("backend.podcast.unknown.error"));
            else throw new IllegalArgumentException(resourceBundle.getString("backend.folder.unknown.error"));
        }

        // Save config
        configNodes.remove(currentNode);
        try {
            configuration.saveConfig();
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }

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
        if (Strings.isNullOrEmpty(settings.getServerName()))
            throw new IllegalArgumentException(resourceBundle.getString("backend.settings.server.name.error"));
        if (settings.getHttpServerPort() == null || settings.getHttpServerPort() < 1024 || settings.getHttpServerPort() > 9999)
            throw new IllegalArgumentException(resourceBundle.getString("backend.settings.http.port.error"));

        configuration.setUpnpServerName(settings.getServerName());
        configuration.setHttpServerPort(settings.getHttpServerPort());
        configuration.setParameter(Parameter.PREPEND_PODCAST_ENTRY_NAME, settings.getPrependPodcastItem());
        configuration.setParameter(Parameter.ENABLE_EXTERNAL_SUBTITLES, settings.getEnableExternalSubtitles());
        try {
            configuration.saveConfig();
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }

    @Override
    public Collection<IndexElement> getMediaIndexElements() {
        Collection<IndexElement> indexElements = Lists.newArrayList();
        for (Entry<String, MediaIndexElement> elementEntry : mediaIndexManager.getElements()) {
            MediaIndexElement el = elementEntry.getValue();
            indexElements.add(new IndexElement(elementEntry.getKey(), el.getParentId(), el.getMediaType(), el.getName(), el.getPath()));
        }
        return indexElements;
    }

    @Override
    public void scanAllMedia() {
        eventBus.post(new MediaEvent(MediaEventType.SCAN_ALL, null));
    }

    /**
     * Check folder does not already existalidate folder
     * 
     * @param folder
     * @param podcast
     */
    private void validateFolder(ConfigurationFolder folder, boolean podcast) {
        // Check folder's name and path are not empty
        if (podcast && Strings.isNullOrEmpty(folder.getName())) throw new IllegalArgumentException(resourceBundle.getString("backend.podcast.name.error"));
        else if (podcast && Strings.isNullOrEmpty(folder.getPath())) throw new IllegalArgumentException(resourceBundle.getString("backend.podcast.url.error"));
        else if (Strings.isNullOrEmpty(folder.getName())) throw new IllegalArgumentException(resourceBundle.getString("backend.folder.name.error"));
        else if (Strings.isNullOrEmpty(folder.getPath())) throw new IllegalArgumentException(resourceBundle.getString("backend.folder.path.error"));

        if (podcast) {
            // Check podcast URL is correct
            if (!folder.getPath().toLowerCase().startsWith("http://"))
                throw new IllegalArgumentException(resourceBundle.getString("backend.podcast.url.malformatted.error"));
        } else {
            // Check folder path is correct
            File file = new File(folder.getPath());
            if (!file.exists() || !file.canRead() || file.isHidden() || !file.isDirectory())
                throw new IllegalArgumentException(resourceBundle.getString("backend.folder.path.unknown.error"));
        }
    }

    /**
     * Check folder does not already exist
     * 
     * @param excludedId
     * @param folder
     * @param configNodes
     * @param podcast
     */
    private void validateDuplicatedFolder(String excludedId, ConfigurationFolder folder, List<ConfigurationNode> configNodes, boolean podcast) {
        for (ConfigurationNode node : configNodes) {
            if (excludedId != null && excludedId.equals(node.getId())) continue;

            if (podcast && node.getLabel().equals(folder.getName())) {
                throw new IllegalArgumentException(resourceBundle.getString("backend.podcast.already.exist.error"));
            } else if (podcast && node.getPath().equals(folder.getPath())) {
                throw new IllegalArgumentException(resourceBundle.getString("backend.podcast.already.exist.error"));
            } else if (node.getLabel().equals(folder.getName())) {
                throw new IllegalArgumentException(resourceBundle.getString("backend.folder.already.exist.error"));
            } else if (node.getPath().equals(folder.getPath())) {
                throw new IllegalArgumentException(resourceBundle.getString("backend.folder.already.exist.error"));
            }
        }
    }
}
