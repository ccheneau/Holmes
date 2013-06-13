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
 * Media type.
 */
public enum MediaType {
    TYPE_VIDEO("video"), //
    TYPE_AUDIO("audio"), //
    TYPE_IMAGE("image"), //
    TYPE_PODCAST("podcast"), //
    TYPE_PLAYLIST("playlist"), //
    TYPE_APPLICATION("application"),//
    TYPE_NONE("none");

    private final String value;

    /**
     * Instantiates a new media type.
     *
     * @param value value
     */
    MediaType(final String value) {
        this.value = value;
    }

    /**
     * Get media type value.
     *
     * @return media type value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Get media type by value.
     *
     * @param mediaTypeValue media type value
     * @return MediaType corresponding to mediaTypeValue or MediaType.TYPE_NONE
     */
    public static MediaType getByValue(String mediaTypeValue) {
        for (MediaType mediaType : MediaType.values()) {
            if (mediaType.value.equals(mediaTypeValue)) return mediaType;
        }
        return MediaType.TYPE_NONE;
    }
}
