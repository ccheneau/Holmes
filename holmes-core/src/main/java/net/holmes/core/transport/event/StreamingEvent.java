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

package net.holmes.core.transport.event;

import com.google.common.base.Objects;

/**
 * Streaming event.
 */
public final class StreamingEvent {
    private final StreamingEventType type;
    private final String deviceId;
    private final boolean success;
    private final String errorMessage;
    private final Long duration;
    private final Long position;

    /**
     * Instantiates a new Streaming success event.
     *
     * @param type     event type
     * @param deviceId device id
     * @param duration playback duration
     * @param position playback position
     */
    private StreamingEvent(final StreamingEventType type, final String deviceId, final Long duration, final Long position) {
        this.type = type;
        this.deviceId = deviceId;
        this.success = true;
        this.errorMessage = null;
        this.duration = duration;
        this.position = position;
    }

    /**
     * Instantiates a new Streaming error event.
     *
     * @param type         streaming event type
     * @param deviceId     device id
     * @param errorMessage error message
     */
    private StreamingEvent(final StreamingEventType type, final String deviceId, final String errorMessage) {
        this.type = type;
        this.deviceId = deviceId;
        this.success = false;
        this.errorMessage = errorMessage;
        this.duration = null;
        this.position = null;
    }

    /**
     * Build a new Streaming success event.
     *
     * @param type     event type
     * @param deviceId device id
     * @param duration playback duration
     * @param position playback position
     */
    public static StreamingEvent newSuccessEvent(final StreamingEventType type, final String deviceId, final Long duration, final Long position) {
        return new StreamingEvent(type, deviceId, duration, position);
    }

    /**
     * Build a new Streaming error event.
     *
     * @param type         streaming event type
     * @param deviceId     device id
     * @param errorMessage error message
     */
    public static StreamingEvent newErrorEvent(final StreamingEventType type, final String deviceId, final String errorMessage) {
        return new StreamingEvent(type, deviceId, errorMessage);
    }

    public StreamingEventType getType() {
        return type;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Long getDuration() {
        return duration;
    }

    public Long getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("type", type)
                .add("deviceId", deviceId)
                .add("success", success)
                .add("errorMessage", errorMessage)
                .add("duration", duration)
                .add("position", position)
                .toString();
    }

    /**
     * Streaming event type.
     */
    public static enum StreamingEventType {
        PLAY, STOP, PAUSE, RESUME, STATUS, UNKNOWN
    }
}
