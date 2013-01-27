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

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;

import net.holmes.core.media.node.AbstractNode;
import net.holmes.core.media.node.ContentNode;
import net.holmes.core.media.node.PodcastEntryNode;
import net.holmes.core.util.mimetype.MimeType;

import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.DIDLObject.Property.DC;
import org.fourthline.cling.support.model.DIDLObject.Property.UPNP;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.PlaylistContainer;
import org.fourthline.cling.support.model.container.StorageFolder;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.model.item.Movie;
import org.fourthline.cling.support.model.item.MusicTrack;
import org.fourthline.cling.support.model.item.Photo;

public final class DirectoryBrowseResult {
    private static final String UPNP_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    private long itemCount;
    private long totalCount;
    private final DIDLContent didl;
    private final long firstResult;
    private final long maxResults;

    public DirectoryBrowseResult(long firstResult, long maxResults) {
        this.firstResult = firstResult;
        this.maxResults = maxResults;
        this.didl = new DIDLContent();
        this.itemCount = 0l;
        this.totalCount = 0l;
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

    public long getMaxResults() {
        return maxResults;
    }

    public DIDLContent getDidl() {
        return didl;
    }

    public void addItem(String parentNodeId, ContentNode contentNode, String url) {
        MimeType mimeType = contentNode.getMimeType();
        Res res = new Res(getUpnpMimeType(contentNode.getMimeType()), contentNode.getSize(), url);

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
        }
        if (item != null) {
            setMetadata(item, contentNode);
            addItem(item);
        }
    }

    public void addItem(String parentNodeId, PodcastEntryNode podcastEntryNode, String entryName) {
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
            setMetadata(item, podcastEntryNode);
            addItem(item);
        }
    }

    private void addItem(Item item) {
        didl.addItem(item);
        itemCount += 1;
    }

    public void addContainer(String parentNodeId, AbstractNode node, int childCount) {
        StorageFolder folder = new StorageFolder(node.getId(), parentNodeId, node.getName(), null, childCount, null);
        setMetadata(folder, node);

        didl.addContainer(folder);
        itemCount += 1;
    }

    public void addPlaylist(String parentNodeId, AbstractNode node, int childCount) {
        PlaylistContainer playlist = new PlaylistContainer(node.getId(), parentNodeId, node.getName(), null, childCount);
        setMetadata(playlist, node);

        didl.addContainer(playlist);
        itemCount += 1;
    }

    /**
     * Filter result according to pagination parameters
     */
    public boolean filterResult() {
        totalCount += 1;
        return maxResults == 0 || (itemCount < maxResults && totalCount >= firstResult + 1);
    }

    private org.seamless.util.MimeType getUpnpMimeType(MimeType mimeType) {
        return new org.seamless.util.MimeType(mimeType.getType(), mimeType.getSubType());
    }

    private void setMetadata(DIDLObject didlObjet, AbstractNode node) {
        if (node.getModifedDate() != null) didlObjet.replaceFirstProperty(new DC.DATE(new SimpleDateFormat(UPNP_DATE_FORMAT).format(node.getModifedDate())));

        try {
            if (node.getIconUrl() != null) didlObjet.replaceFirstProperty(new UPNP.ICON(new URI(node.getIconUrl())));
        } catch (URISyntaxException ignore) {
        }
    }
}
