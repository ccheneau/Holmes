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

package net.holmes.common.event;

import net.holmes.common.configuration.ConfigurationNode;
import net.holmes.common.media.RootNode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * ConfigurationEvent Tester.
 */
public class ConfigurationEventTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: getType()
     */
    @Test
    public void testGetType() throws Exception {
        ConfigurationEvent event = buildConfigurationEvent(null);
        Assert.assertEquals(event.getType(), ConfigurationEvent.EventType.ADD);
    }

    /**
     * Method: getNode()
     */
    @Test
    public void testGetNode() throws Exception {
        ConfigurationNode node = new ConfigurationNode("id", "label", "path");
        ConfigurationEvent event = buildConfigurationEvent(node);
        Assert.assertEquals(event.getNode(), node);
    }

    /**
     * Method: getRootNode()
     */
    @Test
    public void testGetRootNode() throws Exception {
        ConfigurationEvent event = buildConfigurationEvent(null);
        Assert.assertEquals(event.getRootNode(), RootNode.ROOT);
    }

    /**
     * Method: toString()
     */
    @Test
    public void testToString() throws Exception {
        ConfigurationEvent event = buildConfigurationEvent(null);
        Assert.assertNotNull(event.toString());
    }

    private ConfigurationEvent buildConfigurationEvent(ConfigurationNode node) {
        return new ConfigurationEvent(ConfigurationEvent.EventType.ADD, node, RootNode.ROOT);
    }
}
