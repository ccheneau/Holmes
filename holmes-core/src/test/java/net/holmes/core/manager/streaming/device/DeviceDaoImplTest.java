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

package net.holmes.core.manager.streaming.device;

import org.junit.Test;

import java.net.InetAddress;
import java.util.Collection;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DeviceDaoImplTest {

    @Test
    public void testAddDevice() throws UnknownDeviceException {
        Device device = createMock(Device.class);

        expect(device.getId()).andReturn("deviceId");

        replay(device);

        DeviceDaoImpl deviceDao = new DeviceDaoImpl();
        deviceDao.addDevice(device);
        assertEquals(1, deviceDao.getDevices().size());
        assertNotNull(deviceDao.getDevice("deviceId"));

        verify(device);
    }

    @Test
    public void testRemoveDevice() throws UnknownDeviceException {
        Device device = createMock(Device.class);

        expect(device.getId()).andReturn("deviceId");
        device.close();
        expectLastCall();

        replay(device);

        DeviceDaoImpl deviceDao = new DeviceDaoImpl();
        deviceDao.addDevice(device);
        deviceDao.removeDevice("deviceId");
        assertEquals(0, deviceDao.getDevices().size());

        verify(device);
    }

    @Test
    public void testRemoveUnknownDevice() throws UnknownDeviceException {
        Device device = createMock(Device.class);

        replay(device);

        DeviceDaoImpl deviceDao = new DeviceDaoImpl();
        deviceDao.removeDevice("deviceId");
        assertEquals(0, deviceDao.getDevices().size());

        verify(device);
    }

    @Test(expected = UnknownDeviceException.class)
    public void testGetUnknownDevice() throws UnknownDeviceException {
        DeviceDaoImpl deviceDao = new DeviceDaoImpl();
        deviceDao.getDevice("deviceId");
    }

    @Test
    public void testFindDevice() {
        Device device = createMock(Device.class);
        InetAddress address = createMock(InetAddress.class);

        expect(device.getId()).andReturn("deviceId");
        expect(device.getAddress()).andReturn(address);
        expect(address.getHostAddress()).andReturn("localhost");

        replay(device, address);

        DeviceDaoImpl deviceDao = new DeviceDaoImpl();
        deviceDao.addDevice(device);
        Collection<Device> devices = deviceDao.findDevices("localhost");

        assertEquals(1, devices.size());

        verify(device, address);
    }
}


