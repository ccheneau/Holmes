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

package net.holmes.core.common;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.support.connectionmanager.ConnectionManagerService;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UpnpUtilsTest {

    @Test
    public void testTestPrivateConstructor() throws Exception {
        Constructor<UpnpUtils> cnt = UpnpUtils.class.getDeclaredConstructor();
        cnt.setAccessible(true);
        cnt.newInstance();
    }

    @Test
    public void testGetUpnpService() {
        UpnpService upnpService = UpnpUtils.buildUpnpService(5002);
        assertNotNull(upnpService);
    }

    @Test
    public void testGetUpnpMimeType() {
        org.seamless.util.MimeType mimeType = UpnpUtils.getUpnpMimeType(MimeType.valueOf("image/png"));
        assertNotNull(mimeType);
    }

    @Test
    public void testGetConnectionManagerService() {
        LocalService<ConnectionManagerService> connectionManagerService = UpnpUtils.buildConnectionManagerService();
        assertNotNull(connectionManagerService);
    }

    @Test
    public void testGetDeviceDetails() {
        DeviceDetails deviceDetails = UpnpUtils.buildDeviceDetails("serverName", "1.0");
        assertNotNull(deviceDetails);
    }

    @Test
    public void testGetIcons() throws IOException {
        Icon[] icons = UpnpUtils.getIcons();
        assertNotNull(icons);
    }

    @Test
    public void testGetDeviceNameNoDetails() {
        RemoteDevice device = createMock(RemoteDevice.class);

        expect(device.getDetails()).andReturn(null);
        expect(device.getDisplayString()).andReturn("Device Name");

        replay(device);
        String deviceName = UpnpUtils.getDeviceName(device);
        assertEquals("Device Name", deviceName);
        verify(device);
    }

    @Test
    public void testGetDeviceNameWithDetails() {
        RemoteDevice device = createMock(RemoteDevice.class);
        DeviceDetails details = createMock(DeviceDetails.class);

        expect(device.getDetails()).andReturn(details).atLeastOnce();
        expect(details.getFriendlyName()).andReturn("Device Friendly Name");

        replay(device, details);
        String deviceName = UpnpUtils.getDeviceName(device);
        assertEquals("Device Friendly Name", deviceName);
        verify(device, details);
    }

    @Test
    public void testGetDeviceId() {
        RemoteDevice device = createMock(RemoteDevice.class);
        RemoteDeviceIdentity deviceIdentity = createMock(RemoteDeviceIdentity.class);
        UDN udn = createMock(UDN.class);

        expect(device.getIdentity()).andReturn(deviceIdentity);
        expect(deviceIdentity.getUdn()).andReturn(udn);
        expect(udn.getIdentifierString()).andReturn("UDN identifier string");

        replay(device, deviceIdentity, udn);
        String deviceId = UpnpUtils.getDeviceId(device);
        assertEquals("UDN identifier string", deviceId);
        verify(device, deviceIdentity, udn);
    }
}
