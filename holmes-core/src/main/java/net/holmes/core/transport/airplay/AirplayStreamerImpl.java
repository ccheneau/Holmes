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

import net.holmes.core.transport.airplay.model.*;
import net.holmes.core.transport.device.DeviceStreamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Manage streaming on Airplay device.
 */
public class AirplayStreamerImpl implements DeviceStreamer<AirplayDevice> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AirplayStreamerImpl.class);
    private static final String CONTENT_PARAMETER_DURATION = "duration";
    private static final String CONTENT_PARAMETER_POSITION = "position";

    @Override
    public void play(AirplayDevice device, String url) {
        new AsyncHttpClient(new PlayCommand(url, 0d)) {
            @Override
            public void onSuccess(Map<String, String> contentParameters) {

            }

            @Override
            public void onFailure(Throwable throwable) {
                logFailure(throwable);
            }
        }.run(device);
    }

    @Override
    public void stop(AirplayDevice device) {
        new AsyncHttpClient(new StopCommand()) {
            @Override
            public void onSuccess(Map<String, String> contentParameters) {

            }

            @Override
            public void onFailure(Throwable throwable) {
                logFailure(throwable);
            }
        }.run(device);
    }

    @Override
    public void pause(AirplayDevice device) {
        new AsyncHttpClient(new RateCommand(0d)) {
            @Override
            public void onSuccess(Map<String, String> contentParameters) {

            }

            @Override
            public void onFailure(Throwable throwable) {
                logFailure(throwable);
            }
        }.run(device);
    }

    @Override
    public void resume(AirplayDevice device) {
        new AsyncHttpClient(new RateCommand(1d)) {
            @Override
            public void onSuccess(Map<String, String> contentParameters) {

            }

            @Override
            public void onFailure(Throwable throwable) {
                logFailure(throwable);
            }
        }.run(device);
    }

    @Override
    public void updateStatus(AirplayDevice device) {
        new AsyncHttpClient(new PlayStatusCommand()) {
            @Override
            public void onSuccess(Map<String, String> contentParameters) {
                Double duration = getContentParameterValue(CONTENT_PARAMETER_DURATION, contentParameters);
                Double position = getContentParameterValue(CONTENT_PARAMETER_POSITION, contentParameters);

            }

            @Override
            public void onFailure(Throwable throwable) {
                logFailure(throwable);
            }
        }.run(device);
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

    /**
     * Log failure.
     *
     * @param exception exception
     */
    private void logFailure(Throwable exception) {
        LOGGER.error(exception.getMessage(), exception);
    }
}
