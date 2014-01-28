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

package net.holmes.core.media.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class IcecastGenreNodeTest {

    @Test
    public void testHashCode() {
        IcecastGenreNode node1 = buildIcecastGenreNode();
        IcecastGenreNode node2 = buildIcecastGenreNode();
        assertNotNull(node1.hashCode());
        assertEquals(node1.hashCode(), node2.hashCode());
    }

    @Test
    public void testEquals() throws Exception {
        IcecastGenreNode node1 = buildIcecastGenreNode();
        IcecastGenreNode node2 = buildIcecastGenreNode();
        assertEquals(node1, node1);
        assertEquals(node1, node2);
        assertNotEquals(node1, null);
        assertNotEquals(node1, "node1");
    }

    @Test
    public void testToString() throws Exception {
        IcecastGenreNode node1 = buildIcecastGenreNode();
        IcecastGenreNode node2 = buildIcecastGenreNode();
        assertNotNull(node1.toString());
        assertEquals(node1.toString(), node2.toString());
    }

    private IcecastGenreNode buildIcecastGenreNode() {
        return new IcecastGenreNode("id", "parentId", "name", "genre");
    }
}
