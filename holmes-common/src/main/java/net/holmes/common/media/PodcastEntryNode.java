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
package net.holmes.common.media;

import net.holmes.common.mimetype.MimeType;

/**
 * Podcast entry node.
 */
public final class PodcastEntryNode extends AbstractNode {

    private final MimeType mimeType;
    private final String url;
    private final String duration;

    /**
     * Instantiates a new podcast entry node.
     *
     * @param id       node id
     * @param parentId parent node id
     * @param name     node name
     * @param mimeType mime type
     * @param url      url
     * @param duration duration
     */
    public PodcastEntryNode(final String id, final String parentId, final String name, final MimeType mimeType, final String url,
                            final String duration) {
        super(NodeType.TYPE_PODCAST_ENTRY, id, parentId, name);
        this.url = url;
        this.duration = duration;
        this.mimeType = mimeType;
    }

    /**
     * Gets the podcast entry mime type.
     *
     * @return the podcast entry mime type
     */
    public MimeType getMimeType() {
        return this.mimeType;
    }

    /**
     * Gets the podcast entry url.
     *
     * @return the podcast entry url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets the podcast entry duration.
     *
     * @return the podcast entry duration
     */
    public String getDuration() {
        return duration;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((duration == null) ? 0 : duration.hashCode());
        result = prime * result + ((mimeType == null) ? 0 : mimeType.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        PodcastEntryNode other = (PodcastEntryNode) obj;
        if (duration == null) {
            if (other.duration != null) return false;
        } else if (!duration.equals(other.duration)) return false;
        if (mimeType == null) {
            if (other.mimeType != null) return false;
        } else if (!mimeType.equals(other.mimeType)) return false;
        if (url == null) {
            if (other.url != null) return false;
        } else if (!url.equals(other.url)) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PodcastEntryNode [mimeType=");
        builder.append(mimeType);
        builder.append(", url=");
        builder.append(url);
        builder.append(", duration=");
        builder.append(duration);
        builder.append(", id=");
        builder.append(id);
        builder.append(", parentId=");
        builder.append(parentId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", modifiedDate=");
        builder.append(modifiedDate);
        builder.append(", type=");
        builder.append(type);
        builder.append(", iconUrl=");
        builder.append(iconUrl);
        builder.append("]");
        return builder.toString();
    }
}
