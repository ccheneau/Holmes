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
package net.holmes.core.configuration.xml;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import net.holmes.core.configuration.ConfigurationNode;
import net.holmes.core.configuration.Parameter;

public final class XmlRootNode implements Serializable {
    private static final long serialVersionUID = 1607439493422835211L;

    private String upnpServerName;
    private Integer httpServerPort;
    private LinkedList<ConfigurationNode> videoFolders;
    private LinkedList<ConfigurationNode> pictureFolders;
    private LinkedList<ConfigurationNode> audioFolders;
    private LinkedList<ConfigurationNode> podcasts;
    private Properties parameters;

    public void check() {
        if (this.videoFolders == null) this.videoFolders = new LinkedList<ConfigurationNode>();
        if (this.audioFolders == null) this.audioFolders = new LinkedList<ConfigurationNode>();
        if (this.pictureFolders == null) this.pictureFolders = new LinkedList<ConfigurationNode>();
        if (this.podcasts == null) this.podcasts = new LinkedList<ConfigurationNode>();
        if (this.parameters == null) this.parameters = new Properties();
        for (Parameter param : Parameter.values()) {
            if (this.parameters.getProperty(param.getName()) == null) this.parameters.put(param.getName(), param.getDefaultValue());
        }
    }

    public String getUpnpServerName() {
        return this.upnpServerName;
    }

    public void setUpnpServerName(String upnpServerName) {
        this.upnpServerName = upnpServerName;
    }

    public Integer getHttpServerPort() {
        return this.httpServerPort;
    }

    public void setHttpServerPort(Integer httpServerPort) {
        this.httpServerPort = httpServerPort;
    }

    public List<ConfigurationNode> getVideoFolders() {
        return this.videoFolders;
    }

    public void setVideoFolders(LinkedList<ConfigurationNode> videoFolders) {
        this.videoFolders = videoFolders;
    }

    public List<ConfigurationNode> getPodcasts() {
        return this.podcasts;
    }

    public void setPodcasts(LinkedList<ConfigurationNode> podcasts) {
        this.podcasts = podcasts;
    }

    public List<ConfigurationNode> getAudioFolders() {
        return this.audioFolders;
    }

    public void setAudioFolders(LinkedList<ConfigurationNode> audioFolders) {
        this.audioFolders = audioFolders;
    }

    public List<ConfigurationNode> getPictureFolders() {
        return this.pictureFolders;
    }

    public void setPictureFolders(LinkedList<ConfigurationNode> pictureFolders) {
        this.pictureFolders = pictureFolders;
    }

    public Boolean getParameter(Parameter param) {
        String value = (String) this.parameters.get(param.getName());
        if (value == null) value = param.getDefaultValue();
        return Boolean.parseBoolean(value);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("XmlRootNode [upnpServerName=");
        builder.append(upnpServerName);
        builder.append(", httpServerPort=");
        builder.append(httpServerPort);
        builder.append(", videoFolders=");
        builder.append(videoFolders);
        builder.append(", pictureFolders=");
        builder.append(pictureFolders);
        builder.append(", audioFolders=");
        builder.append(audioFolders);
        builder.append(", podcasts=");
        builder.append(podcasts);
        builder.append(", parameters=");
        builder.append(parameters);
        builder.append("]");
        return builder.toString();
    }
}
