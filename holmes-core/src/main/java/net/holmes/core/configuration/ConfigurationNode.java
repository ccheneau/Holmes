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
 * A ConfigurationNode may represent:
 * <ul>
 * <li>Holmes root node: {@link net.holmes.core.configuration.ConfigurationNode#ROOT_NODE_ID} </li>
 * <li>root node for video folders: {@link net.holmes.core.configuration.ConfigurationNode#ROOT_VIDEO_NODE_ID} </li>
 * <li>root node for audio folders: {@link net.holmes.core.configuration.ConfigurationNode#ROOT_AUDIO_NODE_ID} </li>
 * <li>root node for picture folders: {@link net.holmes.core.configuration.ConfigurationNode#ROOT_PICTURE_NODE_ID} </li>
 * <li>root node for pod-cast URLs: {@link net.holmes.core.configuration.ConfigurationNode#ROOT_PODCAST_NODE_ID} </li>
 * <li>video / audio / picture folders defined in {@link net.holmes.core.configuration.Configuration} </li>
 * <li>pod-cast URLs defined in {@link net.holmes.core.configuration.Configuration} </li>
  *</ul>
 */
public final class ConfigurationNode implements Serializable {
    private static final long serialVersionUID = -476678562488489847L;

    public static final String ROOT_NODE_ID = "0";
    public static final String ROOT_VIDEO_NODE_ID = "1_VIDEOS";
    public static final String ROOT_PICTURE_NODE_ID = "2_PICTURES";
    public static final String ROOT_AUDIO_NODE_ID = "3_AUDIOS";
    public static final String ROOT_PODCAST_NODE_ID = "4_PODCASTS";

    private String id;
    private String label;
    private String path;

    public ConfigurationNode(String id, String label, String path) {
        this.id = id;
        this.label = label;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ContentFolder [id=");
        builder.append(id);
        builder.append(", label=");
        builder.append(label);
        builder.append(", path=");
        builder.append(path);
        builder.append("]");
        return builder.toString();
    }
}
