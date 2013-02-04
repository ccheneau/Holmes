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

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import net.holmes.core.configuration.Configuration;
import net.holmes.core.configuration.Parameter;
import net.holmes.core.media.MediaService;
import net.holmes.core.media.node.AbstractNode;
import net.holmes.core.media.node.ContentNode;
import net.holmes.core.media.node.FolderNode;
import net.holmes.core.media.node.PlaylistNode;
import net.holmes.core.media.node.PodcastEntryNode;
import net.holmes.core.media.node.PodcastNode;
import net.holmes.core.util.inject.Loggable;

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
    private MediaService mediaService;

    @Inject
    private Configuration configuration;

    @Inject
    @Named("localIPv4")
    private String localAddress;

    public ContentDirectoryService() {
        super( // search caps
                Arrays.asList("dc:title"),
                // sort caps
                Arrays.asList("dc:title", "dc:date"));
    }

    @Override
    public BrowseResult browse(String objectID, BrowseFlag browseFlag, String filter, long firstResult, long maxResults, SortCriterion[] orderby)
            throws ContentDirectoryException {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("browse  " + ((browseFlag == BrowseFlag.DIRECT_CHILDREN) ? "DC " : "MD ") + "objectid=" + objectID + " indice=" + firstResult
                        + " nbresults=" + maxResults);
                logger.debug("filter: " + filter);
                if (orderby != null) {
                    for (SortCriterion sort : orderby) {
                        logger.debug("orderby: " + sort.toString());
                    }
                }
            }

            DirectoryBrowseResult result = new DirectoryBrowseResult((browseFlag == BrowseFlag.DIRECT_CHILDREN) ? firstResult : 0,
                    (browseFlag == BrowseFlag.DIRECT_CHILDREN) ? maxResults : 1);

            // Get browse node                
            AbstractNode browseNode = mediaService.getNode(objectID);
            if (logger.isDebugEnabled()) logger.debug("browse node:" + browseNode);
            if (browseNode == null) throw new ContentDirectoryException(ContentDirectoryErrorCode.NO_SUCH_OBJECT, objectID);

            if (browseFlag == BrowseFlag.DIRECT_CHILDREN) {
                // Add child nodes
                List<AbstractNode> childNodes = mediaService.getChildNodes(browseNode);
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
                logger.debug("itemCount:" + result.getItemCount());
                logger.debug("totalCount:" + result.getTotalCount());
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
                StringBuilder url = new StringBuilder();
                url.append("http://").append(localAddress).append(":").append(configuration.getHttpServerPort());
                url.append("/content?id=");
                url.append(node.getId());

                // Add item to result
                result.addItem(nodeId, (ContentNode) node, url.toString());
            } else if (node instanceof FolderNode) {
                // Get child counts
                List<AbstractNode> childNodes = mediaService.getChildNodes(node);
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
                String entryName = getPodcastEntryName(result.getItemCount() + result.getFirstResult(), childNodeSize, node.getName());
                result.addItem(nodeId, (PodcastEntryNode) node, entryName);
            }
        }
    }

    /**
     * Get post-cast entry name. If prepend_podcast_entry_name configuration parameter is set to true, 
     * item number is added to title
     */
    private String getPodcastEntryName(long count, long totalCount, String title) {
        if (configuration.getParameter(Parameter.PREPEND_PODCAST_ENTRY_NAME)) {
            if (totalCount > 99) return String.format("%03d - %s", count + 1, title);
            else return String.format("%02d - %s", count + 1, title);
        } else {
            return title;
        }
    }
}