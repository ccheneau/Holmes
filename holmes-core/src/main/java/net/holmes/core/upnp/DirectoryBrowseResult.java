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

import net.holmes.common.media.AbstractNode;
import net.holmes.common.media.ContentNode;
import net.holmes.common.media.PodcastEntryNode;
import net.holmes.common.mimetype.MimeType;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.DIDLObject.Property.DC;
import org.fourthline.cling.support.model.DIDLObject.Property.UPNP;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.PlaylistContainer;
import org.fourthline.cling.support.model.container.StorageFolder;
import org.fourthline.cling.support.model.item.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;

/**
 * UPnP directory browse result.
 */
final class DirectoryBrowseResult {
    private static final String UPNP_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    private long itemCount;
    private long totalCount;
    private final DIDLContent didl;
    private final long firstResult;
    private final long maxResults;

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
     * Adds item to result.
     *
     * @param parentNodeId parent node id
     * @param contentNode  content node
     * @param url          content url
     * @throws URISyntaxException URI syntax exception
     */
    public void addItem(final String parentNodeId, final ContentNode contentNode, final String url) throws URISyntaxException {
        MimeType mimeType = contentNode.getMimeType();
        Res res = new Res(getUpnpMimeType(contentNode.getMimeType()), contentNode.getSize(), url);
        if (contentNode.getResolution() != null) res.setResolution(contentNode.getResolution());

        Item item = null;
        if (mimeType.isVideo()) {
            // Add video item
            item = new Movie(contentNode.getId(), parentNodeId, contentNode.getName(), null, res);
        } else if (mimeType.isAudio()) {
            // Add audio track item
            item = new MusicTrack(contentNode.getId(), parentNodeId, contentNode.getName(), null, null, (String) null, res);
        } else if (mimeType.isImage()) {
            // Add image item
            item = new Photo(contentNode.getId(), parentNodeId, contentNode.getName(), null, null, res);
        } else if (mimeType.isSubtitle()) {
            // Add subtitle item
            item = new TextItem(contentNode.getId(), parentNodeId, contentNode.getName(), null, res);
        }
        if (item != null) {
            setDidlMetadata(item, contentNode);
            addItem(item);
        }
    }

    /**
     * Adds podcast item to result.
     *
     * @param parentNodeId     parent node id
     * @param podcastEntryNode podcast entry node
     * @param entryName        podcast entry name
     * @throws URISyntaxException URI syntax exception
     */
    public void addPodcastItem(final String parentNodeId, final PodcastEntryNode podcastEntryNode, final String entryName) throws URISyntaxException {
        MimeType mimeType = podcastEntryNode.getMimeType();
        Res res = new Res(getUpnpMimeType(mimeType), null, podcastEntryNode.getUrl());
        if (podcastEntryNode.getDuration() != null) res.setDuration(podcastEntryNode.getDuration());

        Item item = null;
        if (mimeType.isAudio()) {
            // Add audio track item
            item = new MusicTrack(podcastEntryNode.getId(), parentNodeId, entryName, null, null, (String) null, res);
        } else if (mimeType.isImage()) {
            // Add image item
            item = new Photo(podcastEntryNode.getId(), parentNodeId, entryName, null, null, res);
        } else if (mimeType.isVideo()) {
            // Add video item
            item = new Movie(podcastEntryNode.getId(), parentNodeId, entryName, null, res);
        }
        if (item != null) {
            setDidlMetadata(item, podcastEntryNode);
            addItem(item);
        }
    }

    /**
     * Adds item to result.
     *
     * @param item item
     */
    private void addItem(final Item item) {
        // Add item to didl
        didl.addItem(item);
        itemCount += 1;
    }

    /**
     * Adds container to result.
     *
     * @param parentNodeId parent node id
     * @param node         container node
     * @param childCount   child count
     * @throws URISyntaxException URI syntax exception
     */
    public void addContainer(final String parentNodeId, final AbstractNode node, final int childCount) throws URISyntaxException {
        StorageFolder folder = new StorageFolder(node.getId(), parentNodeId, node.getName(), null, childCount, null);
        setDidlMetadata(folder, node);

        didl.addContainer(folder);
        itemCount += 1;
    }

    /**
     * Adds the playlist to result.
     *
     * @param parentNodeId parent node id
     * @param node         playlist node
     * @throws URISyntaxException URI syntax exception
     */
    public void addPlaylist(final String parentNodeId, final AbstractNode node) throws URISyntaxException {
        PlaylistContainer playlist = new PlaylistContainer(node.getId(), parentNodeId, node.getName(), null, 1);
        setDidlMetadata(playlist, node);

        didl.addContainer(playlist);
        itemCount += 1;
    }

    /**
     * Filter result according to pagination parameters.
     *
     * @return true, if successful
     */
    public boolean filterResult() {
        totalCount += 1;
        return maxResults == 0 || itemCount < maxResults && totalCount >= firstResult + 1;
    }

    /**
     * Gets UPnP mime type.
     *
     * @param mimeType mime type
     * @return UPnP mime type
     */
    private org.seamless.util.MimeType getUpnpMimeType(final MimeType mimeType) {
        return new org.seamless.util.MimeType(mimeType.getType(), mimeType.getSubType());
    }

    /**
     * Sets the didl metadata.
     *
     * @param didlObject didl object
     * @param node       node
     * @throws URISyntaxException !URI syntax exception
     */
    private void setDidlMetadata(final DIDLObject didlObject, final AbstractNode node) throws URISyntaxException {
        if (node.getModifiedDate() != null)
            didlObject.replaceFirstProperty(new DC.DATE(new SimpleDateFormat(UPNP_DATE_FORMAT).format(node.getModifiedDate())));
        if (node.getIconUrl() != null) didlObject.replaceFirstProperty(new UPNP.ICON(new URI(node.getIconUrl())));
    }
}
