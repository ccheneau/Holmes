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

/**
 * The Class ContentType.
 */
public final class ContentType implements Serializable
{
    private static final long serialVersionUID = -1521224459310661472L;

    /** The Constant TYPE_VIDEO. */
    public final static String TYPE_VIDEO = "video";

    /** The Constant TYPE_AUDIO. */
    public final static String TYPE_AUDIO = "audio";

    /** The Constant TYPE_IMAGE. */
    public final static String TYPE_IMAGE = "image";

    /** The type. */
    private String type;

    /** The sub type. */
    private String subType;

    /** The content type. */
    private String contentType;

    /**
     * Instantiates a new content type.
     *
     * @param contentType the content type
     */
    public ContentType(String contentType)
    {
        this.contentType = contentType;
        String[] types = contentType.split("/");
        if (types != null && types.length > 1)
        {
            this.type = types[0];
            this.subType = types[1];
        }
    }

    /**
     * Gets the content type.
     *
     * @return the content type
     */
    public String getContentType()
    {
        return this.contentType;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType()
    {
        return this.type;
    }

    /**
     * Gets the sub type.
     *
     * @return the sub type
     */
    public String getSubType()
    {
        return this.subType;
    }

    /**
     * Checks if is media.
     *
     * @return true, if is media
     */
    public boolean isMedia()
    {
        return type != null && (type.equals(TYPE_VIDEO) || type.equals(TYPE_AUDIO) || type.equals(TYPE_IMAGE));
    }

    /**
     * Checks if is audio.
     *
     * @return true, if is audio
     */
    public boolean isAudio()
    {
        return TYPE_AUDIO.equals(type);
    }

    /**
     * Checks if is video.
     *
     * @return true, if is video
     */
    public boolean isVideo()
    {
        return TYPE_VIDEO.equals(type);
    }

    /**
     * Checks if is image.
     *
     * @return true, if is image
     */
    public boolean isImage()
    {
        return TYPE_IMAGE.equals(type);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
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
