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

import com.google.common.eventbus.EventBus;
import net.holmes.core.transport.airplay.model.*;
import net.holmes.core.transport.device.DeviceStreamer;

import javax.inject.Inject;
import java.util.Map;

import static net.holmes.core.transport.event.StreamingEvent.StreamingEventType.*;

/**
 * Manage streaming on Airplay device.
 */
public class AirplayStreamerImpl extends DeviceStreamer<AirplayDevice> {
    private static final String CONTENT_PARAMETER_DURATION = "duration";
    private static final String CONTENT_PARAMETER_POSITION = "position";

    /**
     * Instantiates a new Airplay streamer implementation.
     *
     * @param eventBus event bus
     */
    @Inject
    public AirplayStreamerImpl(final EventBus eventBus) {
        super(eventBus);
    }

    @Override
    public void play(final AirplayDevice device, final String contentUrl) {
        new AsyncHttpClient(new PlayCommand(contentUrl, 0d)) {
            @Override
            public void success(Map<String, String> contentParameters) {
                sendSuccess(PLAY, device.getId());
            }

            @Override
            public void failure(Throwable throwable) {
                sendFailure(PLAY, device.getId(), throwable.getMessage());
            }
        }.run(device);
    }

    @Override
    public void stop(final AirplayDevice device) {
        new AsyncHttpClient(new StopCommand()) {
            @Override
            public void success(Map<String, String> contentParameters) {
                sendSuccess(STOP, device.getId());
            }

            @Override
            public void failure(Throwable throwable) {
                sendFailure(STOP, device.getId(), throwable.getMessage());
            }
        }.run(device);
    }

    @Override
    public void pause(final AirplayDevice device) {
        new AsyncHttpClient(new RateCommand(0d)) {
            @Override
            public void success(Map<String, String> contentParameters) {
                sendSuccess(PAUSE, device.getId());
            }

            @Override
            public void failure(Throwable throwable) {
                sendFailure(PAUSE, device.getId(), throwable.getMessage());
            }
        }.run(device);
    }

    @Override
    public void resume(final AirplayDevice device) {
        new AsyncHttpClient(new RateCommand(1d)) {
            @Override
            public void success(Map<String, String> contentParameters) {
                sendSuccess(RESUME, device.getId());
            }

            @Override
            public void failure(Throwable throwable) {
                sendFailure(RESUME, device.getId(), throwable.getMessage());
            }
        }.run(device);
    }

    @Override
    public void updateStatus(final AirplayDevice device) {
        new AsyncHttpClient(new PlayStatusCommand()) {
            @Override
            public void success(Map<String, String> contentParameters) {
                Long duration = getContentParameterValue(CONTENT_PARAMETER_DURATION, contentParameters);
                Long position = getContentParameterValue(CONTENT_PARAMETER_POSITION, contentParameters);
                sendSuccess(STATUS, device.getId(), duration, position);
            }

            @Override
            public void failure(Throwable throwable) {
                sendFailure(STATUS, device.getId(), throwable.getMessage());
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
    private Long getContentParameterValue(final String parameterName, final Map<String, String> contentParameters) {
        if (contentParameters != null && contentParameters.containsKey(parameterName))
            return Double.valueOf(contentParameters.get(parameterName)).longValue();
        else
            return 0l;
    }
}
