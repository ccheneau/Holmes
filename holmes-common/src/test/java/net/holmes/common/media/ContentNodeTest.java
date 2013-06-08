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


import net.holmes.common.mimetype.MimeType;
import org.junit.*;
import org.junit.rules.TestName;

import java.io.File;

/**
 * ContentNode Tester.
 */
public class ContentNodeTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    @Rule
    public TestName testName = new TestName();

    /**
     * Method: getMimeType()
     */
    @Test
    public void testGetMimeType() throws Exception {
        MimeType mimeType = new MimeType("video/x-msvideo");
        File file = File.createTempFile(testName.getMethodName(), "avi");
        ContentNode node = buildContentNode("", file);
        Assert.assertEquals(node.getMimeType(), mimeType);
    }

    /**
     * Method: getSize()
     */
    @Test
    public void testGetSize() throws Exception {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        ContentNode node = buildContentNode("", file);
        Assert.assertEquals(node.getSize().longValue(), file.length());
    }

    /**
     * Method: getPath()
     */
    @Test
    public void testGetPath() throws Exception {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        ContentNode node = buildContentNode("", file);
        Assert.assertEquals(node.getPath(), file.getAbsolutePath());
    }

    /**
     * Method: getResolution()
     */
    @Test
    public void testGetResolution() throws Exception {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        ContentNode node = buildContentNode("", file);
        Assert.assertEquals(node.getResolution(), "resolution");
    }

    /**
     * Method: hashCode()
     */
    @Test
    public void testHashCode() throws Exception {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        ContentNode node1 = buildContentNode("", file);
        ContentNode node2 = buildContentNode("", file);
        Assert.assertNotNull(node1.hashCode());
        Assert.assertEquals(node1.hashCode(), node2.hashCode());
    }

    /**
     * Method: equals(Object obj)
     */
    @Test
    public void testEquals() throws Exception {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        ContentNode node1 = buildContentNode("", file);
        ContentNode node2 = buildContentNode("", file);
        Assert.assertEquals(node1, node2);
    }

    /**
     * Method: toString()
     */
    @Test
    public void testToString() throws Exception {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        ContentNode node1 = buildContentNode("", file);
        ContentNode node2 = buildContentNode("", file);
        Assert.assertNotNull(node1.toString());
        Assert.assertEquals(node1.toString(), node2.toString());
    }

    private ContentNode buildContentNode(String suffix, File file) {
        MimeType mimeType = new MimeType("video/x-msvideo");
        return new ContentNode("id" + suffix, "parentId" + suffix, "name" + suffix, file, mimeType, "resolution" + suffix);
    }
}
