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

package net.holmes.core.manager.streaming.device;

import com.google.common.eventbus.EventBus;
import net.holmes.core.manager.media.model.AbstractNode;
import net.holmes.core.manager.streaming.event.StreamingEvent.StreamingEventType;

import static net.holmes.core.manager.streaming.event.StreamingEvent.newErrorEvent;
import static net.holmes.core.manager.streaming.event.StreamingEvent.newSuccessEvent;


/**
 * Device streamer.
 */
public abstract class DeviceStreamer<T extends Device> {
    private final EventBus eventBus;

    /**
     * Instantiates a new device streamer.
     *
     * @param eventBus event bus
     */
    public DeviceStreamer(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * Play content on device
     *
     * @param device     device
     * @param contentUrl content url
     * @param node       node
     */
    public abstract void play(T device, String contentUrl, AbstractNode node);

    /**
     * Stop content playback.
     *
     * @param device device
     */
    public abstract void stop(T device);

    /**
     * Pause content playback on device.
     *
     * @param device device
     */
    public abstract void pause(T device);

    /**
     * Resume content playback on device.
     *
     * @param device device
     */
    public abstract void resume(T device);

    /**
     * Update content playback status on device.
     *
     * @param device device
     */
    public abstract void updateStatus(T device);

    /**
     * Post error streaming event.
     *
     * @param type         event type
     * @param deviceId     device id
     * @param errorMessage error message
     */
    protected void sendFailure(final StreamingEventType type, final String deviceId, final String errorMessage) {
        eventBus.post(newErrorEvent(type, deviceId, errorMessage));
    }

    /**
     * Post success streaming event.
     *
     * @param type     event type
     * @param deviceId device id
     */
    protected void sendSuccess(final StreamingEventType type, final String deviceId) {
        eventBus.post(newSuccessEvent(type, deviceId, null, null));
    }

    /**
     * Post success streaming event.
     *
     * @param type     event type
     * @param deviceId device id
     * @param duration content duration
     * @param position playback position
     */
    protected void sendSuccess(final StreamingEventType type, final String deviceId, final Long duration, final Long position) {
        eventBus.post(newSuccessEvent(type, deviceId, duration, position));
    }

}
