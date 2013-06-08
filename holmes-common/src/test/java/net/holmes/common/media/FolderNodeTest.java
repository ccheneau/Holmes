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

package net.holmes.common.media;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * FolderNode Tester.
 */
public class FolderNodeTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: hashCode()
     */
    @Test
    public void testHashCode() throws Exception {
        FolderNode node1 = buildFolderNode();
        FolderNode node2 = buildFolderNode();
        Assert.assertNotNull(node1.hashCode());
        Assert.assertEquals(node1.hashCode(), node2.hashCode());
    }

    /**
     * Method: equals(final Object obj)
     */
    @Test
    public void testEquals() throws Exception {
        FolderNode node1 = buildFolderNode();
        FolderNode node2 = buildFolderNode();
        Assert.assertEquals(node1, node2);
    }

    /**
     * Method: toString()
     */
    @Test
    public void testToString() throws Exception {
        FolderNode node1 = buildFolderNode();
        FolderNode node2 = buildFolderNode();
        Assert.assertNotNull(node1.toString());
        Assert.assertEquals(node1.toString(), node2.toString());
    }

    private FolderNode buildFolderNode() {
        return new FolderNode("id", "parentId", "name");
    }
}
