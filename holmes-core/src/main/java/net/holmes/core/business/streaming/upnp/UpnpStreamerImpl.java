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

package net.holmes.core.business.streaming.upnp;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import net.holmes.core.business.media.model.AbstractNode;
import net.holmes.core.business.media.model.ContentNode;
import net.holmes.core.business.streaming.device.DeviceStreamer;
import net.holmes.core.business.streaming.upnp.command.*;
import net.holmes.core.business.streaming.upnp.device.UpnpDevice;
import net.holmes.core.common.exception.HolmesException;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.*;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.model.item.Movie;
import org.fourthline.cling.support.model.item.MusicTrack;
import org.fourthline.cling.support.model.item.Photo;
import org.slf4j.Logger;

import static net.holmes.core.business.streaming.event.StreamingEvent.StreamingEventType;
import static net.holmes.core.business.streaming.event.StreamingEvent.StreamingEventType.*;
import static net.holmes.core.common.UpnpUtils.getUpnpMimeType;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Manage streaming on Upnp device.
 */
public final class UpnpStreamerImpl extends DeviceStreamer<UpnpDevice> {
    private static final Logger LOGGER = getLogger(UpnpStreamerImpl.class);
    private static final String NOT_IMPLEMENTED = "NOT_IMPLEMENTED";
    private final ControlPoint controlPoint;

    /**
     * Instantiates a new Upnp streaming implementation.
     *
     * @param upnpService Upnp service
     * @param eventBus    event bus
     */
    @Inject
    public UpnpStreamerImpl(final UpnpService upnpService, final EventBus eventBus) {
        super(eventBus);
        this.controlPoint = upnpService.getControlPoint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void play(final UpnpDevice device, final String contentUrl, final AbstractNode node) {
        // Get media info
        controlPoint.execute(new GetMediaInfoCommand(device, newCommandFailureHandler(PLAY, device)) {
            @Override
            public void received(MediaInfo mediaInfo) {
                String currentUrl = mediaInfo.getCurrentURI();
                if (currentUrl == null) {
                    // No url set on device, set Url and play
                    setUrlAndPlay(device, contentUrl, node, PLAY);
                } else if (currentUrl.equals(contentUrl)) {
                    // Current Url already set, play
                    play(device, PLAY);
                } else {
                    // Another Url is already set
                    getInfoSetUrlAndPlay(device, contentUrl, node);
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(final UpnpDevice device) {
        // Stop content playback
        controlPoint.execute(new StopCommand(device, newCommandFailureHandler(STOP, device)) {
            @Override
            public void success() {
                sendSuccess(STOP, device.getId());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pause(final UpnpDevice device) {
        // Pause content playback
        controlPoint.execute(new PauseCommand(device, newCommandFailureHandler(PAUSE, device)) {
            @Override
            public void success() {
                sendSuccess(PAUSE, device.getId());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resume(final UpnpDevice device) {
        // Resume content playback
        play(device, RESUME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateStatus(final UpnpDevice device) {
        // Get transport info
        controlPoint.execute(new GetTransportInfoCommand(device, newCommandFailureHandler(STATUS, device)) {
            @Override
            public void received(TransportInfo transportInfo) {
                switch (transportInfo.getCurrentTransportState()) {
                    case PLAYING:
                    case PAUSED_PLAYBACK:
                        updatePlayPosition(device);
                        break;
                    case TRANSITIONING:
                        // Content playback transition, do nothing
                        break;
                    default:
                        // No playback, send stop event
                        sendSuccess(STOP, device.getId());
                }
            }
        });
    }

    /**
     * Play content on device.
     *
     * @param device    device
     * @param eventType event type
     */
    private void play(final UpnpDevice device, final StreamingEventType eventType) {
        controlPoint.execute(new PlayCommand(device, newCommandFailureHandler(eventType, device)) {
            @Override
            public void success() {
                sendSuccess(eventType, device.getId());
            }
        });
    }

    /**
     * Update play position on device
     *
     * @param device device
     */
    private void updatePlayPosition(final UpnpDevice device) {
        // Content is currently playing on device, get position info
        controlPoint.execute(new GetPositionInfoCommand(device, newCommandFailureHandler(STATUS, device)) {
            @Override
            public void received(PositionInfo positionInfo) {
                sendSuccess(STATUS, device.getId(), positionInfo.getTrackDurationSeconds(), positionInfo.getTrackElapsedSeconds());
            }
        });
    }

    /**
     * Get transport info on device, set content URL and play content
     *
     * @param device     device
     * @param contentUrl content url
     * @param node       node
     */
    private void getInfoSetUrlAndPlay(final UpnpDevice device, final String contentUrl, final AbstractNode node) {
        // Another Url is already set on device, get transport info
        controlPoint.execute(new GetTransportInfoCommand(device, newCommandFailureHandler(PLAY, device)) {
            @Override
            public void received(TransportInfo transportInfo) {
                switch (transportInfo.getCurrentTransportState()) {
                    case PLAYING:
                    case PAUSED_PLAYBACK:
                        // Another content is currently playing on device, stop then set Url and play
                        stopSetUrlAndPlay(device, contentUrl, node, PLAY);
                        break;
                    default:
                        // No playback, set Url and play
                        setUrlAndPlay(device, contentUrl, node, PLAY);
                        break;
                }
            }
        });
    }

    /**
     * Stop content playback, set content Url and play content on device
     *
     * @param device     device
     * @param contentUrl content Url
     * @param node       node
     * @param eventType  event type
     */
    private void stopSetUrlAndPlay(final UpnpDevice device, final String contentUrl, final AbstractNode node, final StreamingEventType eventType) {
        // Stop content playback
        controlPoint.execute(new StopCommand(device, newCommandFailureHandler(eventType, device)) {
            @Override
            public void success() {
                // Set Url and play
                setUrlAndPlay(device, contentUrl, node, eventType);
            }
        });
    }

    /**
     * Set content Url and play content on device.
     *
     * @param device     device
     * @param contentUrl content url
     * @param node       node
     * @param eventType  event type
     */
    private void setUrlAndPlay(final UpnpDevice device, final String contentUrl, final AbstractNode node, final StreamingEventType eventType) {
        try {
            // Set content Url
            controlPoint.execute(new SetContentUrlCommand(device, contentUrl, getNodeMetadata(node, contentUrl), newCommandFailureHandler(eventType, device)) {
                @Override
                public void success() {
                    // Play content
                    play(device, eventType);
                }
            });
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            sendFailure(PLAY, device.getId(), e.getMessage());
        }
    }

    /**
     * get node metadata
     *
     * @param node       node
     * @param contentUrl content Url
     * @return DIDL metadata or "NOT_IMPLEMENTED"
     * @throws HolmesException
     */
    private String getNodeMetadata(final AbstractNode node, final String contentUrl) throws HolmesException {
        try {
            return node instanceof ContentNode ? getContentNodeMetadata((ContentNode) node, contentUrl) : NOT_IMPLEMENTED;
        } catch (Exception e) {
            throw new HolmesException(e);
        }
    }

    /**
     * Get content node metadata.
     *
     * @param contentNode content node
     * @param contentUrl  content Url
     * @throws HolmesException
     */
    private String getContentNodeMetadata(final ContentNode contentNode, final String contentUrl) throws HolmesException {
        Res res = new Res(getUpnpMimeType(contentNode.getMimeType()), contentNode.getSize(), contentUrl);
        Item item = null;
        switch (contentNode.getMimeType().getType()) {
            case TYPE_VIDEO:
                // Add video item
                item = new Movie(contentNode.getId(), contentNode.getParentId(), contentNode.getName(), null, res);
                break;
            case TYPE_AUDIO:
                // Add audio track item
                item = new MusicTrack(contentNode.getId(), contentNode.getParentId(), contentNode.getName(), null, null, (String) null, res);
                break;
            case TYPE_IMAGE:
                // Add image item
                item = new Photo(contentNode.getId(), contentNode.getParentId(), contentNode.getName(), null, null, res);
                break;
            default:
                break;
        }
        try {
            return item != null ? new DIDLParser().generate(new DIDLContent().addItem(item)) : NOT_IMPLEMENTED;
        } catch (Exception e) {
            throw new HolmesException(e);
        }
    }
}
