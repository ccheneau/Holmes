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
import net.holmes.core.transport.airplay.model.*;
import net.holmes.core.transport.device.DeviceStreamer;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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
 * Manage streaming on Airplay device.
 */
public class AirplayStreamerImpl implements DeviceStreamer<AirplayDevice> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AirplayStreamerImpl.class);
    private static final String CONTENT_TYPE_PARAMETERS = "text/parameters";
    private static final String CONTENT_PARAMETER_DURATION = "duration";
    private static final String CONTENT_PARAMETER_POSITION = "position";

    private final HttpClient httpClient;

    /**
     * Instantiates a new Airplay streaming manager implementation.
     *
     * @param httpClient Http client
     */
    @Inject
    public AirplayStreamerImpl(final HttpClient httpClient) {
        this.httpClient = httpClient;
    }


    @Override
    public void play(AirplayDevice device, String url) {
        sendCommand(device, new PlayCommand(url, 0d));
    }

    @Override
    public void stop(AirplayDevice device) {
        sendCommand(device, new StopCommand());
    }

    @Override
    public void pause(AirplayDevice device) {
        sendCommand(device, new RateCommand(0d));
    }

    @Override
    public void resume(AirplayDevice device) {
        sendCommand(device, new RateCommand(1d));
    }

    @Override
    public void updateStatus(AirplayDevice device) {
        CommandResponse cmdResponse = sendCommand(device, new PlayStatusCommand());
        /*
        if (cmdResponse.getStatusCode() == SC_OK) {
            Double duration = getContentParameterValue(CONTENT_PARAMETER_DURATION, cmdResponse.getContentParameters());
            Double position = getContentParameterValue(CONTENT_PARAMETER_POSITION, cmdResponse.getContentParameters());
        }
            return new StreamingStatus(duration, position);
        } else
            return new StreamingStatus(0d, 0d);
            */
    }

    /**
     * Send Airplay command to device
     *
     * @param device  Airplay device
     * @param command Airplay command
     * @return command response
     */
    private CommandResponse sendCommand(AirplayDevice device, AbstractCommand command) {
        // Get http request
        HttpRequestBase httpRequest = command.getHttpRequest(device.getHostAddress(), device.getPort());
        if (LOGGER.isDebugEnabled()) LOGGER.debug("sendCommand: {}", httpRequest);

        CommandResponse response;
        try {
            // Launch http request
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            if (LOGGER.isDebugEnabled()) LOGGER.debug("command Response: {}", httpResponse);

            // Build command response
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
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            response = new CommandResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return response;
    }

    /**
     * Get content parameter value.
     *
     * @param parameterName     content parameter name
     * @param contentParameters content parameters
     * @return content parameter value
     */
    private Double getContentParameterValue(final String parameterName, final Map<String, String> contentParameters) {
        if (contentParameters != null && contentParameters.containsKey(parameterName))
            return Double.valueOf(contentParameters.get(parameterName));
        else
            return 0d;
    }
}
