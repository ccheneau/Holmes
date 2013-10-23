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

package net.holmes.core.media.dao.icecast;

import net.holmes.core.common.configuration.Configuration;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import static net.holmes.core.common.configuration.Parameter.ENABLE_ICECAST_DIRECTORY;
import static net.holmes.core.common.configuration.Parameter.ICECAST_GENRE_LIST;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class IcecastDaoTest {

    @Test
    public void testParseAndFilterYellowPage() throws URISyntaxException {
        URL ypUrl = this.getClass().getResource("/Icecast.xml");

        String localHolmesDataDir = System.getProperty("java.io.tmpdir");
        Configuration configuration = createMock(Configuration.class);

        expect(configuration.getParameter(ICECAST_GENRE_LIST)).andReturn("genre1,genre2").atLeastOnce();
        expect(configuration.getBooleanParameter(ENABLE_ICECAST_DIRECTORY)).andReturn(true).atLeastOnce();

        replay(configuration);
        try {
            IcecastDaoImpl icecastDao = new IcecastDaoImpl(configuration, localHolmesDataDir);

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
        } finally {
            verify(configuration);
        }
    }

    @Test
    public void testGetGenres() {
        String localHolmesDataDir = System.getProperty("java.io.tmpdir");
        Configuration configuration = createMock(Configuration.class);

        expect(configuration.getParameter(ICECAST_GENRE_LIST)).andReturn("genre1,genre2").atLeastOnce();
        expect(configuration.getBooleanParameter(ENABLE_ICECAST_DIRECTORY)).andReturn(true).atLeastOnce();

        replay(configuration);
        IcecastDaoImpl icecastDao = new IcecastDaoImpl(configuration, localHolmesDataDir);
        try {
            List<String> genres = icecastDao.getGenres();
            assertNotNull(genres);
            assertEquals(2, genres.size());
            assertTrue(genres.contains("genre1"));
            assertTrue(genres.contains("genre2"));
        } finally {
            verify(configuration);
        }

    }
}
