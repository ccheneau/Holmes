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
package net.holmes.core.business.mimetype.dao;

import net.holmes.core.business.mimetype.model.MimeType;
import net.holmes.core.common.MediaType;
import net.holmes.core.common.exception.HolmesRuntimeException;
import org.junit.Test;

import static org.junit.Assert.*;

public class MimeTypeDaoImplTest {

    /**
     * Test mime type.
     */
    @Test
    public void testMimeType() {
        MimeTypeDao mimeTypeDao = new MimeTypeDaoImpl("/mimetypes.properties");

        String fileName = "movie.avi";
        MimeType mimeType = mimeTypeDao.getMimeType(fileName);

        assertNotNull(mimeType);
        assertEquals(MediaType.TYPE_VIDEO, mimeType.getType());
        assertEquals("avi", mimeType.getSubType());
        assertEquals("video/avi", mimeType.getMimeType());
    }

    /**
     * Test bad mime type.
     */
    @Test
    public void testBadMimeType() {
        MimeTypeDao mimeTypeDao = new MimeTypeDaoImpl("/mimetypes.properties");

        String fileName = "movie.blabla";
        MimeType mimeType = mimeTypeDao.getMimeType(fileName);
        assertNull(mimeType);
    }

    /**
     * Test bad mime type path.
     */
    @Test(expected = HolmesRuntimeException.class)
    public void testBadMimePath() {
        new MimeTypeDaoImpl("/badMimeTypePath");
    }

    @Test
    public void testGetAliasMimeType() {
        MimeTypeDao mimeTypeDao = new MimeTypeDaoImpl("/mimetypes.properties");

        MimeType mimeType = MimeType.valueOf("video/x-msvideo");

        MimeType aliasMimeType = mimeTypeDao.getAliasMimeType(mimeType);
        assertNotNull(aliasMimeType);
    }

    @Test
    public void testGetBadAliasMimeType() {
        MimeTypeDao mimeTypeDao = new MimeTypeDaoImpl("/mimetypes.properties");

        MimeType mimeType = MimeType.valueOf("video/something");

        MimeType aliasMimeType = mimeTypeDao.getAliasMimeType(mimeType);
        assertNull(aliasMimeType);
    }

}
