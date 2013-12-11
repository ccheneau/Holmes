/*
 * Copyright (C) 2012-2013  Cedric Cheneau
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

package net.holmes.core.transport.upnp;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import net.holmes.core.transport.device.DeviceStreamer;
import net.holmes.core.transport.upnp.model.UpnpDevice;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.support.avtransport.callback.*;
import org.fourthline.cling.support.model.PositionInfo;

import static net.holmes.core.transport.event.StreamingEvent.StreamingEventType.*;

/**
 * Manage streaming on Upnp device.
 */
public class UpnpStreamerImpl extends DeviceStreamer<UpnpDevice> {
    private final ControlPoint controlPoint;

    /**
     * Instantiates a new Upnp streaming implementation.
     *
     * @param upnpService Upnp service
     * @param eventBus    event bus
     */
    @Inject
    public UpnpStreamerImpl(final UpnpService upnpService, final EventBus eventBus) {
        super(eventBus);
        this.controlPoint = upnpService.getControlPoint();
    }

    @Override
    public void play(final UpnpDevice device, final String url) {
        // Set content Url
        controlPoint.execute(new SetAVTransportURI(device.getAvTransportService(), url) {
            @Override
            public void success(ActionInvocation invocation) {
                // Play content
                controlPoint.execute(new Play(device.getAvTransportService()) {
                    @Override
                    public void success(ActionInvocation invocation) {
                        sendSuccess(PLAY, device.getId());
                    }

                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                        sendFailure(PLAY, device.getId(), defaultMsg);
                    }
                });
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                sendFailure(PLAY, device.getId(), defaultMsg);
            }

        });
    }

    @Override
    public void stop(final UpnpDevice device) {
        controlPoint.execute(new Stop(device.getAvTransportService()) {
            @Override
            public void success(ActionInvocation invocation) {
                sendSuccess(STOP, device.getId());
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                sendFailure(STOP, device.getId(), defaultMsg);
            }
        });
    }

    @Override
    public void pause(final UpnpDevice device) {
        controlPoint.execute(new Pause(device.getAvTransportService()) {
            @Override
            public void success(ActionInvocation invocation) {
                sendSuccess(PAUSE, device.getId());
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                sendFailure(PAUSE, device.getId(), defaultMsg);
            }
        });
    }

    @Override
    public void resume(final UpnpDevice device) {
        // Resume content playback
        controlPoint.execute(new Play(device.getAvTransportService()) {
            @Override
            public void success(ActionInvocation invocation) {
                sendSuccess(RESUME, device.getId());
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                sendFailure(RESUME, device.getId(), defaultMsg);
            }
        });
    }

    @Override
    public void updateStatus(final UpnpDevice device) {
        controlPoint.execute(new GetPositionInfo(device.getAvTransportService()) {
            @Override
            public void received(ActionInvocation invocation, PositionInfo positionInfo) {
                sendSuccess(STATUS, device.getId(), positionInfo.getTrackElapsedSeconds(), positionInfo.getTrackDurationSeconds());
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                sendFailure(STATUS, device.getId(), defaultMsg);
            }
        });
    }
}
