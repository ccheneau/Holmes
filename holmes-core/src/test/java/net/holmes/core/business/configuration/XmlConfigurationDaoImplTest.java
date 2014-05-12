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

import net.holmes.core.business.media.model.RootNode;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static net.holmes.core.common.ConfigurationParameter.*;
import static org.junit.Assert.*;

public class XmlConfigurationDaoImplTest {

    @Test
    public void testXmlConfiguration() {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        try {
            XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
            assertNotNull(configuration.getNodes(RootNode.AUDIO));
            assertNotNull(configuration.getNodes(RootNode.VIDEO));
            assertNotNull(configuration.getNodes(RootNode.PICTURE));
            assertNotNull(configuration.getNodes(RootNode.PODCAST));
            assertTrue(configuration.getNodes(RootNode.ROOT).isEmpty());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testXmlConfigurationEmpty() {
        String configDir = new File(this.getClass().getResource("/configurationEmpty").getPath()).getAbsolutePath();
        try {
            XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
            assertNotNull(configuration.getNodes(RootNode.AUDIO));
            assertNotNull(configuration.getNodes(RootNode.VIDEO));
            assertNotNull(configuration.getNodes(RootNode.PICTURE));
            assertNotNull(configuration.getNodes(RootNode.PODCAST));
            assertTrue(configuration.getNodes(RootNode.ROOT).isEmpty());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testXmlConfigurationNull() {
        String configDir = new File(this.getClass().getResource("/configurationNull").getPath()).getAbsolutePath();
        try {
            XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
            assertNotNull(configuration.getNodes(RootNode.AUDIO));
            assertNotNull(configuration.getNodes(RootNode.VIDEO));
            assertNotNull(configuration.getNodes(RootNode.PICTURE));
            assertNotNull(configuration.getNodes(RootNode.PODCAST));
            assertTrue(configuration.getNodes(RootNode.ROOT).isEmpty());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testXmlConfigurationBooleanParameter() {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        try {
            XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
            assertTrue(configuration.getParameter(SYSTRAY_ENABLE));
            configuration.setParameter(SYSTRAY_ENABLE, false);
            assertFalse(configuration.getParameter(SYSTRAY_ENABLE));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testXmlConfigurationIntParameter() {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        try {
            XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
            assertEquals(Integer.valueOf(2), configuration.getParameter(PODCAST_CACHE_EXPIRE_HOURS));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testXmlConfigurationStringParameter() {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        try {
            XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
            assertNotNull(configuration.getParameter(UPNP_SERVER_NAME));
            configuration.setParameter(UPNP_SERVER_NAME, "test");
            assertEquals("test", configuration.getParameter(UPNP_SERVER_NAME));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testXmlConfigurationListParameter() {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        try {
            XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
            assertTrue(configuration.getParameter(ICECAST_GENRE_LIST).size() > 0);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testXmlConfigurationSaveConfig() {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        try {
            XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
            configuration.saveConfig();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
