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

package net.holmes.core.transport.upnp;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import net.holmes.core.media.model.AbstractNode;
import net.holmes.core.media.model.ContentNode;
import net.holmes.core.transport.device.DeviceStreamer;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.support.avtransport.callback.*;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.model.item.Movie;
import org.fourthline.cling.support.model.item.MusicTrack;
import org.fourthline.cling.support.model.item.Photo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.holmes.core.common.upnp.UpnpUtils.getUpnpMimeType;
import static net.holmes.core.transport.event.StreamingEvent.StreamingEventType;
import static net.holmes.core.transport.event.StreamingEvent.StreamingEventType.*;

/**
 * Manage streaming on Upnp device.
 */
public class UpnpStreamerImpl extends DeviceStreamer<UpnpDevice> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpnpStreamerImpl.class);
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

    @Override
    public void play(final UpnpDevice device, final String contentUrl, final AbstractNode node) {
        controlPoint.execute(new GetMediaInfo(device.getAvTransportService()) {

            @Override
            public void received(ActionInvocation invocation, MediaInfo mediaInfo) {
                String currentUrl = mediaInfo.getCurrentURI();
                if (currentUrl == null)
                    // No url set on device, set Url and play
                    setUrlAndPlay(device, contentUrl, node);
                else if (contentUrl.equals(currentUrl))
                    // Current url already defined, play
                    play(device, PLAY);
                else
                    // Another url is already set on device, stop then set Url and play
                    controlPoint.execute(new Stop(device.getAvTransportService()) {
                        @Override
                        public void success(ActionInvocation invocation) {
                            setUrlAndPlay(device, contentUrl, node);
                        }

                        @Override
                        public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                            sendFailure(PLAY, device.getId(), defaultMsg);
                        }
                    });
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                sendFailure(PLAY, device.getId(), defaultMsg);
            }
        });
    }

    @Override
    public void stop(final UpnpDevice device) {
        controlPoint.execute(new Stop(device.getAvTransportService()) {
            @Override
            public void success(ActionInvocation invocation) {
                sendSuccess(STOP, device.getId());
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                sendFailure(STOP, device.getId(), defaultMsg);
            }
        });
    }

    @Override
    public void pause(final UpnpDevice device) {
        controlPoint.execute(new Pause(device.getAvTransportService()) {
            @Override
            public void success(ActionInvocation invocation) {
                sendSuccess(PAUSE, device.getId());
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                sendFailure(PAUSE, device.getId(), defaultMsg);
            }
        });
    }

    @Override
    public void resume(final UpnpDevice device) {
        // Resume content playback
        play(device, RESUME);
    }

    @Override
    public void updateStatus(final UpnpDevice device) {
        controlPoint.execute(new GetPositionInfo(device.getAvTransportService()) {
            @Override
            public void received(ActionInvocation invocation, PositionInfo positionInfo) {
                sendSuccess(STATUS, device.getId(), positionInfo.getTrackElapsedSeconds(), positionInfo.getTrackDurationSeconds());
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                sendFailure(STATUS, device.getId(), defaultMsg);
            }
        });
    }

    private void play(final UpnpDevice device, final StreamingEventType eventType) {
        controlPoint.execute(new Play(device.getAvTransportService()) {
            @Override
            public void success(ActionInvocation invocation) {
                sendSuccess(eventType, device.getId());
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                sendFailure(eventType, device.getId(), defaultMsg);
            }
        });
    }

    private void setUrlAndPlay(final UpnpDevice device, final String contentUrl, final AbstractNode node) {
        try {
            // Set content Url
            controlPoint.execute(new SetAVTransportURI(device.getAvTransportService(), contentUrl, getNodeMetadata(node, contentUrl)) {
                @Override
                public void success(ActionInvocation invocation) {
                    // Play content
                    play(device, PLAY);
                }

                @Override
                public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                    sendFailure(PLAY, device.getId(), defaultMsg);
                }

            });
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            sendFailure(PLAY, device.getId(), e.getMessage());
        }
    }

    /**
     * get Node metadata
     *
     * @param node       node
     * @param contentUrl content url
     * @return DIDL metadata or "NOT_IMPLEMENTED"
     * @throws Exception
     */
    private String getNodeMetadata(final AbstractNode node, final String contentUrl) throws Exception {
        if (node instanceof ContentNode) return getContentNodeMetadata((ContentNode) node, contentUrl);
        return NOT_IMPLEMENTED;
    }

    /**
     * Build content node metadata.
     *
     * @param contentNode content node
     * @param contentUrl  content url
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
