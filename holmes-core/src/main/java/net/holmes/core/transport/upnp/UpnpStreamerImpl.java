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

import com.google.inject.Inject;
import net.holmes.core.transport.device.DeviceStreamer;
import net.holmes.core.transport.upnp.model.UpnpDevice;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.support.avtransport.callback.Pause;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.avtransport.callback.Stop;

/**
 * Manage streaming on Upnp device.
 */
public class UpnpStreamerImpl implements DeviceStreamer<UpnpDevice> {
    private final ControlPoint controlPoint;

    @Inject
    public UpnpStreamerImpl(UpnpService upnpService) {
        this.controlPoint = upnpService.getControlPoint();
    }

    @Override
    public void play(final UpnpDevice device, final String url) {
        // Set content Url
        controlPoint.execute(new SetAVTransportURI(device.getAvTransportService(), url) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                logFailure(invocation, operation, defaultMsg);
            }

            @Override
            public void success(ActionInvocation invocation) {
                // Play content
                controlPoint.execute(new Play(device.getAvTransportService()) {
                    @Override
                    public void success(ActionInvocation invocation) {
                    }

                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                        logFailure(invocation, response, defaultMsg);
                    }
                });
            }
        });
    }

    @Override
    public void stop(UpnpDevice device) {
        controlPoint.execute(new Stop(device.getAvTransportService()) {
            @Override
            public void success(ActionInvocation invocation) {
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                logFailure(invocation, operation, defaultMsg);
            }
        });
    }

    @Override
    public void pause(UpnpDevice device) {
        controlPoint.execute(new Pause(device.getAvTransportService()) {
            @Override
            public void success(ActionInvocation invocation) {
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                logFailure(invocation, response, defaultMsg);
            }
        });
    }

    @Override
    public void resume(UpnpDevice device) {
        // Resume content playback
        controlPoint.execute(new Play(device.getAvTransportService()) {
            @Override
            public void success(ActionInvocation invocation) {
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                logFailure(invocation, response, defaultMsg);
            }
        });
    }

    @Override
    public void updateStatus(UpnpDevice device) {
    }

    private void logFailure(final ActionInvocation invocation, final UpnpResponse response, final String defaultMsg) {

    }
}
