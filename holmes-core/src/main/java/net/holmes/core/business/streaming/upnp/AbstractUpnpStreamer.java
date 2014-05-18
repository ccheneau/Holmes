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

package net.holmes.core.business.streaming.upnp;

import com.google.common.eventbus.EventBus;
import net.holmes.core.business.streaming.device.DeviceStreamer;
import net.holmes.core.business.streaming.upnp.device.UpnpDevice;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.support.avtransport.callback.*;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportInfo;

import static net.holmes.core.business.streaming.event.StreamingEvent.StreamingEventType;

/**
 * Abstract UPnP streamer
 */
public abstract class AbstractUpnpStreamer extends DeviceStreamer<UpnpDevice> {
    /**
     * Instantiates a new device streamer.
     *
     * @param eventBus event bus
     */
    public AbstractUpnpStreamer(EventBus eventBus) {
        super(eventBus);
    }

    /**
     * Get transport info on device
     */
    protected abstract class GetDeviceTransportInfo extends GetTransportInfo {

        private final UpnpDevice device;
        private final StreamingEventType eventType;

        /**
         * Instantiates a new get transport info command
         *
         * @param device    device
         * @param eventType event type
         */
        public GetDeviceTransportInfo(UpnpDevice device, StreamingEventType eventType) {
            super(device.getAvTransportService());
            this.device = device;
            this.eventType = eventType;
        }

        @Override
        public final void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
            sendFailure(eventType, device.getId(), defaultMsg);
        }


        @Override
        public final void received(ActionInvocation invocation, TransportInfo transportInfo) {
            success(transportInfo);
        }

        /**
         * Success callback.
         *
         * @param transportInfo transport info
         */
        public abstract void success(TransportInfo transportInfo);
    }

    /**
     * Get media info on device
     */
    protected abstract class GetDeviceMediaInfo extends GetMediaInfo {

        private final UpnpDevice device;
        private final StreamingEventType eventType;

        /**
         * Instantiates a new get media info command
         *
         * @param device    device
         * @param eventType event type
         */
        public GetDeviceMediaInfo(UpnpDevice device, StreamingEventType eventType) {
            super(device.getAvTransportService());
            this.device = device;
            this.eventType = eventType;
        }

        @Override
        public final void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
            sendFailure(eventType, device.getId(), defaultMsg);
        }


        @Override
        public final void received(ActionInvocation invocation, MediaInfo mediaInfo) {
            success(mediaInfo);
        }

        /**
         * Success callback.
         *
         * @param mediaInfo media info
         */
        public abstract void success(MediaInfo mediaInfo);
    }

    /**
     * Get position info on device
     */
    protected abstract class GetDevicePositionInfo extends GetPositionInfo {

        private final UpnpDevice device;
        private final StreamingEventType eventType;

        /**
         * Instantiates a new get position info command
         *
         * @param device    device
         * @param eventType event type
         */
        public GetDevicePositionInfo(UpnpDevice device, StreamingEventType eventType) {
            super(device.getAvTransportService());
            this.device = device;
            this.eventType = eventType;
        }

        @Override
        public final void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
            sendFailure(eventType, device.getId(), defaultMsg);
        }


        @Override
        public final void received(ActionInvocation invocation, PositionInfo positionInfo) {
            success(positionInfo);
        }

        /**
         * Success callback.
         *
         * @param positionInfo position info
         */
        public abstract void success(PositionInfo positionInfo);
    }

    /**
     * Set content url on device
     */
    protected abstract class SetContentUrlOnDevice extends SetAVTransportURI {

        private final UpnpDevice device;
        private final StreamingEventType eventType;

        /**
         * Instantiates a new set content url command
         *
         * @param device     device
         * @param eventType  event type
         * @param contentUrl content Url
         * @param metadata   content metadata
         */
        public SetContentUrlOnDevice(UpnpDevice device, StreamingEventType eventType, String contentUrl, String metadata) {
            super(device.getAvTransportService(), contentUrl, metadata);
            this.device = device;
            this.eventType = eventType;
        }

        @Override
        public final void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
            sendFailure(eventType, device.getId(), defaultMsg);
        }


        @Override
        public final void success(ActionInvocation invocation) {
            success();
        }

        /**
         * Success callback.
         */
        public abstract void success();
    }

    /**
     * Play content on device
     */
    protected abstract class PlayOnDevice extends Play {

        private final UpnpDevice device;
        private final StreamingEventType playEventType;

        /**
         * Instantiates a new play command
         *
         * @param device        device
         * @param playEventType play event type
         */
        public PlayOnDevice(UpnpDevice device, StreamingEventType playEventType) {
            super(device.getAvTransportService());
            this.device = device;
            this.playEventType = playEventType;
        }

        @Override
        public final void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
            sendFailure(playEventType, device.getId(), defaultMsg);
        }


        @Override
        public final void success(ActionInvocation invocation) {
            success();
        }

        /**
         * Success callback.
         */
        public abstract void success();
    }

    /**
     * Stop playing content on device
     */
    protected abstract class StopOnDevice extends Stop {

        private final UpnpDevice device;
        private final StreamingEventType stopEventType;

        /**
         * Instantiates a new stop command
         *
         * @param device        device
         * @param stopEventType event type
         */
        public StopOnDevice(UpnpDevice device, StreamingEventType stopEventType) {
            super(device.getAvTransportService());
            this.device = device;
            this.stopEventType = stopEventType;
        }

        @Override
        public final void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
            sendFailure(stopEventType, device.getId(), defaultMsg);
        }


        @Override
        public final void success(ActionInvocation invocation) {
            success();
        }

        /**
         * Success callback.
         */
        public abstract void success();
    }

    /**
     * Suspend playing content on device
     */
    protected abstract class PauseOnDevice extends Pause {

        private final UpnpDevice device;

        /**
         * Instantiates a new pause command
         *
         * @param device device
         */
        public PauseOnDevice(UpnpDevice device) {
            super(device.getAvTransportService());
            this.device = device;
        }

        @Override
        public final void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
            sendFailure(StreamingEventType.PAUSE, device.getId(), defaultMsg);
        }


        @Override
        public final void success(ActionInvocation invocation) {
            success();
        }

        /**
         * Success callback.
         */
        public abstract void success();
    }
}
