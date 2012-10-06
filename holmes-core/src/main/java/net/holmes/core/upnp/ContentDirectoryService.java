/**
* Copyright (C) 2012  Cedric Cheneau
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

import net.holmes.core.configuration.IConfiguration;
import net.holmes.core.configuration.Parameter;
import net.holmes.core.media.IMediaService;
import net.holmes.core.media.node.AbstractNode;
import net.holmes.core.media.node.ContentNode;
import net.holmes.core.media.node.FolderNode;
import net.holmes.core.media.node.PlaylistNode;
import net.holmes.core.media.node.PodcastEntryNode;
import net.holmes.core.media.node.PodcastNode;
import net.holmes.core.util.mimetype.MimeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.binding.annotations.UpnpStateVariable;
import org.teleal.cling.binding.annotations.UpnpStateVariables;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.protocol.sync.ReceivingAction;
import org.teleal.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.teleal.cling.support.contentdirectory.ContentDirectoryErrorCode;
import org.teleal.cling.support.contentdirectory.ContentDirectoryException;
import org.teleal.cling.support.contentdirectory.DIDLParser;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.BrowseResult;
import org.teleal.cling.support.model.SortCriterion;

@UpnpStateVariables({ @UpnpStateVariable(name = "A_ARG_TYPE_ObjectID", sendEvents = false, datatype = "string"),
        @UpnpStateVariable(name = "A_ARG_TYPE_Result", sendEvents = false, datatype = "string"),
        @UpnpStateVariable(name = "A_ARG_TYPE_BrowseFlag", sendEvents = false, datatype = "string", allowedValuesEnum = BrowseFlag.class),
        @UpnpStateVariable(name = "A_ARG_TYPE_SearchCriteria", sendEvents = false, datatype = "string"),
        @UpnpStateVariable(name = "A_ARG_TYPE_Filter", sendEvents = false, datatype = "string"),
        @UpnpStateVariable(name = "A_ARG_TYPE_ContainerID", sendEvents = false, datatype = "string"),
        @UpnpStateVariable(name = "A_ARG_TYPE_SortCriteria", sendEvents = false, datatype = "string"),
        @UpnpStateVariable(name = "A_ARG_TYPE_Index", sendEvents = false, datatype = "ui4"),
        @UpnpStateVariable(name = "A_ARG_TYPE_Count", sendEvents = false, datatype = "ui4"),
        @UpnpStateVariable(name = "A_ARG_TYPE_UpdateID", sendEvents = false, datatype = "ui4"),
        @UpnpStateVariable(name = "A_ARG_TYPE_URI", sendEvents = false, datatype = "uri") })
public final class ContentDirectoryService extends AbstractContentDirectoryService {
    private Logger logger = LoggerFactory.getLogger(ContentDirectoryService.class);

    private IMediaService mediaService;
    private IConfiguration configuration;
    private String localAddress;

    public ContentDirectoryService() {
        super( // search caps
                Arrays.asList("dc:title"),
                // sort caps
                Arrays.asList("dc:title"));
        try {
            this.localAddress = getLocalAddress();
            if (logger.isDebugEnabled()) logger.debug("local address:" + this.localAddress);
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void setMediaService(IMediaService mediaService) {
        this.mediaService = mediaService;
    }

    public void setConfiguration(IConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Get local IPv4 address (InetAddress.getLocalHost().getHostAddress() does not work on Linux)
     */
    private static String getLocalAddress() throws UnknownHostException {
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
            throw new UnknownHostException();
        }
    }

    @Override
    public BrowseResult browse(String objectID, BrowseFlag browseFlag, String filter, long firstResult, long maxResults, SortCriterion[] orderby)
            throws ContentDirectoryException {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("browse  " + ((browseFlag == BrowseFlag.DIRECT_CHILDREN) ? "DC " : "MD ") + "objectid=" + objectID + " indice=" + firstResult
                        + " nbresults=" + maxResults);
                logger.debug("filter: " + filter);
                try {
                    String userAgent = ReceivingAction.getRequestMessage().getHeaders().getFirstHeader(UpnpHeader.Type.USER_AGENT).getString();
                    logger.debug("RequestFrom agent: " + userAgent);
                } catch (NullPointerException ex) {
                    logger.debug("RequestFrom agent: Anonymous");
                }
            }

            DirectoryBrowseResult result = new DirectoryBrowseResult((browseFlag == BrowseFlag.DIRECT_CHILDREN) ? firstResult : 0,
                    (browseFlag == BrowseFlag.DIRECT_CHILDREN) ? maxResults : 1);

            // Get browse node                
            AbstractNode browseNode = mediaService.getNode(objectID);
            if (logger.isDebugEnabled()) logger.debug("browse node:" + browseNode);

            if (browseNode != null) {
                if (browseFlag == BrowseFlag.DIRECT_CHILDREN) {
                    // Browse child nodes
                    if (browseNode instanceof FolderNode) {
                        // Add folder child nodes
                        List<AbstractNode> childNodes = mediaService.getChildNodes(browseNode);
                        if (childNodes != null && !childNodes.isEmpty()) {
                            for (AbstractNode childNode : childNodes) {
                                addNode(objectID, childNode, result);
                            }
                        }
                    } else if (browseNode instanceof PlaylistNode) {
                        // Add playlist child nodes
                        List<AbstractNode> childNodes = mediaService.getChildNodes(browseNode);
                        if (childNodes != null && !childNodes.isEmpty()) {
                            for (AbstractNode childNode : childNodes) {
                                addNode(objectID, childNode, result);
                            }
                        }
                    } else if (browseNode instanceof PodcastNode) {
                        // Add pod-cast entry nodes
                        addPodcastEntries((PodcastNode) browseNode, result);
                    }
                } else if (browseFlag == BrowseFlag.METADATA) {
                    // Get node metadata
                    addNode(browseNode.getParentId(), browseNode, result);
                }
            } else {
                throw new ContentDirectoryException(ContentDirectoryErrorCode.NO_SUCH_OBJECT, objectID);
            }

            BrowseResult br = new BrowseResult(new DIDLParser().generate(result.getDidl()), result.getItemCount(), result.getTotalCount());
            if (logger.isDebugEnabled()) {
                logger.debug("itemCount:" + result.getItemCount());
                logger.debug("totalCount:" + result.getTotalCount());
                logger.debug(br.getResult());
            }
            return br;
        } catch (ContentDirectoryException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ContentDirectoryException(ContentDirectoryErrorCode.CANNOT_PROCESS, ex.getMessage());
        }
    }

    private void addNode(String nodeId, AbstractNode node, DirectoryBrowseResult result) {
        if (node instanceof ContentNode) {
            if (result.filterResult()) {
                // Build content url
                StringBuilder url = new StringBuilder();
                url.append("http://").append(localAddress).append(":").append(configuration.getHttpServerPort());
                url.append("/content?id=");
                url.append(node.getId());

                // Add item to result
                result.addItem(nodeId, (ContentNode) node, url.toString());
            }
            result.addTotalCount();
        } else if (node instanceof FolderNode) {
            if (result.filterResult()) {
                // Get child counts
                List<AbstractNode> childNodes = mediaService.getChildNodes(node);
                int childCount = childNodes != null ? childNodes.size() : 0;

                // Add container to result
                result.addContainer(nodeId, node, childCount);
            }
            result.addTotalCount();
        } else if (node instanceof FolderNode) {
            if (result.filterResult()) {
                // Add playlist to result
                result.addPlaylist(nodeId, node, 1);
            }
        } else if (node instanceof PodcastNode) {
            if (result.filterResult()) {
                // Add container to result
                result.addContainer(nodeId, node, 1);
            }
            result.addTotalCount();
        }
    }

    private void addPodcastEntries(PodcastNode parentNode, DirectoryBrowseResult result) {
        // Get pod-cast child nodes
        List<AbstractNode> childNodes = mediaService.getChildNodes(parentNode);
        if (childNodes != null && !childNodes.isEmpty()) {
            PodcastEntryNode podcastEntryNode = null;
            for (AbstractNode node : childNodes) {
                if (node instanceof PodcastEntryNode) {
                    podcastEntryNode = (PodcastEntryNode) node;
                    MimeType mimeType = podcastEntryNode.getMimeType();
                    if (mimeType.isMedia()) {
                        if (result.filterResult()) {
                            // Add child item to result
                            String entryName = getPodcastEntryName(result.getItemCount() + result.getFirstResult(), podcastEntryNode.getName());
                            result.addItem(parentNode.getId(), podcastEntryNode, entryName);
                        }
                        result.addTotalCount();
                    }
                }
            }
        }
    }

    /**
     * Get post-cast entry name. If prepend_podcast_entry_name configuration parameter is set to true, item number is added to title
     */
    private String getPodcastEntryName(long count, String title) {
        if (configuration.getParameter(Parameter.PREPEND_PODCAST_ENTRY_NAME)) {
            return String.format("%02d - %s", count + 1, title);
        } else {
            return title;
        }
    }
}