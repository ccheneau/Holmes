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

package com.sun.syndication.feed.module.mediarss.types;

import com.sun.syndication.feed.impl.EqualsBean;

import java.io.Serializable;

/**
 * <strong>&lt;media:group&gt;</strong></p>
 * <p>&lt;media:group&gt; is a sub-element of &lt;item&gt;. It allows grouping of &lt;media:content&gt; elements that are effectively the same content, yet different representations.&nbsp;For instance: the same song recorded in both the WAV and MP3 format.
 * It's an optional element that must only be used for this purpose.</p>
 *
 * @author cooper
 */
public final class MediaGroup implements Cloneable, Serializable {
    private static final long serialVersionUID = 768465435081309082L;
    private Integer defaultContentIndex;
    private Metadata metadata;
    private MediaContent[] contents;

    /**
     * Creates a new instance of MediaGroup
     *
     * @param contents Contents of the group.
     */
    public MediaGroup(final MediaContent[] contents) {
        this.setContents(contents);
    }

    /**
     * Creates a new instance of MediaGroup
     *
     * @param contents            contents of the group
     * @param defaultContentIndex index of the default content value.
     */
    public MediaGroup(final MediaContent[] contents, final Integer defaultContentIndex) {
        this.setContents(contents);
        this.setDefaultContentIndex(defaultContentIndex);
    }

    /**
     * Creates a new instance of MediaGroup
     *
     * @param contents            contents of the group
     * @param defaultContentIndex index of the default content item.
     * @param metadata            metadata for the group.
     */
    private MediaGroup(final MediaContent[] contents, final Integer defaultContentIndex, final Metadata metadata) {
        this.setContents(contents);
        this.setDefaultContentIndex(defaultContentIndex);
        this.setMetadata(metadata);
    }

    /**
     * MediaContents for the group
     *
     * @return MediaContents for the group
     */
    public MediaContent[] getContents() {
        return contents;
    }

    /**
     * MediaContents for the group
     *
     * @param contents MediaContents for the group
     */
    private void setContents(final MediaContent[] contents) {
        this.contents = (contents == null) ? new MediaContent[0] : contents;
    }

    /**
     * Default content index MediaContent.
     *
     * @param defaultContentIndex Default content index MediaContent.
     */
    public void setDefaultContentIndex(final Integer defaultContentIndex) {
        for (int i = 0; i < getContents().length; i++) {
            getContents()[i].setDefaultContent(i == defaultContentIndex);
        }

        this.defaultContentIndex = defaultContentIndex;
    }

    /**
     * Metadata for the group
     *
     * @return Metadata for the group
     */
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * Metadata for the group
     *
     * @param metadata Metadata for the group
     */
    public void setMetadata(final Metadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        return new MediaGroup(contents, defaultContentIndex, metadata);
    }

    @Override
    public boolean equals(final Object obj) {
        return new EqualsBean(this.getClass(), this).beanEquals(obj);
    }

    @Override
    public int hashCode() {
        return new EqualsBean(this.getClass(), this).beanHashCode();
    }
}
