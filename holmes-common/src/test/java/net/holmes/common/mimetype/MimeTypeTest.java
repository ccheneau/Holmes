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

import junit.framework.TestCase;
import org.junit.Before;

public class MimeTypeTest extends TestCase {

    private MimeTypeManager mimeTypeManager;

    @Override
    @Before
    public void setUp() {
        mimeTypeManager = new MimeTypeManagerImpl();
    }

    /**
     * Test mime type.
     */
    public void testMimeType() {
        try {
            String fileName = "movie.avi";

            MimeType mimeType = mimeTypeManager.getMimeType(fileName);

            assertNotNull(mimeType);
            assertEquals("video", mimeType.getType());
            assertEquals("x-msvideo", mimeType.getSubType());
            assertEquals("video/x-msvideo", mimeType.getMimeType());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test bad mime type.
     */
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
