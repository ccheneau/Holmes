/**
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

/**
 * Settings.
 */
public final class Settings {

    private String serverName;
    private Integer httpServerPort;
    private Boolean prependPodcastItem;
    private Boolean enableExternalSubtitles;

    /**
     * Instantiates a new settings.
     */
    public Settings() {
    }

    /**
     * Instantiates a new settings.
     *
     * @param serverName server name
     * @param httpServerPort Http server port
     * @param prependPodcastItem prepend podcast item
     * @param enableExternalSubtitles enable external subtitles
     */
    public Settings(final String serverName, final Integer httpServerPort, final Boolean prependPodcastItem, final Boolean enableExternalSubtitles) {
        this.serverName = serverName;
        this.httpServerPort = httpServerPort;
        this.prependPodcastItem = prependPodcastItem;
        this.enableExternalSubtitles = enableExternalSubtitles;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((enableExternalSubtitles == null) ? 0 : enableExternalSubtitles.hashCode());
        result = prime * result + ((httpServerPort == null) ? 0 : httpServerPort.hashCode());
        result = prime * result + ((prependPodcastItem == null) ? 0 : prependPodcastItem.hashCode());
        result = prime * result + ((serverName == null) ? 0 : serverName.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Settings other = (Settings) obj;
        if (enableExternalSubtitles == null) {
            if (other.enableExternalSubtitles != null) return false;
        } else if (!enableExternalSubtitles.equals(other.enableExternalSubtitles)) return false;
        if (httpServerPort == null) {
            if (other.httpServerPort != null) return false;
        } else if (!httpServerPort.equals(other.httpServerPort)) return false;
        if (prependPodcastItem == null) {
            if (other.prependPodcastItem != null) return false;
        } else if (!prependPodcastItem.equals(other.prependPodcastItem)) return false;
        if (serverName == null) {
            if (other.serverName != null) return false;
        } else if (!serverName.equals(other.serverName)) return false;
        return true;
    }
}
