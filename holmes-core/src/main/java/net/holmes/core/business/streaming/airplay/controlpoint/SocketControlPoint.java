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

import com.google.common.collect.Lists;
import net.holmes.core.business.streaming.airplay.command.Command;
import net.holmes.core.business.streaming.airplay.device.AirplayDevice;

import javax.net.SocketFactory;
import java.io.*;
import java.net.Socket;
import java.util.List;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Airplay socket control point
 */
public class SocketControlPoint implements ControlPoint {
    private static final String CONTENT_TYPE_PARAMETERS = "text/parameters";
    private final SocketFactory socketFactory;

    /**
     * Instantiates a new socket control point.
     *
     * @param socketFactory socket factory
     */
    public SocketControlPoint(final SocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final AirplayDevice device, final Command command) {
        runDeviceCommand(device, command);
    }

    /**
     * Run command on device.
     *
     * @param device  Airplay device
     * @param command command
     */
    protected void runDeviceCommand(final AirplayDevice device, final Command command) {
        try {
            // Get device socket
            Socket socket = device.getConnection(socketFactory);

            // Send command
            sendCommand(socket, command);

            // Read command response
            CommandResponse response = readCommandResponse(socket);
            if (response.getCode() == OK.code())
                command.success(response.getContentParameters());
            else
                command.failure(response.getMessage());

        } catch (IOException e) {
            command.failure(e.getMessage());
        }
    }

    /**
     * Send command.
     *
     * @param socket  socket
     * @param command command
     * @throws IOException
     */
    protected void sendCommand(final Socket socket, final Command command) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8));

        // Write request to socket
        out.write(command.getRequest());
        out.flush();
    }

    /**
     * Read command response.
     *
     * @param socket socket
     * @return command response
     * @throws IOException
     */
    protected CommandResponse readCommandResponse(final Socket socket) throws IOException {
        CommandResponse response = new CommandResponse();
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));

        // Read Http response
        List<String> httpResponseLines = Lists.newArrayList();
        String line = in.readLine();
        while (line != null && line.trim().length() != 0) {
            httpResponseLines.add(line);
            line = in.readLine();
        }

        // Decode command http response
        response.decodeHttpResponse(httpResponseLines);

        // Get content parameters
        int contentLength = response.getContentLength();
        if (contentLength > 0) {
            // Read response content
            StringBuilder sbContent = new StringBuilder(contentLength);
            char[] buffer = new char[1024];
            int read;
            int totalRead = 0;
            while (totalRead < contentLength && (read = in.read(buffer)) != -1) {
                totalRead += read;
                sbContent.append(buffer, 0, read);
            }

            // Decode content parameters
            if (CONTENT_TYPE_PARAMETERS.equals(response.getContentType()))
                response.decodeContentParameters(sbContent.toString());
        }
        return response;
    }
}
