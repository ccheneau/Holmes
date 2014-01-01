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

package net.holmes.core.transport.airplay;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.holmes.core.transport.airplay.command.Command;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * Airplay control point: execute command on device
 */
public class ControlPoint {
    private static final String CONTENT_TYPE_PARAMETERS = "text/parameters";
    private static final String EOL = "\n";
    private static final String SPACE = " ";
    private static final char PARAMETER_SEPARATOR = ':';
    private static final int EXECUTOR_POOL_SIZE = 4;

    private final ExecutorService executor;

    /**
     * Instantiates a new Airplay control point.
     */
    public ControlPoint() {
        executor = Executors.newFixedThreadPool(EXECUTOR_POOL_SIZE);
    }

    /**
     * Execute command on device;
     *
     * @param device  device
     * @param command command to run
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

    /**
     * Decode device response.
     *
     * @param responseLines response lines
     * @return device response
     */
    private DeviceResponse decodeResponse(final List<String> responseLines) {
        // Decode http response on first line
        Iterable<String> responseIt = Splitter.on(SPACE).split(responseLines.get(0));
        int code = Integer.valueOf(Iterables.get(responseIt, 1));
        String message = Iterables.get(responseIt, 2);

        // Decode http headers on next lines
        Map<String, String> headers = Maps.newHashMap();
        for (int i = 1; i < responseLines.size(); i++) {
            Iterable<String> it = Splitter.on(PARAMETER_SEPARATOR).trimResults().split(responseLines.get(i));
            headers.put(Iterables.get(it, 0), Iterables.getLast(it));
        }
        return new DeviceResponse(code, message, headers);
    }

    /**
     * Decode content parameters.
     *
     * @param content content
     * @return content parameters map
     */
    private Map<String, String> decodeContentParameters(String content) {
        Map<String, String> parametersMap = Maps.newHashMap();
        for (String line : Splitter.on(EOL).split(content)) {
            Iterable<String> it = Splitter.on(PARAMETER_SEPARATOR).trimResults().split(line);
            parametersMap.put(Iterables.get(it, 0), Iterables.getLast(it));
        }
        return parametersMap;
    }

    /**
     * Device response
     */
    private class DeviceResponse {
        private final int code;
        private final String message;
        private final Map<String, String> headers;

        /**
         * Instantiates a new device response.
         *
         * @param code    http response code
         * @param message message
         * @param headers http headers
         */
        private DeviceResponse(int code, String message, Map<String, String> headers) {
            this.code = code;
            this.message = message;
            this.headers = headers;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        /**
         * Get content length.
         *
         * @return content length
         */
        public int getContentLength() {
            return headers.get(CONTENT_LENGTH) != null ? Integer.valueOf(headers.get(CONTENT_LENGTH)) : 0;
        }

        /**
         * Get content type.
         *
         * @return content type
         */
        public String getContentType() {
            return headers.get(CONTENT_TYPE);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .addValue(code)
                    .addValue(headers)
                    .toString();
        }
    }
}
