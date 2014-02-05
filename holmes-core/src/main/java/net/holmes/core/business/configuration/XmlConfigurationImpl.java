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
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import net.holmes.core.business.media.model.RootNode;

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
    private static final String CONF_DIR = "conf";
    private final String localHolmesDataDir;
    private final XStream xstream;
    private XmlRootNode rootNode = null;

    /**
     * Instantiates a new xml configuration.
     *
     * @param localHolmesDataDir local Holmes data directory
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Inject
    public XmlConfigurationImpl(@Named("localHolmesDataDir") final String localHolmesDataDir) throws IOException {
        this.localHolmesDataDir = localHolmesDataDir;
        this.xstream = new XStream(new DomDriver("UTF-8"));
        this.xstream.alias("config", XmlRootNode.class);
        this.xstream.alias("node", ConfigurationNode.class);
        this.xstream.ignoreUnknownElements();
        loadConfig();
    }

    /**
     * Get Holmes configuration file.
     *
     * @return configuration file
     */
    private Path getConfigFile() {
        Path confPath = Paths.get(localHolmesDataDir, CONF_DIR);
        if (Files.isDirectory(confPath) || confPath.toFile().mkdirs())
            return Paths.get(confPath.toString(), CONF_FILE_NAME);

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
        if (Files.isReadable(confFile))
            try (InputStream in = new FileInputStream(confFile.toFile())) {
                // Load configuration from XML
                rootNode = (XmlRootNode) xstream.fromXML(in);
                configLoaded = true;
            } catch (FileNotFoundException e) {
                //Ignore
            }

        if (rootNode == null) rootNode = new XmlRootNode();
        rootNode.checkDefaultValues();
        rootNode.checkParameters();

        // Save default config if nothing is loaded
        if (!configLoaded) saveConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveConfig() throws IOException {
        try (OutputStream out = new FileOutputStream(getConfigFile().toFile())) {
            // Save configuration to XML
            xstream.toXML(rootNode, out);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ConfigurationNode> getFolders(final RootNode folderRootNode) {
        List<ConfigurationNode> folders;
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
                folders = Lists.newArrayList();
                break;
        }
        return folders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getBooleanParameter(final Parameter parameter) {
        return Boolean.valueOf(getParameter(parameter));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getIntParameter(final Parameter parameter) {
        return Integer.valueOf(getParameter(parameter));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParameter(final Parameter parameter) {
        return this.rootNode.getParameter(parameter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParameter(Parameter parameter, String value) {
        this.rootNode.setParameter(parameter, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBooleanParameter(final Parameter parameter, final Boolean value) {
        this.rootNode.setParameter(parameter, value.toString());
    }
}
