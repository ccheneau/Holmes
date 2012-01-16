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
package net.holmes.core.configuration;

import java.io.Serializable;

/**
 * The Class ContentFolder.
 */
public final class ContentFolder implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -476678562488489847L;

    /** The Constant ROOT_NODE_ID. */
    public static final String ROOT_NODE_ID = "0";

    /** The Constant ROOT_VIDEO_NODE_ID. */
    public static final String ROOT_VIDEO_NODE_ID = "1_VIDEO";

    /** The Constant ROOT_PICTURE_NODE_ID. */
    public static final String ROOT_PICTURE_NODE_ID = "2_PICUTRES";

    /** The Constant ROOT_AUDIO_NODE_ID. */
    public static final String ROOT_AUDIO_NODE_ID = "3_AUDIOS";

    /** The Constant ROOT_PODCAST_NODE_ID. */
    public static final String ROOT_PODCAST_NODE_ID = "4_PODCASTS";

    /** The id. */
    private String id;

    /** The label. */
    private String label;

    /** The path. */
    private String path;

    /**
     * Instantiates a new configuration directory.
     *
     * @param id the id
     * @param label the label
     * @param path the path
     */
    public ContentFolder(String id, String label, String path)
    {
        this.id = id;
        this.label = label;
        this.path = path;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Gets the label.
     *
     * @return the label
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * Sets the label.
     *
     * @param label the new label
     */
    public void setLabel(String label)
    {
        this.label = label;
    }

    /**
     * Gets the path.
     *
     * @return the path
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Sets the path.
     *
     * @param path the new path
     */
    public void setPath(String path)
    {
        this.path = path;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ConfigurationDirectory [id=");
        builder.append(id);
        builder.append(", label=");
        builder.append(label);
        builder.append(", path=");
        builder.append(path);
        builder.append("]");
        return builder.toString();
    }

}
