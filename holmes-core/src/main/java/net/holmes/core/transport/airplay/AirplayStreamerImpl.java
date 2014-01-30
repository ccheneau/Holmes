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

import com.google.common.eventbus.EventBus;
import net.holmes.core.media.model.AbstractNode;
import net.holmes.core.transport.airplay.command.PlayCommand;
import net.holmes.core.transport.airplay.command.PlayStatusCommand;
import net.holmes.core.transport.airplay.command.RateCommand;
import net.holmes.core.transport.airplay.command.StopCommand;
import net.holmes.core.transport.airplay.controlpoint.ControlPoint;
import net.holmes.core.transport.airplay.device.AirplayDevice;
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
    private final ControlPoint controlPoint;

    /**
     * Instantiates a new Airplay streamer implementation.
     *
     * @param eventBus event bus
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
        controlPoint.execute(device, new PlayCommand(contentUrl, 0d) {
            /**
             * {@inheritDoc}
             */
            @Override
            public void success(Map<String, String> contentParameters) {
                sendSuccess(PLAY, device.getId());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void failure(String errorMessage) {
                sendFailure(PLAY, device.getId(), errorMessage);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(final AirplayDevice device) {
        controlPoint.execute(device, new StopCommand() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void success(Map<String, String> contentParameters) {
                sendSuccess(STOP, device.getId());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void failure(String errorMessage) {
                sendFailure(STOP, device.getId(), errorMessage);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pause(final AirplayDevice device) {
        controlPoint.execute(device, new RateCommand(0d) {
            /**
             * {@inheritDoc}
             */
            @Override
            public void success(Map<String, String> contentParameters) {
                sendSuccess(PAUSE, device.getId());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void failure(String errorMessage) {
                sendFailure(PAUSE, device.getId(), errorMessage);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resume(final AirplayDevice device) {
        controlPoint.execute(device, new RateCommand(1d) {
            /**
             * {@inheritDoc}
             */
            @Override
            public void success(Map<String, String> contentParameters) {
                sendSuccess(RESUME, device.getId());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void failure(String errorMessage) {
                sendFailure(RESUME, device.getId(), errorMessage);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateStatus(final AirplayDevice device) {
        controlPoint.execute(device, new PlayStatusCommand() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void success(Map<String, String> contentParameters) {
                if (contentParameters != null && !contentParameters.isEmpty()) {
                    Long duration = getContentParameterValue(CONTENT_PARAMETER_DURATION, contentParameters);
                    Long position = getContentParameterValue(CONTENT_PARAMETER_POSITION, contentParameters);
                    sendSuccess(STATUS, device.getId(), duration, position);

                    // If end of streaming is reached, send stop command
                    if (duration > 0 && position >= duration) stop(device);
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void failure(String errorMessage) {
                sendFailure(STATUS, device.getId(), errorMessage);
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
        return contentParameters.containsKey(parameterName) ? Double.valueOf(contentParameters.get(parameterName)).longValue() : 0l;
    }
}
