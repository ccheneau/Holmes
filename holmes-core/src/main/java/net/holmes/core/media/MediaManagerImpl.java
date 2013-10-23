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

package net.holmes.core.media;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import net.holmes.core.common.event.MediaEvent;
import net.holmes.core.media.dao.MediaDao;
import net.holmes.core.media.model.AbstractNode;
import net.holmes.core.media.model.FolderNode;
import net.holmes.core.media.model.RootNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.ResourceBundle;

import static net.holmes.core.media.model.RootNode.ROOT;

/**
 * Media manager implementation.
 */
public final class MediaManagerImpl implements MediaManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MediaManagerImpl.class);
    private final ResourceBundle resourceBundle;
    private final MediaDao mediaDao;

    /**
     * Instantiates a new media manager implementation.
     *
     * @param resourceBundle resource bundle
     * @param mediaDao       media dao
     */
    @Inject
    public MediaManagerImpl(final ResourceBundle resourceBundle, final MediaDao mediaDao) {
        this.resourceBundle = resourceBundle;
        this.mediaDao = mediaDao;
    }

    @Override
    public AbstractNode getNode(final String nodeId) {
        AbstractNode node = null;
        RootNode rootNode = RootNode.getById(nodeId);
        if (rootNode != RootNode.NONE)
            // Root node
            node = new FolderNode(rootNode.getId(), rootNode.getParentId(), resourceBundle.getString("rootNode." + rootNode.getId()));
        else if (nodeId != null)
            node = mediaDao.getNode(nodeId);

        return node;
    }

    @Override
    public List<AbstractNode> getChildNodes(final AbstractNode parentNode) {
        List<AbstractNode> childNodes;
        RootNode rootNode = RootNode.getById(parentNode.getId());
        if (rootNode == ROOT) {
            // Get child nodes of root node
            childNodes = Lists.newArrayList();
            for (RootNode aRootNode : RootNode.values()) {
                if (aRootNode.getParentId().equals(ROOT.getId()))
                    addRootNode(childNodes, aRootNode);
            }
        } else if (rootNode.getParentId().equals(ROOT.getId()))
            // Child nodes are stored in configuration
            childNodes = mediaDao.getSubRootChildNodes(rootNode);
        else
            // Get child nodes
            childNodes = mediaDao.getChildNodes(parentNode.getId());

        return childNodes;
    }

    /**
     * Adds the root node.
     *
     * @param childNodes the child nodes
     * @param rootNode   the root node
     */
    private void addRootNode(List<AbstractNode> childNodes, final RootNode rootNode) {
        if (!mediaDao.getSubRootChildNodes(rootNode).isEmpty())
            childNodes.add(new FolderNode(rootNode.getId(), rootNode.getParentId(), resourceBundle.getString(rootNode.getBundleKey())));
    }

    /**
     * Scan all configuration nodes
     */
    @Override
    public void scanAll() {
        AbstractNode rootNode = getNode(RootNode.ROOT.getId());
        scanNode(rootNode);
    }

    /**
     * Scan a specific node and its children
     *
     * @param node node to scan
     */
    private void scanNode(final AbstractNode node) {
        if (node instanceof FolderNode) {
            List<AbstractNode> childNodes = getChildNodes(node);
            if (childNodes != null)
                for (AbstractNode childNode : childNodes)
                    scanNode(childNode);
        }
    }

    /**
     * Handle media event.
     *
     * @param mediaEvent media event
     */
    @Subscribe
    public void handleMediaEvent(final MediaEvent mediaEvent) {
        switch (mediaEvent.getType()) {
            case SCAN_ALL:
                scanAll();
                break;
            case SCAN_NODE:
                AbstractNode node = getNode(mediaEvent.getParameter());
                if (node != null) scanNode(node);
                break;
            default:
                LOGGER.error("Unknown event");
                break;
        }
    }
}
