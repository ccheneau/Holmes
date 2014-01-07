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

package net.holmes.core.transport.airplay.controlpoint;

import com.google.common.collect.Lists;
import net.holmes.core.transport.airplay.command.Command;
import net.holmes.core.transport.airplay.device.AirplayDevice;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * Asynchronous Airplay control point
 */
public class AsyncControlPoint extends ControlPoint {
    private static final int EXECUTOR_POOL_SIZE = 4;
    private final ExecutorService executor;

    /**
     * Instantiates a new asynchronous Airplay control point
     */
    public AsyncControlPoint() {
        executor = Executors.newFixedThreadPool(EXECUTOR_POOL_SIZE);
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final AirplayDevice device, final Command command) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Get device socket
                    Socket socket = device.getConnection();

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                    // Write request to socket
                    out.write(command.getRequest());
                    out.flush();

                    // Read response
                    List<String> responseLines = Lists.newArrayList();
                    String line;
                    while ((line = in.readLine().trim()).length() != 0)
                        responseLines.add(line);

                    // Decode http response
                    DeviceResponse response = decodeResponse(responseLines);

                    Map<String, String> contentParameters = null;
                    int contentLength = response.getContentLength();
                    if (contentLength > 0) {
                        // Read response content
                        StringBuilder sbContent = new StringBuilder(contentLength);
                        char buffer[] = new char[1024];
                        int read;
                        int totalRead = 0;
                        while (totalRead < contentLength && (read = in.read(buffer)) != -1) {
                            totalRead += read;
                            sbContent.append(buffer, 0, read);
                        }
                        if (CONTENT_TYPE_PARAMETERS.equals(response.getContentType()))
                            // Decode content parameters
                            contentParameters = decodeContentParameters(sbContent.toString());
                    }

                    if (response.getCode() == OK.code())
                        command.success(contentParameters);
                    else
                        command.failure(response.getMessage());

                } catch (IOException e) {
                    command.failure(e.getMessage());
                }
            }
        });
    }
}
