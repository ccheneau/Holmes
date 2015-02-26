/*
 * Copyright (C) 2012-2015  Cedric Cheneau
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
import org.junit.Test;

import static net.holmes.core.business.media.model.MediaNode.NodeType.TYPE_PODCAST_ENTRY;
import static org.junit.Assert.*;

/**
 * PodcastEntryNode Tester.
 */
public class PodcastEntryNodeTest {

    /**
     * Method: getMimeType()
     */
    @Test
    public void testGetMimeType() throws Exception {
        MimeType mimeType = MimeType.valueOf("video/x-msvideo");
        RawUrlNode node = new RawUrlNode(TYPE_PODCAST_ENTRY, "id", "parentId", "name", mimeType, "url", "duration");
        assertEquals(node.getMimeType(), mimeType);
    }

    /**
     * Method: getUrl()
     */
    @Test
    public void testGetUrl() throws Exception {
        MimeType mimeType = MimeType.valueOf("video/x-msvideo");
        RawUrlNode node = new RawUrlNode(TYPE_PODCAST_ENTRY, "id", "parentId", "name", mimeType, "url", "duration");
        assertEquals(node.getUrl(), "url");
    }

    /**
     * Method: getDuration()
     */
    @Test
    public void testGetDuration() throws Exception {
        MimeType mimeType = MimeType.valueOf("video/x-msvideo");
        RawUrlNode node = new RawUrlNode(TYPE_PODCAST_ENTRY, "id", "parentId", "name", mimeType, "url", "duration");
        assertEquals(node.getDuration(), "duration");
    }

    /**
     * Method: hashCode()
     */
    @Test
    public void testHashCode() throws Exception {
        MimeType mimeType = MimeType.valueOf("video/x-msvideo");
        RawUrlNode node1 = new RawUrlNode(TYPE_PODCAST_ENTRY, "id", "parentId", "name", mimeType, "url", "duration");
        RawUrlNode node2 = new RawUrlNode(TYPE_PODCAST_ENTRY, "id", "parentId", "name", mimeType, "url", "duration");
        assertNotNull(node1.hashCode());
        assertEquals(node1.hashCode(), node2.hashCode());
    }

    /**
     * Method: equals(final Object obj)
     */
    @Test
    public void testEquals() throws Exception {
        MimeType mimeType = MimeType.valueOf("video/x-msvideo");
        RawUrlNode node1 = new RawUrlNode(TYPE_PODCAST_ENTRY, "id", "parentId", "name", mimeType, "url", "duration");
        RawUrlNode node2 = new RawUrlNode(TYPE_PODCAST_ENTRY, "id", "parentId", "name", mimeType, "url", "duration");
        RawUrlNode node3 = new RawUrlNode(TYPE_PODCAST_ENTRY, "id1", "parentId", "name", mimeType, "url", "duration");
        RawUrlNode node4 = new RawUrlNode(TYPE_PODCAST_ENTRY, "id", "parentId1", "name", mimeType, "url", "duration");
        RawUrlNode node5 = new RawUrlNode(TYPE_PODCAST_ENTRY, "id", "parentId", "name1", mimeType, "url", "duration");
        RawUrlNode node6 = new RawUrlNode(TYPE_PODCAST_ENTRY, "id", "parentId", "name", null, "url", "duration");
        RawUrlNode node7 = new RawUrlNode(TYPE_PODCAST_ENTRY, "id", "parentId", "name", mimeType, "url1", "duration");
        RawUrlNode node8 = new RawUrlNode(TYPE_PODCAST_ENTRY, "id", "parentId", "name", mimeType, "url", "duration1");
        assertEquals(node1, node1);
        assertEquals(node1, node2);
        assertNotEquals(node1, null);
        assertNotEquals(node1, "node1");
        assertNotEquals(node1, node3);
        assertNotEquals(node1, node4);
        assertNotEquals(node1, node5);
        assertNotEquals(node1, node6);
        assertNotEquals(node1, node7);
        assertNotEquals(node1, node8);
    }

    /**
     * Method: toString()
     */
    @Test
    public void testToString() throws Exception {
        MimeType mimeType = MimeType.valueOf("video/x-msvideo");
        RawUrlNode node1 = new RawUrlNode(TYPE_PODCAST_ENTRY, "id", "parentId", "name", mimeType, "url", "duration");
        RawUrlNode node2 = new RawUrlNode(TYPE_PODCAST_ENTRY, "id", "parentId", "name", mimeType, "url", "duration");
        assertNotNull(node1.toString());
        assertEquals(node1.toString(), node2.toString());
    }

}
