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

import net.holmes.core.business.streaming.device.CommandFailureHandler;
import net.holmes.core.business.streaming.upnp.device.UpnpDevice;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;

/**
 * Set content url on device
 */
public abstract class SetContentUrlCommand extends SetAVTransportURI implements UpnpSetCommand {
    private final CommandFailureHandler failureHandler;

    /**
     * Instantiates a new set content url command
     *
     * @param device         device
     * @param contentUrl     content Url
     * @param metadata       content metadata
     * @param failureHandler failure handler
     */
    public SetContentUrlCommand(UpnpDevice device, String contentUrl, String metadata, CommandFailureHandler failureHandler) {
        super(device.getAvTransportService(), contentUrl, metadata);
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

