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

package net.holmes.core.business.configuration;

import net.holmes.core.business.media.model.RootNode;
import net.holmes.core.common.ConfigurationParameter;

import java.io.IOException;
import java.util.List;

/**
 * Holmes configuration dao.
 */
public interface ConfigurationDao {

    /**
     * Gets the configuration nodes.
     *
     * @param rootNode root node
     * @return configuration nodes
     */
    List<ConfigurationNode> getNodes(RootNode rootNode);

    /**
     * Gets the configuration node.
     *
     * @param rootNode root node
     * @param nodeId   node ID
     * @return configuration node
     * @throws UnknownNodeException if node is not found
     */
    ConfigurationNode getNode(RootNode rootNode, String nodeId) throws UnknownNodeException;

    /**
     * Find configuration node with same label or path.
     *
     * @param rootNode       root node
     * @param excludedNodeId exclude this node id from search (can be null)
     * @param label          node label to search
     * @param path           node path to search
     * @return null or configuration node with same label or path
     */
    ConfigurationNode findNode(RootNode rootNode, String excludedNodeId, String label, String path);

    /**
     * Remove node.
     *
     * @param rootNode root node
     * @param node     node to add
     * @return true if node is added
     * @throws IOException
     */
    boolean addNode(RootNode rootNode, ConfigurationNode node) throws IOException;

    /**
     * Find configuration node with same label or path.
     *
     * @param rootNode root node
     * @param nodeId   exclude this node id to edit
     * @param label    new node label
     * @param path     new node path
     * @return edited configuration node
     * @throws IOException
     * @throws UnknownNodeException
     */
    ConfigurationNode editNode(RootNode rootNode, String nodeId, String label, String path) throws IOException, UnknownNodeException;

    /**
     * Save configuration.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    void save() throws IOException;

    /**
     * Remove node.
     *
     * @param nodeId   Id of node to remove
     * @param rootNode root node
     * @return removed node
     * @throws IOException
     * @throws UnknownNodeException
     */
    ConfigurationNode removeNode(String nodeId, RootNode rootNode) throws IOException, UnknownNodeException;

    /**
     * Get parameter value.
     *
     * @param parameter parameter
     * @return parameter value
     */
    <T> T getParameter(final ConfigurationParameter<T> parameter);

    /**
     * Sets parameter value.
     *
     * @param parameter parameter
     * @param value     parameter value
     */
    <T> void setParameter(final ConfigurationParameter<T> parameter, T value);
}
