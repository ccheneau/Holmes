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

package net.holmes.core.manager.streaming.airplay;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import net.holmes.core.manager.media.model.AbstractNode;
import net.holmes.core.manager.streaming.airplay.command.Command;
import net.holmes.core.manager.streaming.airplay.controlpoint.ControlPoint;
import net.holmes.core.manager.streaming.airplay.device.AirplayDevice;
import net.holmes.core.manager.streaming.event.StreamingEvent;
import org.easymock.Capture;
import org.junit.Test;

import java.util.Map;

import static net.holmes.core.manager.streaming.event.StreamingEvent.StreamingEventType.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class AirplayStreamerImplTest {

    @Test
    public void testPlaySuccess() {
        EventBus eventBus = createMock(EventBus.class);
        ControlPoint controlPoint = new ControlPoint() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void execute(AirplayDevice device, Command command) {
                assertNotNull(command.getRequest());
                command.success(Maps.<String, String>newHashMap());
            }
        };
        AirplayDevice device = createMock(AirplayDevice.class);
        AbstractNode node = createMock(AbstractNode.class);
        Capture<StreamingEvent> captureStreamingEvent = new Capture<>();
        eventBus.post(capture(captureStreamingEvent));
        expectLastCall().atLeastOnce();

        expect(device.getId()).andReturn("id");

        replay(eventBus, device, node);
        AirplayStreamerImpl streamer = new AirplayStreamerImpl(eventBus, controlPoint);
        streamer.play(device, "ContentUrl", node);
        assertEquals(PLAY, captureStreamingEvent.getValue().getType());
        assertEquals("id", captureStreamingEvent.getValue().getDeviceId());
        assertTrue(captureStreamingEvent.getValue().isSuccess());
        verify(eventBus, device, node);
    }

    @Test
    public void testPlayFailure() {
        EventBus eventBus = createMock(EventBus.class);
        ControlPoint controlPoint = new ControlPoint() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void execute(AirplayDevice device, Command command) {
                assertNotNull(command.getRequest());
                command.failure("errorMessage");
            }
        };
        AirplayDevice device = createMock(AirplayDevice.class);
        AbstractNode node = createMock(AbstractNode.class);
        Capture<StreamingEvent> captureStreamingEvent = new Capture<>();
        eventBus.post(capture(captureStreamingEvent));
        expectLastCall().atLeastOnce();

        expect(device.getId()).andReturn("id");

        replay(eventBus, device, node);
        AirplayStreamerImpl streamer = new AirplayStreamerImpl(eventBus, controlPoint);
        streamer.play(device, "ContentUrl", node);
        assertEquals(PLAY, captureStreamingEvent.getValue().getType());
        assertEquals("id", captureStreamingEvent.getValue().getDeviceId());
        assertFalse(captureStreamingEvent.getValue().isSuccess());
        assertEquals("errorMessage", captureStreamingEvent.getValue().getErrorMessage());
        verify(eventBus, device, node);
    }

    @Test
    public void testStopSuccess() {
        EventBus eventBus = createMock(EventBus.class);
        ControlPoint controlPoint = new ControlPoint() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void execute(AirplayDevice device, Command command) {
                assertNotNull(command.getRequest());
                command.success(Maps.<String, String>newHashMap());
            }
        };
        AirplayDevice device = createMock(AirplayDevice.class);
        Capture<StreamingEvent> captureStreamingEvent = new Capture<>();
        eventBus.post(capture(captureStreamingEvent));
        expectLastCall().atLeastOnce();

        expect(device.getId()).andReturn("id");

        replay(eventBus, device);
        AirplayStreamerImpl streamer = new AirplayStreamerImpl(eventBus, controlPoint);
        streamer.stop(device);
        assertEquals(STOP, captureStreamingEvent.getValue().getType());
        assertEquals("id", captureStreamingEvent.getValue().getDeviceId());
        assertTrue(captureStreamingEvent.getValue().isSuccess());
        verify(eventBus, device);
    }

    @Test
    public void testStopFailure() {
        EventBus eventBus = createMock(EventBus.class);
        ControlPoint controlPoint = new ControlPoint() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void execute(AirplayDevice device, Command command) {
                assertNotNull(command.getRequest());
                command.failure("errorMessage");
            }
        };
        AirplayDevice device = createMock(AirplayDevice.class);
        Capture<StreamingEvent> captureStreamingEvent = new Capture<>();
        eventBus.post(capture(captureStreamingEvent));
        expectLastCall().atLeastOnce();

        expect(device.getId()).andReturn("id");

        replay(eventBus, device);
        AirplayStreamerImpl streamer = new AirplayStreamerImpl(eventBus, controlPoint);
        streamer.stop(device);
        assertEquals(STOP, captureStreamingEvent.getValue().getType());
        assertEquals("id", captureStreamingEvent.getValue().getDeviceId());
        assertFalse(captureStreamingEvent.getValue().isSuccess());
        assertEquals("errorMessage", captureStreamingEvent.getValue().getErrorMessage());
        verify(eventBus, device);
    }

    @Test
    public void testPauseSuccess() {
        EventBus eventBus = createMock(EventBus.class);
        ControlPoint controlPoint = new ControlPoint() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void execute(AirplayDevice device, Command command) {
                assertNotNull(command.getRequest());
                command.success(Maps.<String, String>newHashMap());
            }
        };
        AirplayDevice device = createMock(AirplayDevice.class);
        Capture<StreamingEvent> captureStreamingEvent = new Capture<>();
        eventBus.post(capture(captureStreamingEvent));
        expectLastCall().atLeastOnce();

        expect(device.getId()).andReturn("id");

        replay(eventBus, device);
        AirplayStreamerImpl streamer = new AirplayStreamerImpl(eventBus, controlPoint);
        streamer.pause(device);
        assertEquals(PAUSE, captureStreamingEvent.getValue().getType());
        assertEquals("id", captureStreamingEvent.getValue().getDeviceId());
        assertTrue(captureStreamingEvent.getValue().isSuccess());
        verify(eventBus, device);
    }

    @Test
    public void testPauseFailure() {
        EventBus eventBus = createMock(EventBus.class);
        ControlPoint controlPoint = new ControlPoint() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void execute(AirplayDevice device, Command command) {
                assertNotNull(command.getRequest());
                command.failure("errorMessage");
            }
        };
        AirplayDevice device = createMock(AirplayDevice.class);
        Capture<StreamingEvent> captureStreamingEvent = new Capture<>();
        eventBus.post(capture(captureStreamingEvent));
        expectLastCall().atLeastOnce();

        expect(device.getId()).andReturn("id");

        replay(eventBus, device);
        AirplayStreamerImpl streamer = new AirplayStreamerImpl(eventBus, controlPoint);
        streamer.pause(device);
        assertEquals(PAUSE, captureStreamingEvent.getValue().getType());
        assertEquals("id", captureStreamingEvent.getValue().getDeviceId());
        assertFalse(captureStreamingEvent.getValue().isSuccess());
        assertEquals("errorMessage", captureStreamingEvent.getValue().getErrorMessage());
        verify(eventBus, device);
    }

    @Test
    public void testResumeSuccess() {
        EventBus eventBus = createMock(EventBus.class);
        ControlPoint controlPoint = new ControlPoint() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void execute(AirplayDevice device, Command command) {
                assertNotNull(command.getRequest());
                command.success(Maps.<String, String>newHashMap());
            }
        };
        AirplayDevice device = createMock(AirplayDevice.class);
        Capture<StreamingEvent> captureStreamingEvent = new Capture<>();
        eventBus.post(capture(captureStreamingEvent));
        expectLastCall().atLeastOnce();

        expect(device.getId()).andReturn("id");

        replay(eventBus, device);
        AirplayStreamerImpl streamer = new AirplayStreamerImpl(eventBus, controlPoint);
        streamer.resume(device);
        assertEquals(RESUME, captureStreamingEvent.getValue().getType());
        assertEquals("id", captureStreamingEvent.getValue().getDeviceId());
        assertTrue(captureStreamingEvent.getValue().isSuccess());
        verify(eventBus, device);
    }

    @Test
    public void testResumeFailure() {
        EventBus eventBus = createMock(EventBus.class);
        ControlPoint controlPoint = new ControlPoint() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void execute(AirplayDevice device, Command command) {
                assertNotNull(command.getRequest());
                command.failure("errorMessage");
            }
        };
        AirplayDevice device = createMock(AirplayDevice.class);
        Capture<StreamingEvent> captureStreamingEvent = new Capture<>();
        eventBus.post(capture(captureStreamingEvent));
        expectLastCall().atLeastOnce();

        expect(device.getId()).andReturn("id");

        replay(eventBus, device);
        AirplayStreamerImpl streamer = new AirplayStreamerImpl(eventBus, controlPoint);
        streamer.resume(device);
        assertEquals(RESUME, captureStreamingEvent.getValue().getType());
        assertEquals("id", captureStreamingEvent.getValue().getDeviceId());
        assertFalse(captureStreamingEvent.getValue().isSuccess());
        assertEquals("errorMessage", captureStreamingEvent.getValue().getErrorMessage());
        verify(eventBus, device);
    }

    @Test
    public void testUpdateStatusSuccessEmptyParameters() {
        EventBus eventBus = createMock(EventBus.class);
        ControlPoint controlPoint = new ControlPoint() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void execute(AirplayDevice device, Command command) {
                assertNotNull(command.getRequest());
                command.success(Maps.<String, String>newHashMap());
            }
        };
        AirplayDevice device = createMock(AirplayDevice.class);

        replay(eventBus, device);
        AirplayStreamerImpl streamer = new AirplayStreamerImpl(eventBus, controlPoint);
        streamer.updateStatus(device);
        verify(eventBus, device);
    }

    @Test
    public void testUpdateStatusSuccessNullParameters() {
        EventBus eventBus = createMock(EventBus.class);
        ControlPoint controlPoint = new ControlPoint() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void execute(AirplayDevice device, Command command) {
                assertNotNull(command.getRequest());
                command.success(null);
            }
        };
        AirplayDevice device = createMock(AirplayDevice.class);

        replay(eventBus, device);
        AirplayStreamerImpl streamer = new AirplayStreamerImpl(eventBus, controlPoint);
        streamer.updateStatus(device);
        verify(eventBus, device);
    }

    @Test
    public void testUpdateStatusSuccessWithParameters() {
        EventBus eventBus = createMock(EventBus.class);
        ControlPoint controlPoint = new ControlPoint() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void execute(AirplayDevice device, Command command) {
                assertNotNull(command.getRequest());
                Map<String, String> parameters = Maps.newHashMap();
                parameters.put("duration", "60");
                parameters.put("position", "3");
                command.success(parameters);
            }
        };
        AirplayDevice device = createMock(AirplayDevice.class);
        Capture<StreamingEvent> captureStreamingEvent = new Capture<>();
        eventBus.post(capture(captureStreamingEvent));
        expectLastCall().atLeastOnce();

        expect(device.getId()).andReturn("id");

        replay(eventBus, device);
        AirplayStreamerImpl streamer = new AirplayStreamerImpl(eventBus, controlPoint);
        streamer.updateStatus(device);
        assertEquals(STATUS, captureStreamingEvent.getValue().getType());
        assertEquals("id", captureStreamingEvent.getValue().getDeviceId());
        assertTrue(captureStreamingEvent.getValue().isSuccess());
        assertEquals(60l, captureStreamingEvent.getValue().getDuration().longValue());
        assertEquals(3l, captureStreamingEvent.getValue().getPosition().longValue());
        verify(eventBus, device);
    }

    @Test
    public void testUpdateStatusSuccessWithStopParameters() {
        EventBus eventBus = createMock(EventBus.class);
        ControlPoint controlPoint = new ControlPoint() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void execute(AirplayDevice device, Command command) {
                assertNotNull(command.getRequest());
                Map<String, String> parameters = Maps.newHashMap();
                parameters.put("duration", "60");
                parameters.put("position", "70");
                command.success(parameters);
            }
        };
        AirplayDevice device = createMock(AirplayDevice.class);
        Capture<StreamingEvent> captureStreamingEvent = new Capture<>();
        eventBus.post(capture(captureStreamingEvent));
        expectLastCall().atLeastOnce();

        expect(device.getId()).andReturn("id").atLeastOnce();

        replay(eventBus, device);
        AirplayStreamerImpl streamer = new AirplayStreamerImpl(eventBus, controlPoint);
        streamer.updateStatus(device);
        assertEquals(STOP, captureStreamingEvent.getValue().getType());
        assertEquals("id", captureStreamingEvent.getValue().getDeviceId());
        assertTrue(captureStreamingEvent.getValue().isSuccess());
        verify(eventBus, device);
    }

    @Test
    public void testUpdateStatusSuccessWithBadParameters() {
        EventBus eventBus = createMock(EventBus.class);
        ControlPoint controlPoint = new ControlPoint() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void execute(AirplayDevice device, Command command) {
                assertNotNull(command.getRequest());
                Map<String, String> parameters = Maps.newHashMap();
                parameters.put("bad_duration", "60");
                parameters.put("bad_position", "70");
                command.success(parameters);
            }
        };
        AirplayDevice device = createMock(AirplayDevice.class);
        Capture<StreamingEvent> captureStreamingEvent = new Capture<>();
        eventBus.post(capture(captureStreamingEvent));
        expectLastCall().atLeastOnce();

        expect(device.getId()).andReturn("id").atLeastOnce();

        replay(eventBus, device);
        AirplayStreamerImpl streamer = new AirplayStreamerImpl(eventBus, controlPoint);
        streamer.updateStatus(device);
        assertEquals(STATUS, captureStreamingEvent.getValue().getType());
        assertEquals("id", captureStreamingEvent.getValue().getDeviceId());
        assertTrue(captureStreamingEvent.getValue().isSuccess());
        verify(eventBus, device);
    }

    @Test
    public void testUpdateStatusFailure() {
        EventBus eventBus = createMock(EventBus.class);
        ControlPoint controlPoint = new ControlPoint() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void execute(AirplayDevice device, Command command) {
                assertNotNull(command.getRequest());
                command.failure("errorMessage");
            }
        };
        AirplayDevice device = createMock(AirplayDevice.class);
        Capture<StreamingEvent> captureStreamingEvent = new Capture<>();
        eventBus.post(capture(captureStreamingEvent));
        expectLastCall().atLeastOnce();

        expect(device.getId()).andReturn("id");

        replay(eventBus, device);
        AirplayStreamerImpl streamer = new AirplayStreamerImpl(eventBus, controlPoint);
        streamer.updateStatus(device);
        assertEquals(STATUS, captureStreamingEvent.getValue().getType());
        assertEquals("id", captureStreamingEvent.getValue().getDeviceId());
        assertFalse(captureStreamingEvent.getValue().isSuccess());
        assertEquals("errorMessage", captureStreamingEvent.getValue().getErrorMessage());
        verify(eventBus, device);
    }
}
