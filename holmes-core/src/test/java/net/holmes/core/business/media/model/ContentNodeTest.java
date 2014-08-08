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

package net.holmes.core.business.media.model;


import net.holmes.core.business.mimetype.model.MimeType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.File;

import static org.junit.Assert.*;

/**
 * ContentNode Tester.
 */
public class ContentNodeTest {

    @Rule
    public TestName testName = new TestName();

    /**
     * Method: getMimeType()
     */
    @Test
    public void testGetMimeType() throws Exception {
        MimeType mimeType = MimeType.valueOf("video/x-msvideo");
        File file = File.createTempFile(testName.getMethodName(), "avi");
        file.deleteOnExit();
        ContentNode node = buildContentNode(file);
        assertEquals(node.getMimeType(), mimeType);
    }

    /**
     * Method: getSize()
     */
    @Test
    public void testGetSize() throws Exception {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        file.deleteOnExit();
        ContentNode node = buildContentNode(file);
        assertEquals(node.getSize().longValue(), file.length());
    }

    /**
     * Method: getPath()
     */
    @Test
    public void testGetPath() throws Exception {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        file.deleteOnExit();
        ContentNode node = buildContentNode(file);
        assertEquals(node.getPath(), file.getAbsolutePath());
    }

    /**
     * Method: hashCode()
     */
    @Test
    public void testHashCode() throws Exception {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        file.deleteOnExit();
        ContentNode node1 = buildContentNode(file);
        ContentNode node2 = buildContentNode(file);
        assertNotNull(node1.hashCode());
        assertEquals(node1.hashCode(), node2.hashCode());
    }

    /**
     * Method: equals(Object obj)
     */
    @Test
    public void testEquals() throws Exception {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        file.deleteOnExit();
        ContentNode node1 = buildContentNode(file);
        ContentNode node2 = buildContentNode(file);
        assertEquals(node1, node1);
        assertEquals(node1, node2);
        assertNotEquals(node1, null);
        assertNotEquals(node1, "node1");
    }

    /**
     * Method: toString()
     */
    @Test
    public void testToString() throws Exception {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        file.deleteOnExit();
        ContentNode node1 = buildContentNode(file);
        ContentNode node2 = buildContentNode(file);
        assertNotNull(node1.toString());
        assertEquals(node1.toString(), node2.toString());
    }

    private ContentNode buildContentNode(File file) {
        MimeType mimeType = MimeType.valueOf("video/x-msvideo");
        return new ContentNode("id", "parentId", "name", file, mimeType);
    }
}
