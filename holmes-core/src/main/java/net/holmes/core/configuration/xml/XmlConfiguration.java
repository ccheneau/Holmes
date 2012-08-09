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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import net.holmes.core.configuration.ConfigurationNode;
import net.holmes.core.configuration.IConfiguration;
import net.holmes.core.configuration.Parameter;

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
     * Get path to Holmes configuration file 
     */
    private String getConfigFilePath() {
        String userHomeHolmes = System.getProperty("user.home") + File.separator + CONF_FILE_PATH;

        // Create holmes user home directory if it does not exist
        File userHomeHolmesDir = new File(userHomeHolmes);
        if (!userHomeHolmesDir.exists() || !userHomeHolmesDir.isDirectory()) {
            userHomeHolmesDir.mkdir();
        }
        return userHomeHolmes + File.separator + CONF_FILE_NAME;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#loadConfig()
     */
    @Override
    public void loadConfig() {
        rootNode = null;

        XStream xs = getXStream();

        String filePath = getConfigFilePath();
        if (filePath != null) {
            File confFile = new File(filePath);
            if (confFile.exists() && confFile.canRead()) {
                InputStream in = null;
                try {
                    in = new FileInputStream(confFile);
                    rootNode = (XmlRootNode) xs.fromXML(in);
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
        }
        if (rootNode == null) rootNode = new XmlRootNode();
        rootNode.check();

    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#saveConfig()
     */
    @Override
    public void saveConfig() {
        XStream xs = getXStream();

        String filePath = getConfigFilePath();
        if (filePath != null) {
            File confFile = new File(filePath);

            OutputStream out = null;
            try {
                out = new FileOutputStream(confFile);
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
        String upnpServerName = this.rootNode.getUpnpServerName();
        if (upnpServerName == null || upnpServerName.trim().length() == 0) {
            return DEFAULT_UPNP_SERVER_NAME;
        }
        else {
            return upnpServerName;
        }
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
        Integer httpServerPort = rootNode.getHttpServerPort();
        if (httpServerPort == null) {
            return DEFAULT_HTTP_SERVER_PORT;
        }
        else {
            return httpServerPort;
        }
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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.rootNode.toString();
    }
}
