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

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import net.holmes.core.backend.backbone.response.ConfigurationFolder;
import net.holmes.core.configuration.Configuration;
import net.holmes.core.configuration.ConfigurationNode;

import com.google.common.collect.Lists;

public final class BackboneManagerImpl implements BackboneManager {
    private final Configuration configuration;

    @Inject
    public BackboneManagerImpl(Configuration configuration) {
        this.configuration = configuration;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.backend.backbone.BackboneManager#getFolders(java.util.List)
     */
    @Override
    public Collection<ConfigurationFolder> getFolders(List<ConfigurationNode> configNodes) {
        Collection<ConfigurationFolder> folders = Lists.newArrayList();
        for (ConfigurationNode node : configNodes) {
            folders.add(new ConfigurationFolder(node.getId(), node.getLabel(), node.getPath()));
        }
        return folders;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.backend.backbone.BackboneManager#getFolder(java.lang.String, java.util.List)
     */
    @Override
    public ConfigurationFolder getFolder(String id, List<ConfigurationNode> configNodes) {
        //TODO validation
        for (ConfigurationNode node : configNodes) {
            if (node.getId().equals(id)) return new ConfigurationFolder(node.getId(), node.getLabel(), node.getPath());
        }
        return null;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.backend.backbone.BackboneManager#addFolder(net.holmes.core.backend.response.backbone.ConfigurationFolder, java.util.List)
     */
    @Override
    public void addFolder(ConfigurationFolder folder, List<ConfigurationNode> configNodes) {
        //TODO validation
        folder.setId(UUID.randomUUID().toString());
        configNodes.add(new ConfigurationNode(folder.getId(), folder.getName(), folder.getPath()));
        configuration.saveConfig();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.backend.backbone.BackboneManager#editFolder(java.lang.String, net.holmes.core.backend.response.backbone.ConfigurationFolder, java.util.List)
     */
    @Override
    public void editFolder(String id, ConfigurationFolder folder, List<ConfigurationNode> configNodes) {
        //TODO validation
        ConfigurationNode currentNode = null;
        for (ConfigurationNode node : configNodes) {
            if (node.getId().equals(id)) {
                currentNode = node;
                break;
            }
        }
        if (currentNode != null) {
            currentNode.setLabel(folder.getName());
            currentNode.setPath(folder.getPath());
            configuration.saveConfig();
        }
    }

    /* (non-Javadoc)
     * @see net.holmes.core.backend.backbone.BackboneManager#removeFolder(java.lang.String, java.util.List)
     */
    @Override
    public void removeFolder(String id, List<ConfigurationNode> configNodes) {
        //TODO validation
        ConfigurationNode currentNode = null;
        for (ConfigurationNode node : configNodes) {
            if (node.getId().equals(id)) {
                currentNode = node;
                break;
            }
        }
        if (currentNode != null) {
            configNodes.remove(currentNode);
            configuration.saveConfig();
        }
    }

}
