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

package net.holmes.core.common.mimetype;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import net.holmes.core.common.MediaType;

import java.util.List;

import static net.holmes.core.common.MediaType.*;

/**
 * Mime type.
 */
public final class MimeType {

    public static final String SUB_TYPE_SUBTITLE = "x-subrip";
    public static final String SUB_TYPE_OGG = "ogg";
    private final MediaType type;
    private final String subType;
    private final String mimeType;

    /**
     * Instantiates a new mime type.
     *
     * @param mimeType mime type
     */
    public MimeType(final String mimeType) {
        this.mimeType = mimeType;
        Iterable<String> iterable = Splitter.on('/').split(mimeType);
        this.type = MediaType.getByValue(Iterables.getFirst(iterable, ""));
        this.subType = Iterables.getLast(iterable, "");
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
        return TYPE_APPLICATION == type && SUB_TYPE_SUBTITLE.equals(subType);
    }

    /**
     * Check mime type is compliant with available mime types.
     *
     * @param availableMimeTypes available mime types
     * @return true is mime type is compliant
     */
    public boolean isCompliant(final List<String> availableMimeTypes) {
        if (availableMimeTypes == null || availableMimeTypes.isEmpty() || availableMimeTypes.contains(mimeType))
            return true;
        else
            for (String availableMimeType : availableMimeTypes)
                if (availableMimeType.equals("*/*") || availableMimeType.equals(type.getValue() + "/*"))
                    return true;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type, subType, mimeType);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        final MimeType other = (MimeType) obj;
        return Objects.equal(this.type, other.type) && Objects.equal(this.subType, other.subType) && Objects.equal(this.mimeType, other.mimeType);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(type)
                .addValue(subType)
                .addValue(mimeType)
                .toString();
    }
}
