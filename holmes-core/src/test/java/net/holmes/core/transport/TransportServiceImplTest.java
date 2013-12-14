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

package net.holmes.core.transport;

import net.holmes.core.transport.airplay.AirplayDevice;
import net.holmes.core.transport.device.Device;
import net.holmes.core.transport.device.DeviceDao;
import net.holmes.core.transport.device.DeviceStreamer;
import net.holmes.core.transport.device.UnknownDeviceException;
import net.holmes.core.transport.session.SessionDao;
import net.holmes.core.transport.session.UnknownSessionException;
import net.holmes.core.transport.upnp.UpnpDevice;
import org.junit.Test;

import static org.easymock.EasyMock.*;

public class TransportServiceImplTest {

    @Test
    public void testAddUpnpDevice() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        deviceDao.addDevice(isA(UpnpDevice.class));
        expectLastCall().atLeastOnce();

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.addDevice(new UpnpDevice("id", "name", "hostAddress", null, null));

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testAddAirplayDevice() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        deviceDao.addDevice(isA(AirplayDevice.class));
        expectLastCall().atLeastOnce();

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.addDevice(new AirplayDevice("id", "name", "hostAddress", 0));

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testRemoveDevice() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        sessionDao.removeDevice("deviceId");
        expectLastCall();
        deviceDao.removeDevice("deviceId");
        expectLastCall();

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.removeDevice("deviceId");

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testFindDevices() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        expect(deviceDao.findDevices("hostAddress")).andReturn(null);

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.findDevices("hostAddress");

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testGetDevices() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        expect(deviceDao.getDevices()).andReturn(null);

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.getDevices();

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testGetSession() throws UnknownSessionException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        expect(sessionDao.getSession("deviceId")).andReturn(null);

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.getSession("deviceId");

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test(expected = UnknownSessionException.class)
    public void testGetSessionUnknownDevice() throws UnknownSessionException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        expect(sessionDao.getSession("deviceId")).andThrow(new UnknownSessionException("deviceId"));

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.getSession("deviceId");

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPlayOnUpnpDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        expect(deviceDao.getDevice("deviceId")).andReturn(new UpnpDevice("id", "name", "hostAddress", null, null));
        sessionDao.initSession("deviceId", "contentUrl", "contentName");
        expectLastCall();
        upnpDeviceStreamer.play(isA(UpnpDevice.class), eq("contentUrl"));
        expectLastCall();

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.play("deviceId", "contentUrl", "contentName");

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPlayOnAirplayDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        expect(deviceDao.getDevice("deviceId")).andReturn(new AirplayDevice("id", "name", "hostAddress", 0));
        sessionDao.initSession("deviceId", "contentUrl", "contentName");
        expectLastCall();
        airplayDeviceStreamer.play(isA(AirplayDevice.class), eq("contentUrl"));
        expectLastCall();

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.play("deviceId", "contentUrl", "contentName");

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlayOnFakeDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        expect(deviceDao.getDevice("deviceId")).andReturn(new FakeDevice("id", "name", "hostAddress"));
        sessionDao.initSession("deviceId", "contentUrl", "contentName");
        expectLastCall();

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.play("deviceId", "contentUrl", "contentName");

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStopOnUpnpDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        expect(deviceDao.getDevice("deviceId")).andReturn(new UpnpDevice("id", "name", "hostAddress", null, null));
        upnpDeviceStreamer.stop(isA(UpnpDevice.class));
        expectLastCall();

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.stop("deviceId");

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStopOnAirplayDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        expect(deviceDao.getDevice("deviceId")).andReturn(new AirplayDevice("id", "name", "hostAddress", 0));
        airplayDeviceStreamer.stop(isA(AirplayDevice.class));
        expectLastCall();

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.stop("deviceId");

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStopOnFakeDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        expect(deviceDao.getDevice("deviceId")).andReturn(new FakeDevice("id", "name", "hostAddress"));

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.stop("deviceId");

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPauseOnUpnpDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        expect(deviceDao.getDevice("deviceId")).andReturn(new UpnpDevice("id", "name", "hostAddress", null, null));
        upnpDeviceStreamer.pause(isA(UpnpDevice.class));
        expectLastCall();

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.pause("deviceId");

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPauseOnAirplayDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        expect(deviceDao.getDevice("deviceId")).andReturn(new AirplayDevice("id", "name", "hostAddress", 0));
        airplayDeviceStreamer.pause(isA(AirplayDevice.class));
        expectLastCall();

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.pause("deviceId");

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPauseOnFakeDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        expect(deviceDao.getDevice("deviceId")).andReturn(new FakeDevice("id", "name", "hostAddress"));

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.pause("deviceId");

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testResumeOnUpnpDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        expect(deviceDao.getDevice("deviceId")).andReturn(new UpnpDevice("id", "name", "hostAddress", null, null));
        upnpDeviceStreamer.resume(isA(UpnpDevice.class));
        expectLastCall();

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.resume("deviceId");

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testResumeOnAirplayDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        expect(deviceDao.getDevice("deviceId")).andReturn(new AirplayDevice("id", "name", "hostAddress", 0));
        airplayDeviceStreamer.resume(isA(AirplayDevice.class));
        expectLastCall();

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.resume("deviceId");

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResumeOnFakeDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        expect(deviceDao.getDevice("deviceId")).andReturn(new FakeDevice("id", "name", "hostAddress"));

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.resume("deviceId");

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateStatusOnUpnpDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        expect(deviceDao.getDevice("deviceId")).andReturn(new UpnpDevice("id", "name", "hostAddress", null, null));
        upnpDeviceStreamer.updateStatus(isA(UpnpDevice.class));
        expectLastCall();

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.updateStatus("deviceId");

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateStatusOnAirplayDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        expect(deviceDao.getDevice("deviceId")).andReturn(new AirplayDevice("id", "name", "hostAddress", 0));
        airplayDeviceStreamer.updateStatus(isA(AirplayDevice.class));
        expectLastCall();

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.updateStatus("deviceId");

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateStatusOnFakeDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        TransportServiceImpl transportService = new TransportServiceImpl(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        expect(deviceDao.getDevice("deviceId")).andReturn(new FakeDevice("id", "name", "hostAddress"));

        replay(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        transportService.updateStatus("deviceId");

        verify(deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    private class FakeDevice extends Device {
        /**
         * Instantiates a new device
         *
         * @param id          device id
         * @param name        device name
         * @param hostAddress device host
         */
        public FakeDevice(String id, String name, String hostAddress) {
            super(id, name, hostAddress);
        }
    }
}
