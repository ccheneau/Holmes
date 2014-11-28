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

package net.holmes.core.business.configuration.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * ConfigurationNode Tester.
 */
public class ConfigurationNodeTest {

    /**
     * Method: getId()
     */
    @Test
    public void testGetId() throws Exception {
        ConfigurationNode node = new ConfigurationNode("id", "label", "path");
        assertEquals(node.getId(), "id");
    }

    /**
     * Method: getLabel()
     */
    @Test
    public void testGetLabel() throws Exception {
        ConfigurationNode node = new ConfigurationNode("id", "label", "path");
        assertEquals(node.getLabel(), "label");
    }

    /**
     * Method: setLabel(final String label)
     */
    @Test
    public void testSetLabel() throws Exception {
        ConfigurationNode node = new ConfigurationNode("id", "label", "path");
        node.setLabel("newLabel");
        assertEquals(node.getLabel(), "newLabel");
    }

    /**
     * Method: getPath()
     */
    @Test
    public void testGetPath() throws Exception {
        ConfigurationNode node = new ConfigurationNode("id", "label", "path");
        assertEquals(node.getPath(), "path");
    }

    /**
     * Method: setPath(final String path)
     */
    @Test
    public void testSetPath() throws Exception {
        ConfigurationNode node = new ConfigurationNode("id", "label", "path");
        node.setPath("newPath");
        assertEquals(node.getPath(), "newPath");
    }

    /**
     * Method: hashCode()
     */
    @Test
    public void testHashCode() throws Exception {
        ConfigurationNode node1 = new ConfigurationNode("id", "label", "path");
        ConfigurationNode node2 = new ConfigurationNode("id", "label", "path");
        ConfigurationNode node3 = new ConfigurationNode("id3", "label", "path");
        assertNotNull(node1.hashCode());
        assertNotNull(node2.hashCode());
        assertNotNull(node3.hashCode());
        assertEquals(node1.hashCode(), node2.hashCode());
        assertNotEquals(node1.hashCode(), node3.hashCode());
    }

    /**
     * Method: equals(final Object obj)
     */
    @Test
    public void testEquals() throws Exception {
        ConfigurationNode node1 = new ConfigurationNode("id", "label", "path");
        ConfigurationNode node2 = new ConfigurationNode("id", "label", "path");
        ConfigurationNode node3 = new ConfigurationNode("id3", "label", "path");
        ConfigurationNode node4 = new ConfigurationNode("id", "label4", "path");
        ConfigurationNode node5 = new ConfigurationNode("id", "label", "path4");
        assertEquals(node1, node1);
        assertEquals(node1, node2);
        assertNotEquals(node1, null);
        assertNotEquals(node1, "node1");
        assertNotEquals(node1, node3);
        assertNotEquals(node1, node4);
        assertNotEquals(node1, node5);
    }

    /**
     * Method: toString()
     */
    @Test
    public void testToString() throws Exception {
        ConfigurationNode node1 = new ConfigurationNode("id", "label", "path");
        ConfigurationNode node2 = new ConfigurationNode("id", "label", "path");
        assertNotNull(node1.toString());
        assertEquals(node1.toString(), node2.toString());
    }
}
