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

package net.holmes.core.airplay.command;

import org.junit.Test;

import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;

public class AirplayCommandManagerImplTest {

    @Test
    public void testAddDevice() throws UnknownHostException {
        AirplayCommandManagerImpl airplayCommandManager = new AirplayCommandManagerImpl(null);
        airplayCommandManager.addDevice(new AirplayDevice("name", "127.0.0.1", 8080));
        assertEquals(airplayCommandManager.getDevices().size(), 1);
    }

    @Test
    public void testAddNullDevice() throws UnknownHostException {
        AirplayCommandManagerImpl airplayCommandManager = new AirplayCommandManagerImpl(null);
        airplayCommandManager.addDevice(null);
        assertEquals(airplayCommandManager.getDevices().size(), 0);
    }

    @Test
    public void testAddDeviceTwice() throws UnknownHostException {
        AirplayCommandManagerImpl airplayCommandManager = new AirplayCommandManagerImpl(null);
        airplayCommandManager.addDevice(new AirplayDevice("name", "127.0.0.1", 8080));
        airplayCommandManager.addDevice(new AirplayDevice("name", "127.0.0.1", 8080));
        assertEquals(airplayCommandManager.getDevices().size(), 1);
    }

    @Test
    public void testRemoveDevice() throws UnknownHostException {
        AirplayCommandManagerImpl airplayCommandManager = new AirplayCommandManagerImpl(null);
        airplayCommandManager.addDevice(new AirplayDevice("name", "127.0.0.1", 8080));
        airplayCommandManager.removeDevice(new AirplayDevice("name", "127.0.0.1", 8080));
        assertEquals(airplayCommandManager.getDevices().size(), 0);
    }

    @Test
    public void testRemoveNullDevice() throws UnknownHostException {
        AirplayCommandManagerImpl airplayCommandManager = new AirplayCommandManagerImpl(null);
        airplayCommandManager.addDevice(new AirplayDevice("name", "127.0.0.1", 8080));
        airplayCommandManager.removeDevice(null);
        assertEquals(airplayCommandManager.getDevices().size(), 1);
    }
}
