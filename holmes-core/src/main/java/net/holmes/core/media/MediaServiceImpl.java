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
import net.holmes.core.common.configuration.Configuration;
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
import javax.inject.Named;
import java.net.InetAddress;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import static net.holmes.core.common.Constants.HTTP_CONTENT_ID;
import static net.holmes.core.common.Constants.HTTP_CONTENT_REQUEST_PATH;
import static net.holmes.core.common.configuration.Parameter.HTTP_SERVER_PORT;
import static net.holmes.core.common.event.MediaEvent.MediaEventType.SCAN_NODE;
import static net.holmes.core.media.model.RootNode.NONE;
import static net.holmes.core.media.model.RootNode.ROOT;

/**
 * Media service implementation.
 */
public final class MediaServiceImpl implements MediaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MediaServiceImpl.class);
    private final Configuration configuration;
    private final ResourceBundle resourceBundle;
    private final MediaDao mediaDao;
    private final MimeTypeManager mimeTypeManager;
    private final InetAddress localAddress;

    /**
     * Instantiates a new media service implementation.
     *
     * @param configuration   configuration
     * @param resourceBundle  resource bundle
     * @param mediaDao        media dao
     * @param mimeTypeManager mime type manager
     */
    @Inject
    public MediaServiceImpl(final Configuration configuration, final ResourceBundle resourceBundle, final MediaDao mediaDao,
                            final MimeTypeManager mimeTypeManager, @Named("localAddress") final InetAddress localAddress) {
        this.configuration = configuration;
        this.resourceBundle = resourceBundle;
        this.mediaDao = mediaDao;
        this.mimeTypeManager = mimeTypeManager;
        this.localAddress = localAddress;
    }

    @Override
    public AbstractNode getNode(final String nodeId) {
        AbstractNode node = null;
        RootNode rootNode = RootNode.getById(nodeId);
        if (rootNode != NONE)
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
            for (RootNode subRootNode : RootNode.values())
                if (subRootNode.getParentId().equals(ROOT.getId()) && !mediaDao.getSubRootChildNodes(subRootNode).isEmpty())
                    childNodes.add(new FolderNode(subRootNode.getId(), ROOT.getId(), resourceBundle.getString(subRootNode.getBundleKey())));

        } else if (rootNode.getParentId().equals(ROOT.getId()))
            // Get child nodes of sub root node
            childNodes = mediaDao.getSubRootChildNodes(rootNode);
        else
            // Get child nodes
            childNodes = mediaDao.getChildNodes(request.getParentNode().getId());

        // Filter child nodes according to available mime types
        Collection<AbstractNode> result = Collections2.filter(childNodes, new MimeTypeFilter(request.getAvailableMimeTypes()));
        return new ChildNodeResult(result);
    }

    @Override
    public String getNodeUrl(AbstractNode node) {
        return "http://" + localAddress.getHostAddress() + ":" + configuration.getIntParameter(HTTP_SERVER_PORT) +
                HTTP_CONTENT_REQUEST_PATH + "?" + HTTP_CONTENT_ID + "=" + node.getId();
    }

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
        if (mediaEvent.getType() == SCAN_NODE)
            scanNode(getNode(mediaEvent.getParameter()));
        else
            LOGGER.error("Unknown media event {}", mediaEvent);
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
