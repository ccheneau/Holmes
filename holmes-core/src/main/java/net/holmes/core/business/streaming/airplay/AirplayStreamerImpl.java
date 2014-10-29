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

package net.holmes.core.business.streaming.airplay;

import com.google.common.eventbus.EventBus;
import net.holmes.core.business.media.model.AbstractNode;
import net.holmes.core.business.streaming.airplay.command.PlayCommand;
import net.holmes.core.business.streaming.airplay.command.PlayStatusCommand;
import net.holmes.core.business.streaming.airplay.command.RateCommand;
import net.holmes.core.business.streaming.airplay.command.StopCommand;
import net.holmes.core.business.streaming.airplay.controlpoint.ControlPoint;
import net.holmes.core.business.streaming.airplay.device.AirplayDevice;
import net.holmes.core.business.streaming.device.DeviceStreamer;

import javax.inject.Inject;
import java.util.Map;

import static net.holmes.core.business.streaming.event.StreamingEvent.StreamingEventType.*;

/**
 * Manage streaming on Airplay device.
 */
public class AirplayStreamerImpl extends DeviceStreamer<AirplayDevice> {
    private static final String CONTENT_PARAMETER_DURATION = "duration";
    private static final String CONTENT_PARAMETER_POSITION = "position";
    private final ControlPoint controlPoint;

    /**
     * Instantiates a new Airplay streamer implementation.
     *
     * @param eventBus     event bus
     * @param controlPoint control point
     */
    @Inject
    public AirplayStreamerImpl(final EventBus eventBus, final ControlPoint controlPoint) {
        super(eventBus);
        this.controlPoint = controlPoint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void play(final AirplayDevice device, final String contentUrl, final AbstractNode node) {
        controlPoint.execute(device, new PlayCommand(contentUrl, 0d, newCommandFailureHandler(PLAY, device)) {
            @Override
            public void success(Map<String, String> contentParameters) {
                sendSuccess(PLAY, device.getId());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(final AirplayDevice device) {
        controlPoint.execute(device, new StopCommand(newCommandFailureHandler(STOP, device)) {
            @Override
            public void success(Map<String, String> contentParameters) {
                sendSuccess(STOP, device.getId());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pause(final AirplayDevice device) {
        controlPoint.execute(device, new RateCommand(0d, newCommandFailureHandler(PAUSE, device)) {
            @Override
            public void success(Map<String, String> contentParameters) {
                sendSuccess(PAUSE, device.getId());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resume(final AirplayDevice device) {
        controlPoint.execute(device, new RateCommand(1d, newCommandFailureHandler(RESUME, device)) {
            @Override
            public void success(Map<String, String> contentParameters) {
                sendSuccess(RESUME, device.getId());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateStatus(final AirplayDevice device) {
        controlPoint.execute(device, new PlayStatusCommand(newCommandFailureHandler(STATUS, device)) {
            @Override
            public void success(Map<String, String> contentParameters) {
                if (contentParameters != null && !contentParameters.isEmpty()) {
                    Long duration = getContentParameterValue(CONTENT_PARAMETER_DURATION, contentParameters);
                    Long position = getContentParameterValue(CONTENT_PARAMETER_POSITION, contentParameters);
                    sendSuccess(STATUS, device.getId(), duration, position);

                    if (duration > 0 && position >= duration) {
                        // End of streaming is reached, send stop command
                        stop(device);
                    }
                }
            }
        });
    }

    /**
     * Get content parameter value.
     *
     * @param parameterName     content parameter name
     * @param contentParameters content parameters
     * @return content parameter value
     */
    private Long getContentParameterValue(final String parameterName, final Map<String, String> contentParameters) {
        return contentParameters.containsKey(parameterName) ? Double.valueOf(contentParameters.get(parameterName)).longValue() : 0L;
    }
}
