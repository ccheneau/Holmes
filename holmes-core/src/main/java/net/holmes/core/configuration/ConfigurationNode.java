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
 * <li>video / audio / picture folders defined in {@link net.holmes.core.configuration.IConfiguration} </li>
 * <li>pod-cast URLs defined in {@link net.holmes.core.configuration.IConfiguration} </li>
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
        builder.append("ConfigurationNode [id=");
        builder.append(id);
        builder.append(", label=");
        builder.append(label);
        builder.append(", path=");
        builder.append(path);
        builder.append("]");
        return builder.toString();
    }
}
