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

package net.holmes.core.upnp;

import net.holmes.core.common.mimetype.MimeType;
import net.holmes.core.media.model.ContentNode;
import net.holmes.core.media.model.FolderNode;
import net.holmes.core.media.model.PodcastEntryNode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class DirectoryBrowseResultTest {

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testDirectoryBrowseResult() {
        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        assertEquals(directoryBrowseResult.getFirstResult(), 0);
        assertEquals(directoryBrowseResult.getTotalCount(), 0);
        assertEquals(directoryBrowseResult.getItemCount(), 0);
        assertNotNull(directoryBrowseResult.getDidl());
    }

    @Test
    public void testAddVideoItem() throws IOException, URISyntaxException {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        MimeType mimeType = new MimeType("video/x-msvideo");
        ContentNode node = new ContentNode("id", "1", "name", file, mimeType, null);

        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        directoryBrowseResult.addItem("1", node, "http://google.com");
        assertEquals(directoryBrowseResult.getItemCount(), 1);
        assertEquals(directoryBrowseResult.getDidl().getCount(), 1);
    }

    @Test
    public void testAddAudioItem() throws IOException, URISyntaxException {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        MimeType mimeType = new MimeType("audio/mpeg");
        ContentNode node = new ContentNode("id", "1", "name", file, mimeType, null);
        node.setIconUrl("http://google.com");

        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        directoryBrowseResult.addItem("1", node, "http://google.com");
        assertEquals(directoryBrowseResult.getItemCount(), 1);
        assertEquals(directoryBrowseResult.getDidl().getCount(), 1);
    }

    @Test
    public void testAddImageItem() throws IOException, URISyntaxException {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        MimeType mimeType = new MimeType("image/jpeg");
        ContentNode node = new ContentNode("id", "1", "name", file, mimeType, null);

        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        directoryBrowseResult.addItem("1", node, "http://google.com");
        assertEquals(directoryBrowseResult.getItemCount(), 1);
        assertEquals(directoryBrowseResult.getDidl().getCount(), 1);
    }

    @Test
    public void testAddSubtitleItem() throws IOException, URISyntaxException {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        MimeType mimeType = new MimeType("application/x-subrip");
        ContentNode node = new ContentNode("id", "1", "name", file, mimeType, null);

        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        directoryBrowseResult.addItem("1", node, "http://google.com");
        assertEquals(directoryBrowseResult.getItemCount(), 1);
        assertEquals(directoryBrowseResult.getDidl().getCount(), 1);
    }

    @Test
    public void testAddBadSubtitleItem() throws IOException, URISyntaxException {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        MimeType mimeType = new MimeType("application/bad-subrip");
        ContentNode node = new ContentNode("id", "1", "name", file, mimeType, null);

        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        directoryBrowseResult.addItem("1", node, "http://google.com");
        assertEquals(directoryBrowseResult.getItemCount(), 0);
        assertEquals(directoryBrowseResult.getDidl().getCount(), 0);
    }

    @Test
    public void testAddBadItem() throws IOException, URISyntaxException {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        MimeType mimeType = new MimeType("bad-type/bad-subtype");
        ContentNode node = new ContentNode("id", "1", "name", file, mimeType, null);

        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        directoryBrowseResult.addItem("1", node, "http://google.com");
        assertEquals(directoryBrowseResult.getItemCount(), 0);
        assertEquals(directoryBrowseResult.getDidl().getCount(), 0);
    }

    @Test
    public void testAddContainer() throws URISyntaxException {
        FolderNode node = new FolderNode("id", "parentId", "name");
        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        directoryBrowseResult.addContainer("1", node, 1);
        assertEquals(directoryBrowseResult.getItemCount(), 1);
        assertEquals(directoryBrowseResult.getDidl().getCount(), 1);
    }

    @Test
    public void testAddPodcastItem() throws URISyntaxException {
        MimeType mimeType = new MimeType("video/x-msvideo");
        PodcastEntryNode node = new PodcastEntryNode("id", "parentId", "name", mimeType, "url", "duration");
        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        directoryBrowseResult.addPodcastItem("1", node, "name");
        assertEquals(directoryBrowseResult.getItemCount(), 1);
        assertEquals(directoryBrowseResult.getDidl().getCount(), 1);
    }

    @Test
    public void testFilterResult() throws IOException, URISyntaxException {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        MimeType mimeType = new MimeType("audio/mpeg");
        ContentNode node = new ContentNode("id", "1", "name", file, mimeType, null);
        node.setIconUrl("http://google.com");

        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        assertTrue(directoryBrowseResult.filterResult());
        assertEquals(directoryBrowseResult.getTotalCount(), 1);
        directoryBrowseResult.addItem("1", node, "http://google.com");
        assertEquals(directoryBrowseResult.getItemCount(), 1);
        assertEquals(directoryBrowseResult.getDidl().getCount(), 1);

        assertFalse(directoryBrowseResult.filterResult());
        assertEquals(directoryBrowseResult.getTotalCount(), 2);
    }

    @Test
    public void testFilterResultNoMaxResult() throws IOException, URISyntaxException {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        MimeType mimeType = new MimeType("audio/mpeg");
        ContentNode node = new ContentNode("id", "1", "name", file, mimeType, null);
        node.setIconUrl("http://google.com");

        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 0);
        assertTrue(directoryBrowseResult.filterResult());
        assertEquals(directoryBrowseResult.getTotalCount(), 1);
        directoryBrowseResult.addItem("1", node, "http://google.com");
        assertEquals(directoryBrowseResult.getItemCount(), 1);
        assertEquals(directoryBrowseResult.getDidl().getCount(), 1);

        assertTrue(directoryBrowseResult.filterResult());
        assertEquals(directoryBrowseResult.getTotalCount(), 2);
    }

}
