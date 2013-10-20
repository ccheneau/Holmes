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

package net.holmes.core.media.dao;

import net.holmes.core.media.model.IcecastDirectory;
import net.holmes.core.media.model.IcecastEntry;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IcecastDaoTest {

    @Test
    public void testParseAndFilterYellowPage() throws URISyntaxException {
        URL ypUrl = this.getClass().getResource("/Icecast.xml");
        IcecastDaoImpl icecastDao = new IcecastDaoImpl();
        icecastDao.parseYellowPage(new File(ypUrl.toURI()));
        IcecastDirectory directory = icecastDao.getDirectory();
        assertNotNull(directory);
        assertNotNull(directory.getEntries());
        assertEquals(5, directory.getEntries().size());

        Collection<IcecastEntry> entries = icecastDao.getEntriesByGenre("genre4test");
        assertNotNull(entries);
        assertEquals(5, entries.size());

        entries = icecastDao.getEntriesByGenre("nonExistingGenre");
        assertNotNull(entries);
        assertEquals(0, entries.size());
    }
}
