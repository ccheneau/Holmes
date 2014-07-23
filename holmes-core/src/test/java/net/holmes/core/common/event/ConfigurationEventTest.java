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

package net.holmes.core.common.event;

import net.holmes.core.business.configuration.ConfigurationNode;
import net.holmes.core.business.media.model.RootNode;
import org.junit.Test;

import static net.holmes.core.common.event.ConfigurationEvent.EventType.ADD_FOLDER;
import static org.junit.Assert.*;

/**
 * ConfigurationEvent Tester.
 */
public class ConfigurationEventTest {

    /**
     * Method: getType()
     */
    @Test
    public void testGetType() throws Exception {
        ConfigurationEvent event = buildConfigurationEvent(null);
        assertEquals(event.getType(), ADD_FOLDER);
    }

    /**
     * Method: getNode()
     */
    @Test
    public void testGetNode() throws Exception {
        ConfigurationNode node = new ConfigurationNode("id", "label", "path");
        ConfigurationEvent event = buildConfigurationEvent(node);
        assertEquals(event.getNode(), node);
    }

    /**
     * Method: getRootNode()
     */
    @Test
    public void testGetRootNode() throws Exception {
        ConfigurationEvent event = buildConfigurationEvent(null);
        assertEquals(event.getRootNode(), RootNode.ROOT);
    }

    /**
     * Method: toString()
     */
    @Test
    public void testToString() throws Exception {
        ConfigurationEvent event = buildConfigurationEvent(null);
        assertNotNull(event.toString());
    }

    private ConfigurationEvent buildConfigurationEvent(ConfigurationNode node) {
        return new ConfigurationEvent(ADD_FOLDER, node, RootNode.ROOT);
    }
}
