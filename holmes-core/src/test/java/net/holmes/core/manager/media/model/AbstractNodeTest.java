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

package net.holmes.core.manager.media.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * AbstractNode Tester.
 */
public class AbstractNodeTest {

    @Test
    public void testGetId() {
        AbstractNodeTester node = buildAbstractNodeTester("");
        assertEquals(node.getId(), "id");
    }

    @Test
    public void testGetParentId() {
        AbstractNodeTester node = buildAbstractNodeTester("");
        assertEquals(node.getParentId(), "parentId");
    }

    @Test
    public void testGetName() {
        AbstractNodeTester node = buildAbstractNodeTester("");
        assertEquals(node.getName(), "name");
    }

    @Test
    public void testGetType() {
        AbstractNodeTester node = buildAbstractNodeTester("");
        assertEquals(node.getType(), AbstractNode.NodeType.TYPE_CONTENT);
    }

    @Test
    public void testGetModifiedDate() {
        AbstractNodeTester node = buildAbstractNodeTester("");
        assertNull(node.getModifiedDate());
    }

    @Test
    public void testSetModifiedDate() {
        AbstractNodeTester node = buildAbstractNodeTester("");
        node.setModifiedDate(1L);
        assertEquals(node.getModifiedDate(), Long.valueOf(1));
    }

    @Test
    public void testGetIconUrl() {
        AbstractNodeTester node = buildAbstractNodeTester("");
        assertNull(node.getIconUrl());
    }

    @Test
    public void testSetIconUrl() {
        AbstractNodeTester node = buildAbstractNodeTester("");
        node.setIconUrl("iconUrl");
        assertEquals(node.getIconUrl(), "iconUrl");
    }

    @Test
    public void testCompareTo() {
        AbstractNodeTester node = buildAbstractNodeTester("");
        AbstractNodeTester node1 = buildAbstractNodeTester("");
        AbstractNodeTester node2 = buildAbstractNodeTester("2");
        assertEquals(node.compareTo(node1), 0);
        assertNotEquals(node.compareTo(node2), 0);
        assertNotEquals(node.compareTo(buildAbstractNodeTesterFolder("")), 0);
        assertNotEquals(node.compareTo(buildAbstractNodeTesterPodcast("")), 0);
    }

    @Test
    public void testHashCode() {
        AbstractNodeTester node = buildAbstractNodeTester("");
        AbstractNodeTester node1 = buildAbstractNodeTester("");
        AbstractNodeTester node2 = buildAbstractNodeTester("2");
        assertEquals(node.hashCode(), node1.hashCode());
        assertNotEquals(node.hashCode(), node2.hashCode());
    }

    @Test
    public void testEquals() {
        AbstractNodeTester node = buildAbstractNodeTester("");
        AbstractNodeTester node1 = buildAbstractNodeTester("");
        AbstractNodeTester node2 = buildAbstractNodeTester("2");
        assertEquals(node, node);
        assertEquals(node, node1);
        assertNotEquals(node, null);
        assertNotEquals(node, "node");
        assertNotEquals(node, node2);
        assertNotEquals(node, buildAbstractNodeTesterFolder(""));
    }

    private AbstractNodeTester buildAbstractNodeTester(String suffix) {
        return new AbstractNodeTester(AbstractNode.NodeType.TYPE_CONTENT, "id" + suffix, "parentId" + suffix, "name" + suffix);
    }

    private AbstractNodeTester buildAbstractNodeTesterFolder(String suffix) {
        return new AbstractNodeTester(AbstractNode.NodeType.TYPE_FOLDER, "id" + suffix, "parentId" + suffix, "name" + suffix);
    }

    private AbstractNodeTester buildAbstractNodeTesterPodcast(String suffix) {
        return new AbstractNodeTester(AbstractNode.NodeType.TYPE_PODCAST, "id" + suffix, "parentId" + suffix, "name" + suffix);
    }

    private class AbstractNodeTester extends AbstractNode {
        /**
         * Instantiates a new abstract node.
         *
         * @param type     node type
         * @param id       node id
         * @param parentId parent node id
         * @param name     node name
         */
        AbstractNodeTester(NodeType type, String id, String parentId, String name) {
            super(type, id, parentId, name);
        }
    }
}
