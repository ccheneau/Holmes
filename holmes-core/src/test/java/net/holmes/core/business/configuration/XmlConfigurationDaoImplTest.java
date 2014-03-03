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

import static org.junit.Assert.*;

public class XmlConfigurationDaoImplTest {

    @Test
    public void testXmlConfiguration() {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        try {
            XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
            assertNotNull(configuration.getFolders(RootNode.AUDIO));
            assertNotNull(configuration.getFolders(RootNode.VIDEO));
            assertNotNull(configuration.getFolders(RootNode.PICTURE));
            assertNotNull(configuration.getFolders(RootNode.PODCAST));
            assertTrue(configuration.getFolders(RootNode.ROOT).isEmpty());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testXmlConfigurationEmpty() {
        String configDir = new File(this.getClass().getResource("/configurationEmpty").getPath()).getAbsolutePath();
        try {
            XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
            assertNotNull(configuration.getFolders(RootNode.AUDIO));
            assertNotNull(configuration.getFolders(RootNode.VIDEO));
            assertNotNull(configuration.getFolders(RootNode.PICTURE));
            assertNotNull(configuration.getFolders(RootNode.PODCAST));
            assertTrue(configuration.getFolders(RootNode.ROOT).isEmpty());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testXmlConfigurationNull() {
        String configDir = new File(this.getClass().getResource("/configurationNull").getPath()).getAbsolutePath();
        try {
            XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
            assertNotNull(configuration.getFolders(RootNode.AUDIO));
            assertNotNull(configuration.getFolders(RootNode.VIDEO));
            assertNotNull(configuration.getFolders(RootNode.PICTURE));
            assertNotNull(configuration.getFolders(RootNode.PODCAST));
            assertTrue(configuration.getFolders(RootNode.ROOT).isEmpty());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test(expected = RuntimeException.class)
    public void testXmlConfigurationWithBadPath() {
        String configDir = "///bbb";
        try {
            new XmlConfigurationDaoImpl(configDir);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testXmlConfigurationParameter() {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        try {
            XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
            assertTrue(configuration.getBooleanParameter(Parameter.SYSTRAY_ENABLE));
            configuration.setBooleanParameter(Parameter.SYSTRAY_ENABLE, false);
            assertFalse(configuration.getBooleanParameter(Parameter.SYSTRAY_ENABLE));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testXmlConfigurationIntParameter() {
        String configDir = new File(this.getClass().getResource("/configuration").getPath()).getAbsolutePath();
        try {
            XmlConfigurationDaoImpl configuration = new XmlConfigurationDaoImpl(configDir);
            assertEquals(Integer.valueOf(2), configuration.getIntParameter(Parameter.PODCAST_CACHE_EXPIRE_HOURS));
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
