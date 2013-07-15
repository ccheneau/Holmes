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

package net.holmes.core.http;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.DefaultChannelPromise;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.*;
import net.holmes.core.http.handler.HttpRequestException;
import net.holmes.core.http.handler.HttpRequestHandler;
import net.holmes.core.test.TestModule;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;

import static org.easymock.EasyMock.*;

public class HttpChannelHandlerTest {

    private HttpRequestHandler httpUIRequestHandler = createMock(HttpRequestHandler.class);
    private HttpRequestHandler httpBackendRequestHandler = createMock(HttpRequestHandler.class);
    private HttpRequestHandler httpContentRequestHandler = createMock(HttpRequestHandler.class);
    private ChannelHandlerContext channelHandlerContext = createMock(ChannelHandlerContext.class);
    private FullHttpRequest request = createMock(FullHttpRequest.class);
    private Channel channel = createMock(Channel.class);
    private Injector injector;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new TestModule());
    }

    private HttpChannelHandler getHandler() {
        HttpChannelHandler handler = new HttpChannelHandler(httpContentRequestHandler, httpBackendRequestHandler, httpUIRequestHandler);
        injector.injectMembers(handler);
        return handler;
    }

    @Test
    public void testContentChannelHandler() throws Exception {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeaders.Names.HOST, "localhost");

        expect(request.getUri()).andReturn("/content?id=2").atLeastOnce();
        expect(request.headers()).andReturn(headers).atLeastOnce();
        expect(request.getMethod()).andReturn(HttpMethod.GET).atLeastOnce();
        expect(channelHandlerContext.channel()).andReturn(channel).atLeastOnce();
        expect(httpContentRequestHandler.canProcess(isA(String.class), isA(HttpMethod.class))).andReturn(true).atLeastOnce();
        httpContentRequestHandler.processRequest(isA(FullHttpRequest.class), isA(Channel.class));
        expectLastCall().atLeastOnce();

        replay(request, channel, channelHandlerContext, httpContentRequestHandler);
        HttpChannelHandler handler = getHandler();
        handler.channelRead0(channelHandlerContext, request);
        verify(request, channel, channelHandlerContext, httpContentRequestHandler);
    }

    @Test
    public void testContentChannelHandlerThrowsException() throws Exception {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeaders.Names.HOST, "localhost");

        expect(request.getUri()).andReturn("/content?id=2").atLeastOnce();
        expect(request.headers()).andReturn(headers).atLeastOnce();
        expect(request.getMethod()).andReturn(HttpMethod.GET).atLeastOnce();
        expect(channelHandlerContext.channel()).andReturn(channel).atLeastOnce();
        expect(channel.write(isA(Object.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(httpContentRequestHandler.canProcess(isA(String.class), isA(HttpMethod.class))).andReturn(true).atLeastOnce();
        httpContentRequestHandler.processRequest(isA(FullHttpRequest.class), isA(Channel.class));
        expectLastCall().andThrow(new HttpRequestException("message", HttpResponseStatus.NOT_FOUND)).atLeastOnce();

        replay(request, channel, channelHandlerContext, httpContentRequestHandler);
        HttpChannelHandler handler = getHandler();
        handler.channelRead0(channelHandlerContext, request);
        verify(request, channel, channelHandlerContext, httpContentRequestHandler);
    }

    @Test
    public void testBackendChannelHandler() throws Exception {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeaders.Names.HOST, "localhost");

        expect(request.getUri()).andReturn("/backend/getVersion").atLeastOnce();
        expect(request.headers()).andReturn(headers).atLeastOnce();
        expect(request.getMethod()).andReturn(HttpMethod.POST).atLeastOnce();
        expect(request.content()).andReturn(Unpooled.copiedBuffer("Param1", Charset.defaultCharset())).atLeastOnce();
        expect(channelHandlerContext.channel()).andReturn(channel).atLeastOnce();
        expect(httpContentRequestHandler.canProcess(isA(String.class), isA(HttpMethod.class))).andReturn(false).atLeastOnce();
        expect(httpBackendRequestHandler.canProcess(isA(String.class), isA(HttpMethod.class))).andReturn(true).atLeastOnce();
        httpBackendRequestHandler.processRequest(isA(FullHttpRequest.class), isA(Channel.class));
        expectLastCall().atLeastOnce();

        replay(request, channel, channelHandlerContext, httpContentRequestHandler, httpBackendRequestHandler);
        HttpChannelHandler handler = getHandler();
        handler.channelRead0(channelHandlerContext, request);
        verify(request, channel, channelHandlerContext, httpContentRequestHandler, httpBackendRequestHandler);
    }

    @Test
    public void testUIChannelHandler() throws Exception {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeaders.Names.HOST, "localhost");

        expect(request.getUri()).andReturn("/index.html").atLeastOnce();
        expect(request.headers()).andReturn(headers).atLeastOnce();
        expect(request.getMethod()).andReturn(HttpMethod.GET).atLeastOnce();
        expect(channelHandlerContext.channel()).andReturn(channel).atLeastOnce();
        expect(httpContentRequestHandler.canProcess(isA(String.class), isA(HttpMethod.class))).andReturn(false).atLeastOnce();
        expect(httpBackendRequestHandler.canProcess(isA(String.class), isA(HttpMethod.class))).andReturn(false).atLeastOnce();
        expect(httpUIRequestHandler.canProcess(isA(String.class), isA(HttpMethod.class))).andReturn(true).atLeastOnce();
        httpUIRequestHandler.processRequest(isA(FullHttpRequest.class), isA(Channel.class));
        expectLastCall().atLeastOnce();

        replay(request, channel, channelHandlerContext, httpContentRequestHandler, httpBackendRequestHandler, httpUIRequestHandler);
        HttpChannelHandler handler = getHandler();
        handler.channelRead0(channelHandlerContext, request);
        verify(request, channel, channelHandlerContext, httpContentRequestHandler, httpBackendRequestHandler, httpUIRequestHandler);
    }

    @Test
    public void testBadChannelHandler() throws Exception {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeaders.Names.HOST, "localhost");

        expect(request.getUri()).andReturn("/index.html").atLeastOnce();
        expect(request.headers()).andReturn(headers).atLeastOnce();
        expect(request.getMethod()).andReturn(HttpMethod.GET).atLeastOnce();
        expect(channel.write(isA(Object.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(channelHandlerContext.channel()).andReturn(channel).atLeastOnce();
        expect(httpContentRequestHandler.canProcess(isA(String.class), isA(HttpMethod.class))).andReturn(false).atLeastOnce();
        expect(httpBackendRequestHandler.canProcess(isA(String.class), isA(HttpMethod.class))).andReturn(false).atLeastOnce();
        expect(httpUIRequestHandler.canProcess(isA(String.class), isA(HttpMethod.class))).andReturn(false).atLeastOnce();

        replay(request, channel, channelHandlerContext, httpContentRequestHandler, httpBackendRequestHandler, httpUIRequestHandler);
        HttpChannelHandler handler = getHandler();
        handler.channelRead0(channelHandlerContext, request);
        verify(request, channel, channelHandlerContext, httpContentRequestHandler, httpBackendRequestHandler, httpUIRequestHandler);
    }

    @Test
    public void testExceptionCaught() throws Exception {
        expect(channelHandlerContext.channel()).andReturn(channel).atLeastOnce();
        expect(channel.isActive()).andReturn(true).atLeastOnce();
        expect(channel.write(isA(Object.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();

        replay(channelHandlerContext, channel);
        ChannelInboundHandler handler = getHandler();
        try {
            handler.exceptionCaught(channelHandlerContext, new HttpRequestException("message", HttpResponseStatus.NOT_FOUND));
        } finally {
            verify(channelHandlerContext, channel);
        }
    }

    @Test
    public void testExceptionCaughtChannelInactive() throws Exception {
        expect(channelHandlerContext.channel()).andReturn(channel).atLeastOnce();
        expect(channel.isActive()).andReturn(false).atLeastOnce();

        replay(channelHandlerContext, channel);
        ChannelInboundHandler handler = getHandler();
        try {
            handler.exceptionCaught(channelHandlerContext, new HttpRequestException("message", HttpResponseStatus.NOT_FOUND));
        } finally {
            verify(channelHandlerContext, channel);
        }
    }

    @Test
    public void testExceptionCaughtChannelInactiveTooLongFrameException() throws Exception {
        expect(channelHandlerContext.channel()).andReturn(channel).atLeastOnce();
        expect(channel.write(isA(Object.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();

        replay(channelHandlerContext, channel);
        ChannelInboundHandler handler = getHandler();
        try {
            handler.exceptionCaught(channelHandlerContext, new TooLongFrameException("message"));
        } finally {
            verify(channelHandlerContext, channel);
        }
    }

}
