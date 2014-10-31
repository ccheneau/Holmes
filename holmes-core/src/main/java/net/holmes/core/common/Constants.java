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

package net.holmes.core.common;

/**
 * Global constants.
 */
public enum Constants {
    HOLMES_SITE_URL("http://ccheneau.github.io/Holmes/"),
    HOLMES_WIKI_URL("https://github.com/ccheneau/Holmes/wiki"),
    HOLMES_UPNP_SERVER_NAME("Holmes media Server"),
    HOLMES_UPNP_DESCRIPTION("UPnP Content Directory 1.0 Compliant Media Server"),
    HOLMES_HTTP_SERVER_NAME("Holmes HTTP Server"),
    HOLMES_APPLICATION_WELCOME_FILE("/index.html"),
    HOLMES_HOME_UI_DIRECTORY("ui"),
    HOLMES_HOME_CONF_DIRECTORY("conf"),
    HOLMES_GITHUB_RELEASE_API_URL("https://api.github.com/repos/ccheneau/Holmes/releases"),
    HTTP_CONTENT_REQUEST_PATH("/content"),
    HTTP_CONTENT_ID("id");

    /**
     * Constant value.
     */
    private final String value;

    /**
     * Instantiates a new constant.
     *
     * @param value constant value
     */
    private Constants(final String value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.value;
    }
}
