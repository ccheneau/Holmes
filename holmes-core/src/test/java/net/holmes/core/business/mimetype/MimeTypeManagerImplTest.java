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
package net.holmes.core.business.mimetype;

import net.holmes.core.business.mimetype.dao.MimeTypeDaoImpl;
import net.holmes.core.business.mimetype.model.MimeType;
import org.junit.Test;

import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;

public class MimeTypeManagerImplTest {

    @Test
    public void testGetMimeType() {
        MimeTypeManager mimeTypeManager = new MimeTypeManagerImpl(new MimeTypeDaoImpl("/mimetypes.properties"));

        assertNotNull(mimeTypeManager.getMimeType("someVideo.avi"));
        assertNull(mimeTypeManager.getMimeType("someVideo.badExtension"));
    }

    @Test
    public void testIsMimeTypeCompliant() {
        MimeTypeManager mimeTypeManager = new MimeTypeManagerImpl(new MimeTypeDaoImpl("/mimetypes.properties"));

        MimeType mimeType = MimeType.valueOf("video/avi");
        assertTrue(mimeTypeManager.isMimeTypeCompliant(mimeType, newArrayList("video/avi")));
        assertFalse(mimeTypeManager.isMimeTypeCompliant(mimeType, newArrayList("audio/mpeg")));
        assertTrue(mimeTypeManager.isMimeTypeCompliant(mimeType, new ArrayList<String>(0)));
        assertTrue(mimeTypeManager.isMimeTypeCompliant(mimeType, null));
        assertTrue(mimeTypeManager.isMimeTypeCompliant(null, null));

        mimeType = MimeType.valueOf("video/x-msvideo");
        assertTrue(mimeTypeManager.isMimeTypeCompliant(mimeType, newArrayList("video/avi")));
        assertFalse(mimeTypeManager.isMimeTypeCompliant(mimeType, newArrayList("audio/mpeg")));

        mimeType = MimeType.valueOf("");
        assertTrue(mimeTypeManager.isMimeTypeCompliant(mimeType, newArrayList("video/avi")));

        mimeType = MimeType.valueOf("audio/mpeg");
        assertTrue(mimeTypeManager.isMimeTypeCompliant(mimeType, newArrayList("audio/mpeg")));
        assertTrue(mimeTypeManager.isMimeTypeCompliant(mimeType, newArrayList("audio/*")));
        assertFalse(mimeTypeManager.isMimeTypeCompliant(mimeType, newArrayList("video/*")));
        assertTrue(mimeTypeManager.isMimeTypeCompliant(mimeType, newArrayList("*/*")));
    }
}
