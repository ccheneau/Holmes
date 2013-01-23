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

import net.holmes.core.util.SystemUtils;
import net.holmes.core.util.inject.Loggable;

import org.slf4j.Logger;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Loggable
public final class XmlConfigurationImpl implements Configuration {
    private Logger logger;

    private static final String CONF_FILE_NAME = "config.xml";

    private XmlRootNode rootNode = null;

    public XmlConfigurationImpl() {
        loadConfig();
    }

    /**
     * Get Holmes configuration file 
     */
    private File getConfigFile() {
        File fConfPath = new File(SystemUtils.getLocalUserDataDir(), "conf");
        if (!fConfPath.exists() || !fConfPath.isDirectory())
            if (!fConfPath.mkdirs()) throw new RuntimeException("Failed to create " + fConfPath.getAbsolutePath());

        return new File(fConfPath.getAbsolutePath() + File.separator + CONF_FILE_NAME);
    }

    private void loadConfig() {
        boolean configLoaded = false;

        File confFile = getConfigFile();
        if (confFile.exists() && confFile.canRead()) {
            InputStream in = null;
            try {
                // Load configuration from XML
                in = new FileInputStream(confFile);
                rootNode = (XmlRootNode) getXStream().fromXML(in);
                configLoaded = true;
            } catch (FileNotFoundException ignore) {
            } finally {
                // Close input stream
                try {
                    if (in != null) in.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        if (rootNode == null) rootNode = new XmlRootNode();
        rootNode.checkDefaultValues();

        // Save default config if nothing is loaded
        if (!configLoaded) saveConfig();
    }

    @Override
    public void saveConfig() {
        OutputStream out = null;
        try {
            // Save configuration to XML
            out = new FileOutputStream(getConfigFile());
            getXStream().toXML(rootNode, out);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException e) {
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

    @Override
    public String getUpnpServerName() {
        return this.rootNode.getUpnpServerName();
    }

    @Override
    public void setUpnpServerName(String upnpServerName) {
        this.rootNode.setUpnpServerName(upnpServerName);
    }

    @Override
    public Integer getHttpServerPort() {
        return rootNode.getHttpServerPort();
    }

    @Override
    public void setHttpServerPort(Integer httpServerPort) {
        this.rootNode.setHttpServerPort(httpServerPort);
    }

    @Override
    public String getTheme() {
        return this.rootNode.getTheme();
    }

    @Override
    public void setTheme(String theme) {
        this.rootNode.setTheme(theme);
    }

    @Override
    public List<ConfigurationNode> getVideoFolders() {
        return this.rootNode.getVideoFolders();
    }

    @Override
    public List<ConfigurationNode> getPodcasts() {
        return this.rootNode.getPodcasts();
    }

    @Override
    public List<ConfigurationNode> getAudioFolders() {
        return this.rootNode.getAudioFolders();
    }

    @Override
    public List<ConfigurationNode> getPictureFolders() {
        return this.rootNode.getPictureFolders();
    }

    @Override
    public Boolean getParameter(Parameter prop) {
        return this.rootNode.getParameter(prop);
    }

    @Override
    public void setParameter(Parameter param, Boolean value) {
        this.rootNode.setParameter(param, value);
    }

    @Override
    public String toString() {
        return this.rootNode.toString();
    }

    private static final class XmlRootNode {
        private String upnpServerName;
        private Integer httpServerPort;
        private String theme;
        private LinkedList<ConfigurationNode> videoFolders;
        private LinkedList<ConfigurationNode> pictureFolders;
        private LinkedList<ConfigurationNode> audioFolders;
        private LinkedList<ConfigurationNode> podcasts;
        private Properties parameters;

        /**
         * Check config default values
         */
        public void checkDefaultValues() {
            if (this.upnpServerName == null) this.upnpServerName = DEFAULT_UPNP_SERVER_NAME;
            if (this.httpServerPort == null) this.httpServerPort = DEFAULT_HTTP_SERVER_PORT;
            if (this.theme == null) this.theme = DEFAULT_THEME;
            if (this.videoFolders == null) this.videoFolders = Lists.newLinkedList();
            if (this.audioFolders == null) this.audioFolders = Lists.newLinkedList();
            if (this.pictureFolders == null) this.pictureFolders = Lists.newLinkedList();
            if (this.podcasts == null) this.podcasts = Lists.newLinkedList();
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

        public String getTheme() {
            return this.theme;
        }

        public void setTheme(String theme) {
            this.theme = theme;
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
