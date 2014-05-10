/*
 * Copyright (C) 2012-2014  Cedric Cheneau
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

package net.holmes.core.business.configuration;

import com.google.common.collect.Lists;
import net.holmes.core.common.parameter.ConfigurationParameter;

import java.util.List;
import java.util.Properties;

/**
 * Xml root node: result of Xml configuration deserialization
 */
public final class XmlRootNode {
    private Properties parameters;
    private List<ConfigurationNode> videoFolders;
    private List<ConfigurationNode> pictureFolders;
    private List<ConfigurationNode> audioFolders;
    private List<ConfigurationNode> podcasts;

    /**
     * Check config default values.
     */
    public void checkDefaultValues() {
        if (this.videoFolders == null) this.videoFolders = Lists.newLinkedList();
        if (this.audioFolders == null) this.audioFolders = Lists.newLinkedList();
        if (this.pictureFolders == null) this.pictureFolders = Lists.newLinkedList();
        if (this.podcasts == null) this.podcasts = Lists.newLinkedList();
        if (this.parameters == null) this.parameters = new Properties();
    }

    /**
     * Check configuration parameters.
     */
    @SuppressWarnings("unchecked")
    public void checkParameters() {
        // Check new parameters
        List<String> availableParams = Lists.newArrayList();
        for (ConfigurationParameter param : ConfigurationParameter.PARAMETERS) {
            availableParams.add(param.getName());
            // If a parameter is not present in configuration, add parameter with default value
            if (this.parameters.getProperty(param.getName()) == null)
                this.parameters.put(param.getName(), param.format(param.getDefaultValue()));
        }

        // Check obsolete parameters
        List<String> obsoleteParams = Lists.newArrayList();
        for (Object paramKey : this.parameters.keySet())
            if (!availableParams.contains(paramKey.toString()))
                obsoleteParams.add(paramKey.toString());

        // Remove obsolete parameters
        for (String obsoleteParam : obsoleteParams)
            this.parameters.remove(obsoleteParam);
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
     * Get parameter value.
     *
     * @param parameter parameter name
     * @return parameter value
     */
    public String getParameter(final String parameter) {
        return (String) this.parameters.get(parameter);
    }

    /**
     * Sets the parameter.
     *
     * @param parameter parameter
     * @param value     parameter value
     */
    public void setParameter(final String parameter, final String value) {
        this.parameters.setProperty(parameter, value);
    }
}
