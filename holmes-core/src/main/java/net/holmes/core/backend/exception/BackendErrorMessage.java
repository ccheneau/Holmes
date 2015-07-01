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

package net.holmes.core.backend.exception;

/**
 * Backend error messages enum
 */
public enum BackendErrorMessage {
    FOLDER_NAME_ERROR("backend.folder.name.error"),
    FOLDER_PATH_ERROR("backend.folder.path.error"),
    FOLDER_PATH_UNKNOWN_ERROR("backend.folder.path.unknown.error"),
    FOLDER_UNKNOWN_ERROR("backend.folder.unknown.error"),
    FOLDER_DUPLICATED_ERROR("backend.folder.already.exist.error"),
    PODCAST_NAME_ERROR("backend.podcast.name.error"),
    PODCAST_URL_ERROR("backend.podcast.url.error"),
    PODCAST_BAD_URL_ERROR("backend.podcast.url.malformed.error"),
    PODCAST_DUPLICATED_ERROR("backend.podcast.already.exist.error"),
    PODCAST_UNKNOWN_ERROR("backend.podcast.unknown.error"),
    SETTINGS_SERVER_NAME_ERROR("backend.settings.server.name.error");

    private final String messageKey;

    /**
     * Instantiates a new backend message.
     *
     * @param messageKey message key
     */
    BackendErrorMessage(final String messageKey) {
        this.messageKey = messageKey;
    }

    /**
     * Get message key.
     *
     * @return message key
     */
    public String getMessageKey() {
        return messageKey;
    }
}
