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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.holmes.core.business.media.model.RootNode;

import java.io.IOException;
import java.util.List;

/**
 * Abstract class for configuration DAO
 */
public abstract class AbstractConfigurationDao implements ConfigurationDao {

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean addNode(final RootNode rootNode, final ConfigurationNode node) throws IOException {
        boolean saved = false;

        List<ConfigurationNode> nodes = getNodes(rootNode);

        // Search for duplicate node
        ConfigurationNode existingNode = Iterables.find(nodes, new Predicate<ConfigurationNode>() {
            @Override
            public boolean apply(ConfigurationNode aNode) {
                return node.equals(aNode);
            }
        }, null);

        if (existingNode == null) {
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
    public final ConfigurationNode editNode(RootNode rootNode, String nodeId, String label, String path) throws IOException, UnknownNodeException {
        // Get node to edit
        ConfigurationNode currentNode = getNode(rootNode, nodeId);

        if (!currentNode.getLabel().equals(label) || !currentNode.getPath().equals(path)) {
            // Update node
            currentNode.setLabel(label);
            currentNode.setPath(path);

            // Save configuration
            save();

            return currentNode;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ConfigurationNode removeNode(String nodeId, RootNode rootNode) throws IOException, UnknownNodeException {
        // Get node
        ConfigurationNode node = getNode(rootNode, nodeId);

        // Remove node
        getNodes(rootNode).remove(node);

        // Save configuration
        save();

        return node;
    }
}
