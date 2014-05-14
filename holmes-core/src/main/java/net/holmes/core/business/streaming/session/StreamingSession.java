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

package net.holmes.core.business.streaming.session;

import com.google.common.base.Objects;

/**
 * Streaming session
 */
public final class StreamingSession {
    private final String contentName;
    private final String contentUrl;
    private SessionStatus status = SessionStatus.WAITING;
    private Long duration = 0L;
    private Long position = 0L;

    /**
     * Instantiates a new streaming session
     *
     * @param contentName content name
     * @param contentUrl  content URL
     */
    public StreamingSession(final String contentName, final String contentUrl) {
        this.contentName = contentName;
        this.contentUrl = contentUrl;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public String getContentName() {
        return contentName;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("status", status)
                .add("contentUrl", contentUrl)
                .add("contentName", contentName)
                .add("duration", duration)
                .add("position", position)
                .toString();
    }
}
