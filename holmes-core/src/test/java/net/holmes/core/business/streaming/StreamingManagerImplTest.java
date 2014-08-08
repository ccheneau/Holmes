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

package net.holmes.core.business.streaming;

import net.holmes.core.business.configuration.ConfigurationDao;
import net.holmes.core.business.media.model.AbstractNode;
import net.holmes.core.business.media.model.ContentNode;
import net.holmes.core.business.mimetype.model.MimeType;
import net.holmes.core.business.streaming.airplay.device.AirplayDevice;
import net.holmes.core.business.streaming.device.Device;
import net.holmes.core.business.streaming.device.DeviceDao;
import net.holmes.core.business.streaming.device.DeviceStreamer;
import net.holmes.core.business.streaming.device.UnknownDeviceException;
import net.holmes.core.business.streaming.event.StreamingEvent;
import net.holmes.core.business.streaming.session.SessionDao;
import net.holmes.core.business.streaming.session.UnknownSessionException;
import net.holmes.core.business.streaming.upnp.device.UpnpDevice;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static net.holmes.core.business.streaming.event.StreamingEvent.StreamingEventType.*;
import static net.holmes.core.business.streaming.session.SessionStatus.*;
import static net.holmes.core.common.ConfigurationParameter.STREAMING_STATUS_UPDATE_DELAY_SECONDS;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertNull;

public class StreamingManagerImplTest {

    @Test
    public void testAddUpnpDevice() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        deviceDao.addDevice(isA(UpnpDevice.class));
        expectLastCall().atLeastOnce();
        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.addDevice(new UpnpDevice("id", "name", null, null, null));

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testAddAirplayDevice() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        deviceDao.addDevice(isA(AirplayDevice.class));
        expectLastCall().atLeastOnce();
        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.addDevice(new AirplayDevice("id", "name", null, 0, null));

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testRemoveDevice() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(deviceDao.removeDevice("deviceId")).andReturn(true);
        sessionDao.removeDevice("deviceId");
        expectLastCall();
        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.removeDevice("deviceId");

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testRemoveUnknownDevice() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(deviceDao.removeDevice("deviceId")).andReturn(false);
        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.removeDevice("deviceId");

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testFindDevices() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(deviceDao.findDevices("hostAddress")).andReturn(null);
        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.findDevices("hostAddress");

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testGetDevices() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(deviceDao.getDevices()).andReturn(null);
        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.getDevices();

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testGetDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(null);
        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        Device device = streamingManager.getDevice("deviceId");

        assertNull(device);

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testGetSession() throws UnknownSessionException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(sessionDao.getSession("deviceId")).andReturn(null);
        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.getSession("deviceId");

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test(expected = UnknownSessionException.class)
    public void testGetSessionUnknownDevice() throws UnknownSessionException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(sessionDao.getSession("deviceId")).andThrow(new UnknownSessionException("deviceId"));
        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.getSession("deviceId");

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPlayOnUpnpDevice() throws UnknownDeviceException, IOException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new UpnpDevice("id", "name", null, null, null));
        sessionDao.initSession("deviceId", "contentUrl", "contentName");
        expectLastCall();
        upnpDeviceStreamer.play(isA(UpnpDevice.class), eq("contentUrl"), isA(AbstractNode.class));
        expectLastCall();
        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        File file = File.createTempFile("contentNode", "avi");
        file.deleteOnExit();

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.play("deviceId", "contentUrl", new ContentNode("contentNodeId", "parentNodeId", "contentName", file, MimeType.valueOf("video/avi")));

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPlayOnAirplayDevice() throws UnknownDeviceException, IOException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new AirplayDevice("id", "name", null, 0, null));
        sessionDao.initSession("deviceId", "contentUrl", "contentName");
        expectLastCall();
        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        airplayDeviceStreamer.play(isA(AirplayDevice.class), eq("contentUrl"), isA(AbstractNode.class));
        expectLastCall();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        File file = File.createTempFile("contentNode", "avi");
        file.deleteOnExit();

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.play("deviceId", "contentUrl", new ContentNode("contentNodeId", "parentNodeId", "contentName", file, MimeType.valueOf("video/avi")));

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlayOnFakeDevice() throws UnknownDeviceException, IOException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new FakeDevice());
        sessionDao.initSession("deviceId", "contentUrl", "contentName");
        expectLastCall();
        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        File file = File.createTempFile("contentNode", "avi");
        file.deleteOnExit();

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.play("deviceId", "contentUrl", new ContentNode("contentNodeId", "parentNodeId", "contentName", file, MimeType.valueOf("video/avi")));

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStopOnUpnpDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new UpnpDevice("id", "name", null, null, null));
        upnpDeviceStreamer.stop(isA(UpnpDevice.class));
        expectLastCall();
        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.stop("deviceId");

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStopOnAirplayDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new AirplayDevice("id", "name", null, 0, null));
        airplayDeviceStreamer.stop(isA(AirplayDevice.class));
        expectLastCall();
        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.stop("deviceId");

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStopOnFakeDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new FakeDevice());
        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.stop("deviceId");

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPauseOnUpnpDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new UpnpDevice("id", "name", null, null, null));
        upnpDeviceStreamer.pause(isA(UpnpDevice.class));
        expectLastCall();
        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.pause("deviceId");

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPauseOnAirplayDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new AirplayDevice("id", "name", null, 0, null));
        airplayDeviceStreamer.pause(isA(AirplayDevice.class));
        expectLastCall();
        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.pause("deviceId");

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPauseOnFakeDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new FakeDevice());
        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.pause("deviceId");

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testResumeOnUpnpDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new UpnpDevice("id", "name", null, null, null));
        upnpDeviceStreamer.resume(isA(UpnpDevice.class));
        expectLastCall();
        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.resume("deviceId");

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testResumeOnAirplayDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new AirplayDevice("id", "name", null, 0, null));
        airplayDeviceStreamer.resume(isA(AirplayDevice.class));
        expectLastCall();
        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.resume("deviceId");

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResumeOnFakeDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new FakeDevice());
        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.resume("deviceId");

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testHandleErrorStreamingEvent() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.handleStreamingEvent(new StreamingEvent(STATUS, "deviceId", "errorMessage"));

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testHandleUnknownSessionStreamingEvent() throws Exception {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();
        sessionDao.updateSessionStatus("deviceId", PLAYING);
        expectLastCall().andThrow(new UnknownSessionException("deviceId"));

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.handleStreamingEvent(new StreamingEvent(PLAY, "deviceId", 0l, 0l));

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testHandleUnknownStreamingEvent() throws Exception {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.handleStreamingEvent(new StreamingEvent(UNKNOWN, "deviceId", 0l, 0l));

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testHandlePlayStreamingEvent() throws Exception {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();
        sessionDao.updateSessionStatus("deviceId", PLAYING);
        expectLastCall();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.handleStreamingEvent(new StreamingEvent(PLAY, "deviceId", 0l, 0l));

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testHandleResumeStreamingEvent() throws Exception {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();
        sessionDao.updateSessionStatus("deviceId", PLAYING);
        expectLastCall();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.handleStreamingEvent(new StreamingEvent(RESUME, "deviceId", 0l, 0l));

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testHandleStopStreamingEvent() throws Exception {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();
        sessionDao.updateSessionStatus("deviceId", WAITING);
        expectLastCall();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.handleStreamingEvent(new StreamingEvent(STOP, "deviceId", 0l, 0l));

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testHandlePauseStreamingEvent() throws Exception {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();
        sessionDao.updateSessionStatus("deviceId", PAUSED);
        expectLastCall();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.handleStreamingEvent(new StreamingEvent(PAUSE, "deviceId", 0l, 0l));

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testHandleStatusStreamingEvent() throws Exception {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(configurationDao.getParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();
        sessionDao.updateSessionPosition("deviceId", 0l, 0l);
        expectLastCall();

        replay(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        StreamingManagerImpl streamingManager = new StreamingManagerImpl(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        streamingManager.handleStreamingEvent(new StreamingEvent(STATUS, "deviceId", 0l, 0l));

        verify(configurationDao, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    private class FakeDevice extends Device {
        /**
         * Instantiates a new device
         */
        public FakeDevice() {
            super("id", "name", null, null);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getType() {
            return "FakeDeviceType";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isVideoSupported() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isAudioSupported() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isImageSupported() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isSlideShowSupported() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void close() {
            // Nothing
        }
    }
}
