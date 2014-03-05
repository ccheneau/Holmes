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
import net.holmes.core.business.streaming.upnp.device.UpnpDevice;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.support.avtransport.callback.*;
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
        controlPoint.execute(new GetMediaInfo(device.getAvTransportService()) {
            /**
             * {@inheritDoc}
             */
            @Override
            public void received(ActionInvocation invocation, MediaInfo mediaInfo) {
                String currentUrl = mediaInfo.getCurrentURI();
                if (currentUrl == null)
                    // No url set on device, set Url and play
                    setUrlAndPlay(device, contentUrl, node, PLAY);
                else if (contentUrl.equals(currentUrl))
                    // Current Url already defined, play
                    play(device, PLAY);
                else
                    // Another Url is already set on device, get transport info
                    controlPoint.execute(new GetTransportInfo(device.getAvTransportService()) {
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public void received(ActionInvocation invocation, TransportInfo transportInfo) {
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

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                            sendFailure(PLAY, device.getId(), defaultMsg);
                        }
                    });
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                // Failed to get media info
                sendFailure(PLAY, device.getId(), defaultMsg);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(final UpnpDevice device) {
        // Stop content playback
        controlPoint.execute(new Stop(device.getAvTransportService()) {
            /**
             * {@inheritDoc}
             */
            @Override
            public void success(ActionInvocation invocation) {
                sendSuccess(STOP, device.getId());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                sendFailure(STOP, device.getId(), defaultMsg);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pause(final UpnpDevice device) {
        // Pause content playback
        controlPoint.execute(new Pause(device.getAvTransportService()) {
            /**
             * {@inheritDoc}
             */
            @Override
            public void success(ActionInvocation invocation) {
                sendSuccess(PAUSE, device.getId());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                sendFailure(PAUSE, device.getId(), defaultMsg);
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
        controlPoint.execute(new GetTransportInfo(device.getAvTransportService()) {
            /**
             * {@inheritDoc}
             */
            @Override
            public void received(ActionInvocation invocation, TransportInfo transportInfo) {
                switch (transportInfo.getCurrentTransportState()) {
                    case PLAYING:
                    case PAUSED_PLAYBACK:
                        // Content is currently playing on device, get position info
                        controlPoint.execute(new GetPositionInfo(device.getAvTransportService()) {
                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public void received(ActionInvocation invocation, PositionInfo positionInfo) {
                                sendSuccess(STATUS, device.getId(), positionInfo.getTrackDurationSeconds(), positionInfo.getTrackElapsedSeconds());
                            }

                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                                sendFailure(STATUS, device.getId(), defaultMsg);
                            }
                        });
                        break;
                    case TRANSITIONING:
                        // Content playback transition, do nothing
                        break;
                    default:
                        // No playback, send stop event
                        sendSuccess(STOP, device.getId());
                        break;
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                sendFailure(STATUS, device.getId(), defaultMsg);
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
        controlPoint.execute(new Play(device.getAvTransportService()) {
            /**
             * {@inheritDoc}
             */
            @Override
            public void success(ActionInvocation invocation) {
                sendSuccess(eventType, device.getId());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                sendFailure(eventType, device.getId(), defaultMsg);
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
        controlPoint.execute(new Stop(device.getAvTransportService()) {
            /**
             * {@inheritDoc}
             */
            @Override
            public void success(ActionInvocation invocation) {
                // Set Url and play
                setUrlAndPlay(device, contentUrl, node, eventType);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                sendFailure(eventType, device.getId(), defaultMsg);
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
            controlPoint.execute(new SetAVTransportURI(device.getAvTransportService(), contentUrl, getNodeMetadata(node, contentUrl)) {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void success(ActionInvocation invocation) {
                    // Play content
                    play(device, eventType);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                    sendFailure(eventType, device.getId(), defaultMsg);
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
     * @throws Exception
     */
    private String getNodeMetadata(final AbstractNode node, final String contentUrl) throws Exception {
        if (node instanceof ContentNode)
            return getContentNodeMetadata((ContentNode) node, contentUrl);

        return NOT_IMPLEMENTED;
    }

    /**
     * Get content node metadata.
     *
     * @param contentNode content node
     * @param contentUrl  content Url
     */
    private String getContentNodeMetadata(final ContentNode contentNode, final String contentUrl) throws Exception {
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
        if (item != null)
            return new DIDLParser().generate(new DIDLContent().addItem(item));
        else
            return NOT_IMPLEMENTED;
    }
}
