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

import net.holmes.core.business.configuration.exception.UnknownNodeException;
import net.holmes.core.business.configuration.model.ConfigurationNode;
import net.holmes.core.business.media.model.RootNode;
import net.holmes.core.common.ConfigurationParameter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Configuration manager
 */
public interface ConfigurationManager {

    /**
     * Gets the root node child configuration nodes.
     *
     * @param rootNode root node
     * @return configuration nodes
     */
    List<ConfigurationNode> getNodes(RootNode rootNode);

    /**
     * Gets the configuration node.
     *
     * @param rootNode root node
     * @param nodeId   node ID to find
     * @return configuration node
     * @throws UnknownNodeException if node is not found
     */
    ConfigurationNode getNode(RootNode rootNode, String nodeId) throws UnknownNodeException;

    /**
     * Find configuration node with same label or path.
     *
     * @param rootNode       root node
     * @param excludedNodeId exclude this node ID from search (can be null)
     * @param label          node label to search
     * @param path           node path to search
     * @return optional configuration node with same label or path
     */
    Optional<ConfigurationNode> findNode(RootNode rootNode, String excludedNodeId, String label, String path);

    /**
     * Add configuration node.
     *
     * @param rootNode root node
     * @param node     node to add
     * @return true if node is added
     * @throws IOException Signals that an I/O exception has occurred
     */
    boolean addNode(RootNode rootNode, ConfigurationNode node) throws IOException;

    /**
     * Edit configuration node.
     *
     * @param rootNode root node
     * @param nodeId   node ID to edit
     * @param label    new node label
     * @param path     new node path
     * @return optional updated configuration node or empty if node is not modified
     * @throws IOException          Signals that an I/O exception has occurred
     * @throws UnknownNodeException Signals configuration node was not found
     */
    Optional<ConfigurationNode> editNode(RootNode rootNode, String nodeId, String label, String path) throws IOException, UnknownNodeException;

    /**
     * Save configuration.
     *
     * @throws IOException Signals that an I/O exception has occurred
     */
    void save() throws IOException;

    /**
     * Remove node.
     *
     * @param nodeId   node ID to remove
     * @param rootNode root node
     * @return removed node
     * @throws IOException          Signals that an I/O exception has occurred
     * @throws UnknownNodeException Signals configuration node was not found
     */
    ConfigurationNode removeNode(String nodeId, RootNode rootNode) throws IOException, UnknownNodeException;

    /**
     * Get parameter value.
     *
     * @param parameter parameter to get
     * @return parameter value
     */
    <T> T getParameter(ConfigurationParameter<T> parameter);

    /**
     * Sets parameter value.
     *
     * @param parameter parameter to set
     * @param value     parameter value
     */
    <T> void setParameter(ConfigurationParameter<T> parameter, T value);
}
