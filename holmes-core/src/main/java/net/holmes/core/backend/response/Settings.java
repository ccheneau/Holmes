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

package net.holmes.core.backend.response;

import com.google.common.base.Objects;

/**
 * Settings.
 */
public final class Settings {

    private String serverName;
    private Integer httpServerPort;
    private Boolean prependPodcastItem;
    private Boolean enableExternalSubtitles;
    private Boolean hideEmptyRootNodes;

    /**
     * Instantiates a new settings.
     */
    public Settings() {
    }

    /**
     * Instantiates a new settings.
     *
     * @param serverName              server name
     * @param httpServerPort          Http server port
     * @param prependPodcastItem      prepend pod-cast item
     * @param enableExternalSubtitles enable external subtitles
     * @param hideEmptyRootNodes      hide empty root nodes
     */
    public Settings(final String serverName, final Integer httpServerPort, final Boolean prependPodcastItem,
                    final Boolean enableExternalSubtitles, final boolean hideEmptyRootNodes) {
        this.serverName = serverName;
        this.httpServerPort = httpServerPort;
        this.prependPodcastItem = prependPodcastItem;
        this.enableExternalSubtitles = enableExternalSubtitles;
        this.hideEmptyRootNodes = hideEmptyRootNodes;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }

    public Integer getHttpServerPort() {
        return httpServerPort;
    }

    public void setHttpServerPort(final Integer httpServerPort) {
        this.httpServerPort = httpServerPort;
    }

    public Boolean getPrependPodcastItem() {
        return prependPodcastItem;
    }

    public void setPrependPodcastItem(final Boolean prependPodcastItem) {
        this.prependPodcastItem = prependPodcastItem;
    }

    public Boolean getEnableExternalSubtitles() {
        return enableExternalSubtitles;
    }

    public void setEnableExternalSubtitles(final Boolean enableExternalSubtitles) {
        this.enableExternalSubtitles = enableExternalSubtitles;
    }

    public Boolean getHideEmptyRootNodes() {
        return hideEmptyRootNodes;
    }

    public void setHideEmptyRootNodes(Boolean hideEmptyRootNodes) {
        this.hideEmptyRootNodes = hideEmptyRootNodes;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(serverName, httpServerPort, prependPodcastItem, enableExternalSubtitles, hideEmptyRootNodes);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        final Settings other = (Settings) obj;
        return Objects.equal(this.serverName, other.serverName) && Objects.equal(this.httpServerPort, other.httpServerPort) && Objects.equal(this.prependPodcastItem, other.prependPodcastItem) && Objects.equal(this.enableExternalSubtitles, other.enableExternalSubtitles) && Objects.equal(this.hideEmptyRootNodes, other.hideEmptyRootNodes);
    }
}
