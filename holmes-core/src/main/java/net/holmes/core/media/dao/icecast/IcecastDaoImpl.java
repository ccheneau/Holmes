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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;
import net.holmes.core.common.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import static java.util.Calendar.DAY_OF_YEAR;
import static net.holmes.core.common.configuration.Parameter.ICECAST_GENRE_LIST;
import static net.holmes.core.common.configuration.Parameter.ICECAST_YELLOW_PAGE_URL;

/**
 * Icecast Dao implementation.
 */
public final class IcecastDaoImpl implements IcecastDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(IcecastDaoImpl.class);
    private static final String ICECAST_FILE_NAME = "icecast.xml";
    private static final String DATA_DIR = "data";
    private static final int DWL_DATA_LENGTH = 8192;
    private final Configuration configuration;
    private final String localHolmesDataDir;
    private final List<String> genreList;
    private final Object directoryLock = new Object();
    private IcecastDirectory directory;

    /**
     * Instantiates a new Icecast Dao implementation.
     *
     * @param configuration      configuration
     * @param localHolmesDataDir local Holmes data directory
     */
    @Inject
    public IcecastDaoImpl(final Configuration configuration, @Named("localHolmesDataDir") final String localHolmesDataDir) {
        this.configuration = configuration;
        this.localHolmesDataDir = localHolmesDataDir;
        this.genreList = Splitter.on(",").splitToList(configuration.getParameter(ICECAST_GENRE_LIST));
    }

    @Override
    public boolean checkDownloadYellowPage() {
        // Check xml file already exists and is not older than one day.
        // If not, download Yellow page.
        Calendar cal = Calendar.getInstance();
        cal.add(DAY_OF_YEAR, -1);
        Path xmlFile = getIcecastXmlFile();
        try {
            return Files.exists(xmlFile) && Files.getLastModifiedTime(xmlFile).toMillis() >= cal.getTimeInMillis() || downloadYellowPage();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void parseYellowPage() {
        Path ypPath = getIcecastXmlFile();
        if (Files.exists(ypPath))
            parseYellowPage(ypPath.toFile());
    }

    @Override
    public boolean loaded() {
        return directory != null && directory.getEntries().size() > 0;
    }

    @Override
    public Collection<IcecastEntry> getEntriesByGenre(final String genre) {
        if (directory != null && directory.getEntries() != null)
            return Collections2.filter(directory.getEntries(), new IcecastEntryGenrePredicate(genre));

        return Lists.newArrayList();
    }

    @Override
    public List<String> getGenres() {
        return genreList;
    }

    /**
     * Get local data directory.
     *
     * @return local data directory
     */
    private Path getDataPath() {
        Path dataPath = Paths.get(localHolmesDataDir, DATA_DIR);
        if (Files.isDirectory(dataPath) || dataPath.toFile().mkdirs())
            return dataPath;

        throw new RuntimeException("Failed to create " + dataPath);

    }

    /**
     * Get Icecast local file.
     *
     * @return Icecast local file
     */
    private Path getIcecastXmlFile() {
        return Paths.get(getDataPath().toString(), ICECAST_FILE_NAME);
    }

    /**
     * Get Icecast local file.
     *
     * @return Icecast local file
     */
    private Path getIcecastXmlTempFile() {
        return Paths.get(getDataPath().toString(), ICECAST_FILE_NAME + ".tmp");
    }

    /**
     * Download Icecast yellow page.
     *
     * @return true on download success
     */
    private boolean downloadYellowPage() throws IOException {
        LOGGER.info("Start downloading Icecast Yellow page");
        URL icecastYellowPage = new URL(configuration.getParameter(ICECAST_YELLOW_PAGE_URL));
        File tempFile = getIcecastXmlTempFile().toFile();
        try (InputStream in = new BufferedInputStream(icecastYellowPage.openStream());
             OutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile))) {

            // Download Icecast yellow page to temp file.
            byte data[] = new byte[DWL_DATA_LENGTH];
            int count;
            while ((count = in.read(data, 0, DWL_DATA_LENGTH)) != -1)
                out.write(data, 0, count);

            LOGGER.info("End downloading Icecast Yellow page");
        }
        // Rename temp file once download is complete
        Path xmlFile = getIcecastXmlFile();
        Files.deleteIfExists(xmlFile);
        return tempFile.renameTo(xmlFile.toFile());
    }

    @VisibleForTesting
    void parseYellowPage(final File ypFile) {
        XStream xstream = new XStream(new Xpp3Driver());
        xstream.alias("directory", IcecastDirectory.class);
        xstream.alias("entry", IcecastEntry.class);
        xstream.addImplicitCollection(IcecastDirectory.class, "entries");
        xstream.aliasField("server_name", IcecastEntry.class, "name");
        xstream.aliasField("listen_url", IcecastEntry.class, "url");
        xstream.aliasField("server_type", IcecastEntry.class, "type");
        xstream.ignoreUnknownElements();

        try (InputStream in = new FileInputStream(ypFile)) {
            // Load configuration from XML
            synchronized (directoryLock) {
                setDirectory((IcecastDirectory) xstream.fromXML(in));
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Icecast directory contains {} entries", directory.getEntries().size());
            }
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    @VisibleForTesting
    IcecastDirectory getDirectory() {
        return directory;
    }

    @VisibleForTesting
    void setDirectory(IcecastDirectory directory) {
        this.directory = directory;
    }

    /**
     * Predicate used to filter Icecast entries by genre.
     */
    private static final class IcecastEntryGenrePredicate implements Predicate<IcecastEntry> {
        private final String genre;

        IcecastEntryGenrePredicate(final String genre) {
            this.genre = genre;
        }

        @Override
        public boolean apply(final IcecastEntry entry) {
            return entry != null && entry.getGenre().contains(genre);
        }
    }
}