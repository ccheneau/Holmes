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

package net.holmes.core.business.streaming.airplay.controlpoint;

import net.holmes.core.business.streaming.airplay.command.Command;
import net.holmes.core.business.streaming.airplay.device.AirplayDevice;
import org.easymock.Capture;
import org.junit.Test;

import javax.net.SocketFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class SocketControlPointTest {

    @Test
    public void testExecuteOKResponse() throws IOException {

        AirplayDevice device = createMock(AirplayDevice.class);
        Command command = createMock(Command.class);
        Socket socket = createMock(Socket.class);
        SocketFactory socketFactory = createMock(SocketFactory.class);
        OutputStream socketOutputStream = createMock(OutputStream.class);
        InputStream socketInputStream = new ByteArrayInputStream("HTTP/1.1 200 OK\n\n".getBytes());

        String request = "Request";

        Capture<byte[]> captureRequest = new Capture<>();
        Capture<Map<String, String>> captureContentParameters = new Capture<>();

        expect(device.getConnection(isA(SocketFactory.class))).andReturn(socket).atLeastOnce();
        expect(socket.getOutputStream()).andReturn(socketOutputStream).atLeastOnce();
        expect(socket.getInputStream()).andReturn(socketInputStream).atLeastOnce();
        expect(command.getRequest()).andReturn(request).atLeastOnce();
        socketOutputStream.write(capture(captureRequest), eq(0), eq(request.length()));
        expectLastCall().atLeastOnce();
        socketOutputStream.flush();
        expectLastCall().atLeastOnce();
        command.success(capture(captureContentParameters));
        expectLastCall().atLeastOnce();

        replay(device, command, socket, socketFactory, socketOutputStream);
        SocketControlPoint controlPoint = new SocketControlPoint(socketFactory);
        controlPoint.execute(device, command);

        assertEquals(request, new String(captureRequest.getValue(), 0, request.length()));
        assertEquals(0, captureContentParameters.getValue().size());

        verify(device, command, socket, socketFactory, socketOutputStream);
    }

    @Test
    public void testExecuteOKResponseWithParameters() throws IOException {

        AirplayDevice device = createMock(AirplayDevice.class);
        Command command = createMock(Command.class);
        Socket socket = createMock(Socket.class);
        SocketFactory socketFactory = createMock(SocketFactory.class);
        OutputStream socketOutputStream = createMock(OutputStream.class);

        String content = "duration: 83.124794\nposition: 14.467000\n";
        String httpHeader = "HTTP/1.1 200 OK\nContent-Type: text/parameters\nContent-Length: " + content.length() + "\n\n";
        String response = httpHeader + content;

        InputStream socketInputStream = new ByteArrayInputStream(response.getBytes());

        String request = "Request";

        Capture<byte[]> captureRequest = new Capture<>();
        Capture<Map<String, String>> captureContentParameters = new Capture<>();

        expect(device.getConnection(isA(SocketFactory.class))).andReturn(socket).atLeastOnce();
        expect(socket.getOutputStream()).andReturn(socketOutputStream).atLeastOnce();
        expect(socket.getInputStream()).andReturn(socketInputStream).atLeastOnce();
        expect(command.getRequest()).andReturn(request).atLeastOnce();
        socketOutputStream.write(capture(captureRequest), eq(0), eq(request.length()));
        expectLastCall().atLeastOnce();
        socketOutputStream.flush();
        expectLastCall().atLeastOnce();
        command.success(capture(captureContentParameters));
        expectLastCall().atLeastOnce();

        replay(device, command, socket, socketFactory, socketOutputStream);
        SocketControlPoint controlPoint = new SocketControlPoint(socketFactory);
        controlPoint.execute(device, command);

        assertEquals(request, new String(captureRequest.getValue(), 0, request.length()));
        assertEquals(2, captureContentParameters.getValue().size());

        verify(device, command, socket, socketFactory, socketOutputStream);
    }

    @Test
    public void testExecuteOKResponseWithNoParameters() throws IOException {

        AirplayDevice device = createMock(AirplayDevice.class);
        Command command = createMock(Command.class);
        Socket socket = createMock(Socket.class);
        SocketFactory socketFactory = createMock(SocketFactory.class);
        OutputStream socketOutputStream = createMock(OutputStream.class);

        String content = "some content\n";
        String httpHeader = "HTTP/1.1 200 OK\nContent-Type: text/something\nContent-Length: 20000\n\n";
        String response = httpHeader + content;

        InputStream socketInputStream = new ByteArrayInputStream(response.getBytes());

        String request = "Request";

        Capture<byte[]> captureRequest = new Capture<>();
        Capture<Map<String, String>> captureContentParameters = new Capture<>();

        expect(device.getConnection(isA(SocketFactory.class))).andReturn(socket).atLeastOnce();
        expect(socket.getOutputStream()).andReturn(socketOutputStream).atLeastOnce();
        expect(socket.getInputStream()).andReturn(socketInputStream).atLeastOnce();
        expect(command.getRequest()).andReturn(request).atLeastOnce();
        socketOutputStream.write(capture(captureRequest), eq(0), eq(request.length()));
        expectLastCall().atLeastOnce();
        socketOutputStream.flush();
        expectLastCall().atLeastOnce();
        command.success(capture(captureContentParameters));
        expectLastCall().atLeastOnce();

        replay(device, command, socket, socketFactory, socketOutputStream);
        SocketControlPoint controlPoint = new SocketControlPoint(socketFactory);
        controlPoint.execute(device, command);

        assertEquals(request, new String(captureRequest.getValue(), 0, request.length()));
        assertEquals(0, captureContentParameters.getValue().size());

        verify(device, command, socket, socketFactory, socketOutputStream);
    }

    @Test
    public void testExecuteKOResponse() throws IOException {

        AirplayDevice device = createMock(AirplayDevice.class);
        Command command = createMock(Command.class);
        Socket socket = createMock(Socket.class);
        SocketFactory socketFactory = createMock(SocketFactory.class);
        OutputStream socketOutputStream = createMock(OutputStream.class);
        InputStream socketInputStream = new ByteArrayInputStream("HTTP/1.1 404 NOT FOUND\n\n".getBytes());

        String request = "Request";

        Capture<byte[]> captureRequest = new Capture<>();
        Capture<String> captureMessage = new Capture<>();

        expect(device.getConnection(isA(SocketFactory.class))).andReturn(socket).atLeastOnce();
        expect(socket.getOutputStream()).andReturn(socketOutputStream).atLeastOnce();
        expect(socket.getInputStream()).andReturn(socketInputStream).atLeastOnce();
        expect(command.getRequest()).andReturn(request).atLeastOnce();
        socketOutputStream.write(capture(captureRequest), eq(0), eq(request.length()));
        expectLastCall().atLeastOnce();
        socketOutputStream.flush();
        expectLastCall().atLeastOnce();
        command.failure(capture(captureMessage));
        expectLastCall().atLeastOnce();

        replay(device, command, socket, socketFactory, socketOutputStream);
        SocketControlPoint controlPoint = new SocketControlPoint(socketFactory);
        controlPoint.execute(device, command);

        assertEquals(request, new String(captureRequest.getValue(), 0, request.length()));
        assertEquals("NOT FOUND", captureMessage.getValue());

        verify(device, command, socket, socketFactory, socketOutputStream);
    }

    @Test
    public void testExecuteIOException() throws IOException {

        AirplayDevice device = createMock(AirplayDevice.class);
        Command command = createMock(Command.class);
        Socket socket = createMock(Socket.class);
        SocketFactory socketFactory = createMock(SocketFactory.class);
        OutputStream socketOutputStream = createMock(OutputStream.class);

        Capture<IOException> captureMessage = new Capture<>();

        expect(device.getConnection(isA(SocketFactory.class))).andThrow(new IOException("IOException message"));
        command.failure(capture(captureMessage));
        expectLastCall();

        replay(device, command, socket, socketFactory, socketOutputStream);
        SocketControlPoint controlPoint = new SocketControlPoint(socketFactory);
        controlPoint.execute(device, command);

        assertEquals("IOException message", captureMessage.getValue().getMessage());

        verify(device, command, socket, socketFactory, socketOutputStream);
    }

}
