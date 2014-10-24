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

package net.holmes.core.backend.response;

/**
 * Settings.
 */
public final class Settings {

    private String serverName;
    private Boolean prependPodcastItem;

    /**
     * Instantiates a new settings.
     */
    public Settings() {
    }

    /**
     * Instantiates a new settings.
     *
     * @param serverName         server name
     * @param prependPodcastItem prepend pod-cast item
     */
    public Settings(final String serverName, final Boolean prependPodcastItem) {
        this.serverName = serverName;
        this.prependPodcastItem = prependPodcastItem;
    }

    /**
     * Get server name.
     *
     * @return server name
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Set server name.
     *
     * @param serverName new server name
     */
    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }

    /**
     * Check if podcast number should be added to podcast name.
     *
     * @return true if podcast number should be added to podcast name
     */
    public Boolean getPrependPodcastItem() {
        return prependPodcastItem;
    }

    /**
     * Set whether podcast number should be added to podcast name.
     *
     * @param prependPodcastItem prepend podcast item
     */
    public void setPrependPodcastItem(final Boolean prependPodcastItem) {
        this.prependPodcastItem = prependPodcastItem;
    }
}
