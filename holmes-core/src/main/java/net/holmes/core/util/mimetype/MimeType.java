/**
* Copyright (C) 2012  Cedric Cheneau
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
package net.holmes.core.util.mimetype;

import java.io.Serializable;

public final class MimeType implements Serializable {
    private static final long serialVersionUID = -1521224459310661472L;

    public final static String TYPE_VIDEO = "video";
    public final static String TYPE_AUDIO = "audio";
    public final static String TYPE_IMAGE = "image";
    public final static String TYPE_PODCAST = "podcast";

    private String type;
    private String subType;
    private String mimeType;

    public MimeType(String mimeType) {
        this.mimeType = mimeType;
        String[] types = mimeType.split("/");
        if (types != null && types.length > 1) {
            this.type = types[0];
            this.subType = types[1];
        }
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
        return TYPE_AUDIO.equals(type);
    }

    public boolean isVideo() {
        return TYPE_VIDEO.equals(type);
    }

    public boolean isImage() {
        return TYPE_IMAGE.equals(type);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
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
