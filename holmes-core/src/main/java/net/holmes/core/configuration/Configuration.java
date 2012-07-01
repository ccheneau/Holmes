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

import net.holmes.core.util.LogUtil;

public final class Configuration implements Serializable {
    private static final long serialVersionUID = 1607439493422835211L;

    private static final String DEFAULT_SERVER_NAME = "Holmes";
    private static final String DEFAULT_LOG_LEVEL = "INFO";
    private static final int DEFAULT_HTTP_PORT = 8085;

    private String serverName;
    private Integer httpServerPort;
    private String logLevel;
    private LinkedList<ContentFolder> videoFolders;
    private LinkedList<ContentFolder> pictureFolders;
    private LinkedList<ContentFolder> audioFolders;
    private LinkedList<ContentFolder> podcasts;

    public String getServerName() {
        if (serverName == null || serverName.trim().length() == 0) {
            return DEFAULT_SERVER_NAME;
        }
        else {
            return serverName;
        }
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
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

    public String getLogLevel() {
        if (logLevel == null || logLevel.trim().length() == 0) {
            return DEFAULT_LOG_LEVEL;
        }
        else {
            return logLevel;
        }
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
        LogUtil.setLevel(logLevel);
    }

    public List<ContentFolder> getVideoFolders() {
        return videoFolders;
    }

    public void setVideoFolders(LinkedList<ContentFolder> videoFolders) {
        this.videoFolders = videoFolders;
    }

    public List<ContentFolder> getPodcasts() {
        return podcasts;
    }

    public void setPodcasts(LinkedList<ContentFolder> podcasts) {
        this.podcasts = podcasts;
    }

    public List<ContentFolder> getAudioFolders() {
        return audioFolders;
    }

    public void setAudioFolders(LinkedList<ContentFolder> audioFolders) {
        this.audioFolders = audioFolders;
    }

    public List<ContentFolder> getPictureFolders() {
        return pictureFolders;
    }

    public void setPictureFolders(LinkedList<ContentFolder> pictureFolders) {
        this.pictureFolders = pictureFolders;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Configuration [serverName=");
        builder.append(serverName);
        builder.append(", httpServerPort=");
        builder.append(httpServerPort);
        builder.append(", logLevel=");
        builder.append(logLevel);
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
