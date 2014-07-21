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

package net.holmes.core.test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.holmes.core.business.configuration.AbstractConfigurationDao;
import net.holmes.core.business.configuration.ConfigurationNode;
import net.holmes.core.business.configuration.UnknownNodeException;
import net.holmes.core.business.media.model.RootNode;
import net.holmes.core.common.ConfigurationParameter;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class TestConfigurationDao extends AbstractConfigurationDao {

    private final List<ConfigurationNode> videoFolders;
    private final List<ConfigurationNode> pictureFolders;
    private final List<ConfigurationNode> audioFolders;
    private final List<ConfigurationNode> podcasts;
    private Map<String, String> parameters;

    @Inject
    @SuppressWarnings("unchecked")
    public TestConfigurationDao() {
        videoFolders = Lists.newArrayList(getTestContentFolder("videosTest", "/videosTest/"));
        audioFolders = Lists.newArrayList(getTestContentFolder("audiosTest", "/audiosTest/"));
        pictureFolders = Lists.newArrayList(getTestContentFolder("imagesTest", "/imagesTest/"));
        podcasts = Lists.newArrayList();
        podcasts.add(new ConfigurationNode("fauxRaccordsTest", "fauxRaccordsTest", this.getClass().getResource("/allocineFauxRaccordRss.xml").toString()));
        parameters = Maps.newHashMap();
        for (ConfigurationParameter parameter : ConfigurationParameter.PARAMETERS) {
            parameters.put(parameter.getName(), parameter.format(parameter.getDefaultValue()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save() throws IOException {
        // Nothing
    }

    private ConfigurationNode getTestContentFolder(String label, String path) {
        ConfigurationNode contentFolder = null;

        URL rs = this.getClass().getResource(path);
        if (rs != null) {
            String filePath = new File(rs.getFile()).getAbsolutePath();
            contentFolder = new ConfigurationNode(label, label, filePath);
        }

        return contentFolder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ConfigurationNode> getNodes(RootNode rootNode) {
        List<ConfigurationNode> folders;
        switch (rootNode) {
            case AUDIO:
                folders = this.audioFolders;
                break;
            case PICTURE:
                folders = this.pictureFolders;
                break;
            case PODCAST:
                folders = this.podcasts;
                break;
            case VIDEO:
                folders = this.videoFolders;
                break;
            default:
                folders = Lists.newArrayList();
                break;
        }
        return folders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigurationNode getNode(final RootNode rootNode, final String nodeId) throws UnknownNodeException {
        try {
            return Iterables.find(getNodes(rootNode), new Predicate<ConfigurationNode>() {
                @Override
                public boolean apply(ConfigurationNode node) {
                    return node.getId().equals(nodeId);
                }
            });
        } catch (NoSuchElementException e) {
            throw new UnknownNodeException(nodeId);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigurationNode findNode(RootNode rootNode, final String excludedNodeId, final String label, final String path) {
        return Iterables.find(getNodes(rootNode), new Predicate<ConfigurationNode>() {
            @Override
            public boolean apply(ConfigurationNode node) {
                if (excludedNodeId != null && excludedNodeId.equals(node.getId())) {
                    return false;
                } else if (node.getLabel().equals(label) || node.getPath().equals(path)) {
                    return true;
                }
                return false;
            }
        }, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getParameter(ConfigurationParameter<T> parameter) {
        return parameter.parse(parameters.get(parameter.getName()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void setParameter(ConfigurationParameter<T> parameter, T value) {
        parameters.put(parameter.getName(), parameter.format(value));
    }
}
