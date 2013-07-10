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

import net.holmes.core.media.model.RootNode;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class XmlConfigurationTest {

    @Test
    public void testXmlConfiguration() {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        try {
            XmlConfigurationImpl configuration = new XmlConfigurationImpl(configDir);
            assertNotNull(configuration.getFolders(RootNode.AUDIO));
            assertNotNull(configuration.getFolders(RootNode.VIDEO));
            assertNotNull(configuration.getFolders(RootNode.PICTURE));
            assertNotNull(configuration.getFolders(RootNode.PODCAST));
            assertNull(configuration.getFolders(RootNode.ROOT));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testXmlConfigurationEmpty() {
        String configDir = new File(this.getClass().getResource("/configurationEmpty").getPath()).getAbsolutePath();
        try {
            XmlConfigurationImpl configuration = new XmlConfigurationImpl(configDir);
            assertNotNull(configuration.getFolders(RootNode.AUDIO));
            assertNotNull(configuration.getFolders(RootNode.VIDEO));
            assertNotNull(configuration.getFolders(RootNode.PICTURE));
            assertNotNull(configuration.getFolders(RootNode.PODCAST));
            assertNull(configuration.getFolders(RootNode.ROOT));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testXmlConfigurationNull() {
        String configDir = new File(this.getClass().getResource("/configurationNull").getPath()).getAbsolutePath();
        try {
            XmlConfigurationImpl configuration = new XmlConfigurationImpl(configDir);
            assertNotNull(configuration.getFolders(RootNode.AUDIO));
            assertNotNull(configuration.getFolders(RootNode.VIDEO));
            assertNotNull(configuration.getFolders(RootNode.PICTURE));
            assertNotNull(configuration.getFolders(RootNode.PODCAST));
            assertNull(configuration.getFolders(RootNode.ROOT));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test(expected = RuntimeException.class)
    public void testXmlConfigurationWithBadPath() {
        String configDir = "///bbb";
        try {
            new XmlConfigurationImpl(configDir);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testXmlConfigurationHttpServerPort() {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        try {
            XmlConfigurationImpl configuration = new XmlConfigurationImpl(configDir);
            assertNotNull(configuration.getHttpServerPort());
            configuration.setHttpServerPort(9999);
            assertEquals(Integer.valueOf(9999), configuration.getHttpServerPort());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testXmlConfigurationUpnpServerName() {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        try {
            XmlConfigurationImpl configuration = new XmlConfigurationImpl(configDir);
            assertNotNull(configuration.getUpnpServerName());
            configuration.setUpnpServerName("testUpnpServerName");
            assertEquals("testUpnpServerName", configuration.getUpnpServerName());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testXmlConfigurationParameter() {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        try {
            XmlConfigurationImpl configuration = new XmlConfigurationImpl(configDir);
            assertTrue(configuration.getParameter(Parameter.ENABLE_SYSTRAY));
            configuration.setParameter(Parameter.ENABLE_SYSTRAY, false);
            assertFalse(configuration.getParameter(Parameter.ENABLE_SYSTRAY));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testXmlConfigurationIntParameter() {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        try {
            XmlConfigurationImpl configuration = new XmlConfigurationImpl(configDir);
            assertEquals(Integer.valueOf(2), configuration.getIntParameter(Parameter.PODCAST_CACHE_EXPIRE_HOURS));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testXmlConfigurationSaveConfig() {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        try {
            XmlConfigurationImpl configuration = new XmlConfigurationImpl(configDir);
            configuration.saveConfig();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
