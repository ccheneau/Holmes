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

package net.holmes.core.business.configuration.dao;

import com.google.common.base.Predicate;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import net.holmes.core.business.configuration.exception.UnknownNodeException;
import net.holmes.core.business.configuration.model.ConfigurationNode;
import net.holmes.core.business.media.model.RootNode;
import net.holmes.core.common.ConfigurationParameter;
import net.holmes.core.common.exception.HolmesRuntimeException;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import static com.google.common.collect.Iterables.find;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * XML configuration dao implementation.
 */
public final class XmlConfigurationDaoImpl implements ConfigurationDao {

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
    public XmlConfigurationDaoImpl(@Named("localHolmesDataDir") final String localHolmesDataDir) throws IOException {
        super();
        this.localHolmesDataDir = localHolmesDataDir;

        // Instantiates a new XStream
        this.xstream = new XStream(new DomDriver(UTF_8.name()));

        // Define XStream aliases
        this.xstream.alias("config", XmlRootNode.class);
        this.xstream.alias("node", ConfigurationNode.class);
        this.xstream.ignoreUnknownElements();

        // Load configuration
        loadConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ConfigurationNode> getNodes(final RootNode rootNode) {
        return this.rootNode.getConfigurationNodes(rootNode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigurationNode getNode(final RootNode rootNode, final String nodeId) throws UnknownNodeException {
        try {
            return find(this.rootNode.getConfigurationNodes(rootNode), new Predicate<ConfigurationNode>() {
                @Override
                public boolean apply(ConfigurationNode node) {
                    return node.getId().equals(nodeId);
                }
            });
        } catch (NoSuchElementException e) {
            throw new UnknownNodeException(nodeId, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigurationNode findNode(final RootNode rootNode, final String excludedNodeId, final String label, final String path) {
        return find(this.rootNode.getConfigurationNodes(rootNode), new Predicate<ConfigurationNode>() {
            @Override
            public boolean apply(ConfigurationNode node) {
                if (excludedNodeId != null && excludedNodeId.equals(node.getId())) {
                    return false;
                } else if (node.getLabel().equals(label) || node.getPath().equals(path)) {
                    return true;
                }
                return false;
            }
        }, null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void save() throws IOException {
        try (OutputStream out = new FileOutputStream(getConfigFile().toFile())) {
            // Save configuration to XML
            xstream.toXML(rootNode, out);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getParameter(final ConfigurationParameter<T> parameter) {
        String value = this.rootNode.getParameter(parameter.getName());
        return value != null ? parameter.parse(value) : parameter.getDefaultValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void setParameter(final ConfigurationParameter<T> parameter, final T value) {
        this.rootNode.setParameter(parameter.getName(), parameter.format(value));
    }

    /**
     * Get Holmes configuration file path.
     *
     * @return configuration file path
     */
    private Path getConfigFile() {
        Path confPath = Paths.get(localHolmesDataDir, CONF_DIR);
        if (Files.isDirectory(confPath) || confPath.toFile().mkdirs()) {
            return Paths.get(confPath.toString(), CONF_FILE_NAME);
        }

        throw new HolmesRuntimeException("Failed to create " + confPath);
    }

    /**
     * Load configuration from Xml file.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void loadConfig() throws IOException {
        boolean configLoaded = false;

        Path confFile = getConfigFile();
        if (Files.isReadable(confFile)) {
            try (InputStream in = new FileInputStream(confFile.toFile())) {
                // Load configuration from XML
                rootNode = (XmlRootNode) xstream.fromXML(in);
                configLoaded = true;
            }
        }

        if (rootNode == null) {
            rootNode = new XmlRootNode();
        }
        rootNode.checkDefaultValues();
        rootNode.checkParameters();

        // Save default config if nothing is loaded
        if (!configLoaded) {
            save();
        }
    }

    /**
     * Xml root node: result of Xml configuration deserialization
     */
    private static final class XmlRootNode {
        private Properties parameters;
        private List<ConfigurationNode> videoFolders;
        private List<ConfigurationNode> pictureFolders;
        private List<ConfigurationNode> audioFolders;
        private List<ConfigurationNode> podcasts;

        /**
         * Check config default values.
         */
        public void checkDefaultValues() {
            if (this.videoFolders == null) {
                this.videoFolders = new ArrayList<>(0);
            }
            if (this.audioFolders == null) {
                this.audioFolders = new ArrayList<>(0);
            }
            if (this.pictureFolders == null) {
                this.pictureFolders = new ArrayList<>(0);
            }
            if (this.podcasts == null) {
                this.podcasts = new ArrayList<>(0);
            }
            if (this.parameters == null) {
                this.parameters = new Properties();
            }
        }

        /**
         * Check configuration parameters.
         */
        @SuppressWarnings("unchecked")
        public void checkParameters() {
            // Check new parameters
            List<String> availableParams = new ArrayList<>(ConfigurationParameter.PARAMETERS.size());
            for (ConfigurationParameter param : ConfigurationParameter.PARAMETERS) {
                availableParams.add(param.getName());
                // If a parameter is not present in configuration, add parameter with default value
                if (this.parameters.getProperty(param.getName()) == null) {
                    this.parameters.put(param.getName(), param.format(param.getDefaultValue()));
                }
            }

            // Check obsolete parameters
            List<String> obsoleteParams = new ArrayList<>();
            for (Object paramKey : this.parameters.keySet()) {
                if (!availableParams.contains(paramKey.toString())) {
                    obsoleteParams.add(paramKey.toString());
                }
            }

            // Remove obsolete parameters
            for (String obsoleteParam : obsoleteParams) {
                this.parameters.remove(obsoleteParam);
            }
        }

        /**
         * Gets configuration nodes.
         *
         * @param rootNode root node
         * @return configuration nodes corresponding to root node
         */
        public List<ConfigurationNode> getConfigurationNodes(final RootNode rootNode) {
            List<ConfigurationNode> configurationNodes;
            switch (rootNode) {
                case AUDIO:
                    configurationNodes = this.audioFolders;
                    break;
                case PICTURE:
                    configurationNodes = this.pictureFolders;
                    break;
                case PODCAST:
                    configurationNodes = this.podcasts;
                    break;
                case VIDEO:
                    configurationNodes = this.videoFolders;
                    break;
                default:
                    configurationNodes = new ArrayList<>(0);
                    break;
            }
            return configurationNodes;
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
}
