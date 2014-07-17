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

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import net.holmes.core.backend.exception.BackendErrorMessage;
import net.holmes.core.backend.exception.BackendException;
import net.holmes.core.backend.response.ConfigurationFolder;
import net.holmes.core.backend.response.Settings;
import net.holmes.core.business.configuration.ConfigurationDao;
import net.holmes.core.business.configuration.ConfigurationNode;
import net.holmes.core.business.configuration.UnknownNodeException;
import net.holmes.core.business.media.model.RootNode;
import net.holmes.core.common.event.ConfigurationEvent;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static net.holmes.core.backend.exception.BackendErrorMessage.*;
import static net.holmes.core.common.ConfigurationParameter.*;
import static net.holmes.core.common.FileUtils.isValidDirectory;
import static net.holmes.core.common.UniqueIdGenerator.newUniqueId;
import static net.holmes.core.common.event.ConfigurationEvent.EventType.*;

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
        return Lists.transform(configurationDao.getNodes(rootNode), new ConfigurationNodeFactory());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigurationFolder getFolder(final String id, final RootNode rootNode) {
        return new ConfigurationNodeFactory().apply(findConfigurationNode(id, rootNode));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFolder(final ConfigurationFolder folder, final RootNode rootNode) {
        List<ConfigurationNode> configNodes = configurationDao.getNodes(rootNode);

        // Validate
        if (rootNode == RootNode.PODCAST) {
            validatePodcast(folder, configNodes, null);
        } else {
            validateFolder(folder, configNodes, null);
        }

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

        // Check folder
        if (rootNode == RootNode.PODCAST) {
            validatePodcast(folder, configNodes, id);
        } else {
            validateFolder(folder, configNodes, id);
        }

        // Save config if name or path has changed
        ConfigurationNode currentNode = findConfigurationNode(id, rootNode);
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
        ConfigurationNode currentNode = findConfigurationNode(id, rootNode);
        List<ConfigurationNode> configNodes = configurationDao.getNodes(rootNode);

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
        checkNonEmpty(settings.getServerName(), SETTINGS_SERVER_NAME_ERROR);

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
        checkNonEmpty(folder.getName(), FOLDER_NAME_ERROR);
        checkNonEmpty(folder.getPath(), FOLDER_PATH_ERROR);

        // Check folder path exists
        if (!isValidDirectory(folder.getPath())) {
            throw new BackendException(FOLDER_PATH_UNKNOWN_ERROR);
        }

        // Check for duplication
        checkDuplicatedConfigurationFolder(folder, configNodes, excludedId, FOLDER_DUPLICATED_ERROR);
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
        checkNonEmpty(podcast.getName(), PODCAST_NAME_ERROR);
        checkNonEmpty(podcast.getPath(), PODCAST_URL_ERROR);

        // Check podcast URL is correct
        if (!URL_PATTERN.matcher(podcast.getPath()).matches()) {
            throw new BackendException(PODCAST_BAD_URL_ERROR);
        }

        // Check for duplication
        checkDuplicatedConfigurationFolder(podcast, configNodes, excludedId, PODCAST_DUPLICATED_ERROR);
    }

    /**
     * Find configuration node.
     *
     * @param id       node id
     * @param rootNode root configuration node
     * @return configuration node
     */
    private ConfigurationNode findConfigurationNode(String id, RootNode rootNode) {
        try {
            return configurationDao.getNode(rootNode, id);
        } catch (UnknownNodeException e) {
            throw new BackendException(rootNode == RootNode.PODCAST ? PODCAST_UNKNOWN_ERROR : FOLDER_UNKNOWN_ERROR, e);
        }
    }

    /**
     * Checks string is not null or empty.
     *
     * @param toCheck string to check
     * @param message error message
     */
    private void checkNonEmpty(String toCheck, BackendErrorMessage message) {
        if (Strings.isNullOrEmpty(toCheck)) {
            throw new BackendException(message);
        }
    }

    /**
     * Checks configuration does not already exist.
     *
     * @param folder       configuration folder to check
     * @param configNodes  existing configuration nodes
     * @param excludedId   folder id to exclude from duplication check
     * @param errorMessage error message
     */
    private void checkDuplicatedConfigurationFolder(final ConfigurationFolder folder, final List<ConfigurationNode> configNodes, final String excludedId, final BackendErrorMessage errorMessage) {
        for (ConfigurationNode node : configNodes) {
            if (excludedId != null && excludedId.equals(node.getId())) {
                continue;
            }
            if (node.getLabel().equals(folder.getName()) || node.getPath().equals(folder.getPath())) {
                throw new BackendException(errorMessage);
            }
        }
    }

    /**
     *
     */
    private static final class ConfigurationNodeFactory implements Function<ConfigurationNode, ConfigurationFolder> {

        /**
         * {@inheritDoc}
         */
        @Override
        public ConfigurationFolder apply(ConfigurationNode node) {
            return node == null ? null : new ConfigurationFolder(node.getId(), node.getLabel(), node.getPath());
        }
    }
}
