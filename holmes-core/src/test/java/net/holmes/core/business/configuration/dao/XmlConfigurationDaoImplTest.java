/*
 * Copyright (C) 2012-2015  Cedric Cheneau
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

import net.holmes.core.business.configuration.exception.UnknownNodeException;
import net.holmes.core.business.configuration.model.ConfigurationNode;
import net.holmes.core.business.media.model.RootNode;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static net.holmes.core.business.media.model.RootNode.VIDEO;
import static net.holmes.core.common.ConfigurationParameter.*;
import static org.junit.Assert.*;

public class XmlConfigurationDaoImplTest {

    @Test
    public void testXmlConfiguration() throws IOException {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
        assertNotNull(configuration.getNodes(RootNode.AUDIO));
        assertNotNull(configuration.getNodes(VIDEO));
        assertNotNull(configuration.getNodes(RootNode.PICTURE));
        assertNotNull(configuration.getNodes(RootNode.PODCAST));
        assertTrue(configuration.getNodes(RootNode.ROOT).isEmpty());
    }

    @Test
    public void testXmlConfigurationEmpty() throws IOException {
        String configDir = new File(this.getClass().getResource("/configurationEmpty").getPath()).getAbsolutePath();
        XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
        assertNotNull(configuration.getNodes(RootNode.AUDIO));
        assertNotNull(configuration.getNodes(VIDEO));
        assertNotNull(configuration.getNodes(RootNode.PICTURE));
        assertNotNull(configuration.getNodes(RootNode.PODCAST));
        assertTrue(configuration.getNodes(RootNode.ROOT).isEmpty());
    }

    @Test
    public void testXmlConfigurationNull() throws IOException {
        String configDir = new File(this.getClass().getResource("/configurationNull").getPath()).getAbsolutePath();
        XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
        assertNotNull(configuration.getNodes(RootNode.AUDIO));
        assertNotNull(configuration.getNodes(VIDEO));
        assertNotNull(configuration.getNodes(RootNode.PICTURE));
        assertNotNull(configuration.getNodes(RootNode.PODCAST));
        assertTrue(configuration.getNodes(RootNode.ROOT).isEmpty());
    }

    @Test
    public void testGetConfigurationNode() throws IOException, UnknownNodeException {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
        ConfigurationNode node = configuration.getNode(VIDEO, "video1");
        assertNotNull(node);
    }

    @Test(expected = UnknownNodeException.class)
    public void testGetBadConfigurationNode() throws IOException, UnknownNodeException {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
        ConfigurationNode node = configuration.getNode(VIDEO, "badVideo");
        assertNotNull(node);
    }

    @Test
    public void testXmlConfigurationBooleanParameter() throws IOException {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
        assertTrue(configuration.getParameter(SYSTRAY_ENABLE));
        configuration.setParameter(SYSTRAY_ENABLE, false);
        assertFalse(configuration.getParameter(SYSTRAY_ENABLE));
    }

    @Test
    public void testXmlConfigurationIntParameter() throws IOException {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
        assertEquals(Integer.valueOf(2), configuration.getParameter(PODCAST_CACHE_EXPIRE_HOURS));
    }

    @Test
    public void testXmlConfigurationStringParameter() throws IOException {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
        assertNotNull(configuration.getParameter(UPNP_SERVER_NAME));
        configuration.setParameter(UPNP_SERVER_NAME, "test");
        assertEquals("test", configuration.getParameter(UPNP_SERVER_NAME));
    }

    @Test
    public void testXmlConfigurationSaveConfig() throws IOException {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
        configuration.save();
    }

    @Test
    public void testFindConfigurationNodeExcluded() throws IOException, UnknownNodeException {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
        ConfigurationNode node = configuration.getNode(VIDEO, "video1");
        assertNotNull(node);

        Optional<ConfigurationNode> foundNode = configuration.findNode(VIDEO, "video1", node.getLabel(), node.getPath());
        assertFalse(foundNode.isPresent());
    }

    @Test
    public void testFindConfigurationNodeWithSameLabel() throws IOException, UnknownNodeException {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
        ConfigurationNode node = configuration.getNode(VIDEO, "video1");
        assertNotNull(node);

        Optional<ConfigurationNode> foundNode = configuration.findNode(VIDEO, null, node.getLabel(), "");
        assertNotNull(foundNode);
        assertTrue(foundNode.isPresent());
        assertEquals(node, foundNode.get());
    }

    @Test
    public void testFindConfigurationNodeWithSamePath() throws IOException, UnknownNodeException {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
        ConfigurationNode node = configuration.getNode(VIDEO, "video1");
        assertNotNull(node);

        Optional<ConfigurationNode> foundNode = configuration.findNode(VIDEO, null, "", node.getPath());
        assertNotNull(foundNode);
        assertTrue(foundNode.isPresent());
        assertEquals(node, foundNode.get());
    }

    @Test
    public void testFindConfigurationNodeUnknownNode() throws IOException, UnknownNodeException {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
        ConfigurationNode node = configuration.getNode(VIDEO, "video1");
        assertNotNull(node);

        Optional<ConfigurationNode> foundNode = configuration.findNode(VIDEO, "bad_id", "", "");
        assertFalse(foundNode.isPresent());
    }

}
