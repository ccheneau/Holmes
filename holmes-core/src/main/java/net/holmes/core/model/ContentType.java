/**
* Copyright (c) 2012 Cedric Cheneau
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/
package net.holmes.core.model;

import java.io.Serializable;

public final class ContentType implements Serializable {
    private static final long serialVersionUID = -1521224459310661472L;

    public final static String TYPE_VIDEO = "video";
    public final static String TYPE_AUDIO = "audio";
    public final static String TYPE_IMAGE = "image";
    public final static String TYPE_PODCAST = "podcast";

    private String type;
    private String subType;
    private String contentType;

    public ContentType(String contentType) {
        this.contentType = contentType;
        String[] types = contentType.split("/");
        if (types != null && types.length > 1) {
            this.type = types[0];
            this.subType = types[1];
        }
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getType() {
        return this.type;
    }

    public String getSubType() {
        return this.subType;
    }

    public boolean isMedia() {
        return type != null && (type.equals(TYPE_VIDEO) || type.equals(TYPE_AUDIO) || type.equals(TYPE_IMAGE));
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
        builder.append("ContentType [type=");
        builder.append(type);
        builder.append(", subType=");
        builder.append(subType);
        builder.append(", contentType=");
        builder.append(contentType);
        builder.append("]");
        return builder.toString();
    }
}
