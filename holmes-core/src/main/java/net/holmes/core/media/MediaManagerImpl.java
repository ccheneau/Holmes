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

package net.holmes.core.media;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import net.holmes.core.common.event.MediaEvent;
import net.holmes.core.common.mimetype.MimeTypeManager;
import net.holmes.core.media.dao.MediaDao;
import net.holmes.core.media.model.AbstractNode;
import net.holmes.core.media.model.FolderNode;
import net.holmes.core.media.model.MimeTypeNode;
import net.holmes.core.media.model.RootNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
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
    private final MimeTypeManager mimeTypeManager;

    /**
     * Instantiates a new media manager implementation.
     *
     * @param resourceBundle  resource bundle
     * @param mediaDao        media dao
     * @param mimeTypeManager mime type manager
     */
    @Inject
    public MediaManagerImpl(final ResourceBundle resourceBundle, final MediaDao mediaDao, MimeTypeManager mimeTypeManager) {
        this.resourceBundle = resourceBundle;
        this.mediaDao = mediaDao;
        this.mimeTypeManager = mimeTypeManager;
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
    public ChildNodeResult getChildNodes(final ChildNodeRequest request) {
        List<AbstractNode> childNodes;
        RootNode rootNode = RootNode.getById(request.getParentNode().getId());
        if (rootNode == ROOT) {
            // Get child nodes of root node
            childNodes = Lists.newArrayList();
            for (RootNode subRootNode : RootNode.values()) {
                if (subRootNode.getParentId().equals(ROOT.getId()) && !mediaDao.getSubRootChildNodes(subRootNode).isEmpty())
                    childNodes.add(new FolderNode(subRootNode.getId(), ROOT.getId(), resourceBundle.getString(subRootNode.getBundleKey())));
            }
        } else if (rootNode.getParentId().equals(ROOT.getId()))
            // Get child nodes of sub root node
            childNodes = mediaDao.getSubRootChildNodes(rootNode);
        else
            // Get child nodes
            childNodes = mediaDao.getChildNodes(request.getParentNode().getId());

        // Filter child nodes according to available mime types
        Collection<AbstractNode> result = Collections2.filter(childNodes, new MimeTypeFilter(request.getAvailableMimeTypes()));
        return new ChildNodeResult(result, result.size());
    }

    /**
     * Scan a specific node and its children
     *
     * @param node node to scan
     */
    private void scanNode(final AbstractNode node) {
        if (node instanceof FolderNode) {
            ChildNodeResult result = getChildNodes(new ChildNodeRequest(node));
            if (result.getChildNodes() != null)
                for (AbstractNode childNode : result.getChildNodes())
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
            case SCAN_NODE:
                AbstractNode node = getNode(mediaEvent.getParameter());
                if (node != null) scanNode(node);
                break;
            default:
                LOGGER.error("Unknown event");
                break;
        }
    }

    /**
     * Filter nodes according to available mime types.
     */
    private final class MimeTypeFilter implements Predicate<AbstractNode> {
        private final List<String> availableMimeTypes;

        MimeTypeFilter(final List<String> availableMimeTypes) {
            this.availableMimeTypes = availableMimeTypes;
        }

        @Override
        public boolean apply(final AbstractNode node) {
            return !(node instanceof MimeTypeNode)
                    || mimeTypeManager.isMimeTypeCompliant(((MimeTypeNode) node).getMimeType(), availableMimeTypes);
        }
    }
}
