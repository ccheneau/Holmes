/**
* Copyright (c) 2012 Cedric Cheneau
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/
package net.holmes.core.upnp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import net.holmes.core.configuration.IConfiguration;
import net.holmes.core.configuration.Parameter;
import net.holmes.core.media.IMediaService;
import net.holmes.core.media.node.AbstractNode;
import net.holmes.core.media.node.ContentNode;
import net.holmes.core.media.node.FolderNode;
import net.holmes.core.media.node.PodcastEntryNode;
import net.holmes.core.media.node.PodcastNode;
import net.holmes.core.util.StringUtils;
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
            int itemCount = 0;
            DIDLContent didl = new DIDLContent();

            String nodeId = StringUtils.unescapeUpnpId(objectID);
            // Get node                
            AbstractNode browseNode = mediaService.getNode(nodeId);
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
                                itemCount += addNode(nodeId, childNode, didl);
                            }
                        }
                    }
                    else if (browseNode instanceof PodcastNode) {
                        // Add pod-cast entry nodes
                        itemCount += addPodcastEntries((PodcastNode) browseNode, didl);
                    }
                }
                else if (browseFlag == BrowseFlag.METADATA) {
                    // Get node metadata
                    itemCount += addNode(nodeId, browseNode, didl);
                }
            }
            return new BrowseResult(new DIDLParser().generate(didl), itemCount, itemCount);
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
    private int addNode(String nodeId, AbstractNode node, DIDLContent didl) {
        int itemCount = 0;

        if (node instanceof ContentNode) {
            addContent(nodeId, (ContentNode) node, didl);
            itemCount += 1;
        }
        else if (node instanceof FolderNode) {
            addFolder(nodeId, (FolderNode) node, didl);
            itemCount += 1;
        }
        else if (node instanceof PodcastNode) {
            addPodcast(nodeId, (PodcastNode) node, didl);
            itemCount += 1;
        }
        return itemCount;
    }

    /**
     * Add content to didl
     */
    private void addContent(String parentNodeId, ContentNode contentNode, DIDLContent didl) {
        String escNodeId = StringUtils.escapeUpnpId(contentNode.getId());
        String escParentNodeId = StringUtils.escapeUpnpId(parentNodeId);

        StringBuilder url = new StringBuilder();
        url.append("http://").append(localAddress).append(":").append(configuration.getHttpServerPort());
        url.append("/content?id=");
        url.append(escNodeId);

        if (logger.isDebugEnabled()) {
            logger.debug("add content item:" + contentNode);
            logger.debug("url:" + url);
        }

        Res res = new Res(contentNode.getMimeType().toUpnpMimeType(), contentNode.getSize(), url.toString());

        if (contentNode.getMimeType().isVideo()) {
            // Add video
            Movie movie = new Movie(escNodeId, escParentNodeId, contentNode.getName(), "", res);
            setModifiedDate(movie, contentNode);
            didl.addItem(movie);
        }
        else if (contentNode.getMimeType().isAudio()) {
            // Add audio track
            MusicTrack musicTrack = new MusicTrack(escNodeId, escParentNodeId, contentNode.getName(), "", "", "", res);
            setModifiedDate(musicTrack, contentNode);
            didl.addItem(musicTrack);
        }
        else if (contentNode.getMimeType().isImage()) {
            // Add image
            Photo photo = new Photo(escNodeId, escParentNodeId, contentNode.getName(), "", "", res);
            setModifiedDate(photo, contentNode);
            didl.addItem(photo);
        }
    }

    /**
     * Add folder to didl
     */
    private void addFolder(String parentNodeId, FolderNode folderNode, DIDLContent didl) {
        if (logger.isDebugEnabled()) logger.debug("add folder node:" + folderNode);

        List<AbstractNode> childNodes = mediaService.getChildNodes(folderNode);
        Integer childCount = childNodes != null ? childNodes.size() : 0;

        String escNodeId = StringUtils.escapeUpnpId(folderNode.getId());
        String escParentNodeId = StringUtils.escapeUpnpId(parentNodeId);

        StorageFolder folder = new StorageFolder(escNodeId, escParentNodeId, folderNode.getName(), "", childCount, 0L);
        setModifiedDate(folder, folderNode);

        didl.addContainer(folder);
    }

    /**
     * Add pod-cast to didl
     */
    private void addPodcast(String parentNodeId, PodcastNode podcastNode, DIDLContent didl) {
        if (logger.isDebugEnabled()) logger.debug("add podcast:" + podcastNode);

        StorageFolder folder = new StorageFolder(podcastNode.getId(), parentNodeId, podcastNode.getName(), "", 1, 0L);

        didl.addContainer(folder);
    }

    /**
     * Add pod-cast entries to didl
     * @return the number of added items
     */
    private int addPodcastEntries(PodcastNode parentNode, DIDLContent didl) {
        int itemCount = 0;

        // Get child nodes
        List<AbstractNode> childNodes = mediaService.getChildNodes(parentNode);
        if (childNodes != null && !childNodes.isEmpty()) {
            PodcastEntryNode podcastEntryNode = null;
            for (AbstractNode node : childNodes) {
                if (node instanceof PodcastEntryNode) {
                    podcastEntryNode = (PodcastEntryNode) node;
                    MimeType mimeType = null;
                    Res res = null;
                    mimeType = podcastEntryNode.getMimeType();
                    if (mimeType.isMedia()) {
                        String entryName = getPodcastEntryName(itemCount, podcastEntryNode.getName());
                        res = new Res(mimeType.toUpnpMimeType(), podcastEntryNode.getSize(), podcastEntryNode.getUrl());

                        if (mimeType.isAudio()) {
                            // Add audio track
                            MusicTrack musicTrack = new MusicTrack(UUID.randomUUID().toString(), parentNode.getId(), entryName, "", "", "", res);
                            setModifiedDate(musicTrack, podcastEntryNode);
                            didl.addItem(musicTrack);
                            itemCount++;
                        }
                        else if (mimeType.isImage()) {
                            // Adds image
                            Photo photo = new Photo(UUID.randomUUID().toString(), parentNode.getId(), entryName, "", "", res);
                            setModifiedDate(photo, podcastEntryNode);
                            didl.addItem(photo);
                            itemCount++;

                        }
                        else if (mimeType.isVideo()) {
                            // Adds video
                            Movie movie = new Movie(UUID.randomUUID().toString(), parentNode.getId(), entryName, "", res);
                            setModifiedDate(movie, podcastEntryNode);
                            didl.addItem(movie);
                            itemCount++;
                        }
                    }
                }
            }
        }
        return itemCount;
    }

    private void setModifiedDate(DIDLObject didlObjet, AbstractNode node) {
        if (node.getModifedDate() != null) didlObjet.replaceFirstProperty(new DC.DATE(node.getModifedDate()));
    }

    private String getPodcastEntryName(int count, String title) {
        if (configuration.getParameter(Parameter.PREPEND_PODCAST_ENTRY_NAME)) {
            return String.format("%02d %s", count, title);
        }
        else {
            return title;
        }
    }
}
