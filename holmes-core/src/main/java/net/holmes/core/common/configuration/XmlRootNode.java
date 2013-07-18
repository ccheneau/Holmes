/*
 * Copyright (C) 2012-2013  Cedric Cheneau
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

package net.holmes.core.common.configuration;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Properties;

import static net.holmes.core.common.configuration.Configuration.*;

/**
 * Xml root node.
 */
public final class XmlRootNode {

    private String upnpServerName;
    private Integer httpServerPort;
    private List<ConfigurationNode> videoFolders;
    private List<ConfigurationNode> pictureFolders;
    private List<ConfigurationNode> audioFolders;
    private List<ConfigurationNode> podcasts;
    private Properties parameters;

    /**
     * Check config default values.
     */
    public void checkDefaultValues() {
        if (Strings.isNullOrEmpty(this.upnpServerName)) this.upnpServerName = DEFAULT_UPNP_SERVER_NAME;
        if (this.httpServerPort == null || this.httpServerPort <= MIN_HTTP_SERVER_PORT)
            this.httpServerPort = DEFAULT_HTTP_SERVER_PORT;
        if (this.videoFolders == null) this.videoFolders = Lists.newLinkedList();
        if (this.audioFolders == null) this.audioFolders = Lists.newLinkedList();
        if (this.pictureFolders == null) this.pictureFolders = Lists.newLinkedList();
        if (this.podcasts == null) this.podcasts = Lists.newLinkedList();
        if (this.parameters == null) this.parameters = new Properties();


    }

    /**
     * Check configuration parameters.
     */
    public void checkParameters() {
        // Check new parameters
        List<String> availableParams = Lists.newArrayList();
        for (Parameter param : Parameter.values()) {
            availableParams.add(param.getName());
            if (this.parameters.getProperty(param.getName()) == null) {
                this.parameters.put(param.getName(), param.getDefaultValue());
            }
        }
        // Check obsolete parameters
        List<String> obsoleteParams = Lists.newArrayList();
        for (Object paramKey : this.parameters.keySet()) {
            if (!availableParams.contains(paramKey.toString())) {
                obsoleteParams.add(paramKey.toString());
            }
        }
        // Remove obsolete parameters
        for (String obsoleteParam : obsoleteParams)
            this.parameters.remove(obsoleteParam);

    }

    /**
     * Gets the UPnP server name.
     *
     * @return the UPnP server name
     */
    public String getUpnpServerName() {
        return this.upnpServerName;
    }

    /**
     * Sets the UPnP server name.
     *
     * @param upnpServerName the new UPnP server name
     */
    public void setUpnpServerName(final String upnpServerName) {
        this.upnpServerName = upnpServerName;
    }

    /**
     * Gets the http server port.
     *
     * @return the http server port
     */
    public Integer getHttpServerPort() {
        return this.httpServerPort;
    }

    /**
     * Sets the http server port.
     *
     * @param httpServerPort the new http server port
     */
    public void setHttpServerPort(final Integer httpServerPort) {
        this.httpServerPort = httpServerPort;
    }

    /**
     * Gets the video folders.
     *
     * @return the video folders
     */
    public List<ConfigurationNode> getVideoFolders() {
        return this.videoFolders;
    }

    /**
     * Gets the podcasts.
     *
     * @return the podcasts
     */
    public List<ConfigurationNode> getPodcasts() {
        return this.podcasts;
    }

    /**
     * Gets the audio folders.
     *
     * @return the audio folders
     */
    public List<ConfigurationNode> getAudioFolders() {
        return this.audioFolders;
    }

    /**
     * Gets the picture folders.
     *
     * @return the picture folders
     */
    public List<ConfigurationNode> getPictureFolders() {
        return this.pictureFolders;
    }

    /**
     * Gets parameter.
     *
     * @param parameter parameter
     * @return parameter boolean value
     */
    public Boolean getParameter(final Parameter parameter) {
        String value = (String) this.parameters.get(parameter.getName());
        return Boolean.valueOf(value == null ? parameter.getDefaultValue() : value);
    }

    /**
     * Gets int parameter value.
     *
     * @param parameter parameter
     * @return int parameter value
     */
    public Integer getIntParameter(final Parameter parameter) {
        String value = (String) this.parameters.get(parameter.getName());
        return Integer.valueOf(value == null ? parameter.getDefaultValue() : value);
    }

    /**
     * Sets the parameter.
     *
     * @param parameter parameter
     * @param value     parameter value
     */
    public void setParameter(final Parameter parameter, final Boolean value) {
        this.parameters.setProperty(parameter.getName(), value.toString());
    }
}
