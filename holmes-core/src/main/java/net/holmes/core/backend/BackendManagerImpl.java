/*
 * Copyright (C) 2012-2014  Cedric Cheneau
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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import net.holmes.core.backend.exception.BackendException;
import net.holmes.core.backend.response.ConfigurationFolder;
import net.holmes.core.backend.response.Settings;
import net.holmes.core.business.configuration.ConfigurationDao;
import net.holmes.core.business.configuration.ConfigurationNode;
import net.holmes.core.business.media.model.RootNode;
import net.holmes.core.common.event.ConfigurationEvent;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static net.holmes.core.common.FileUtils.isValidDirectory;
import static net.holmes.core.common.UniqueIdGenerator.newUniqueId;
import static net.holmes.core.common.event.ConfigurationEvent.EventType.*;
import static net.holmes.core.common.parameter.ConfigurationParameter.*;

/**
 * Backend manager implementation.
 */
public final class BackendManagerImpl implements BackendManager {
    private static final Pattern URL_PATTERN = Pattern.compile("^(https?|ftp|file)://.+$", Pattern.CASE_INSENSITIVE);

    private final ConfigurationDao configurationDao;
    private final EventBus eventBus;

    /**
     * Instantiates a new backend manager implementation.
     *
     * @param configurationDao configuration dao
     * @param eventBus         event bus
     */
    @Inject
    public BackendManagerImpl(final ConfigurationDao configurationDao, final EventBus eventBus) {
        this.configurationDao = configurationDao;
        this.eventBus = eventBus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ConfigurationFolder> getFolders(final RootNode rootNode) {
        List<ConfigurationNode> configNodes = configurationDao.getNodes(rootNode);
        Collection<ConfigurationFolder> folders = Lists.newArrayList();
        for (ConfigurationNode node : configNodes)
            folders.add(new ConfigurationFolder(node.getId(), node.getLabel(), node.getPath()));

        return folders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigurationFolder getFolder(final String id, final RootNode rootNode) {
        ConfigurationNode node = findConfigurationNode(id, configurationDao.getNodes(rootNode), rootNode == RootNode.PODCAST);
        return new ConfigurationFolder(node.getId(), node.getLabel(), node.getPath());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFolder(final ConfigurationFolder folder, final RootNode rootNode) {
        List<ConfigurationNode> configNodes = configurationDao.getNodes(rootNode);

        // Validate
        if (rootNode == RootNode.PODCAST) validatePodcast(folder, configNodes, null);
        else validateFolder(folder, configNodes, null);

        // Set new folder id
        folder.setId(newUniqueId());

        // Save config
        ConfigurationNode newNode = new ConfigurationNode(folder.getId(), folder.getName(), folder.getPath());
        configNodes.add(newNode);
        try {
            configurationDao.saveConfig();
        } catch (IOException e) {
            throw new BackendException(e);
        }

        // Post event
        eventBus.post(new ConfigurationEvent(ADD_FOLDER, newNode, rootNode));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void editFolder(final String id, final ConfigurationFolder folder, final RootNode rootNode) {
        List<ConfigurationNode> configNodes = configurationDao.getNodes(rootNode);
        boolean podcast = rootNode == RootNode.PODCAST;

        // Check folder
        if (podcast) validatePodcast(folder, configNodes, id);
        else validateFolder(folder, configNodes, id);

        // Save config if name or path has changed
        ConfigurationNode currentNode = findConfigurationNode(id, configNodes, podcast);
        if (!currentNode.getLabel().equals(folder.getName()) || !currentNode.getPath().equals(folder.getPath())) {
            currentNode.setLabel(folder.getName());
            currentNode.setPath(folder.getPath());
            try {
                configurationDao.saveConfig();
            } catch (IOException e) {
                throw new BackendException(e);
            }
            // Post Event
            eventBus.post(new ConfigurationEvent(UPDATE_FOLDER, currentNode, rootNode));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFolder(final String id, final RootNode rootNode) {
        List<ConfigurationNode> configNodes = configurationDao.getNodes(rootNode);
        ConfigurationNode currentNode = findConfigurationNode(id, configNodes, rootNode == RootNode.PODCAST);

        // Remove node
        configNodes.remove(currentNode);
        try {
            // Save config
            configurationDao.saveConfig();
        } catch (IOException e) {
            throw new BackendException(e);
        }
        // Post Event
        eventBus.post(new ConfigurationEvent(DELETE_FOLDER, currentNode, rootNode));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Settings getSettings() {
        return new Settings(configurationDao.getParameter(UPNP_SERVER_NAME),
                configurationDao.getParameter(PODCAST_PREPEND_ENTRY_NAME),
                configurationDao.getParameter(ICECAST_ENABLE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings(final Settings settings) {
        checkNonEmpty(settings.getServerName(), "backend.settings.server.name.error");

        configurationDao.setParameter(UPNP_SERVER_NAME, settings.getServerName());
        configurationDao.setParameter(PODCAST_PREPEND_ENTRY_NAME, settings.getPrependPodcastItem());
        configurationDao.setParameter(ICECAST_ENABLE, settings.getEnableIcecastDirectory());
        try {
            // save settings
            configurationDao.saveConfig();
        } catch (IOException e) {
            throw new BackendException(e);
        }
        // Post event
        eventBus.post(new ConfigurationEvent(SAVE_SETTINGS));
    }

    /**
     * Validate folder.
     *
     * @param folder      folder to validate
     * @param configNodes existing folders
     * @param excludedId  folder id excluded from duplication checking
     */
    private void validateFolder(final ConfigurationFolder folder, final List<ConfigurationNode> configNodes, final String excludedId) {
        // Check folder's name and path are not empty
        checkNonEmpty(folder.getName(), "backend.folder.name.error");
        checkNonEmpty(folder.getPath(), "backend.folder.path.error");

        // Check folder path exists
        if (!isValidDirectory(folder.getPath()))
            throw new BackendException("backend.folder.path.unknown.error");

        // Check for duplication
        checkDuplicatedConfigurationFolder(folder, configNodes, excludedId, "backend.folder.already.exist.error");
    }

    /**
     * Validate podcast.
     *
     * @param podcast     podcast to validate
     * @param configNodes existing podcasts
     * @param excludedId  podcast id excluded from duplication check
     */
    private void validatePodcast(final ConfigurationFolder podcast, final List<ConfigurationNode> configNodes, final String excludedId) {
        // Check podcast name and path are not empty
        checkNonEmpty(podcast.getName(), "backend.podcast.name.error");
        checkNonEmpty(podcast.getPath(), "backend.podcast.url.error");

        // Check podcast URL is correct
        if (!URL_PATTERN.matcher(podcast.getPath()).matches())
            throw new BackendException("backend.podcast.url.malformed.error");

        // Check for duplication
        checkDuplicatedConfigurationFolder(podcast, configNodes, excludedId, "backend.podcast.already.exist.error");
    }

    /**
     * Find configuration node.
     *
     * @param id          node id
     * @param configNodes existing config nodes
     * @param podcast     podcast or not
     * @return configuration node
     */
    private ConfigurationNode findConfigurationNode(String id, List<ConfigurationNode> configNodes, boolean podcast) {
        for (ConfigurationNode node : configNodes)
            if (node.getId().equals(id)) return node;

        throw new BackendException(podcast ? "backend.podcast.unknown.error" : "backend.folder.unknown.error");
    }

    /**
     * Checks string is not null or empty.
     *
     * @param toCheck      string to check
     * @param errorMessage error message
     */
    private void checkNonEmpty(String toCheck, String errorMessage) {
        if (Strings.isNullOrEmpty(toCheck)) throw new BackendException(errorMessage);
    }

    /**
     * Checks configuration does not already exist.
     *
     * @param folder       configuration folder to check
     * @param configNodes  existing configuration nodes
     * @param excludedId   folder id to exclude from duplication check
     * @param errorMessage error message
     */
    private void checkDuplicatedConfigurationFolder(final ConfigurationFolder folder, final List<ConfigurationNode> configNodes, final String excludedId, final String errorMessage) {
        for (ConfigurationNode node : configNodes) {
            if (excludedId != null && excludedId.equals(node.getId())) continue;
            if (node.getLabel().equals(folder.getName()) || node.getPath().equals(folder.getPath()))
                throw new BackendException(errorMessage);
        }
    }
}
