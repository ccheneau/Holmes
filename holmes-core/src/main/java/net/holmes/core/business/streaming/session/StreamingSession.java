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

package net.holmes.core.business.streaming.session;

import static com.google.common.base.MoreObjects.toStringHelper;

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

    /**
     * Get status.
     *
     * @return status
     */
    public SessionStatus getStatus() {
        return status;
    }

    /**
     * Set status.
     *
     * @param status new status
     */
    public void setStatus(final SessionStatus status) {
        this.status = status;
    }

    /**
     * Get content URL.
     *
     * @return content URL
     */
    public String getContentUrl() {
        return contentUrl;
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
     * Get duration.
     *
     * @return duration
     */
    public Long getDuration() {
        return duration;
    }

    /**
     * Set duration.
     *
     * @param duration new duration
     */
    public void setDuration(final Long duration) {
        this.duration = duration;
    }

    /**
     * Get position.
     *
     * @return position
     */
    public Long getPosition() {
        return position;
    }

    /**
     * Set position.
     *
     * @param position new position
     */
    public void setPosition(final Long position) {
        this.position = position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toStringHelper(this)
                .add("status", status)
                .add("contentUrl", contentUrl)
                .add("contentName", contentName)
                .add("duration", duration)
                .add("position", position)
                .toString();
    }
}
