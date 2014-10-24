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
 * Playback status.
 */
public class PlaybackStatus {
    private String contentName;
    private long duration;
    private long position;
    private String errorMessage;

    /**
     * Instantiates a new playback status
     */
    public PlaybackStatus() {
    }

    /**
     * Get content name.
     *
     * @return content name
     */
    public String getContentName() {
        return contentName;
    }

    /**
     * Set content name.
     *
     * @param contentName new content name
     */
    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    /**
     * Get duration.
     *
     * @return duration
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Set duration.
     *
     * @param duration new duration
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * Get position.
     *
     * @return position
     */
    public long getPosition() {
        return position;
    }

    /**
     * Set position.
     *
     * @param position new position
     */
    public void setPosition(long position) {
        this.position = position;
    }

    /**
     * Get error message.
     *
     * @return error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set error message.
     *
     * @param errorMessage new error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
