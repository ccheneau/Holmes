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
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;
import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.common.event.ConfigurationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.Calendar.HOUR;
import static net.holmes.core.common.configuration.Parameter.*;
import static net.holmes.core.common.event.ConfigurationEvent.EventType.SAVE_SETTINGS;

/**
 * Icecast Dao implementation.
 */
public final class IcecastDaoImpl implements IcecastDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(IcecastDaoImpl.class);
    private static final String ICECAST_FILE_NAME = "icecast.xml";
    private static final String DATA_DIR = "data";
    private final Configuration configuration;
    private final String localHolmesDataDir;
    private final List<String> genreList;
    private final Object directoryLock = new Object();
    private final Object settingsLock = new Object();
    private boolean enable;
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
        this.enable = configuration.getBooleanParameter(ENABLE_ICECAST_DIRECTORY);
    }

    @Override
    public boolean isAvailableYellowPage() {
        // Check local Yellow page exists and is not too old.
        // If not, download new Yellow page.
        try {
            return enable && (!needsYellowPageDownload() || downloadYellowPage());
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean parseYellowPage() {
        Path ypPath = getIcecastXmlFile();
        return Files.exists(ypPath) && parseYellowPage(ypPath.toFile());
    }

    @Override
    public boolean isLoaded() {
        synchronized (directoryLock) {
            return enable && directory != null && directory.getEntries().size() > 0;
        }
    }

    @Override
    public Collection<IcecastEntry> getEntriesByGenre(final String genre) {
        synchronized (directoryLock) {
            if (directory != null && directory.getEntries() != null)
                return Collections2.filter(directory.getEntries(), new IcecastEntryGenrePredicate(genre));

            return Lists.newArrayList();
        }
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
     * @throws IOException
     */
    private boolean downloadYellowPage() throws IOException {
        LOGGER.info("Downloading Icecast Yellow page");
        URL icecastYellowPage = new URL(configuration.getParameter(ICECAST_YELLOW_PAGE_URL));
        File tempFile = getIcecastXmlTempFile().toFile();
        try (ReadableByteChannel in = Channels.newChannel(icecastYellowPage.openStream());
             FileOutputStream out = new FileOutputStream(tempFile)) {

            // Download Icecast yellow page to temp file.
            out.getChannel().transferFrom(in, 0, Long.MAX_VALUE);

            LOGGER.info("Icecast Yellow page downloaded");
        }
        // Rename temp file once download is complete
        Path xmlFile = getIcecastXmlFile();
        Files.deleteIfExists(xmlFile);
        return tempFile.renameTo(xmlFile.toFile());
    }

    /**
     * Check if Icecast Yellow page needs download.
     *
     * @return true if Icecast Yellow page need download
     */
    private boolean needsYellowPageDownload() throws IOException {
        Calendar cal = Calendar.getInstance();
        cal.add(HOUR, -(configuration.getIntParameter(ICECAST_YELLOW_PAGE_DOWNLOAD_DELAY_HOURS)));

        Path xmlFile = getIcecastXmlFile();
        return !Files.exists(xmlFile) || Files.getLastModifiedTime(xmlFile).toMillis() < cal.getTimeInMillis();

    }

    @VisibleForTesting
    boolean parseYellowPage(final File ypFile) {
        boolean result = true;

        // Configure XStream
        XStream xstream = new XStream(new Xpp3Driver());
        xstream.alias("directory", IcecastDirectory.class);
        xstream.alias("entry", IcecastEntry.class);
        xstream.addImplicitCollection(IcecastDirectory.class, "entries");
        xstream.aliasField("server_name", IcecastEntry.class, "name");
        xstream.aliasField("listen_url", IcecastEntry.class, "url");
        xstream.aliasField("server_type", IcecastEntry.class, "type");
        xstream.ignoreUnknownElements();

        // Parse Xml file using object input stream
        try (InputStream in = new FileInputStream(ypFile);
             ObjectInputStream ois = xstream.createObjectInputStream(in)) {

            Set<IcecastEntry> entries = Sets.newHashSet();
            try {
                Object object = ois.readObject();
                while (object != null) {
                    if (object instanceof IcecastEntry)
                        entries.add((IcecastEntry) object);

                    object = ois.readObject();
                }
            } catch (EOFException e) {
                // End of file reached. Ignore
            }
            // Set new Icecast directory
            if (!entries.isEmpty())
                setDirectory(new IcecastDirectory(entries));

        } catch (IOException | XStreamException | ClassNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            result = false;
        }

        // Remove Yellow page on error
        if (!result && !ypFile.delete()) LOGGER.error("Failed to remove {}", ypFile.getAbsolutePath());

        return result;
    }

    @VisibleForTesting
    IcecastDirectory getDirectory() {
        return directory;
    }

    private void setDirectory(IcecastDirectory directory) {
        synchronized (directoryLock) {
            this.directory = directory;
            LOGGER.info("Icecast directory contains {} entries", this.directory != null ? this.directory.getEntries().size() : 0);
        }
    }

    /**
     * Configuration settings have changed, check for download Icecast Yellow page.
     *
     * @param configurationEvent configuration event
     */
    @Subscribe
    public void handleConfigEvent(final ConfigurationEvent configurationEvent) throws IOException {
        if (configurationEvent.getType() == SAVE_SETTINGS)
            synchronized (settingsLock) {
                enable = configuration.getBooleanParameter(ENABLE_ICECAST_DIRECTORY);
                if (enable) {
                    // Parse Yellow page if it is not already loaded
                    if (isAvailableYellowPage() && !isLoaded()) parseYellowPage();
                } else {
                    // Reset directory
                    setDirectory(null);
                }
            }
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