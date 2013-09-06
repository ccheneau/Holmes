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

package net.holmes.core.common;

/**
 * Global constants.
 */
public enum Constants {
    HOLMES_SITE_URL("http://ccheneau.github.io/Holmes/"), //
    HOLMES_WIKI_URL("https://github.com/ccheneau/Holmes/wiki"), //
    HOLMES_FRIENDLY_NAME("Holmes UPnP Server"), //
    HOLMES_SHORT_NAME("HUS"), //
    HOLMES_DESCRIPTION("UPnP/AV 1.0 Compliant Media Server"),
    HOLMES_MODEL_NUMBER("01"),
    UPNP_DATE_FORMAT("yyyy-MM-dd'T'HH:mm:ssZ");

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
