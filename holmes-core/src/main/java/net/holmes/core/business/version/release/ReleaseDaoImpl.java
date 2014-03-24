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

import com.google.gson.Gson;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Release DAO implementation
 */
public class ReleaseDaoImpl implements ReleaseDao {
    private static final Logger LOGGER = getLogger(ReleaseDaoImpl.class);

    private volatile Release latestRelease;

    /**
     * Default constructor.
     */
    public ReleaseDaoImpl() {
        latestRelease = new Release();
        latestRelease.setDraft(false);
        latestRelease.setName("");
        latestRelease.setUrl("");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateRelease(final String releaseApiUrl) {
        try {
            // Connection  to Github release API
            URLConnection con = new URL(releaseApiUrl).openConnection();

            // Read json content
            StringBuilder json = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String line = in.readLine();
                while (line != null) {
                    json.append(line);
                    line = in.readLine();
                }
            }

            // Parse json content
            Release[] releases = new Gson().fromJson(json.toString(), Release[].class);

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
