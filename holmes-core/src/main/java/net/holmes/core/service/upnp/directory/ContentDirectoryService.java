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

package net.holmes.core.service.upnp.directory;

import com.google.common.annotations.VisibleForTesting;
import net.holmes.core.business.configuration.ConfigurationManager;
import net.holmes.core.business.media.MediaManager;
import net.holmes.core.business.media.MediaSearchRequest;
import net.holmes.core.business.media.model.ContentNode;
import net.holmes.core.business.media.model.FolderNode;
import net.holmes.core.business.media.model.MediaNode;
import net.holmes.core.business.streaming.StreamingManager;
import net.holmes.core.business.streaming.upnp.device.UpnpDevice;
import org.fourthline.cling.model.profile.RemoteClientInfo;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;

import javax.inject.Inject;
import java.util.*;

import static net.holmes.core.business.mimetype.model.MimeType.MIME_TYPE_SUBTITLE;
import static net.holmes.core.common.ConfigurationParameter.UPNP_ADD_SUBTITLE;
import static org.fourthline.cling.support.contentdirectory.ContentDirectoryErrorCode.NO_SUCH_OBJECT;
import static org.fourthline.cling.support.model.BrowseFlag.*;

/**
 * UPnP Content directory service.
 */
public final class ContentDirectoryService extends AbstractContentDirectoryService {
    @Inject
    private ConfigurationManager configurationManager;
    @Inject
    private MediaManager mediaManager;
    @Inject
    private StreamingManager streamingManager;

    /**
     * Instantiates a new content directory service.
     */
    public ContentDirectoryService() {
        // search caps, sort caps
        super(Collections.singletonList("dc:title"), Arrays.asList("dc:title", "dc:date"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BrowseResult browse(final String objectID, final BrowseFlag browseFlag, final long firstResult, final long maxResults,
                               final RemoteClientInfo remoteClientInfo) throws ContentDirectoryException {
        // Get browse node
        MediaNode browseNode = mediaManager.getNode(objectID)
                .orElseThrow(() -> new ContentDirectoryException(NO_SUCH_OBJECT, objectID));

        // Get available mime types
        List<String> availableMimeTypes = getAvailableMimeType(remoteClientInfo);

        // Build browse result
        DirectoryBrowseResult result;
        if (DIRECT_CHILDREN == browseFlag) {
            result = new DirectoryBrowseResult(firstResult, maxResults);
            // Add child nodes
            Collection<MediaNode> searchResult = mediaManager.searchChildNodes(new MediaSearchRequest(browseNode, availableMimeTypes));
            for (MediaNode childNode : searchResult) {
                addNode(objectID, childNode, result, availableMimeTypes);
            }
        } else if (METADATA == browseFlag) {
            result = new DirectoryBrowseResult(0, 1);
            // Get node
            addNode(browseNode.getParentId(), browseNode, result, availableMimeTypes);
        } else {
            result = new DirectoryBrowseResult(0, 1);
        }

        return result.buildBrowseResult(new DIDLParser());
    }

    /**
     * Get available mime types
     *
     * @param remoteClientInfo remote client info
     * @return available mime types
     */
    private List<String> getAvailableMimeType(final RemoteClientInfo remoteClientInfo) {
        // Get available mime types
        List<String> availableMimeTypes = new ArrayList<>();
        if (remoteClientInfo.getConnection() != null) {
            streamingManager.findDevices(remoteClientInfo.getRemoteAddress().getHostAddress()).stream()
                    .filter(device -> device instanceof UpnpDevice)
                    .forEach(device -> availableMimeTypes.addAll(device.getSupportedMimeTypes()));
        }

        // Add subtitle
        if (!availableMimeTypes.isEmpty() && configurationManager.getParameter(UPNP_ADD_SUBTITLE)) {
            availableMimeTypes.add(MIME_TYPE_SUBTITLE.getMimeType());
        }
        return availableMimeTypes;
    }

    /**
     * Adds node.
     *
     * @param nodeId             node id
     * @param node               node
     * @param result             result
     * @param availableMimeTypes availableMimeTypes
     * @throws ContentDirectoryException
     */
    private void addNode(final String nodeId, final MediaNode node, final DirectoryBrowseResult result, final List<String> availableMimeTypes) throws ContentDirectoryException {
        if (result.acceptNode()) {
            if (node instanceof ContentNode) {
                // Add item to result
                result.addItem(nodeId, (ContentNode) node, mediaManager.getNodeUrl(node));
            } else if (node instanceof FolderNode) {
                // Get child counts
                Collection<MediaNode> searchResult = mediaManager.searchChildNodes(new MediaSearchRequest(node, availableMimeTypes));
                // Add container to result
                result.addContainer(nodeId, node, searchResult.size());
            }
        }
    }

    @VisibleForTesting
    void setConfigurationManager(final ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    @VisibleForTesting
    void setMediaManager(final MediaManager mediaManager) {
        this.mediaManager = mediaManager;
    }

    @VisibleForTesting
    void setStreamingManager(final StreamingManager streamingManager) {
        this.streamingManager = streamingManager;
    }
}
