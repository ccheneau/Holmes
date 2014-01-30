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

package net.holmes.core.upnp;

import com.google.common.collect.Lists;
import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.common.configuration.Parameter;
import net.holmes.core.media.MediaService;
import net.holmes.core.media.model.*;
import net.holmes.core.transport.TransportService;
import net.holmes.core.transport.device.Device;
import net.holmes.core.transport.upnp.device.UpnpDevice;
import org.fourthline.cling.model.profile.RemoteClientInfo;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.SortCriterion;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static net.holmes.core.media.MediaService.ChildNodeRequest;
import static net.holmes.core.media.MediaService.ChildNodeResult;
import static net.holmes.core.media.model.AbstractNode.NodeType.TYPE_PODCAST_ENTRY;
import static org.fourthline.cling.model.types.ErrorCode.ACTION_FAILED;
import static org.fourthline.cling.support.contentdirectory.ContentDirectoryErrorCode.NO_SUCH_OBJECT;
import static org.fourthline.cling.support.model.BrowseFlag.DIRECT_CHILDREN;
import static org.fourthline.cling.support.model.BrowseFlag.METADATA;

/**
 * UPnP Content directory service.
 */
public final class ContentDirectoryService extends AbstractContentDirectoryService {
    @Inject
    private Configuration configuration;
    @Inject
    private MediaService mediaService;
    @Inject
    private TransportService transportService;

    /**
     * Instantiates a new content directory service.
     */
    public ContentDirectoryService() {
        super(Arrays.asList("dc:title"), // search caps
                Arrays.asList("dc:title", "dc:date")); // sort caps
    }

    @Override
    public BrowseResult browse(final String objectID, final BrowseFlag browseFlag, final String filter, final long firstResult, final long maxResults,
                               final SortCriterion[] orderBy, final RemoteClientInfo remoteClientInfo) throws ContentDirectoryException {
        // Get available mime types
        List<String> availableMimeTypes = Lists.newArrayList();
        if (remoteClientInfo.getConnection() != null)
            for (Device device : transportService.findDevices(remoteClientInfo.getRemoteAddress().getHostAddress()))
                if (device instanceof UpnpDevice)
                    availableMimeTypes.addAll(device.getSupportedMimeTypes());

        // Get browse node
        AbstractNode browseNode = mediaService.getNode(objectID);
        if (browseNode == null)
            throw new ContentDirectoryException(NO_SUCH_OBJECT, objectID);

        DirectoryBrowseResult result;
        if (DIRECT_CHILDREN == browseFlag) {
            result = new DirectoryBrowseResult(firstResult, maxResults);
            // Add child nodes
            ChildNodeResult childNodeResult = mediaService.getChildNodes(new ChildNodeRequest(browseNode, availableMimeTypes));
            for (AbstractNode childNode : childNodeResult.getChildNodes())
                addNode(objectID, childNode, result, childNodeResult.getTotalCount(), availableMimeTypes);
        } else if (METADATA == browseFlag) {
            result = new DirectoryBrowseResult(0, 1);
            // Get node
            addNode(browseNode.getParentId(), browseNode, result, 0, availableMimeTypes);
        } else
            result = new DirectoryBrowseResult(0, 1);

        return result.buildBrowseResult();
    }

    @Override
    public BrowseResult search(final String containerId, final String searchCriteria, final String filter, final long firstResult,
                               final long maxResults, final SortCriterion[] orderBy, final RemoteClientInfo remoteClientInfo) throws ContentDirectoryException {
        // Search is not implemented
        try {
            return new BrowseResult(new DIDLParser().generate(new DIDLContent()), 0, 0);
        } catch (Exception e) {
            throw new ContentDirectoryException(ACTION_FAILED.getCode(), e.getMessage(), e);
        }
    }

    /**
     * Adds node.
     *
     * @param nodeId             node id
     * @param node               node
     * @param result             result
     * @param totalCount         total count
     * @param availableMimeTypes availableMimeTypes
     * @throws ContentDirectoryException
     */
    private void addNode(final String nodeId, final AbstractNode node, final DirectoryBrowseResult result, final long totalCount, final List<String> availableMimeTypes) throws ContentDirectoryException {
        if (result.acceptNode())
            if (node instanceof ContentNode) {
                // Get node url
                String url = mediaService.getNodeUrl(node);
                // Add item to result
                result.addItem(nodeId, (ContentNode) node, url);
            } else if (node instanceof FolderNode) {
                // Get child counts
                ChildNodeResult childNodeResult = mediaService.getChildNodes(new ChildNodeRequest(node, availableMimeTypes));
                // Add container to result
                result.addContainer(nodeId, node, childNodeResult.getTotalCount());
            } else if (node instanceof PodcastNode) {
                // Add podcast to result
                result.addContainer(nodeId, node, 1);
            } else if (node instanceof RawUrlNode) {
                // Add raw URL to result
                RawUrlNode rawUrlNode = (RawUrlNode) node;
                String entryName = node.getName();
                if (rawUrlNode.getType() == TYPE_PODCAST_ENTRY)
                    // Format podcast entry name
                    entryName = formatPodcastEntryName(result.getResultCount(), totalCount, node.getName());

                result.addUrlItem(nodeId, rawUrlNode, entryName);
            } else if (node instanceof IcecastGenreNode)
                // Add Icecast genre to result
                result.addContainer(nodeId, node, 1);
    }

    /**
     * Format post-cast entry name.
     * If prepend_podcast_entry_name configuration parameter is set to true,
     * item number is added to title.
     *
     * @param count      post-cast entry count
     * @param totalCount post-cast entry total count
     * @param title      title
     * @return post-cast entry name
     */
    private String formatPodcastEntryName(final long count, final long totalCount, final String title) {
        if (configuration.getBooleanParameter(Parameter.PREPEND_PODCAST_ENTRY_NAME))
            if (totalCount > 99) return String.format("%03d - %s", count + 1, title);
            else return String.format("%02d - %s", count + 1, title);
        else return title;
    }
}
