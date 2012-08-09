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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import net.holmes.core.configuration.IConfiguration;
import net.holmes.core.configuration.Parameter;
import net.holmes.core.media.IMediaService;
import net.holmes.core.media.node.AbstractNode;
import net.holmes.core.media.node.ContentNode;
import net.holmes.core.media.node.FolderNode;
import net.holmes.core.media.node.PodcastEntryNode;
import net.holmes.core.media.node.PodcastNode;
import net.holmes.core.util.mimetype.MimeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.binding.annotations.UpnpAction;
import org.teleal.cling.binding.annotations.UpnpInputArgument;
import org.teleal.cling.binding.annotations.UpnpOutputArgument;
import org.teleal.cling.binding.annotations.UpnpStateVariable;
import org.teleal.cling.binding.annotations.UpnpStateVariables;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.protocol.sync.ReceivingAction;
import org.teleal.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.teleal.cling.support.contentdirectory.ContentDirectoryErrorCode;
import org.teleal.cling.support.contentdirectory.ContentDirectoryException;
import org.teleal.cling.support.contentdirectory.DIDLParser;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.BrowseResult;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.DIDLObject.Property.DC;
import org.teleal.cling.support.model.Res;
import org.teleal.cling.support.model.SortCriterion;
import org.teleal.cling.support.model.container.StorageFolder;
import org.teleal.cling.support.model.item.Movie;
import org.teleal.cling.support.model.item.MusicTrack;
import org.teleal.cling.support.model.item.Photo;

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
    private static Logger logger = LoggerFactory.getLogger(ContentDirectoryService.class);

    private IMediaService mediaService;
    private IConfiguration configuration;
    private String localAddress;

    public ContentDirectoryService() {
        super( // search caps
                Arrays.asList("dc:title"),
                // sort caps
                Arrays.asList("dc:title"));
        try {
            this.localAddress = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void setMediaService(IMediaService mediaService) {
        this.mediaService = mediaService;
    }

    public void setConfiguration(IConfiguration configuration) {
        this.configuration = configuration;
    }

    /* (non-Javadoc)
     * @see org.teleal.cling.support.contentdirectory.AbstractContentDirectoryService#browse(java.lang.String, org.teleal.cling.support.model.BrowseFlag, java.lang.String, long, long, org.teleal.cling.support.model.SortCriterion[])
     */
    @Override
    public BrowseResult browse(String objectID, BrowseFlag browseFlag, String filter, long firstResult, long maxResults, SortCriterion[] orderby)
            throws ContentDirectoryException {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("browse  " + ((browseFlag == BrowseFlag.DIRECT_CHILDREN) ? "DC " : "MD ") + "objectid=" + objectID + " filter=" + filter
                        + " indice=" + firstResult + " nbresults=" + maxResults);
                try {
                    String userAgent = ReceivingAction.getRequestMessage().getHeaders().getFirstHeader(UpnpHeader.Type.USER_AGENT).getString();
                    logger.debug("RequestFrom agent: " + userAgent);
                }
                catch (NullPointerException ex) {
                    logger.debug("RequestFrom agent: Anonymous");
                }
            }
            long itemCount = 0;
            long totalCount = 0;

            DIDLContent didl = new DIDLContent();

            // Get node                
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
                                if (logger.isDebugEnabled()) logger.debug("child node:" + childNode);
                                addNode(objectID, childNode, didl, itemCount, totalCount, firstResult, maxResults);
                            }
                        }
                    }
                    else if (browseNode instanceof PodcastNode) {
                        // Add pod-cast entry nodes
                        addPodcastEntries((PodcastNode) browseNode, didl, itemCount, totalCount, firstResult, maxResults);
                    }
                }
                else if (browseFlag == BrowseFlag.METADATA) {
                    // Get node metadata
                    addNode(browseNode.getParentId(), browseNode, didl, itemCount, totalCount, 0, 1);
                }
            }
            else {
                throw new ContentDirectoryException(ContentDirectoryErrorCode.NO_SUCH_OBJECT, objectID);
            }

            BrowseResult br = new BrowseResult(new DIDLParser().generate(didl), itemCount, totalCount);
            if (logger.isDebugEnabled()) logger.debug(br.getResult());
            return br;
        }
        catch (ContentDirectoryException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new ContentDirectoryException(ContentDirectoryErrorCode.CANNOT_PROCESS, ex.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.teleal.cling.support.contentdirectory.AbstractContentDirectoryService#search(java.lang.String, java.lang.String, java.lang.String, org.teleal.cling.model.types.UnsignedIntegerFourBytes, org.teleal.cling.model.types.UnsignedIntegerFourBytes, java.lang.String)
     */
    @Override
    @UpnpAction(out = { @UpnpOutputArgument(name = "Result", stateVariable = "A_ARG_TYPE_Result", getterName = "getResult"),
            @UpnpOutputArgument(name = "NumberReturned", stateVariable = "A_ARG_TYPE_Count", getterName = "getCount"),
            @UpnpOutputArgument(name = "TotalMatches", stateVariable = "A_ARG_TYPE_Count", getterName = "getTotalMatches"),
            @UpnpOutputArgument(name = "UpdateID", stateVariable = "A_ARG_TYPE_UpdateID", getterName = "getContainerUpdateID") })
    public BrowseResult search(@UpnpInputArgument(name = "ContainerID") String objectId, @UpnpInputArgument(name = "SearchCriteria") String searchCriteria,
            @UpnpInputArgument(name = "Filter") String filter,
            @UpnpInputArgument(name = "StartingIndex", stateVariable = "A_ARG_TYPE_Index") UnsignedIntegerFourBytes firstResult,
            @UpnpInputArgument(name = "RequestedCount", stateVariable = "A_ARG_TYPE_Count") UnsignedIntegerFourBytes maxResults,
            @UpnpInputArgument(name = "SortCriteria") String orderBy) throws ContentDirectoryException {
        SortCriterion[] orderByCriteria;
        try {
            orderByCriteria = SortCriterion.valueOf(orderBy);
        }
        catch (Exception ex) {
            throw new ContentDirectoryException(ContentDirectoryErrorCode.UNSUPPORTED_SORT_CRITERIA, ex.toString());
        }

        try {
            return search(objectId, searchCriteria, filter, firstResult.getValue(), maxResults.getValue(), orderByCriteria);
        }
        catch (ContentDirectoryException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new ContentDirectoryException(ErrorCode.ACTION_FAILED, ex.toString());
        }
    }

    /* (non-Javadoc)
     * @see org.teleal.cling.support.contentdirectory.AbstractContentDirectoryService#search(java.lang.String, java.lang.String, java.lang.String, long, long, org.teleal.cling.support.model.SortCriterion[])
     */
    @Override
    public BrowseResult search(String containerId, String searchCriteria, String filter, long firstResult, long maxResults, SortCriterion[] orderBy)
            throws ContentDirectoryException {
        return super.search(containerId, searchCriteria, filter, firstResult, maxResults, orderBy);
    }

    /**
     * Add node to didl
     */
    private void addNode(String nodeId, AbstractNode node, DIDLContent didl, long itemCount, long totalCount, long firstResult, long maxResults) {
        if (node instanceof ContentNode) {
            if (filterDidl(itemCount, totalCount, firstResult, maxResults)) {
                addDidlItem(nodeId, (ContentNode) node, didl);
                itemCount++;
            }
            totalCount++;
        }
        else if (node instanceof FolderNode) {
            if (filterDidl(itemCount, totalCount, firstResult, maxResults)) {
                addDidlContainer(nodeId, (FolderNode) node, didl);
                itemCount++;
            }
            totalCount++;
        }
        else if (node instanceof PodcastNode) {
            if (filterDidl(itemCount, totalCount, firstResult, maxResults)) {
                addDidlContainer(nodeId, (PodcastNode) node, didl);
                itemCount++;
            }
            totalCount++;
        }
    }

    /**
     * Add item to didl
     */
    private void addDidlItem(String parentNodeId, ContentNode contentNode, DIDLContent didl) {
        StringBuilder url = new StringBuilder();
        url.append("http://").append(localAddress).append(":").append(configuration.getHttpServerPort());
        url.append("/content?id=");
        url.append(contentNode.getId());

        if (logger.isDebugEnabled()) {
            logger.debug("add content:" + contentNode);
            logger.debug("url:" + url.toString());
        }

        Res res = new Res(contentNode.getMimeType().toUpnpMimeType(), contentNode.getSize(), url.toString());

        if (contentNode.getMimeType().isVideo()) {
            // Add video item
            Movie movie = new Movie(contentNode.getId(), parentNodeId, contentNode.getName(), null, res);
            setModifiedDate(movie, contentNode);
            didl.addItem(movie);
        }
        else if (contentNode.getMimeType().isAudio()) {
            // Add audio track item
            MusicTrack musicTrack = new MusicTrack(contentNode.getId(), parentNodeId, contentNode.getName(), null, null, (String) null, res);
            setModifiedDate(musicTrack, contentNode);
            didl.addItem(musicTrack);
        }
        else if (contentNode.getMimeType().isImage()) {
            // Add image item
            Photo photo = new Photo(contentNode.getId(), parentNodeId, contentNode.getName(), null, null, res);
            setModifiedDate(photo, contentNode);
            didl.addItem(photo);
        }
    }

    /**
     * Add container to didl
     */
    private void addDidlContainer(String parentNodeId, FolderNode folderNode, DIDLContent didl) {
        if (logger.isDebugEnabled()) logger.debug("add folder node:" + folderNode);

        List<AbstractNode> childNodes = mediaService.getChildNodes(folderNode);
        Integer childCount = childNodes != null ? childNodes.size() : 0;

        StorageFolder folder = new StorageFolder(folderNode.getId(), parentNodeId, folderNode.getName(), null, childCount, null);
        setModifiedDate(folder, folderNode);

        didl.addContainer(folder);
    }

    /**
     * Add pod-cast to didl
     */
    private void addDidlContainer(String parentNodeId, PodcastNode podcastNode, DIDLContent didl) {
        if (logger.isDebugEnabled()) logger.debug("add podcast:" + podcastNode);

        StorageFolder folder = new StorageFolder(podcastNode.getId(), parentNodeId, podcastNode.getName(), null, 1, null);
        didl.addContainer(folder);
    }

    /**
     * Add pod-cast entries to didl
     */
    private void addPodcastEntries(PodcastNode parentNode, DIDLContent didl, long itemCount, long totalCount, long firstResult, long maxResults) {
        // Get pod-cast child nodes
        List<AbstractNode> childNodes = mediaService.getChildNodes(parentNode);
        if (childNodes != null && !childNodes.isEmpty()) {
            PodcastEntryNode podcastEntryNode = null;
            for (AbstractNode node : childNodes) {
                if (node instanceof PodcastEntryNode) {
                    podcastEntryNode = (PodcastEntryNode) node;
                    MimeType mimeType = podcastEntryNode.getMimeType();
                    if (mimeType.isMedia()) {
                        String entryName = getPodcastEntryName(itemCount, podcastEntryNode.getName());
                        Res res = new Res(mimeType.toUpnpMimeType(), podcastEntryNode.getSize(), podcastEntryNode.getUrl());

                        if (mimeType.isAudio()) {
                            if (filterDidl(itemCount, totalCount, firstResult, maxResults)) {
                                // Add audio track item
                                MusicTrack musicTrack = new MusicTrack(node.getId(), parentNode.getId(), entryName, null, null, (String) null, res);
                                setModifiedDate(musicTrack, podcastEntryNode);
                                didl.addItem(musicTrack);
                                itemCount++;
                            }
                            totalCount++;
                        }
                        else if (mimeType.isImage()) {
                            if (filterDidl(itemCount, totalCount, firstResult, maxResults)) {
                                // Add image item
                                Photo photo = new Photo(node.getId(), parentNode.getId(), entryName, null, null, res);
                                setModifiedDate(photo, podcastEntryNode);
                                didl.addItem(photo);
                                itemCount++;
                            }
                            totalCount++;
                        }
                        else if (mimeType.isVideo()) {
                            if (filterDidl(itemCount, totalCount, firstResult, maxResults)) {
                                // Add video item
                                Movie movie = new Movie(node.getId(), parentNode.getId(), entryName, null, res);
                                setModifiedDate(movie, podcastEntryNode);
                                didl.addItem(movie);
                                itemCount++;
                            }
                            totalCount++;
                        }
                    }
                }
            }
        }
    }

    /**
     * Filter didl content
     */
    private boolean filterDidl(long itemCount, long totalCount, long firstResult, long maxResults) {
        return itemCount < maxResults && totalCount >= firstResult;
    }

    /**
     * Set didl content modified date
     */
    private void setModifiedDate(DIDLObject didlObjet, AbstractNode node) {
        if (node.getModifedDate() != null) didlObjet.replaceFirstProperty(new DC.DATE(node.getModifedDate()));
    }

    /**
     * Get post-cast entry name. If prepend_podcast_entry_name parameter is set to true, item number is added to title
     */
    private String getPodcastEntryName(long count, String title) {
        if (configuration.getParameter(Parameter.PREPEND_PODCAST_ENTRY_NAME)) {
            return String.format("%02d %s", count, title);
        }
        else {
            return title;
        }
    }
}
