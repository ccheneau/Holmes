/**
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
package net.holmes.common.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import net.holmes.common.SystemUtils;
import net.holmes.common.media.RootNode;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * XML configuration implementation.
 */
public final class XmlConfigurationImpl implements Configuration {
    private static final String CONF_FILE_NAME = "config.xml";
    private static final String CONF_PATH = "conf";
    private XmlRootNode rootNode = null;

    /**
     * Instantiates a new xml configuration impl.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public XmlConfigurationImpl() throws IOException {
        loadConfig();
    }

    /**
     * Get Holmes configuration file.
     *
     * @return configuration file
     */
    private File getConfigFile() {
        File fConfPath = new File(SystemUtils.getLocalHolmesDataDir(), CONF_PATH);
        if ((!fConfPath.exists() || !fConfPath.isDirectory()) && !fConfPath.mkdirs())
            throw new RuntimeException("Failed to create " + fConfPath.getAbsolutePath());

        return new File(fConfPath.getAbsolutePath() + File.separator + CONF_FILE_NAME);
    }

    /**
     * Load configuration.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void loadConfig() throws IOException {
        boolean configLoaded = false;

        File confFile = getConfigFile();
        if (confFile.exists() && confFile.canRead()) {
            InputStream in = null;
            try {
                // Load configuration from XML
                in = new FileInputStream(confFile);
                rootNode = (XmlRootNode) getXStream().fromXML(in);
                configLoaded = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace(System.err);
            } finally {
                // Close input stream
                if (in != null) in.close();
            }
        }
        if (rootNode == null) rootNode = new XmlRootNode();
        rootNode.checkDefaultValues();

        // Save default config if nothing is loaded
        if (!configLoaded) saveConfig();
    }

    @Override
    public void saveConfig() throws IOException {
        OutputStream out = null;
        try {
            // Save configuration to XML
            out = new FileOutputStream(getConfigFile());
            getXStream().toXML(rootNode, out);
        } finally {
            if (out != null) out.close();
        }
    }

    /**
     * Gets XStream.
     *
     * @return XStream
     */
    private XStream getXStream() {
        XStream xs = new XStream(new DomDriver("UTF-8"));
        xs.alias("config", XmlRootNode.class);
        xs.alias("node", ConfigurationNode.class);

        return xs;
    }

    @Override
    public String getUpnpServerName() {
        return this.rootNode.getUpnpServerName();
    }

    @Override
    public void setUpnpServerName(final String upnpServerName) {
        this.rootNode.setUpnpServerName(upnpServerName);
    }

    @Override
    public Integer getHttpServerPort() {
        return rootNode.getHttpServerPort();
    }

    @Override
    public void setHttpServerPort(final Integer httpServerPort) {
        this.rootNode.setHttpServerPort(httpServerPort);
    }

    @Override
    public List<ConfigurationNode> getFolders(final RootNode folderRootNode) {
        List<ConfigurationNode> folders = null;
        switch (folderRootNode) {
        case AUDIO:
            folders = this.rootNode.getAudioFolders();
            break;
        case PICTURE:
            folders = this.rootNode.getPictureFolders();
            break;
        case PODCAST:
            folders = this.rootNode.getPodcasts();
            break;
        case VIDEO:
            folders = this.rootNode.getVideoFolders();
            break;

        default:
            break;
        }
        return folders;
    }

    @Override
    public Boolean getParameter(final Parameter param) {
        return this.rootNode.getParameter(param);
    }

    @Override
    public Integer getIntParameter(final Parameter param) {
        return this.rootNode.getIntParameter(param);
    }

    @Override
    public void setParameter(final Parameter param, final Boolean value) {
        this.rootNode.setParameter(param, value);
    }

    @Override
    public String toString() {
        return this.rootNode.toString();
    }

    /**
     * Xml root node.
     */
    private final class XmlRootNode {

        private String upnpServerName;
        private Integer httpServerPort;
        private List<ConfigurationNode> videoFolders;
        private List<ConfigurationNode> pictureFolders;
        private List<ConfigurationNode> audioFolders;
        private List<ConfigurationNode> podcasts;
        private Properties parameters;

        @SuppressWarnings("unused")
        @Deprecated
        private transient String theme;

        /**
         * Check config default values.
         */
        public void checkDefaultValues() {
            if (Strings.isNullOrEmpty(this.upnpServerName)) this.upnpServerName = DEFAULT_UPNP_SERVER_NAME;
            if (this.httpServerPort == null || this.httpServerPort <= Configuration.MIN_HTTP_SERVER_PORT) this.httpServerPort = DEFAULT_HTTP_SERVER_PORT;
            if (this.videoFolders == null) this.videoFolders = Lists.newLinkedList();
            if (this.audioFolders == null) this.audioFolders = Lists.newLinkedList();
            if (this.pictureFolders == null) this.pictureFolders = Lists.newLinkedList();
            if (this.podcasts == null) this.podcasts = Lists.newLinkedList();
            if (this.parameters == null) this.parameters = new Properties();
            for (Parameter param : Parameter.values()) {
                if (this.parameters.getProperty(param.getName()) == null) this.parameters.put(param.getName(), param.getDefaultValue());
            }
        }

        /**
         * Gets the upnp server name.
         *
         * @return the upnp server name
         */
        public String getUpnpServerName() {
            return this.upnpServerName;
        }

        /**
         * Sets the upnp server name.
         *
         * @param upnpServerName the new upnp server name
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
         * @param param 
         *      parameter
         * @return parameter boolean value
         */
        public Boolean getParameter(final Parameter param) {
            String value = (String) this.parameters.get(param.getName());
            if (value == null) value = param.getDefaultValue();
            return Boolean.valueOf(value);
        }

        /**
         * Gets int parameter value.
         *
         * @param param 
         *      parameter
         * @return int parameter value
         */
        public Integer getIntParameter(final Parameter param) {
            String value = (String) this.parameters.get(param.getName());
            if (value == null) value = param.getDefaultValue();
            return Integer.valueOf(value);
        }

        /**
         * Sets the parameter.
         *
         * @param param 
         *      parameter
         * @param value 
         *      parameter value
         */
        public void setParameter(final Parameter param, final Boolean value) {
            this.parameters.setProperty(param.getName(), value.toString());
        }
    }
}
