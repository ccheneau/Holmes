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

package net.holmes.core.business.version.release;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Release DAO implementation
 */
@Singleton
public final class ReleaseDaoImpl implements ReleaseDao {
    private static final Logger LOGGER = getLogger(ReleaseDaoImpl.class);

    private volatile Release latestRelease;

    /**
     * Default constructor.
     */
    public ReleaseDaoImpl() {
        latestRelease = new Release("", "", false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateRelease(final String releaseApiUrl) {
        try {
            // Parse json content from URL
            List<Release> releases = new ObjectMapper().readValue(new URL(releaseApiUrl), new TypeReference<List<Release>>() {
            });

            // Get latest available release
            for (Release release : releases) {
                if (!release.isDraft()) {
                    LOGGER.info("Latest Holmes release available {}", release.getName());
                    latestRelease = release;
                    break;
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Release getLatestRelease() {
        return latestRelease;
    }
}
