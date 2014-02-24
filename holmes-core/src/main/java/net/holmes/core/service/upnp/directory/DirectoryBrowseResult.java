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

package net.holmes.core.service.upnp.directory;

import net.holmes.core.business.media.model.AbstractNode;
import net.holmes.core.business.media.model.ContentNode;
import net.holmes.core.business.media.model.RawUrlNode;
import net.holmes.core.common.MimeType;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryErrorCode;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.DIDLObject.Property.DC;
import org.fourthline.cling.support.model.DIDLObject.Property.UPNP;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;

import static net.holmes.core.common.Constants.UPNP_DATE_FORMAT;
import static net.holmes.core.common.MimeType.SUB_TYPE_OGG;
import static net.holmes.core.common.MimeType.SUB_TYPE_SUBTITLE;
import static net.holmes.core.common.UpnpUtils.getUpnpMimeType;

/**
 * UPnP directory browse result.
 */
final class DirectoryBrowseResult {
    private static final DIDLObject.Class CONTAINER_CLASS = new DIDLObject.Class("object.container");

    private final DIDLContent didl;
    private final long firstResult;
    private final long maxResults;

    private long itemCount;
    private long totalCount;

    /**
     * Instantiates a new directory browse result.
     *
     * @param firstResult first result
     * @param maxResults  max results
     */
    public DirectoryBrowseResult(final long firstResult, final long maxResults) {
        this.firstResult = firstResult;
        this.maxResults = maxResults;
        this.didl = new DIDLContent();
        this.itemCount = 0L;
        this.totalCount = 0L;
    }

    public long getItemCount() {
        return itemCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public long getFirstResult() {
        return firstResult;
    }

    public DIDLContent getDidl() {
        return didl;
    }

    /**
     * Get total result count.
     *
     * @return total result count
     */
    public long getResultCount() {
        return itemCount + firstResult;
    }

    /**
     * Build browse result.
     *
     * @return browse result
     * @throws ContentDirectoryException
     */
    public BrowseResult buildBrowseResult() throws ContentDirectoryException {
        try {
            return new BrowseResult(new DIDLParser().generate(didl), itemCount, totalCount);
        } catch (Exception e) {
            throw new ContentDirectoryException(ContentDirectoryErrorCode.CANNOT_PROCESS.getCode(), e.getMessage(), e);
        }
    }

    /**
     * Add item to result.
     *
     * @param parentNodeId parent node id
     * @param contentNode  content node
     * @param url          content url
     * @throws ContentDirectoryException
     */
    public void addItem(final String parentNodeId, final ContentNode contentNode, final String url) throws ContentDirectoryException {
        Res res = new Res(getUpnpMimeType(contentNode.getMimeType()), contentNode.getSize(), url);
        addDidlItem(parentNodeId, contentNode, contentNode.getName(), contentNode.getMimeType(), res);
    }

    /**
     * Add raw Url item to result.
     *
     * @param parentNodeId parent node id
     * @param rawUrlNode   Url node
     * @param entryName    entry name
     * @throws ContentDirectoryException
     */
    public void addUrlItem(final String parentNodeId, final RawUrlNode rawUrlNode, final String entryName) throws ContentDirectoryException {
        MimeType mimeType = rawUrlNode.getMimeType();
        Res res = new Res(getUpnpMimeType(mimeType), null, rawUrlNode.getUrl());
        res.setDuration(rawUrlNode.getDuration());

        addDidlItem(parentNodeId, rawUrlNode, entryName, mimeType, res);
    }

    /**
     * Add item to didl.
     *
     * @param parentNodeId parent node id
     * @param node         node to add
     * @param name         node name
     * @param mimeType     node mimeType
     * @param res          didl resource
     * @throws ContentDirectoryException
     */
    private void addDidlItem(String parentNodeId, AbstractNode node, String name, MimeType mimeType, Res res) throws ContentDirectoryException {
        Item item = null;
        switch (mimeType.getType()) {
            case TYPE_VIDEO:
                // Add video item
                item = new Movie(node.getId(), parentNodeId, name, null, res);
                break;
            case TYPE_AUDIO:
                // Add audio track item
                item = new MusicTrack(node.getId(), parentNodeId, name, null, null, (String) null, res);
                break;
            case TYPE_IMAGE:
                // Add image item
                item = new Photo(node.getId(), parentNodeId, name, null, null, res);
                break;
            case TYPE_APPLICATION:
                if (SUB_TYPE_SUBTITLE.equals(mimeType.getSubType()))
                    // Add subtitle item
                    item = new TextItem(node.getId(), parentNodeId, name, null, res);
                else if (SUB_TYPE_OGG.equals(mimeType.getSubType()))
                    // Add OGG item
                    item = new MusicTrack(node.getId(), parentNodeId, name, null, null, (String) null, res);
                break;
            default:
                break;
        }
        if (item != null) {
            setDidlMetadata(item, node);
            didl.addItem(item);
            itemCount++;
        }
    }

    /**
     * Add container to result.
     *
     * @param parentNodeId parent node id
     * @param node         container node
     * @param childCount   child count
     * @throws ContentDirectoryException
     */
    public void addContainer(final String parentNodeId, final AbstractNode node, final int childCount) throws ContentDirectoryException {
        Container container = new Container(node.getId(), parentNodeId, node.getName(), null, CONTAINER_CLASS, childCount);
        container.setSearchable(true);
        setDidlMetadata(container, node);

        didl.addContainer(container);
        itemCount++;
    }

    /**
     * Check if node can be added to result according to pagination parameters.
     *
     * @return true, if successful
     */
    public boolean acceptNode() {
        totalCount++;
        return maxResults == 0 || itemCount < maxResults && totalCount >= firstResult + 1;
    }

    /**
     * Set the didl metadata.
     *
     * @param didlObject didl object
     * @param node       node
     * @throws ContentDirectoryException
     */
    private void setDidlMetadata(final DIDLObject didlObject, final AbstractNode node) throws ContentDirectoryException {
        if (node.getModifiedDate() != null)
            didlObject.replaceFirstProperty(new DC.DATE(new SimpleDateFormat(UPNP_DATE_FORMAT.toString()).format(node.getModifiedDate())));

        if (node.getIconUrl() != null)
            try {
                didlObject.replaceFirstProperty(new UPNP.ICON(new URI(node.getIconUrl())));
            } catch (URISyntaxException e) {
                throw new ContentDirectoryException(ContentDirectoryErrorCode.CANNOT_PROCESS.getCode(), e.getMessage(), e);
            }
    }
}
