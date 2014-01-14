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
    HOLMES_UPNP_SERVER_NAME("Holmes UPnP Server"),
    HOLMES_UPNP_SHORT_NAME("HUS"),
    HOLMES_UPNP_DESCRIPTION("UPnP/AV 1.0 Compliant Media Server"),
    HOLMES_UPNP_MODEL_NUMBER("01"),
    HOLMES_HTTP_SERVER_NAME("Holmes HTTP Server"),
    UPNP_DATE_FORMAT("yyyy-MM-dd'T'HH:mm:ssZ"),
    HTTP_CONTENT_REQUEST_PATH("/content"),
    HTTP_CONTENT_ID("id");

    private final String value;

    /**
     * New constant.
     *
     * @param value value
     */
    Constants(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
