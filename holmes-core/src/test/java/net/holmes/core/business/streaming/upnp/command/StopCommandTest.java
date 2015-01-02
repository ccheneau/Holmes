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
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.Datatype;
import org.junit.Test;

import static java.lang.Boolean.TRUE;
import static org.easymock.EasyMock.*;

@SuppressWarnings("unchecked")
public class StopCommandTest {

    @Test
    public void testStopCommandSuccess() {

        UpnpDevice device = createMock(UpnpDevice.class);
        CommandFailureHandler failureHandler = createMock(CommandFailureHandler.class);
        ActionInvocation invocation = createMock(ActionInvocation.class);
        RemoteService avTransportService = createMock(RemoteService.class);
        Action<RemoteService> stopAction = createMock(Action.class);
        ActionArgument<RemoteService> actionArgument = createMock(ActionArgument.class);
        Datatype dataType = createMock(Datatype.class);

        expect(device.getAvTransportService()).andReturn(avTransportService);
        expect(avTransportService.getAction("Stop")).andReturn(stopAction);
        expect(stopAction.getInputArgument("InstanceID")).andReturn(actionArgument);
        expect(actionArgument.getDatatype()).andReturn(dataType);
        expect(actionArgument.getName()).andReturn("name");
        expect(dataType.isValid(anyObject())).andReturn(TRUE);
        expect(dataType.getString(anyObject())).andReturn("");

        replay(device, failureHandler, invocation, avTransportService, stopAction, actionArgument, dataType);

        StopCommand command = new StopCommand(device, failureHandler) {
            @Override
            public void success() {
            }
        };
        command.success(invocation);

        verify(device, failureHandler, invocation, avTransportService, stopAction, actionArgument, dataType);
    }

    @Test
    public void testStopCommandFailure() {

        UpnpDevice device = createMock(UpnpDevice.class);
        CommandFailureHandler failureHandler = createMock(CommandFailureHandler.class);
        ActionInvocation invocation = createMock(ActionInvocation.class);
        RemoteService avTransportService = createMock(RemoteService.class);
        Action<RemoteService> stopAction = createMock(Action.class);
        ActionArgument<RemoteService> actionArgument = createMock(ActionArgument.class);
        Datatype dataType = createMock(Datatype.class);
        UpnpResponse response = createMock(UpnpResponse.class);

        expect(device.getAvTransportService()).andReturn(avTransportService);
        expect(avTransportService.getAction("Stop")).andReturn(stopAction);
        expect(stopAction.getInputArgument("InstanceID")).andReturn(actionArgument);
        expect(actionArgument.getDatatype()).andReturn(dataType);
        expect(actionArgument.getName()).andReturn("name");
        expect(dataType.isValid(anyObject())).andReturn(TRUE);
        expect(dataType.getString(anyObject())).andReturn("");
        failureHandler.handle("message");
        expectLastCall();

        replay(device, failureHandler, invocation, avTransportService, stopAction, actionArgument, dataType, response);

        StopCommand command = new StopCommand(device, failureHandler) {
            @Override
            public void success() {
            }
        };
        command.failure(invocation, response, "message");

        verify(device, failureHandler, invocation, avTransportService, stopAction, actionArgument, dataType, response);
    }

}