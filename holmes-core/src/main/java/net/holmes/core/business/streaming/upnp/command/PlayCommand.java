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

package net.holmes.core.business.streaming.upnp.command;

import net.holmes.core.business.streaming.device.CommandFailureHandler;
import net.holmes.core.business.streaming.upnp.device.UpnpDevice;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.support.avtransport.callback.Play;

/**
 * Play content on device
 */
public abstract class PlayCommand extends Play implements UpnpSetCommand {
    private final CommandFailureHandler failureHandler;

    /**
     * Instantiates a new play command
     *
     * @param device         device
     * @param failureHandler failure handler
     */
    public PlayCommand(UpnpDevice device, CommandFailureHandler failureHandler) {
        super(device.getAvTransportService());
        this.failureHandler = failureHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
        failureHandler.handle(defaultMsg);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final void success(ActionInvocation invocation) {
        success();
    }
}