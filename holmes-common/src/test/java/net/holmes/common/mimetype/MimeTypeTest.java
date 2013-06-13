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
package net.holmes.common.mimetype;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MimeTypeTest {

    private MimeTypeManager mimeTypeManager;

    @Before
    public void setUp() {
        mimeTypeManager = new MimeTypeManagerImpl();
    }

    /**
     * Test mime type.
     */
    @Test
    public void testMimeType() {
        try {
            String fileName = "movie.avi";

            MimeType mimeType = mimeTypeManager.getMimeType(fileName);

            assertNotNull(mimeType);
            assertEquals("video", mimeType.getType());
            assertEquals("x-msvideo", mimeType.getSubType());
            assertEquals("video/x-msvideo", mimeType.getMimeType());
            assertTrue(mimeType.isVideo());
            assertTrue(mimeType.isMedia());
            assertFalse(mimeType.isSubtitle());
            assertFalse(mimeType.isAudio());
            assertFalse(mimeType.isImage());
            assertNull(mimeType);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test bad mime type.
     */
    @Test
    public void testBadMimeType() {
        try {
            String fileName = "movie.blabla";

            MimeType mimeType = mimeTypeManager.getMimeType(fileName);

            assertNull(mimeType);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
