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
import net.holmes.core.media.IMediaService;
import net.holmes.core.model.AbstractNode;
import net.holmes.core.model.ContainerNode;
import net.holmes.core.model.ContentNode;
import net.holmes.core.model.ContentType;
import net.holmes.core.model.PodcastContainerNode;
import net.holmes.core.model.PodcastItemNode;

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
import org.teleal.cling.support.model.DIDLObject.Property.DC;
import org.teleal.cling.support.model.Res;
import org.teleal.cling.support.model.SortCriterion;
import org.teleal.cling.support.model.container.StorageFolder;
import org.teleal.cling.support.model.item.Movie;
import org.teleal.cling.support.model.item.MusicTrack;
import org.teleal.cling.support.model.item.Photo;
import org.teleal.common.util.MimeType;

/**
 * The Class ContentDirectoryService.
 */
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
public final class ContentDirectoryService extends AbstractContentDirectoryService
{
    private static Logger logger = LoggerFactory.getLogger(ContentDirectoryService.class);

    /** The media service. */
    private IMediaService mediaService;

    /** The configuration. */
    private IConfiguration configuration;

    /** The local ip. */
    private String localIp;

    /**
     * Instantiates a new content directory.
     */
    public ContentDirectoryService()
    {
        super( // search caps
                Arrays.asList("dc:title"),
                // sort caps
                Arrays.asList("dc:title"));
        try
        {
            this.localIp = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e)
        {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Sets the media service.
     *
     * @param mediaService the new media service
     */
    public void setMediaService(IMediaService mediaService)
    {
        this.mediaService = mediaService;
    }

    /**
     * Sets the configuration.
     *
     * @param configuration the new configuration
     */
    public void setConfiguration(IConfiguration configuration)
    {
        this.configuration = configuration;
    }

    /* (non-Javadoc)
     * @see org.teleal.cling.support.contentdirectory.AbstractContentDirectoryService#browse(java.lang.String, org.teleal.cling.support.model.BrowseFlag, java.lang.String, long, long, org.teleal.cling.support.model.SortCriterion[])
     */
    @Override
    public BrowseResult browse(String objectID, BrowseFlag browseFlag, String filter, long firstResult, long maxResults, SortCriterion[] orderby)
            throws ContentDirectoryException
    {
        try
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("[START] browse  " + ((browseFlag == BrowseFlag.DIRECT_CHILDREN) ? "DC " : "MD ") + "objectid=" + objectID + " filter=" + filter
                        + " indice=" + firstResult + " nbresults=" + maxResults);

                try
                {
                    String userAgent = ReceivingAction.getRequestMessage().getHeaders().getFirstHeader(UpnpHeader.Type.USER_AGENT).getString();
                    logger.debug("RequestFrom agent: " + userAgent);
                }
                catch (NullPointerException ex)
                {
                    logger.debug("RequestFrom agent: Anonymous");
                }
            }
            int itemCount = 0;
            if (browseFlag == BrowseFlag.DIRECT_CHILDREN)
            {
                DIDLContent didl = new DIDLContent();

                AbstractNode browseNode = mediaService.getNode(objectID);
                logger.debug("browse node:" + browseNode);

                if (browseNode != null)
                {
                    if (browseNode instanceof ContainerNode)
                    {
                        ContainerNode browseContainer = (ContainerNode) browseNode;
                        if (logger.isDebugEnabled()) logger.debug("browse container node:" + browseContainer);
                        List<AbstractNode> childNodes = mediaService.getChildNodes(browseNode);
                        if (childNodes != null && !childNodes.isEmpty())
                        {
                            for (AbstractNode node : childNodes)
                            {
                                if (logger.isDebugEnabled()) logger.debug("add node:" + node);
                                if (node instanceof ContentNode)
                                {
                                    itemCount += addContentItem(objectID, (ContentNode) node, didl);
                                }
                                else if (node instanceof ContainerNode)
                                {
                                    itemCount += addContainerItem(objectID, (ContainerNode) node, didl);
                                }
                                else if (node instanceof PodcastContainerNode)
                                {
                                    itemCount += addPodcastContainerItem(objectID, (PodcastContainerNode) node, didl);
                                }
                            }
                        }
                    }
                    else if (browseNode instanceof PodcastContainerNode)
                    {
                        itemCount += addPodcastItems(objectID, (PodcastContainerNode) browseNode, didl);
                    }
                }
                return new BrowseResult(new DIDLParser().generate(didl), itemCount, itemCount);
            }

        }
        catch (Exception ex)
        {
            throw new ContentDirectoryException(ContentDirectoryErrorCode.CANNOT_PROCESS, ex.getMessage());
        }
        throw new ContentDirectoryException(ContentDirectoryErrorCode.CANNOT_PROCESS);
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
            @UpnpInputArgument(name = "SortCriteria") String orderBy) throws ContentDirectoryException
    {
        SortCriterion[] orderByCriteria;
        try
        {
            orderByCriteria = SortCriterion.valueOf(orderBy);
        }
        catch (Exception ex)
        {
            throw new ContentDirectoryException(ContentDirectoryErrorCode.UNSUPPORTED_SORT_CRITERIA, ex.toString());
        }

        try
        {
            return search(objectId, searchCriteria, filter, firstResult.getValue(), maxResults.getValue(), orderByCriteria);
        }
        catch (ContentDirectoryException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new ContentDirectoryException(ErrorCode.ACTION_FAILED, ex.toString());
        }
    }

    /* (non-Javadoc)
     * @see org.teleal.cling.support.contentdirectory.AbstractContentDirectoryService#search(java.lang.String, java.lang.String, java.lang.String, long, long, org.teleal.cling.support.model.SortCriterion[])
     */
    @Override
    public BrowseResult search(String containerId, String searchCriteria, String filter, long firstResult, long maxResults, SortCriterion[] orderBy)
            throws ContentDirectoryException
    {
        return super.search(containerId, searchCriteria, filter, firstResult, maxResults, orderBy);
    }

    /**
     * Add content item.
     *
     * @param parentNodeId the parent node id
     * @param contentNode the content node
     * @param didl the didl
     * @return number of items created
     */
    private int addContentItem(String parentNodeId, ContentNode contentNode, DIDLContent didl)
    {
        int itemCount = 0;
        StringBuilder url = new StringBuilder();
        url.append("http://").append(localIp).append(":").append(configuration.getConfig().getHttpServerPort());
        url.append("/content?id=");
        url.append(contentNode.getId());

        if (logger.isDebugEnabled())
        {
            logger.debug("add content item:" + contentNode);
            logger.debug("url:" + url);
        }
        MimeType mimeType = new MimeType(contentNode.getContentType().getType(), contentNode.getContentType().getSubType());
        Res res = new Res(mimeType, contentNode.getSize(), url.toString());

        if (contentNode.getContentType().isVideo())
        {
            Movie movie = new Movie(contentNode.getId(), parentNodeId, contentNode.getName(), "", res);
            if (contentNode.getModifedDate() != null) movie.replaceFirstProperty(new DC.DATE(contentNode.getModifedDate()));

            didl.addItem(movie);
            itemCount++;
        }
        else if (contentNode.getContentType().isAudio())
        {
            MusicTrack musicTrack = new MusicTrack(contentNode.getId(), parentNodeId, contentNode.getName(), "", "", "", res);
            if (contentNode.getModifedDate() != null) musicTrack.replaceFirstProperty(new DC.DATE(contentNode.getModifedDate()));

            didl.addItem(musicTrack);
            itemCount++;
        }
        else if (contentNode.getContentType().isImage())
        {
            Photo photo = new Photo(contentNode.getId(), parentNodeId, contentNode.getName(), "", "", res);
            if (contentNode.getModifedDate() != null) photo.replaceFirstProperty(new DC.DATE(contentNode.getModifedDate()));

            didl.addItem(photo);
            itemCount++;
        }
        return itemCount;
    }

    /**
     * Add container item.
     *
     * @param parentNodeId the parent node id
     * @param containerNode the container node
     * @param didl the didl
     * @return number of items created
     */
    private int addContainerItem(String parentNodeId, ContainerNode containerNode, DIDLContent didl)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("add container item:" + containerNode);
        }
        List<AbstractNode> childNodes = mediaService.getChildNodes(containerNode);
        Integer childCount = childNodes != null ? childNodes.size() : 0;
        StorageFolder container = new StorageFolder(containerNode.getId(), parentNodeId, containerNode.getName(), "", childCount, 0L);
        if (containerNode.getModifedDate() != null) container.replaceFirstProperty(new DC.DATE(containerNode.getModifedDate()));

        didl.addContainer(container);

        return 1;
    }

    /**
     * Add podcast container item.
     *
     * @param parentNodeId the parent node id
     * @param podcastContainerNode the podcast container node
     * @param didl the didl
     * @return number of items created
     */
    private int addPodcastContainerItem(String parentNodeId, PodcastContainerNode podcastContainerNode, DIDLContent didl)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("add podcast container item:" + podcastContainerNode);
        }
        StorageFolder container = new StorageFolder(podcastContainerNode.getId(), parentNodeId, podcastContainerNode.getName(), "", 1, 0L);

        didl.addContainer(container);

        return 1;
    }

    /**
     * Add podcast items - Parse RSS feed.
     *
     * @param parentNodeId the parent node id
     * @param browseNode the browse node
     * @param didl the didl
     * @return number of items created
     */
    private int addPodcastItems(String parentNodeId, PodcastContainerNode browseNode, DIDLContent didl)
    {
        int itemCount = 0;
        List<AbstractNode> childNodes = mediaService.getChildNodes(browseNode);
        if (childNodes != null && !childNodes.isEmpty())
        {
            PodcastItemNode podcastItemNode = null;
            for (AbstractNode node : childNodes)
            {
                if (node instanceof PodcastItemNode)
                {
                    podcastItemNode = (PodcastItemNode) node;
                    ContentType feedEntryType = null;
                    MimeType mimeType = null;
                    Res res = null;
                    feedEntryType = podcastItemNode.getContentType();
                    if (feedEntryType.isMedia())
                    {
                        mimeType = new MimeType(feedEntryType.getType(), feedEntryType.getSubType());
                        res = new Res(mimeType, podcastItemNode.getSize(), podcastItemNode.getUrl());
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("add podcast item:" + podcastItemNode.getName() + " " + podcastItemNode.getUrl());
                        }
                        if (feedEntryType.isAudio())
                        {
                            // Add audio item
                            MusicTrack musicTrack = new MusicTrack(UUID.randomUUID().toString(), parentNodeId, podcastItemNode.getName(), "", "", "", res);
                            if (podcastItemNode.getModifedDate() != null) musicTrack.replaceFirstProperty(new DC.DATE(podcastItemNode.getModifedDate()));

                            didl.addItem(musicTrack);
                            itemCount++;
                        }
                        else if (feedEntryType.isImage())
                        {
                            // Add image item
                            Photo photo = new Photo(UUID.randomUUID().toString(), parentNodeId, podcastItemNode.getName(), "", "", res);
                            if (podcastItemNode.getModifedDate() != null) photo.replaceFirstProperty(new DC.DATE(podcastItemNode.getModifedDate()));

                            didl.addItem(photo);
                            itemCount++;

                        }
                        else if (feedEntryType.isVideo())
                        {
                            // Add video item
                            Movie movie = new Movie(UUID.randomUUID().toString(), parentNodeId, podcastItemNode.getName(), "", res);
                            if (podcastItemNode.getModifedDate() != null) movie.replaceFirstProperty(new DC.DATE(podcastItemNode.getModifedDate()));

                            didl.addItem(movie);
                            itemCount++;
                        }
                    }
                }
            }
        }
        return itemCount;
    }
}
