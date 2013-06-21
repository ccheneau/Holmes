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

package net.holmes.core.media.model;

import com.google.common.base.Objects;
import net.holmes.core.common.mimetype.MimeType;

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
        return Objects.hashCode(id, parentId, name, type, modifiedDate, iconUrl, mimeType, url, duration);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;

        final PodcastEntryNode other = (PodcastEntryNode) obj;
        return Objects.equal(this.mimeType, other.mimeType) && Objects.equal(this.url, other.url) && Objects.equal(this.duration, other.duration);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(mimeType)
                .addValue(url)
                .addValue(duration)
                .addValue(id)
                .addValue(type)
                .addValue(parentId)
                .addValue(name)
                .addValue(modifiedDate)
                .addValue(iconUrl)
                .toString();
    }
}
