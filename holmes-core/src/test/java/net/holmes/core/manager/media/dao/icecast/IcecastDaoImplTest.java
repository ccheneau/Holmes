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

package net.holmes.core.manager.media.dao.icecast;

import net.holmes.core.manager.configuration.Configuration;
import net.holmes.core.manager.media.dao.index.MediaIndexDao;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import static net.holmes.core.manager.configuration.Parameter.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class IcecastDaoImplTest {

    @Test
    public void testParseAndFilterYellowPage() throws URISyntaxException {
        URL ypUrl = this.getClass().getResource("/Icecast.xml");

        String localHolmesDataDir = System.getProperty("java.io.tmpdir");
        Configuration configuration = createMock(Configuration.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        expect(configuration.getParameter(ICECAST_GENRE_LIST)).andReturn("genre1,genre2").atLeastOnce();
        expect(configuration.getBooleanParameter(ENABLE_ICECAST_DIRECTORY)).andReturn(true).atLeastOnce();
        expect(configuration.getIntParameter(ICECAST_MAX_DOWNLOAD_RETRY)).andReturn(3).atLeastOnce();

        mediaIndexDao.removeChildren("IceCastGenre_genre1");
        expectLastCall().atLeastOnce();

        mediaIndexDao.removeChildren("IceCastGenre_genre2");
        expectLastCall().atLeastOnce();

        replay(configuration, mediaIndexDao);
        try {
            IcecastDaoImpl icecastDao = new IcecastDaoImpl(configuration, localHolmesDataDir, mediaIndexDao);

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
            verify(configuration, mediaIndexDao);
        }
    }

    @Test
    public void testGetGenres() {
        String localHolmesDataDir = System.getProperty("java.io.tmpdir");
        Configuration configuration = createMock(Configuration.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        expect(configuration.getParameter(ICECAST_GENRE_LIST)).andReturn("genre1,genre2").atLeastOnce();
        expect(configuration.getBooleanParameter(ENABLE_ICECAST_DIRECTORY)).andReturn(true).atLeastOnce();
        expect(configuration.getIntParameter(ICECAST_MAX_DOWNLOAD_RETRY)).andReturn(3).atLeastOnce();

        replay(configuration, mediaIndexDao);
        IcecastDaoImpl icecastDao = new IcecastDaoImpl(configuration, localHolmesDataDir, mediaIndexDao);
        try {
            List<IcecastGenre> genres = icecastDao.getGenres();
            assertNotNull(genres);
            assertEquals(2, genres.size());
            for (IcecastGenre genre : genres) {
                assertTrue(genre.getName().equals("genre1") || genre.getName().equals("genre2"));
            }
        } finally {
            verify(configuration, mediaIndexDao);
        }
    }
}
