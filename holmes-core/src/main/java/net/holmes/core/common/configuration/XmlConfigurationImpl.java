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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import net.holmes.core.media.model.RootNode;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * XML configuration implementation.
 */
public final class XmlConfigurationImpl implements Configuration {

    private static final String CONF_FILE_NAME = "config.xml";
    private static final String CONF_PATH = "conf";
    private final String localHolmesDataDir;
    private XmlRootNode rootNode = null;

    /**
     * Instantiates a new xml configuration.
     *
     * @param localHolmesDataDir local Holmes data directory
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Inject
    public XmlConfigurationImpl(@Named("localHolmesDataDir") String localHolmesDataDir) throws IOException {
        this.localHolmesDataDir = localHolmesDataDir;
        loadConfig();
    }

    /**
     * Get Holmes configuration file.
     *
     * @return configuration file
     */
    private Path getConfigFile() {
        Path confPath = Paths.get(localHolmesDataDir, CONF_PATH);
        if ((Files.exists(confPath) && Files.isDirectory(confPath)) || confPath.toFile().mkdirs()) {
            return Paths.get(confPath.toString(), CONF_FILE_NAME);
        }
        throw new RuntimeException("Failed to create " + confPath);

    }

    /**
     * Load configuration.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void loadConfig() throws IOException {
        boolean configLoaded = false;

        Path confFile = getConfigFile();
        if (Files.exists(confFile) && Files.isReadable(confFile)) {
            try (InputStream in = new FileInputStream(confFile.toFile())) {
                // Load configuration from XML
                rootNode = (XmlRootNode) getXStream().fromXML(in);
                configLoaded = true;
            } catch (FileNotFoundException e) {
                //Ignore
            }
        }
        if (rootNode == null) rootNode = new XmlRootNode();
        rootNode.checkDefaultValues();
        rootNode.checkParameters();

        // Save default config if nothing is loaded
        if (!configLoaded) saveConfig();
    }

    @Override
    public void saveConfig() throws IOException {
        try (OutputStream out = new FileOutputStream(getConfigFile().toFile())) {
            // Save configuration to XML
            getXStream().toXML(rootNode, out);
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
    public Boolean getParameter(final Parameter parameter) {
        return this.rootNode.getParameter(parameter);
    }

    @Override
    public Integer getIntParameter(final Parameter parameter) {
        return this.rootNode.getIntParameter(parameter);
    }

    @Override
    public void setParameter(final Parameter parameter, final Boolean value) {
        this.rootNode.setParameter(parameter, value);
    }
}
