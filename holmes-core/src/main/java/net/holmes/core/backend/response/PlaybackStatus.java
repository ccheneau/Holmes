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
     * Instantiates a new playback status
     *
     * @param contentName content name
     * @param duration    content duration
     * @param position    content position
     */
    public PlaybackStatus(String contentName, long duration, long position) {
        this.contentName = contentName;
        this.duration = duration;
        this.position = position;
        this.errorMessage = null;
    }

    /**
     * Instantiates a new playback status
     *
     * @param errorMessage error message
     */
    public PlaybackStatus(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
