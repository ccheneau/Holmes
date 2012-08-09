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

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class TestConfiguration implements IConfiguration {

    private LinkedList<ConfigurationNode> videoFolders;
    private LinkedList<ConfigurationNode> pictureFolders;
    private LinkedList<ConfigurationNode> audioFolders;
    private LinkedList<ConfigurationNode> podcasts;

    public TestConfiguration() {
        videoFolders = new LinkedList<ConfigurationNode>();
        videoFolders.add(getTestContentFolder("videosTest", "/videosTest/"));
        audioFolders = new LinkedList<ConfigurationNode>();
        audioFolders.add(getTestContentFolder("audiosTest", "/audiosTest/"));
        pictureFolders = new LinkedList<ConfigurationNode>();
        pictureFolders.add(getTestContentFolder("imagesTest", "/imagesTest/"));
        podcasts = new LinkedList<ConfigurationNode>();
        podcasts.add(new ConfigurationNode("castcodersTest", "castcodersTest", "http://lescastcodeurs.libsyn.com/rss"));
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#loadConfig()
     */
    @Override
    public void loadConfig() {
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#saveConfig()
     */
    @Override
    public void saveConfig() {
    }

    private ConfigurationNode getTestContentFolder(String label, String path) {
        ConfigurationNode contentFolder = null;

        URL rs = this.getClass().getResource(path);
        if (rs != null) {
            String fpath = new File(rs.getFile()).getAbsolutePath();
            contentFolder = new ConfigurationNode(label, label, fpath);
        }

        return contentFolder;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getUpnpServerName()
     */
    @Override
    public String getUpnpServerName() {
        return DEFAULT_UPNP_SERVER_NAME;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#setUpnpServerName(java.lang.String)
     */
    @Override
    public void setUpnpServerName(String upnpServerName) {

    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getHttpServerPort()
     */
    @Override
    public Integer getHttpServerPort() {
        return DEFAULT_HTTP_SERVER_PORT;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#setHttpServerPort(java.lang.Integer)
     */
    @Override
    public void setHttpServerPort(Integer httpServerPort) {

    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getVideoFolders()
     */
    @Override
    public List<ConfigurationNode> getVideoFolders() {
        return this.videoFolders;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getPodcasts()
     */
    @Override
    public List<ConfigurationNode> getPodcasts() {
        return this.podcasts;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getAudioFolders()
     */
    @Override
    public List<ConfigurationNode> getAudioFolders() {
        return this.audioFolders;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getPictureFolders()
     */
    @Override
    public List<ConfigurationNode> getPictureFolders() {
        return this.pictureFolders;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getParameter(net.holmes.core.configuration.Parameter)
     */
    @Override
    public Boolean getParameter(Parameter param) {
        return Boolean.FALSE;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TestConfiguration [videoFolders=");
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
