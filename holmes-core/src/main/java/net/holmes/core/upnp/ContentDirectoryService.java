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

package net.holmes.core.upnp;

import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.common.configuration.Parameter;
import net.holmes.core.media.MediaManager;
import net.holmes.core.media.model.*;
import org.fourthline.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryErrorCode;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.support.model.SortCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;

import static org.fourthline.cling.support.model.BrowseFlag.DIRECT_CHILDREN;
import static org.fourthline.cling.support.model.BrowseFlag.METADATA;

/**
 * UPnP Content directory service.
 */
public final class ContentDirectoryService extends AbstractContentDirectoryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentDirectoryService.class);
    @Inject
    private MediaManager mediaManager;
    @Inject
    private Configuration configuration;
    @Inject
    @Named("localIP")
    private String localIP;

    /**
     * Instantiates a new content directory service.
     */
    public ContentDirectoryService() {
        super(Arrays.asList("dc:title"), // search caps
                Arrays.asList("dc:title", "dc:date")); // sort caps
    }

    @Override
    public BrowseResult browse(final String objectID, final BrowseFlag browseFlag, final String filter, final long firstResult, final long maxResults,
                               final SortCriterion[] orderBy) throws ContentDirectoryException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("browse  " + browseFlag + " objectId=" + objectID + " firstResult=" + firstResult
                    + " nbResults=" + maxResults);
            LOGGER.debug("filter: {}", filter);
        }


        // Get browse node
        AbstractNode browseNode = mediaManager.getNode(objectID);
        if (browseNode == null) {
            throw new ContentDirectoryException(ContentDirectoryErrorCode.NO_SUCH_OBJECT, objectID);
        }

        DirectoryBrowseResult result;
        if (DIRECT_CHILDREN == browseFlag) {
            result = new DirectoryBrowseResult(firstResult, maxResults);
            // Add child nodes
            List<AbstractNode> childNodes = mediaManager.getChildNodes(browseNode);
            for (AbstractNode childNode : childNodes)
                addNode(objectID, childNode, result, childNodes.size());
        } else if (METADATA == browseFlag) {
            result = new DirectoryBrowseResult(0, 1);
            // Get node
            addNode(browseNode.getParentId(), browseNode, result, 0);
        } else {
            result = new DirectoryBrowseResult(0, 1);
        }

        BrowseResult br = result.buildBrowseResult();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("itemCount:{}", result.getItemCount());
            LOGGER.debug("totalCount:{}", result.getTotalCount());
            LOGGER.debug(br.getResult());
        }
        return br;
    }

    /**
     * Adds node.
     *
     * @param nodeId        node id
     * @param node          node
     * @param result        result
     * @param childNodeSize child node size
     * @throws ContentDirectoryException
     */
    private void addNode(final String nodeId, final AbstractNode node, final DirectoryBrowseResult result, final long childNodeSize) throws ContentDirectoryException {
        if (result.acceptNode()) {
            if (node instanceof ContentNode) {
                // Build content url
                String url = "http://" + localIP + ":" + configuration.getHttpServerPort() + "/content?id=" + node.getId();
                // Add item to result
                result.addItem(nodeId, (ContentNode) node, url);
            } else if (node instanceof FolderNode) {
                // Get child counts
                List<AbstractNode> childNodes = mediaManager.getChildNodes(node);
                // Add container to result
                result.addContainer(nodeId, node, childNodes != null ? childNodes.size() : 0);
            } else if (node instanceof PodcastNode) {
                // Add podcast to result
                result.addContainer(nodeId, node, 1);
            } else if (node instanceof PodcastEntryNode) {
                // Add podcast entry to result
                String entryName = formatPodcastEntryName(result.getResultCount(), childNodeSize, node.getName());
                result.addPodcastItem(nodeId, (PodcastEntryNode) node, entryName);
            }
        }
    }

    /**
     * Get post-cast entry name.
     * If prepend_podcast_entry_name configuration parameter is set to true,
     * item number is added to title.
     *
     * @param count      post-cast entry count
     * @param totalCount post-cast entry total count
     * @param title      title
     * @return post-cast entry name
     */
    private String formatPodcastEntryName(final long count, final long totalCount, final String title) {
        if (configuration.getParameter(Parameter.PREPEND_PODCAST_ENTRY_NAME)) {
            if (totalCount > 99) return String.format("%03d - %s", count + 1, title);
            else return String.format("%02d - %s", count + 1, title);
        } else return title;
    }
}
