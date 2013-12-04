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

package net.holmes.core.transport.airplay;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import net.holmes.core.transport.airplay.model.AbstractCommand;
import net.holmes.core.transport.airplay.model.CommandResponse;
import net.holmes.core.transport.device.model.Device;
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
 * Airplay streaming manager implementation.
 */
public class AirplayStreamingManagerImpl implements AirplayStreamingManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AirplayStreamingManagerImpl.class);
    private static final String CONTENT_TYPE_PARAMETERS = "text/parameters";

    private final HttpClient httpClient;

    @Inject
    public AirplayStreamingManagerImpl(final HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public CommandResponse sendCommand(Device device, AbstractCommand command) throws IOException {
        // Get http request
        HttpRequestBase httpRequest = command.getHttpRequest(device.getHostAddress(), device.getPort());
        if (LOGGER.isDebugEnabled()) LOGGER.debug("sendCommand: {}", httpRequest);

        // Launch http request
        HttpResponse httpResponse = httpClient.execute(httpRequest);
        if (LOGGER.isDebugEnabled()) LOGGER.debug("command Response: {}", httpResponse);

        // Build command response
        CommandResponse response;
        Header contentType = httpResponse.getFirstHeader(CONTENT_TYPE);
        boolean hasContentParameters = contentType != null && contentType.getValue().equalsIgnoreCase(CONTENT_TYPE_PARAMETERS);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()))) {
            StringBuilder sbMessage = new StringBuilder();
            Map<String, String> contentParameters = Maps.newHashMap();
            String line;
            while ((line = reader.readLine()) != null)
                if (hasContentParameters) {
                    // Parse content parameter
                    Iterable<String> it = Splitter.on(':').trimResults().split(line);
                    contentParameters.put(Iterables.getFirst(it, ""), Iterables.getLast(it));
                } else
                    // Append to message
                    sbMessage.append(line).append('\n');

            response = new CommandResponse(httpResponse.getStatusLine().getStatusCode(), sbMessage.toString(), contentParameters);
        }
        return response;
    }
}
