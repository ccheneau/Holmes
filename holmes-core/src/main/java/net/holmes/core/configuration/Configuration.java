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
import java.util.LinkedList;
import java.util.List;

/**
 * Holmes configuration contains:
 * <ul>
 * <li>UPnP server name</li>
 * <li>HTTP server port</li>
 * <li>video folders</li>
 * <li>audio folders</li>
 * <li>picture folder</li>
 * <li>pod-cast URLs</li>
 * </ul>
 *
 */
public final class Configuration implements Serializable {
    private static final long serialVersionUID = 1607439493422835211L;

    private static final String DEFAULT_UPNP_SERVER_NAME = "Holmes";
    private static final int DEFAULT_HTTP_PORT = 8085;

    private String upnpServerName;
    private Integer httpServerPort;
    private LinkedList<ConfigurationNode> videoFolders;
    private LinkedList<ConfigurationNode> pictureFolders;
    private LinkedList<ConfigurationNode> audioFolders;
    private LinkedList<ConfigurationNode> podcasts;

    public String getUpnpServerName() {
        if (upnpServerName == null || upnpServerName.trim().length() == 0) {
            return DEFAULT_UPNP_SERVER_NAME;
        }
        else {
            return upnpServerName;
        }
    }

    public void setUpnpServerName(String upnpServerName) {
        this.upnpServerName = upnpServerName;
    }

    public Integer getHttpServerPort() {
        if (httpServerPort == null) {
            return DEFAULT_HTTP_PORT;
        }
        else {
            return httpServerPort;
        }
    }

    public void setHttpServerPort(Integer httpServerPort) {
        this.httpServerPort = httpServerPort;
    }

    public List<ConfigurationNode> getVideoFolders() {
        return videoFolders;
    }

    public void setVideoFolders(LinkedList<ConfigurationNode> videoFolders) {
        this.videoFolders = videoFolders;
    }

    public List<ConfigurationNode> getPodcasts() {
        return podcasts;
    }

    public void setPodcasts(LinkedList<ConfigurationNode> podcasts) {
        this.podcasts = podcasts;
    }

    public List<ConfigurationNode> getAudioFolders() {
        return audioFolders;
    }

    public void setAudioFolders(LinkedList<ConfigurationNode> audioFolders) {
        this.audioFolders = audioFolders;
    }

    public List<ConfigurationNode> getPictureFolders() {
        return pictureFolders;
    }

    public void setPictureFolders(LinkedList<ConfigurationNode> pictureFolders) {
        this.pictureFolders = pictureFolders;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Configuration [upnpServerName=");
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
        builder.append("]");
        return builder.toString();
    }
}
