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

package net.holmes.core.business.streaming.upnp.command;

import net.holmes.core.business.streaming.upnp.device.UpnpDevice;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.support.avtransport.callback.GetMediaInfo;
import org.fourthline.cling.support.model.MediaInfo;

import static net.holmes.core.business.streaming.event.StreamingEvent.StreamingEventType;

/**
 * Get media info on device
 */
public abstract class GetMediaInfoCommand extends GetMediaInfo {
    private final UpnpDevice device;
    private final StreamingEventType eventType;
    private final CommandFailureHandler failureHandler;

    /**
     * Instantiates a new get media info command
     *
     * @param device         device
     * @param eventType      event type
     * @param failureHandler failure handler
     */
    public GetMediaInfoCommand(UpnpDevice device, StreamingEventType eventType, CommandFailureHandler failureHandler) {
        super(device.getAvTransportService());
        this.device = device;
        this.eventType = eventType;
        this.failureHandler = failureHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
        failureHandler.handle(eventType, device.getId(), defaultMsg);
    }


    /**
     * {@inheritDoc}
     */
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
