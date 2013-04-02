/**
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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.inject.Inject;

import net.holmes.common.configuration.Configuration;
import net.holmes.common.configuration.Parameter;
import net.holmes.common.inject.Loggable;
import net.holmes.common.media.AbstractNode;
import net.holmes.common.media.ContentNode;
import net.holmes.common.media.FolderNode;
import net.holmes.common.media.PlaylistNode;
import net.holmes.common.media.PodcastEntryNode;
import net.holmes.common.media.PodcastNode;
import net.holmes.core.media.MediaManager;

import org.fourthline.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryErrorCode;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.support.model.SortCriterion;
import org.slf4j.Logger;

@Loggable
public final class ContentDirectoryService extends AbstractContentDirectoryService {
    private Logger logger;

    @Inject
    private MediaManager mediaManager;

    @Inject
    private Configuration configuration;

    private static final String LOCAL_ADDRESS = getLocalIPV4();

    public ContentDirectoryService() {
        super( // search caps
                Arrays.asList("dc:title"),
                // sort caps
                Arrays.asList("dc:title", "dc:date"));
    }

    /**
     * Get local IPv4 address (InetAddress.getLocalHost().getHostAddress() does not work on Linux)
     */
    private static String getLocalIPV4() {
        try {
            for (Enumeration<NetworkInterface> intfaces = NetworkInterface.getNetworkInterfaces(); intfaces.hasMoreElements();) {
                NetworkInterface intf = intfaces.nextElement();
                for (Enumeration<InetAddress> inetAddresses = intf.getInetAddresses(); inetAddresses.hasMoreElements();) {
                    InetAddress inetAddr = inetAddresses.nextElement();
                    if (inetAddr instanceof Inet4Address && !inetAddr.isLoopbackAddress() && inetAddr.isSiteLocalAddress()) {
                        return inetAddr.getHostAddress();
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BrowseResult browse(String objectID, BrowseFlag browseFlag, String filter, long firstResult, long maxResults, SortCriterion[] orderby)
            throws ContentDirectoryException {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("browse  " + ((browseFlag == BrowseFlag.DIRECT_CHILDREN) ? "DC " : "MD ") + "objectid=" + objectID + " indice=" + firstResult
                        + " nbresults=" + maxResults);
                logger.debug("filter: {}", filter);
                if (orderby != null) {
                    for (SortCriterion sort : orderby) {
                        logger.debug("orderby: {}", sort.toString());
                    }
                }
            }

            DirectoryBrowseResult result = new DirectoryBrowseResult((browseFlag == BrowseFlag.DIRECT_CHILDREN) ? firstResult : 0,
                    (browseFlag == BrowseFlag.DIRECT_CHILDREN) ? maxResults : 1);

            // Get browse node                
            AbstractNode browseNode = mediaManager.getNode(objectID);
            if (logger.isDebugEnabled()) logger.debug("browse node:{}", browseNode);
            if (browseNode == null) throw new ContentDirectoryException(ContentDirectoryErrorCode.NO_SUCH_OBJECT, objectID);

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
            throw new ContentDirectoryException(ContentDirectoryErrorCode.CANNOT_PROCESS, ex.getMessage());
        }
    }

    private void addNode(String nodeId, AbstractNode node, DirectoryBrowseResult result, long childNodeSize) {
        if (result.filterResult()) {
            if (node instanceof ContentNode) {
                // Build content url
                StringBuilder url = new StringBuilder().append("http://").append(LOCAL_ADDRESS).append(":").append(configuration.getHttpServerPort())//
                        .append("/content?id=").append(node.getId());

                // Add item to result
                result.addItem(nodeId, (ContentNode) node, url.toString());
            } else if (node instanceof FolderNode) {
                // Get child counts
                List<AbstractNode> childNodes = mediaManager.getChildNodes(node);
                int childCount = childNodes != null ? childNodes.size() : 0;

                // Add container to result
                result.addContainer(nodeId, node, childCount);
            } else if (node instanceof PlaylistNode) {
                // Add playlist to result
                result.addPlaylist(nodeId, node, 1);
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
     * Get post-cast entry name. If prepend_podcast_entry_name configuration parameter is set to true, 
     * item number is added to title
     */
    private String formatPodcastEntryName(long count, long totalCount, String title) {
        if (configuration.getParameter(Parameter.PREPEND_PODCAST_ENTRY_NAME)) {
            if (totalCount > 99) return String.format("%03d - %s", count + 1, title);
            else return String.format("%02d - %s", count + 1, title);
        } else {
            return title;
        }
    }
}