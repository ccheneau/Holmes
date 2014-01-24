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

package net.holmes.core.transport.airplay.device;

import org.junit.Test;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class AirplayDeviceTest {

    @Test
    public void testAirplayDeviceNoFeatures() {
        AirplayFeatures features = new AirplayFeatures("0x000000");
        AirplayDevice device = new AirplayDevice("id", "name", null, 8080, features);
        assertNotNull(device.toString());
        assertNotNull(device.getType());
        assertFalse(device.isVideoSupported());
        assertFalse(device.isAudioSupported());
        assertFalse(device.isImageSupported());
        assertFalse(device.isSlideShowSupported());
        device.close();
    }

    @Test
    public void testAirplayDeviceFullFeatures() {
        AirplayFeatures features = new AirplayFeatures("0xFFFFFF");
        AirplayDevice device = new AirplayDevice("id", "name", null, 8080, features);
        assertNotNull(device.toString());
        assertTrue(device.isVideoSupported());
        assertTrue(device.isAudioSupported());
        assertTrue(device.isImageSupported());
        assertTrue(device.isSlideShowSupported());
        device.close();
    }

    @Test
    public void testAirplayDeviceNullFeatures() {
        AirplayFeatures features = new AirplayFeatures(null);
        AirplayDevice device = new AirplayDevice("id", "name", null, 8080, features);
        assertNotNull(device.toString());
        assertFalse(device.isVideoSupported());
        assertFalse(device.isAudioSupported());
        assertFalse(device.isImageSupported());
        assertFalse(device.isSlideShowSupported());
        device.close();
    }

    @Test
    public void testAirplayDeviceShortFeatures() {
        AirplayFeatures features = new AirplayFeatures("0");
        AirplayDevice device = new AirplayDevice("id", "name", null, 8080, features);
        assertNotNull(device.toString());
        assertFalse(device.isVideoSupported());
        assertFalse(device.isAudioSupported());
        assertFalse(device.isImageSupported());
        assertFalse(device.isSlideShowSupported());
        device.close();
    }

    @Test
    public void testGetConnection() throws IOException {
        SocketFactory socketFactory = createMock(SocketFactory.class);
        Socket socket = createMock(Socket.class);

        expect(socketFactory.createSocket(isNull(InetAddress.class), anyInt())).andReturn(socket);

        replay(socketFactory, socket);

        AirplayFeatures features = new AirplayFeatures("0");
        AirplayDevice device = new AirplayDevice("id", "name", null, 8080, features);
        Socket aSocket = device.getConnection(socketFactory);
        assertNotNull(aSocket);

        verify(socketFactory, socket);
    }

    @Test
    public void testGetConnectionSocketOpen() throws IOException {
        SocketFactory socketFactory = createMock(SocketFactory.class);
        Socket socket = createMock(Socket.class);

        expect(socketFactory.createSocket(isNull(InetAddress.class), anyInt())).andReturn(socket);
        expect(socket.isClosed()).andReturn(false);

        replay(socketFactory, socket);

        AirplayFeatures features = new AirplayFeatures("0");
        AirplayDevice device = new AirplayDevice("id", "name", null, 8080, features);
        Socket aSocket1 = device.getConnection(socketFactory);
        Socket aSocket2 = device.getConnection(socketFactory);
        assertNotNull(aSocket1);
        assertNotNull(aSocket2);

        verify(socketFactory, socket);
    }

    @Test
    public void testGetConnectionSocketClosed() throws IOException {
        SocketFactory socketFactory = createMock(SocketFactory.class);
        Socket socket = createMock(Socket.class);

        expect(socketFactory.createSocket(isNull(InetAddress.class), anyInt())).andReturn(socket).atLeastOnce();
        expect(socket.isClosed()).andReturn(true);

        replay(socketFactory, socket);

        AirplayFeatures features = new AirplayFeatures("0");
        AirplayDevice device = new AirplayDevice("id", "name", null, 8080, features);
        Socket aSocket1 = device.getConnection(socketFactory);
        Socket aSocket2 = device.getConnection(socketFactory);
        assertNotNull(aSocket1);
        assertNotNull(aSocket2);

        verify(socketFactory, socket);
    }

    @Test
    public void testCloseDevice() throws IOException {
        SocketFactory socketFactory = createMock(SocketFactory.class);
        Socket socket = createMock(Socket.class);

        expect(socketFactory.createSocket(isNull(InetAddress.class), anyInt())).andReturn(socket);
        socket.close();
        expectLastCall();

        replay(socketFactory, socket);

        AirplayFeatures features = new AirplayFeatures("0");
        AirplayDevice device = new AirplayDevice("id", "name", null, 8080, features);
        Socket aSocket = device.getConnection(socketFactory);
        device.close();
        assertNotNull(aSocket);

        verify(socketFactory, socket);
    }

    @Test
    public void testCloseDeviceWithIOException() throws IOException {
        SocketFactory socketFactory = createMock(SocketFactory.class);
        Socket socket = createMock(Socket.class);

        expect(socketFactory.createSocket(isNull(InetAddress.class), anyInt())).andReturn(socket);
        socket.close();
        expectLastCall().andThrow(new IOException());

        replay(socketFactory, socket);

        AirplayFeatures features = new AirplayFeatures("0");
        AirplayDevice device = new AirplayDevice("id", "name", null, 8080, features);
        Socket aSocket = device.getConnection(socketFactory);
        device.close();
        assertNotNull(aSocket);

        verify(socketFactory, socket);
    }

}
