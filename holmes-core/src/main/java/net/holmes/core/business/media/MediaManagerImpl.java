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

package net.holmes.core.business.media;

import com.google.common.eventbus.Subscribe;
import net.holmes.core.business.configuration.ConfigurationManager;
import net.holmes.core.business.media.dao.MediaDao;
import net.holmes.core.business.media.model.FolderNode;
import net.holmes.core.business.media.model.MediaNode;
import net.holmes.core.business.media.model.MimeTypeNode;
import net.holmes.core.business.media.model.RootNode;
import net.holmes.core.business.mimetype.MimeTypeManager;
import net.holmes.core.common.event.MediaEvent;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.net.InetAddress;
import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
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
    public Optional<MediaNode> getNode(final String nodeId) {
        Optional<MediaNode> node = Optional.empty();
        RootNode rootNode = getById(nodeId);
        if (rootNode != NONE) {
            // Get Root node
            node = Optional.of(new FolderNode(rootNode.getId(), rootNode.getParentId(), resourceBundle.getString("rootNode." + rootNode.getId())));
        } else if (nodeId != null) {
            node = mediaDao.getNode(nodeId);
        }

        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNodeUrl(final MediaNode node) {
        return "http://" + localAddress.getHostAddress() + ":" + httpServerPort +
                HTTP_CONTENT_REQUEST_PATH + "?" + HTTP_CONTENT_ID + "=" + node.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<MediaNode> searchChildNodes(final MediaSearchRequest request) {
        List<MediaNode> childNodes;
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
        Predicate<MediaNode> p = node -> !(node instanceof MimeTypeNode)
                || mimeTypeManager.isMimeTypeCompliant(((MimeTypeNode) node).getMimeType(), request.getAvailableMimeTypes());

        return childNodes.stream().filter(p).collect(toList());
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
            getNode(mediaEvent.getParameter()).ifPresent(this::scanNode);
        } else {
            LOGGER.error("Unknown media event {}", mediaEvent);
        }
    }

    /**
     * Scan a specific node and its children
     *
     * @param node node to scan
     */
    private void scanNode(final MediaNode node) {
        if (node instanceof FolderNode) {
            searchChildNodes(new MediaSearchRequest(node, null)).forEach(this::scanNode);
        }
    }
}
