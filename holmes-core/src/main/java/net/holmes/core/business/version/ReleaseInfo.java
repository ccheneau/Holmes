/*
 * Copyright (C) 2012-2015  Cedric Cheneau
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

package net.holmes.core.business.version;

/**
 * Release info
 */
public final class ReleaseInfo {
    private final String name;
    private final boolean needsUpdate;
    private final String url;

    /**
     * Instantiates a new version info.
     *
     * @param name        version name
     * @param needsUpdate needs update
     * @param url         update URL
     */
    public ReleaseInfo(final String name, final boolean needsUpdate, final String url) {
        this.name = name;
        this.needsUpdate = needsUpdate;
        this.url = url;
    }

    /**
     * Get release name.
     *
     * @return release name
     */
    public String getName() {
        return name;
    }

    /**
     * Check if release needs update.
     *
     * @return true if release needs update
     */
    public boolean isNeedsUpdate() {
        return needsUpdate;
    }

    /**
     * Get release URL.
     *
     * @return release URL
     */
    public String getUrl() {
        return url;
    }
}
