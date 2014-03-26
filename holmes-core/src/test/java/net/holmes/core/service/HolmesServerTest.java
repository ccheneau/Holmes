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

package net.holmes.core.service;

import com.google.common.eventbus.DeadEvent;
import org.junit.Test;

import static org.easymock.EasyMock.*;

public class HolmesServerTest {

    @Test
    public void testHolmesServer() {
        Service service = createMock(Service.class);

        HolmesServer holmesServer = new HolmesServer(service, service, service, service, service, System.getProperty("java.io.tmpdir"));

        service.start();
        expectLastCall().times(5);
        service.stop();
        expectLastCall().times(5);

        replay(service);
        holmesServer.start();
        holmesServer.stop();
        verify(service);
    }

    @Test(expected = RuntimeException.class)
    public void testHolmesServerStartTwoServers() {
        Service service = createMock(Service.class);

        HolmesServer holmesServer = new HolmesServer(service, service, service, service, service, System.getProperty("java.io.tmpdir"));
        HolmesServer holmesServer2 = new HolmesServer(service, service, service, service, service, System.getProperty("java.io.tmpdir"));

        service.start();
        expectLastCall().times(5);
        service.stop();
        expectLastCall().times(5);

        replay(service);
        try {
            holmesServer.start();
            holmesServer2.start();
        } finally {
            holmesServer.stop();
            verify(service);
        }
    }

    @Test
    public void tesHandleDeadEvent() {
        Service service = createMock(Service.class);
        DeadEvent deadEvent = createMock(DeadEvent.class);

        expect(deadEvent.getEvent()).andReturn("");

        replay(service, deadEvent);

        HolmesServer holmesServer = new HolmesServer(service, service, service, service, service, System.getProperty("java.io.tmpdir"));
        holmesServer.handleDeadEvent(deadEvent);

        verify(service, deadEvent);
    }
}
