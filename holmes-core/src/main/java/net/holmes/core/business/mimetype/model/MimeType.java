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

package net.holmes.core.business.mimetype.model;

import com.google.common.base.Splitter;
import net.holmes.core.common.MediaType;

import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.collect.Iterables.*;
import static net.holmes.core.common.MediaType.*;

/**
 * Mime type.
 */
public final class MimeType {
    public static final MimeType MIME_TYPE_SUBTITLE = new MimeType("application/x-subrip");
    public static final MimeType MIME_TYPE_OGG = new MimeType("application/ogg");

    private final MediaType type;
    private final String subType;
    private final String mimeType;

    /**
     * Instantiates a new mime type.
     *
     * @param mimeType mime type
     */
    private MimeType(final String mimeType) {
        this.mimeType = mimeType;
        Iterable<String> iterable = Splitter.on('/').split(mimeType);
        this.type = MediaType.getByValue(getFirst(iterable, ""));
        this.subType = getLast(iterable, "");
    }

    /**
     * Get mime type
     *
     * @param mimeType mime type string
     * @return mime type or null
     */
    public static MimeType valueOf(final String mimeType) {
        return mimeType == null ? null : new MimeType(mimeType);
    }

    /**
     * Gets the mime type.
     *
     * @return the mime type
     */
    public String getMimeType() {
        return this.mimeType;
    }

    /**
     * Gets the sub type.
     *
     * @return the sub type
     */
    public String getSubType() {
        return this.subType;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public MediaType getType() {
        return this.type;
    }

    /**
     * Checks mimeType is a media.
     *
     * @return true if mimeType is a media
     */
    public boolean isMedia() {
        return TYPE_AUDIO == type || TYPE_IMAGE == type || TYPE_VIDEO == type || TYPE_ANY == type;
    }

    /**
     * Checks mimeType is a subTitle.
     *
     * @return true if mimeType is a subtitle
     */
    public boolean isSubTitle() {
        return this.equals(MIME_TYPE_SUBTITLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(mimeType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final MimeType other = (MimeType) obj;
        return Objects.equals(this.mimeType, other.mimeType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toStringHelper(this)
                .add("type", type)
                .add("subType", subType)
                .add("mimeType", mimeType)
                .toString();
    }
}
