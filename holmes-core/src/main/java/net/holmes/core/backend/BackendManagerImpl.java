/*
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

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import net.holmes.core.backend.exception.BackendException;
import net.holmes.core.backend.response.ConfigurationFolder;
import net.holmes.core.backend.response.Settings;
import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.common.configuration.ConfigurationNode;
import net.holmes.core.common.event.ConfigurationEvent;
import net.holmes.core.media.model.RootNode;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static net.holmes.core.common.UniqueId.newUniqueId;
import static net.holmes.core.common.configuration.Parameter.*;
import static net.holmes.core.common.event.ConfigurationEvent.EventType.*;

/**
 * Backend manager implementation.
 */
public final class BackendManagerImpl implements BackendManager {

    private final Configuration configuration;
    private final EventBus eventBus;
    private final BackendManagerHelper helper;

    /**
     * Instantiates a new backend manager implementation.
     *
     * @param configuration configuration
     * @param eventBus      event bus
     */
    @Inject
    public BackendManagerImpl(final Configuration configuration, final EventBus eventBus) {
        this.configuration = configuration;
        this.eventBus = eventBus;
        this.helper = new BackendManagerHelper();
    }

    @Override
    public Collection<ConfigurationFolder> getFolders(final RootNode rootNode) {
        List<ConfigurationNode> configNodes = configuration.getFolders(rootNode);
        Collection<ConfigurationFolder> folders = Lists.newArrayList();
        for (ConfigurationNode node : configNodes) {
            folders.add(new ConfigurationFolder(node.getId(), node.getLabel(), node.getPath()));
        }
        return folders;
    }

    @Override
    public ConfigurationFolder getFolder(final String id, final RootNode rootNode) {
        ConfigurationNode node = helper.findConfigurationNode(id, configuration.getFolders(rootNode), rootNode == RootNode.PODCAST);
        return new ConfigurationFolder(node.getId(), node.getLabel(), node.getPath());
    }

    @Override
    public void addFolder(final ConfigurationFolder folder, final RootNode rootNode) {
        List<ConfigurationNode> configNodes = configuration.getFolders(rootNode);

        // Validate
        if (rootNode == RootNode.PODCAST) helper.validatePodcast(folder, configNodes, null);
        else helper.validateFolder(folder, configNodes, null);

        // Set new folder id
        folder.setId(newUniqueId());

        // Save config
        ConfigurationNode newNode = new ConfigurationNode(folder.getId(), folder.getName(), folder.getPath());
        configNodes.add(newNode);
        try {
            configuration.saveConfig();
        } catch (IOException e) {
            throw new BackendException(e);
        }

        // Post event
        eventBus.post(new ConfigurationEvent(ADD_FOLDER, newNode, rootNode));
    }

    @Override
    public void editFolder(final String id, final ConfigurationFolder folder, final RootNode rootNode) {
        List<ConfigurationNode> configNodes = configuration.getFolders(rootNode);
        boolean podcast = rootNode == RootNode.PODCAST;

        // Check folder
        if (podcast) helper.validatePodcast(folder, configNodes, id);
        else helper.validateFolder(folder, configNodes, id);

        // Save config if name or path has changed
        ConfigurationNode currentNode = helper.findConfigurationNode(id, configNodes, podcast);
        if (!currentNode.getLabel().equals(folder.getName()) || !currentNode.getPath().equals(folder.getPath())) {
            currentNode.setLabel(folder.getName());
            currentNode.setPath(folder.getPath());
            try {
                configuration.saveConfig();
            } catch (IOException e) {
                throw new BackendException(e);
            }
            // Post Event
            eventBus.post(new ConfigurationEvent(UPDATE_FOLDER, currentNode, rootNode));
        }
    }

    @Override
    public void removeFolder(final String id, final RootNode rootNode) {
        List<ConfigurationNode> configNodes = configuration.getFolders(rootNode);
        ConfigurationNode currentNode = helper.findConfigurationNode(id, configNodes, rootNode == RootNode.PODCAST);

        // Remove node
        configNodes.remove(currentNode);
        try {
            // Save config
            configuration.saveConfig();
        } catch (IOException e) {
            throw new BackendException(e);
        }
        // Post Event
        eventBus.post(new ConfigurationEvent(DELETE_FOLDER, currentNode, rootNode));
    }

    @Override
    public Settings getSettings() {
        return new Settings(configuration.getUpnpServerName(), configuration.getHttpServerPort(),
                configuration.getBooleanParameter(PREPEND_PODCAST_ENTRY_NAME),
                configuration.getBooleanParameter(ENABLE_EXTERNAL_SUBTITLES),
                configuration.getBooleanParameter(ENABLE_ICECAST_DIRECTORY));
    }

    @Override
    public void saveSettings(final Settings settings) {
        helper.validateServerName(settings.getServerName());
        helper.validateHttpServerPort(settings.getHttpServerPort());

        configuration.setUpnpServerName(settings.getServerName());
        configuration.setHttpServerPort(settings.getHttpServerPort());
        configuration.setBooleanParameter(PREPEND_PODCAST_ENTRY_NAME, settings.getPrependPodcastItem());
        configuration.setBooleanParameter(ENABLE_EXTERNAL_SUBTITLES, settings.getEnableExternalSubtitles());
        configuration.setBooleanParameter(ENABLE_ICECAST_DIRECTORY, settings.getEnableIcecastDirectory());
        try {
            // save settings
            configuration.saveConfig();
        } catch (IOException e) {
            throw new BackendException(e);
        }
        // Post event
        eventBus.post(new ConfigurationEvent(SAVE_SETTINGS));
    }
}
