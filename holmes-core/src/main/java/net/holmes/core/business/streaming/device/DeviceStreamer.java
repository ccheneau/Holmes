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

package net.holmes.core.business.streaming.device;

import com.google.common.eventbus.EventBus;
import net.holmes.core.business.media.model.AbstractNode;
import net.holmes.core.business.streaming.event.StreamingEvent;
import net.holmes.core.business.streaming.event.StreamingEvent.StreamingEventType;


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
        eventBus.post(new StreamingEvent(type, deviceId, errorMessage));
    }

    /**
     * Post success streaming event.
     *
     * @param type     event type
     * @param deviceId device id
     */
    protected void sendSuccess(final StreamingEventType type, final String deviceId) {
        eventBus.post(new StreamingEvent(type, deviceId, null, null));
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
        eventBus.post(new StreamingEvent(type, deviceId, duration, position));
    }

    /**
     * Constructs a new Command!failureHandler.
     *
     * @param eventType event type
     * @param device    device
     * @return a new Command!failureHandler
     */
    protected CommandFailureHandler newCommandFailureHandler(StreamingEventType eventType, T device) {
        return new DeviceStreamerCommandFailureHandler<>(eventType, device);
    }

    /**
     * Device streamer command failure handler
     */
    private class DeviceStreamerCommandFailureHandler<U extends Device> extends CommandFailureHandler {
        final StreamingEventType eventType;
        final U device;

        /**
         * Constructs a new device streamer command failure handler.
         *
         * @param eventType event type
         * @param device    device
         */
        DeviceStreamerCommandFailureHandler(StreamingEventType eventType, U device) {
            this.eventType = eventType;
            this.device = device;
        }

        /**
         * Handle failure for streamer command.
         *
         * @param errorMessage error message
         */
        @Override
        public void handle(String errorMessage) {
            sendFailure(eventType, device.getId(), errorMessage);
        }
    }
}
