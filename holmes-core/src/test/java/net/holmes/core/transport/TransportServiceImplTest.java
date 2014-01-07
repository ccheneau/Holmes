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

package net.holmes.core.transport;

import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.common.mimetype.MimeType;
import net.holmes.core.media.model.AbstractNode;
import net.holmes.core.media.model.ContentNode;
import net.holmes.core.transport.airplay.device.AirplayDevice;
import net.holmes.core.transport.device.Device;
import net.holmes.core.transport.device.DeviceDao;
import net.holmes.core.transport.device.DeviceStreamer;
import net.holmes.core.transport.device.UnknownDeviceException;
import net.holmes.core.transport.event.StreamingEvent;
import net.holmes.core.transport.session.SessionDao;
import net.holmes.core.transport.session.UnknownSessionException;
import net.holmes.core.transport.upnp.device.UpnpDevice;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static net.holmes.core.common.configuration.Parameter.STREAMING_STATUS_UPDATE_DELAY_SECONDS;
import static net.holmes.core.transport.event.StreamingEvent.StreamingEventType.*;
import static net.holmes.core.transport.session.SessionStatus.*;
import static org.easymock.EasyMock.*;

public class TransportServiceImplTest {

    @Test
    public void testAddUpnpDevice() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        deviceDao.addDevice(isA(UpnpDevice.class));
        expectLastCall().atLeastOnce();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.addDevice(new UpnpDevice("id", "name", null, null, null));

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testAddAirplayDevice() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        deviceDao.addDevice(isA(AirplayDevice.class));
        expectLastCall().atLeastOnce();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.addDevice(new AirplayDevice("id", "name", null, 0, null));

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testRemoveDevice() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        sessionDao.removeDevice("deviceId");
        expectLastCall();
        deviceDao.removeDevice("deviceId");
        expectLastCall();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.removeDevice("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testFindDevices() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.findDevices("hostAddress")).andReturn(null);
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.findDevices("hostAddress");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testGetDevices() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevices()).andReturn(null);
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.getDevices();

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testGetSession() throws UnknownSessionException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(sessionDao.getSession("deviceId")).andReturn(null);
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.getSession("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test(expected = UnknownSessionException.class)
    public void testGetSessionUnknownDevice() throws UnknownSessionException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(sessionDao.getSession("deviceId")).andThrow(new UnknownSessionException("deviceId"));
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.getSession("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPlayOnUpnpDevice() throws UnknownDeviceException, IOException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new UpnpDevice("id", "name", null, null, null));
        sessionDao.initSession("deviceId", "contentUrl", "contentName");
        expectLastCall();
        upnpDeviceStreamer.play(isA(UpnpDevice.class), eq("contentUrl"), isA(AbstractNode.class));
        expectLastCall();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        File file = File.createTempFile("contentNode", "avi");
        file.deleteOnExit();

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.play("deviceId", "contentUrl", new ContentNode("contentNodeId", "parentNodeId", "contentName", file, new MimeType("video/avi")));

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPlayOnAirplayDevice() throws UnknownDeviceException, IOException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new AirplayDevice("id", "name", null, 0, null));
        sessionDao.initSession("deviceId", "contentUrl", "contentName");
        expectLastCall();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        airplayDeviceStreamer.play(isA(AirplayDevice.class), eq("contentUrl"), isA(AbstractNode.class));
        expectLastCall();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        File file = File.createTempFile("contentNode", "avi");
        file.deleteOnExit();

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.play("deviceId", "contentUrl", new ContentNode("contentNodeId", "parentNodeId", "contentName", file, new MimeType("video/avi")));

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlayOnFakeDevice() throws UnknownDeviceException, IOException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new FakeDevice());
        sessionDao.initSession("deviceId", "contentUrl", "contentName");
        expectLastCall();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        File file = File.createTempFile("contentNode", "avi");
        file.deleteOnExit();

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.play("deviceId", "contentUrl", new ContentNode("contentNodeId", "parentNodeId", "contentName", file, new MimeType("video/avi")));

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStopOnUpnpDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new UpnpDevice("id", "name", null, null, null));
        upnpDeviceStreamer.stop(isA(UpnpDevice.class));
        expectLastCall();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.stop("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStopOnAirplayDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new AirplayDevice("id", "name", null, 0, null));
        airplayDeviceStreamer.stop(isA(AirplayDevice.class));
        expectLastCall();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.stop("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStopOnFakeDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new FakeDevice());
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.stop("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPauseOnUpnpDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new UpnpDevice("id", "name", null, null, null));
        upnpDeviceStreamer.pause(isA(UpnpDevice.class));
        expectLastCall();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.pause("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPauseOnAirplayDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new AirplayDevice("id", "name", null, 0, null));
        airplayDeviceStreamer.pause(isA(AirplayDevice.class));
        expectLastCall();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.pause("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPauseOnFakeDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new FakeDevice());
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.pause("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testResumeOnUpnpDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new UpnpDevice("id", "name", null, null, null));
        upnpDeviceStreamer.resume(isA(UpnpDevice.class));
        expectLastCall();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.resume("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testResumeOnAirplayDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new AirplayDevice("id", "name", null, 0, null));
        airplayDeviceStreamer.resume(isA(AirplayDevice.class));
        expectLastCall();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.resume("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResumeOnFakeDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new FakeDevice());
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.resume("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testHandleErrorStreamingEvent() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.handleStreamingEvent(new StreamingEvent(STATUS, "deviceId", "errorMessage"));

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testHandleUnknownSessionStreamingEvent() throws Exception {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();
        sessionDao.updateSessionStatus("deviceId", PLAYING);
        expectLastCall().andThrow(new UnknownSessionException("deviceId"));

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.handleStreamingEvent(new StreamingEvent(PLAY, "deviceId", 0l, 0l));

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testHandleUnknownStreamingEvent() throws Exception {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.handleStreamingEvent(new StreamingEvent(UNKNOWN, "deviceId", 0l, 0l));

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testHandlePlayStreamingEvent() throws Exception {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();
        sessionDao.updateSessionStatus("deviceId", PLAYING);
        expectLastCall();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.handleStreamingEvent(new StreamingEvent(PLAY, "deviceId", 0l, 0l));

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testHandleResumeStreamingEvent() throws Exception {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();
        sessionDao.updateSessionStatus("deviceId", PLAYING);
        expectLastCall();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.handleStreamingEvent(new StreamingEvent(RESUME, "deviceId", 0l, 0l));

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testHandleStopStreamingEvent() throws Exception {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();
        sessionDao.updateSessionStatus("deviceId", WAITING);
        expectLastCall();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.handleStreamingEvent(new StreamingEvent(STOP, "deviceId", 0l, 0l));

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testHandlePauseStreamingEvent() throws Exception {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();
        sessionDao.updateSessionStatus("deviceId", PAUSED);
        expectLastCall();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.handleStreamingEvent(new StreamingEvent(PAUSE, "deviceId", 0l, 0l));

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testHandleStatusStreamingEvent() throws Exception {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();
        sessionDao.updateSessionPosition("deviceId", 0l, 0l);
        expectLastCall();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.handleStreamingEvent(new StreamingEvent(STATUS, "deviceId", 0l, 0l));

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    private class FakeDevice extends Device {
        /**
         * Instantiates a new device
         */
        public FakeDevice() {
            super("id", "name", null);
        }

        @Override
        public boolean isVideoSupported() {
            return false;
        }

        @Override
        public boolean isAudioSupported() {
            return false;
        }

        @Override
        public boolean isImageSupported() {
            return false;
        }

        @Override
        public boolean isSlideShowSupported() {
            return false;
        }

        @Override
        public void close() {
            // Nothing
        }
    }
}
