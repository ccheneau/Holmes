/*
 * Copyright (C) 2012-2015  Cedric Cheneau
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

package net.holmes.core.business.configuration;

import net.holmes.core.business.configuration.dao.ConfigurationDao;
import net.holmes.core.business.configuration.exception.UnknownNodeException;
import net.holmes.core.business.configuration.model.ConfigurationNode;
import net.holmes.core.business.media.model.RootNode;
import net.holmes.core.common.ConfigurationParameter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Configuration manager implementation.
 */
@Singleton
public class ConfigurationManagerImpl implements ConfigurationManager {

    private final ConfigurationDao configurationDao;

    /**
     * Instantiates a new configuration manager implementation.
     *
     * @param configurationDao configuration DAO
     */
    @Inject
    public ConfigurationManagerImpl(ConfigurationDao configurationDao) {
        this.configurationDao = configurationDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ConfigurationNode> getNodes(final RootNode rootNode) {
        return configurationDao.getNodes(rootNode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigurationNode getNode(final RootNode rootNode, final String nodeId) throws UnknownNodeException {
        return configurationDao.getNode(rootNode, nodeId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ConfigurationNode> findNode(final RootNode rootNode, final String excludedNodeId, final String label, final String path) {
        return configurationDao.findNode(rootNode, excludedNodeId, label, path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addNode(final RootNode rootNode, final ConfigurationNode node) throws IOException {
        boolean saved = false;

        List<ConfigurationNode> nodes = getNodes(rootNode);

        // Search for duplicate node
        if (nodes.stream().noneMatch(node::equals)) {
            // Add node
            nodes.add(node);
            // Save configuration
            save();
            saved = true;
        }
        return saved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ConfigurationNode> editNode(final RootNode rootNode, final String nodeId, final String label, final String path) throws IOException, UnknownNodeException {
        // Get node to edit
        ConfigurationNode currentNode = getNode(rootNode, nodeId);

        if (!currentNode.getLabel().equals(label) || !currentNode.getPath().equals(path)) {
            // Update node
            currentNode.setLabel(label);
            currentNode.setPath(path);

            // Save configuration
            save();

            return Optional.of(currentNode);
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save() throws IOException {
        configurationDao.save();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigurationNode removeNode(final String nodeId, final RootNode rootNode) throws IOException, UnknownNodeException {
        // Get node
        ConfigurationNode node = getNode(rootNode, nodeId);

        // Remove node
        getNodes(rootNode).remove(node);

        // Save configuration
        save();

        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getParameter(final ConfigurationParameter<T> parameter) {
        return configurationDao.getParameter(parameter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void setParameter(final ConfigurationParameter<T> parameter, final T value) {
        configurationDao.setParameter(parameter, value);
    }
}
