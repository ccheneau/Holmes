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

package net.holmes.core.business.media.dao.icecast;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.holmes.core.business.configuration.ConfigurationDao;
import net.holmes.core.business.media.dao.index.MediaIndexDao;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import static net.holmes.core.business.configuration.Parameter.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class IcecastDaoImplTest {

    @Test
    public void testParseAndFilterYellowPage() throws URISyntaxException {
        URL ypUrl = this.getClass().getResource("/Icecast.xml");

        String localHolmesDataDir = System.getProperty("java.io.tmpdir");
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        expect(configurationDao.getListParameter(ICECAST_GENRE_LIST)).andReturn(Lists.newArrayList("genre1", "genre2")).atLeastOnce();
        expect(configurationDao.getBooleanParameter(ICECAST_ENABLE)).andReturn(true).atLeastOnce();
        expect(configurationDao.getIntParameter(ICECAST_MAX_DOWNLOAD_RETRY)).andReturn(3).atLeastOnce();

        mediaIndexDao.removeChildren("IceCastGenre_genre1");
        expectLastCall().atLeastOnce();

        mediaIndexDao.removeChildren("IceCastGenre_genre2");
        expectLastCall().atLeastOnce();

        replay(configurationDao, mediaIndexDao);
        try {
            IcecastDaoImpl icecastDao = new IcecastDaoImpl(configurationDao, localHolmesDataDir, mediaIndexDao);

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
            verify(configurationDao, mediaIndexDao);
        }
    }

    @Test
    public void testGetGenres() {
        String localHolmesDataDir = System.getProperty("java.io.tmpdir");
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDirectory directory = createMock(IcecastDirectory.class);

        expect(configurationDao.getListParameter(ICECAST_GENRE_LIST)).andReturn(Lists.newArrayList("genre1", "genre2")).atLeastOnce();
        expect(configurationDao.getBooleanParameter(ICECAST_ENABLE)).andReturn(true).atLeastOnce();
        expect(configurationDao.getIntParameter(ICECAST_MAX_DOWNLOAD_RETRY)).andReturn(3).atLeastOnce();
        expect(directory.getEntries()).andReturn(Sets.newHashSet(new IcecastEntry("name", "url", "type", "genre"))).atLeastOnce();
        mediaIndexDao.removeChildren(isA(String.class));
        expectLastCall().atLeastOnce();

        replay(configurationDao, mediaIndexDao, directory);
        IcecastDaoImpl icecastDao = new IcecastDaoImpl(configurationDao, localHolmesDataDir, mediaIndexDao);
        try {
            icecastDao.loadDirectory(directory);
            List<IcecastGenre> genres = icecastDao.getGenres();
            assertNotNull(genres);
            assertEquals(2, genres.size());
            for (IcecastGenre genre : genres) {
                assertTrue(genre.getName().equals("genre1") || genre.getName().equals("genre2"));
            }
        } finally {
            verify(configurationDao, mediaIndexDao, directory);
        }
    }

    @Test
    public void testGetGenresDirectoryNotLoaded() {
        String localHolmesDataDir = System.getProperty("java.io.tmpdir");
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        expect(configurationDao.getListParameter(ICECAST_GENRE_LIST)).andReturn(Lists.newArrayList("genre1", "genre2")).atLeastOnce();
        expect(configurationDao.getBooleanParameter(ICECAST_ENABLE)).andReturn(true).atLeastOnce();
        expect(configurationDao.getIntParameter(ICECAST_MAX_DOWNLOAD_RETRY)).andReturn(3).atLeastOnce();
        mediaIndexDao.removeChildren(isA(String.class));
        expectLastCall().atLeastOnce();

        replay(configurationDao, mediaIndexDao);
        IcecastDaoImpl icecastDao = new IcecastDaoImpl(configurationDao, localHolmesDataDir, mediaIndexDao);
        try {
            icecastDao.loadDirectory(null);
            List<IcecastGenre> genres = icecastDao.getGenres();
            assertNotNull(genres);
            assertEquals(0, genres.size());
            for (IcecastGenre genre : genres) {
                assertTrue(genre.getName().equals("genre1") || genre.getName().equals("genre2"));
            }
        } finally {
            verify(configurationDao, mediaIndexDao);
        }
    }

}
