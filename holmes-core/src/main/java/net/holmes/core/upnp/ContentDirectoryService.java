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
import net.holmes.core.inject.InjectLogger;
import net.holmes.core.media.MediaManager;
import net.holmes.core.media.model.*;
import org.fourthline.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryErrorCode;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.support.model.SortCriterion;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.net.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * UPnP Content directory service.
 */
public final class ContentDirectoryService extends AbstractContentDirectoryService {
    private static final String LOCAL_ADDRESS = getLocalIPV4();
    @InjectLogger
    private Logger logger;
    @Inject
    private MediaManager mediaManager;
    @Inject
    private Configuration configuration;

    /**
     * Instantiates a new content directory service.
     */
    public ContentDirectoryService() {
        super(Arrays.asList("dc:title"), // search caps
                Arrays.asList("dc:title", "dc:date")); // sort caps
    }

    /**
     * Get local IPv4 address (InetAddress.getLocalHost().getHostAddress() does not work on Linux).
     *
     * @return local IPv4 address
     */
    private static String getLocalIPV4() {
        try {
            for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
                NetworkInterface networkInterface = interfaces.nextElement();
                for (Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses(); inetAddresses.hasMoreElements(); ) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BrowseResult browse(final String objectID, final BrowseFlag browseFlag, final String filter, final long firstResult, final long maxResults,
                               final SortCriterion[] orderBy) throws ContentDirectoryException {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("browse  " + ((browseFlag == BrowseFlag.DIRECT_CHILDREN) ? "DC " : "MD ") + "objectId=" + objectID + " firstResult=" + firstResult
                        + " nbResults=" + maxResults);
                logger.debug("filter: {}", filter);
                if (orderBy != null) {
                    for (SortCriterion sort : orderBy) {
                        logger.debug("orderBy: {}", sort.toString());
                    }
                }
            }

            DirectoryBrowseResult result = new DirectoryBrowseResult((browseFlag == BrowseFlag.DIRECT_CHILDREN) ? firstResult : 0,
                    (browseFlag == BrowseFlag.DIRECT_CHILDREN) ? maxResults : 1);

            // Get browse node                
            AbstractNode browseNode = mediaManager.getNode(objectID);
            if (logger.isDebugEnabled()) logger.debug("browse node:{}", browseNode);
            if (browseNode == null)
                throw new ContentDirectoryException(ContentDirectoryErrorCode.NO_SUCH_OBJECT, objectID);

            if (browseFlag == BrowseFlag.DIRECT_CHILDREN) {
                // Add child nodes
                List<AbstractNode> childNodes = mediaManager.getChildNodes(browseNode);
                if (childNodes != null) {
                    for (AbstractNode childNode : childNodes) {
                        addNode(objectID, childNode, result, childNodes.size());
                    }
                }
            } else if (browseFlag == BrowseFlag.METADATA) {
                // Get node metadata
                addNode(browseNode.getParentId(), browseNode, result, 0);
            }

            BrowseResult br = new BrowseResult(new DIDLParser().generate(result.getDidl()), result.getItemCount(), result.getTotalCount());
            if (logger.isDebugEnabled()) {
                logger.debug("itemCount:{}", result.getItemCount());
                logger.debug("totalCount:{}", result.getTotalCount());
                logger.debug(br.getResult());
            }
            return br;
        } catch (ContentDirectoryException ex) {
            if (logger.isDebugEnabled()) logger.debug(ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            if (logger.isDebugEnabled()) logger.debug(ex.getMessage(), ex);
            throw new ContentDirectoryException(ContentDirectoryErrorCode.CANNOT_PROCESS.getCode(), ex.getMessage(), ex);
        }
    }

    /**
     * Adds node.
     *
     * @param nodeId        node id
     * @param node          node
     * @param result        result
     * @param childNodeSize child node size
     * @throws URISyntaxException URI syntax exception
     */
    private void addNode(final String nodeId, final AbstractNode node, final DirectoryBrowseResult result, final long childNodeSize) throws URISyntaxException {
        if (result.filterResult()) {
            if (node instanceof ContentNode) {
                // Build content url
                String url = "http://" + LOCAL_ADDRESS + ":" + configuration.getHttpServerPort() + "/content?id=" + node.getId();

                // Add item to result
                result.addItem(nodeId, (ContentNode) node, url);
            } else if (node instanceof FolderNode) {
                // Get child counts
                List<AbstractNode> childNodes = mediaManager.getChildNodes(node);
                int childCount = childNodes != null ? childNodes.size() : 0;

                // Add container to result
                result.addContainer(nodeId, node, childCount);
            } else if (node instanceof PlaylistNode) {
                // Add playlist to result
                result.addPlaylist(nodeId, node);
            } else if (node instanceof PodcastNode) {
                // Add podcast to result
                result.addContainer(nodeId, node, 1);
            } else if (node instanceof PodcastEntryNode) {
                // Add podcast entry to result
                String entryName = formatPodcastEntryName(result.getItemCount() + result.getFirstResult(), childNodeSize, node.getName());
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
        } else {
            return title;
        }
    }
}
