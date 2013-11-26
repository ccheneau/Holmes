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

import com.google.common.collect.Maps;
import net.holmes.core.airplay.command.model.AbstractCommand;
import net.holmes.core.airplay.command.model.CommandResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;

/**
 * Airplay command manager implementation.
 */
public class AirplayCommandManagerImpl implements AirplayCommandManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AirplayCommandManagerImpl.class);
    private Map<Integer, AirplayDevice> devices = Maps.newHashMap();
    private Map<Integer, Socket> sockets = Maps.newHashMap();

    @Override
    public void addDevice(AirplayDevice device) {
        if (device != null && !devices.containsKey(device.hashCode())) {
            LOGGER.info("Add Airplay device {}", device.toString());
            devices.put(device.hashCode(), device);
        }
    }

    @Override
    public void removeDevice(AirplayDevice device) {
        if (device != null && devices.containsKey(device.hashCode())) {
            // Remove device
            LOGGER.info("Remove Airplay device {}", device.toString());
            devices.remove(device.hashCode());

            // Close device socket
            if (sockets.get(device.hashCode()) != null)
                try {
                    sockets.get(device.hashCode()).close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
        }
    }

    @Override
    public Map<Integer, AirplayDevice> getDevices() {
        return devices;
    }

    @Override
    public CommandResponse sendCommand(Integer deviceId, AbstractCommand command) {
        // Get device
        AirplayDevice device = devices.get(deviceId);
        if (device != null) {
            // Get device socket
            Socket socket = sockets.get(deviceId);
            if (socket == null || socket.isClosed()) {
                try {
                    socket = new Socket(device.getInetAddress(), device.getPort());
                    sockets.put(deviceId, socket);
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            if (socket != null && !socket.isClosed()) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

                    // Send command
                    if (LOGGER.isDebugEnabled()) LOGGER.debug("Send command: {}", command.getCommand());
                    out.write(command.getCommand() + "\n");
                    out.flush();

                    // Get response
                    StringBuilder fullResponse = new StringBuilder();
                    String partialResponse;
                    while (!(partialResponse = in.readLine().trim()).equals(""))
                        fullResponse.append(partialResponse).append("\n");
                    if (LOGGER.isDebugEnabled()) LOGGER.debug("Response: {}", fullResponse.toString());

                    // Get content length
                    int contentLength = 0;
                    if (fullResponse.indexOf(CONTENT_LENGTH) != -1) {
                        int start = fullResponse.indexOf(CONTENT_LENGTH);
                        int end = fullResponse.indexOf("\n", start + CONTENT_LENGTH.length() + 1);
                        contentLength = Integer.parseInt(fullResponse.substring(start + CONTENT_LENGTH.length() + 1, end).trim());
                    }
                    if (LOGGER.isDebugEnabled()) LOGGER.debug("Content length: {}", contentLength);

                    // Get content
                    StringBuilder content = null;
                    if (contentLength > 0) {
                        content = new StringBuilder(contentLength);
                        char buffer[] = new char[1024];
                        int read, totalRead = 0;
                        do {
                            read = in.read(buffer);
                            totalRead += read;
                            content.append(buffer, 0, read);
                        } while (read != -1 && totalRead < contentLength);
                    }
                    if (LOGGER.isDebugEnabled()) LOGGER.debug("Content: {}", content);

                    // Return response
                    return new CommandResponse(fullResponse.toString(), content == null ? null : content.toString());
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        } else
            LOGGER.error("Device not found: {}", deviceId);
        return null;
    }
}
