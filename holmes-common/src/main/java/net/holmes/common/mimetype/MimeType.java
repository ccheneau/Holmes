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
package net.holmes.common.mimetype;

import net.holmes.common.MediaType;
import net.holmes.common.MediaType.Subtype;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * Mime type.
 */
public final class MimeType {

    private final String type;
    private final String subType;
    private final String mimeType;

    /**
     * Constructor.
     *
     * @param mimeType 
     *      mime type
     */
    public MimeType(final String mimeType) {
        this.mimeType = mimeType;
        Iterable<String> iter = Splitter.on('/').split(mimeType);
        this.type = Iterables.getFirst(iter, "");
        this.subType = Iterables.getLast(iter, "");
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public String getType() {
        return this.type;
    }

    public String getSubType() {
        return this.subType;
    }

    public boolean isMedia() {
        return isAudio() || isVideo() || isImage();
    }

    public boolean isAudio() {
        return MediaType.TYPE_AUDIO.getValue().equals(type);
    }

    public boolean isVideo() {
        return MediaType.TYPE_VIDEO.getValue().equals(type);
    }

    public boolean isImage() {
        return MediaType.TYPE_IMAGE.getValue().equals(type);
    }

    public boolean isSubTitle() {
        return MediaType.TYPE_APPLICATION.getValue().equals(type) && Subtype.SUBTYPE_SUBTITLE.getValue().equals(subType);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mimeType == null) ? 0 : mimeType.hashCode());
        result = prime * result + ((subType == null) ? 0 : subType.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MimeType other = (MimeType) obj;
        if (mimeType == null) {
            if (other.mimeType != null) return false;
        } else if (!mimeType.equals(other.mimeType)) return false;
        if (subType == null) {
            if (other.subType != null) return false;
        } else if (!subType.equals(other.subType)) return false;
        if (type == null) {
            if (other.type != null) return false;
        } else if (!type.equals(other.type)) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MimeType [type=");
        builder.append(type);
        builder.append(", subType=");
        builder.append(subType);
        builder.append(", mimeType=");
        builder.append(mimeType);
        builder.append("]");
        return builder.toString();
    }
}
