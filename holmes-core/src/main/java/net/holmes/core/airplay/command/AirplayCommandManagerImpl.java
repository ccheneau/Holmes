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

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import net.holmes.core.airplay.command.exception.UnknownDeviceException;
import net.holmes.core.airplay.command.model.AbstractCommand;
import net.holmes.core.airplay.command.model.CommandResponse;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;

/**
 * Airplay command manager implementation.
 */
public class AirplayCommandManagerImpl implements AirplayCommandManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AirplayCommandManagerImpl.class);
    private static final String CONTENT_TYPE_PARAMETERS = "text/parameters";
    private final HttpClient httpClient;
    private final Map<Integer, AirplayDevice> devices;

    /**
     * Instantiates a new Airplay command manager implementation.
     *
     * @param httpClient Http client
     */
    @Inject
    public AirplayCommandManagerImpl(final HttpClient httpClient) {
        this.httpClient = httpClient;
        this.devices = Maps.newHashMap();
    }

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
        }
    }

    @Override
    public Map<Integer, AirplayDevice> getDevices() {
        return devices;
    }

    @Override
    public CommandResponse sendCommand(Integer deviceId, AbstractCommand command) throws IOException, UnknownDeviceException {
        // Get device
        AirplayDevice device = devices.get(deviceId);
        if (device == null) throw new UnknownDeviceException(deviceId);

        // Get http request
        HttpRequestBase httpRequest = command.getHttpRequest(device.getInetAddress().getHostAddress(), device.getPort());
        if (LOGGER.isDebugEnabled()) LOGGER.debug("sendCommand: {}", httpRequest);

        // Launch http request
        HttpResponse httpResponse = httpClient.execute(httpRequest);
        if (LOGGER.isDebugEnabled()) LOGGER.debug("command Response: {}", httpResponse);

        // Build command response
        CommandResponse response;
        Header contentType = httpResponse.getFirstHeader(CONTENT_TYPE);
        boolean hasContentParameters = contentType != null && contentType.getValue().equalsIgnoreCase(CONTENT_TYPE_PARAMETERS);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()))) {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            StringBuilder sbMessage = new StringBuilder();
            Map<String, String> contentParameters = Maps.newHashMap();
            String line;
            while ((line = reader.readLine()) != null) {
                if (hasContentParameters) {
                    // Parse content parameter
                    Iterable<String> it = Splitter.on(':').trimResults().split(line);
                    contentParameters.put(Iterables.getFirst(it, ""), Iterables.getLast(it));
                } else
                    // Append to message
                    sbMessage.append(line).append('\n');
            }
            response = new CommandResponse(statusCode, sbMessage.toString(), contentParameters);
        }
        return response;
    }
}
