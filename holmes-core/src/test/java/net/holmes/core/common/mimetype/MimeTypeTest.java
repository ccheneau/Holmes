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
package net.holmes.core.common.mimetype;

import net.holmes.core.common.MediaType;
import org.junit.Test;

import static org.junit.Assert.*;

public class MimeTypeTest {

    /**
     * Test mime type.
     */
    @Test
    public void testMimeType() {
        MimeTypeManager mimeTypeManager = new MimeTypeManagerImpl("/mimetypes.properties");

        String fileName = "movie.avi";
        MimeType mimeType = mimeTypeManager.getMimeType(fileName);

        assertNotNull(mimeType);
        assertEquals(MediaType.TYPE_VIDEO, mimeType.getType());
        assertEquals("x-msvideo", mimeType.getSubType());
        assertEquals("video/x-msvideo", mimeType.getMimeType());
    }

    /**
     * Test bad mime type.
     */
    @Test
    public void testBadMimeType() {
        MimeTypeManager mimeTypeManager = new MimeTypeManagerImpl("/mimetypes.properties");
        String fileName = "movie.blabla";
        MimeType mimeType = mimeTypeManager.getMimeType(fileName);
        assertNull(mimeType);
    }

    /**
     * Test bad mime type path.
     */
    @Test(expected = RuntimeException.class)
    public void testBadMimePath() {
        new MimeTypeManagerImpl("/badMimeTypePath");
    }
}
