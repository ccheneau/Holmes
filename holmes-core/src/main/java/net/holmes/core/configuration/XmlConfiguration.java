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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import net.holmes.core.util.SystemProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public final class XmlConfiguration implements IConfiguration {

    private static Logger logger = LoggerFactory.getLogger(XmlConfiguration.class);

    private static final String CONF_FILE_NAME = "config.xml";
    private static final String CONF_FILE_PATH = ".holmes";

    private XmlRootNode rootNode = null;

    public XmlConfiguration() {
        loadConfig();
    }

    /**
     * Get Holmes configuration file 
     */
    private File getConfigFile() {
        String userHomeHolmes = System.getProperty(SystemProperty.USER_HOME.getValue()) + File.separator + CONF_FILE_PATH;

        // Create holmes user home directory if it does not exist
        File userHomeHolmesDir = new File(userHomeHolmes);
        if (!userHomeHolmesDir.exists() || !userHomeHolmesDir.isDirectory()) {
            userHomeHolmesDir.mkdir();
        }
        return new File(userHomeHolmes + File.separator + CONF_FILE_NAME);
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#loadConfig()
     */
    @Override
    public void loadConfig() {
        rootNode = null;
        boolean configLoaded = false;
        XStream xs = getXStream();

        File confFile = getConfigFile();
        if (confFile.exists() && confFile.canRead()) {
            InputStream in = null;
            try {
                in = new FileInputStream(confFile);
                rootNode = (XmlRootNode) xs.fromXML(in);
                configLoaded = true;
            }
            catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
            finally {
                try {
                    if (in != null) in.close();
                }
                catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        if (rootNode == null) rootNode = new XmlRootNode();
        rootNode.checkValues();
        if (!configLoaded) saveConfig();

    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#saveConfig()
     */
    @Override
    public void saveConfig() {
        XStream xs = getXStream();

        OutputStream out = null;
        try {
            out = new FileOutputStream(getConfigFile());
            xs.toXML(rootNode, out);
        }
        catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            try {
                if (out != null) out.close();
            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private XStream getXStream() {
        XStream xs = new XStream(new DomDriver("UTF-8"));
        xs.alias("config", XmlRootNode.class);
        xs.alias("node", ConfigurationNode.class);

        return xs;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getUpnpServerName()
     */
    @Override
    public String getUpnpServerName() {
        return this.rootNode.getUpnpServerName();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#setUpnpServerName(java.lang.String)
     */
    @Override
    public void setUpnpServerName(String upnpServerName) {
        this.rootNode.setUpnpServerName(upnpServerName);
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getHttpServerPort()
     */
    @Override
    public Integer getHttpServerPort() {
        return rootNode.getHttpServerPort();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#setHttpServerPort(java.lang.Integer)
     */
    @Override
    public void setHttpServerPort(Integer httpServerPort) {
        this.rootNode.setHttpServerPort(httpServerPort);
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getVideoFolders()
     */
    @Override
    public List<ConfigurationNode> getVideoFolders() {
        return this.rootNode.getVideoFolders();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getPodcasts()
     */
    @Override
    public List<ConfigurationNode> getPodcasts() {
        return this.rootNode.getPodcasts();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getAudioFolders()
     */
    @Override
    public List<ConfigurationNode> getAudioFolders() {
        return this.rootNode.getAudioFolders();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getPictureFolders()
     */
    @Override
    public List<ConfigurationNode> getPictureFolders() {
        return this.rootNode.getPictureFolders();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getParameter(net.holmes.core.configuration.Parameter)
     */
    @Override
    public Boolean getParameter(Parameter prop) {
        return this.rootNode.getParameter(prop);
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#setParameter(net.holmes.core.configuration.Parameter, java.lang.Boolean)
     */
    @Override
    public void setParameter(Parameter param, Boolean value) {
        this.rootNode.setParameter(param, value);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.rootNode.toString();
    }

    private static final class XmlRootNode {
        private String upnpServerName;
        private Integer httpServerPort;
        private LinkedList<ConfigurationNode> videoFolders;
        private LinkedList<ConfigurationNode> pictureFolders;
        private LinkedList<ConfigurationNode> audioFolders;
        private LinkedList<ConfigurationNode> podcasts;
        private Properties parameters;

        /**
         * Check config default values
         */
        public void checkValues() {
            if (this.upnpServerName == null) this.upnpServerName = DEFAULT_UPNP_SERVER_NAME;
            if (this.httpServerPort == null) this.httpServerPort = DEFAULT_HTTP_SERVER_PORT;
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

        public List<ConfigurationNode> getPodcasts() {
            return this.podcasts;
        }

        public List<ConfigurationNode> getAudioFolders() {
            return this.audioFolders;
        }

        public List<ConfigurationNode> getPictureFolders() {
            return this.pictureFolders;
        }

        public Boolean getParameter(Parameter param) {
            String value = (String) this.parameters.get(param.getName());
            if (value == null) value = param.getDefaultValue();
            return Boolean.parseBoolean(value);
        }

        public void setParameter(Parameter param, Boolean value) {
            this.parameters.setProperty(param.getName(), value.toString());
        }
    }
}
