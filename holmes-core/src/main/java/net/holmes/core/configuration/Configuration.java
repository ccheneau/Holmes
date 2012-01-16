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

/**
 * The Class Configuration.
 */
public final class Configuration implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1607439493422835211L;

    /** The Constant DEFAULT_SERVER_NAME. */
    private static final String DEFAULT_SERVER_NAME = "Holmes";

    /** The Constant DEFAULT_LOG_LEVEL. */
    private static final String DEFAULT_LOG_LEVEL = "INFO";

    /** The server name. */
    private String serverName;

    /** The http server port. */
    private Integer httpServerPort;

    /** The log level. */
    private String logLevel;

    /** The videos. */
    private LinkedList<ContentFolder> videoFolders;

    /** The pictures. */
    private LinkedList<ContentFolder> pictureFolders;

    /** The audios. */
    private LinkedList<ContentFolder> audioFolders;

    /** The podcasts. */
    private LinkedList<ContentFolder> podcasts;

    /**
     * Gets the server name.
     *
     * @return the server name
     */
    public String getServerName()
    {
        if (serverName == null || serverName.trim().length() == 0)
        {
            return DEFAULT_SERVER_NAME;
        }
        else
        {
            return serverName;
        }
    }

    /**
     * Sets the server name.
     *
     * @param serverName the new server name
     */
    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    /**
     * Gets the http server port.
     *
     * @return the http server port
     */
    public Integer getHttpServerPort()
    {
        return httpServerPort;
    }

    /**
     * Sets the http server port.
     *
     * @param httpServerPort the new http server port
     */
    public void setHttpServerPort(Integer httpServerPort)
    {
        this.httpServerPort = httpServerPort;
    }

    /**
     * Gets the log level.
     *
     * @return the log level
     */
    public String getLogLevel()
    {
        if (logLevel == null || logLevel.trim().length() == 0)
        {
            return DEFAULT_LOG_LEVEL;
        }
        else
        {
            return logLevel;
        }
    }

    /**
     * Sets the log level.
     *
     * @param logLevel the new log level
     */
    public void setLogLevel(String logLevel)
    {
        this.logLevel = logLevel;
        LogUtil.setLevel(logLevel);
    }

    /**
     * Gets the video folders.
     *
     * @return the video folders
     */
    public List<ContentFolder> getVideoFolders()
    {
        return videoFolders;
    }

    /**
     * Sets the video folders.
     *
     * @param videoFolders the new video folders
     */
    public void setVideoFolders(LinkedList<ContentFolder> videoFolders)
    {
        this.videoFolders = videoFolders;
    }

    /**
     * Gets the podcasts.
     *
     * @return the podcasts
     */
    public List<ContentFolder> getPodcasts()
    {
        return podcasts;
    }

    /**
     * Sets the podcasts.
     *
     * @param podcasts the new podcasts
     */
    public void setPodcasts(LinkedList<ContentFolder> podcasts)
    {
        this.podcasts = podcasts;
    }

    /**
     * Gets the audio folders.
     *
     * @return the audio folders
     */
    public List<ContentFolder> getAudioFolders()
    {
        return audioFolders;
    }

    /**
     * Sets the audio folders.
     *
     * @param audioFolders the new audio folders
     */
    public void setAudioFolders(LinkedList<ContentFolder> audioFolders)
    {
        this.audioFolders = audioFolders;
    }

    /**
     * Gets the picture folders.
     *
     * @return the picture folders
     */
    public List<ContentFolder> getPictureFolders()
    {
        return pictureFolders;
    }

    /**
     * Sets the picture folders.
     *
     * @param pictureFolders the new picture folders
     */
    public void setPictureFolders(LinkedList<ContentFolder> pictureFolders)
    {
        this.pictureFolders = pictureFolders;
    }

}
