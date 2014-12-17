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

package net.holmes.core.business.media;

import com.google.common.base.Predicate;
import com.google.common.eventbus.Subscribe;
import net.holmes.core.business.configuration.ConfigurationManager;
import net.holmes.core.business.media.dao.MediaDao;
import net.holmes.core.business.media.model.AbstractNode;
import net.holmes.core.business.media.model.FolderNode;
import net.holmes.core.business.media.model.MimeTypeNode;
import net.holmes.core.business.media.model.RootNode;
import net.holmes.core.business.mimetype.MimeTypeManager;
import net.holmes.core.common.event.MediaEvent;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import static com.google.common.collect.Collections2.filter;
import static net.holmes.core.business.media.model.RootNode.*;
import static net.holmes.core.common.ConfigurationParameter.HTTP_SERVER_PORT;
import static net.holmes.core.common.Constants.*;
import static net.holmes.core.common.event.MediaEvent.MediaEventType.SCAN_NODE;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Media manager implementation.
 */
@Singleton
public final class MediaManagerImpl implements MediaManager {
    private static final Logger LOGGER = getLogger(MediaManagerImpl.class);
    private final ResourceBundle resourceBundle;
    private final MediaDao mediaDao;
    private final MimeTypeManager mimeTypeManager;
    private final InetAddress localAddress;
    private final Integer httpServerPort;

    /**
     * Instantiates a new media manager implementation.
     *
     * @param configurationManager configuration manager
     * @param resourceBundle       resource bundle
     * @param mediaDao             media dao
     * @param mimeTypeManager      mime type manager
     * @param localAddress         local IP address
     */
    @Inject
    public MediaManagerImpl(final ConfigurationManager configurationManager, final ResourceBundle resourceBundle, final MediaDao mediaDao,
                            final MimeTypeManager mimeTypeManager, @Named("localAddress") final InetAddress localAddress) {
        this.resourceBundle = resourceBundle;
        this.mediaDao = mediaDao;
        this.mimeTypeManager = mimeTypeManager;
        this.localAddress = localAddress;
        this.httpServerPort = configurationManager.getParameter(HTTP_SERVER_PORT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractNode getNode(final String nodeId) {
        AbstractNode node = null;
        RootNode rootNode = getById(nodeId);
        if (rootNode != NONE) {
            // Get Root node
            node = new FolderNode(rootNode.getId(), rootNode.getParentId(), resourceBundle.getString("rootNode." + rootNode.getId()));
        } else if (nodeId != null) {
            node = mediaDao.getNode(nodeId);
        }

        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNodeUrl(final AbstractNode node) {
        return "http://" + localAddress.getHostAddress() + ":" + httpServerPort +
                HTTP_CONTENT_REQUEST_PATH + "?" + HTTP_CONTENT_ID + "=" + node.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<AbstractNode> searchChildNodes(final MediaSearchRequest request) {
        List<AbstractNode> childNodes;
        RootNode rootNode = getById(request.getParentNode().getId());
        if (rootNode == ROOT) {
            // Get child nodes of root node
            RootNode[] rootNodes = RootNode.values();
            childNodes = new ArrayList<>(rootNodes.length);
            for (RootNode subRootNode : rootNodes) {
                if (subRootNode.getParentId().equals(ROOT.getId()) && !mediaDao.getRootNodeChildren(subRootNode).isEmpty()) {
                    childNodes.add(new FolderNode(subRootNode.getId(), ROOT.getId(), resourceBundle.getString(subRootNode.getBundleKey())));
                }
            }
        } else if (rootNode.getParentId().equals(ROOT.getId())) {
            // Get child nodes of sub root node
            childNodes = mediaDao.getRootNodeChildren(rootNode);
        } else {
            // Get child nodes
            childNodes = mediaDao.getChildNodes(request.getParentNode().getId());
        }

        // Filter child nodes according to available mime types
        return filter(childNodes, new Predicate<AbstractNode>() {
            /**
             * {@inheritDoc}
             */
            @Override
            public boolean apply(AbstractNode node) {
                return !(node instanceof MimeTypeNode)
                        || mimeTypeManager.isMimeTypeCompliant(((MimeTypeNode) node).getMimeType(), request.getAvailableMimeTypes());

            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanUpCache() {
        mediaDao.cleanUpCache();
    }

    /**
     * Handle media event.
     *
     * @param mediaEvent media event
     */
    @Subscribe
    public void handleMediaEvent(final MediaEvent mediaEvent) {
        if (mediaEvent.getType() == SCAN_NODE) {
            scanNode(getNode(mediaEvent.getParameter()));
        } else {
            LOGGER.error("Unknown media event {}", mediaEvent);
        }
    }

    /**
     * Scan a specific node and its children
     *
     * @param node node to scan
     */
    private void scanNode(final AbstractNode node) {
        if (node instanceof FolderNode) {
            Collection<AbstractNode> result = searchChildNodes(new MediaSearchRequest(node, null));
            for (AbstractNode childNode : result) {
                scanNode(childNode);
            }
        }
    }
}
