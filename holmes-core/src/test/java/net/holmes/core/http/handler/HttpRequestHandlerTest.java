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

package net.holmes.core.http.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultChannelPromise;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import org.junit.Test;

import java.io.IOException;

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static org.easymock.EasyMock.*;

public class HttpRequestHandlerTest {

    @Test
    public void testExceptionCaughtHttpRequestException() throws Exception {
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        Channel channel = createMock(Channel.class);

        expect(context.channel()).andReturn(channel).atLeastOnce();
        expect(channel.isActive()).andReturn(true).atLeastOnce();
        expect(channel.writeAndFlush(isA(Object.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        replay(context, channel);
        getHandler().exceptionCaught(context, new HttpRequestException("message", NOT_FOUND));
        verify(context, channel);
    }

    @Test
    public void testExceptionCaughtHttpRequestExceptionFromIoException() throws Exception {
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        Channel channel = createMock(Channel.class);

        expect(context.channel()).andReturn(channel).atLeastOnce();
        expect(channel.isActive()).andReturn(true).atLeastOnce();
        expect(channel.writeAndFlush(isA(Object.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        replay(context, channel);
        getHandler().exceptionCaught(context, new HttpRequestException(new IOException(), NOT_FOUND));
        verify(context, channel);
    }

    @Test
    public void testExceptionCaughtIoException() throws Exception {
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        Channel channel = createMock(Channel.class);

        expect(context.channel()).andReturn(channel).atLeastOnce();
        expect(channel.isActive()).andReturn(true).atLeastOnce();
        expect(channel.writeAndFlush(isA(Object.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        replay(context, channel);
        getHandler().exceptionCaught(context, new IOException());
        verify(context, channel);
    }

    @Test
    public void testExceptionCaughtHttpRequestChannelInactive() throws Exception {
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        Channel channel = createMock(Channel.class);

        expect(context.channel()).andReturn(channel).atLeastOnce();
        expect(channel.isActive()).andReturn(false).atLeastOnce();
        replay(context, channel);
        getHandler().exceptionCaught(context, new HttpRequestException("message", NOT_FOUND));
        verify(context, channel);
    }

    private HttpRequestHandler getHandler() {
        return new HttpRequestHandler() {
            @Override
            boolean accept(String requestPath, HttpMethod method) {
                return true;
            }

            @Override
            HttpRequestFile getRequestFile(FullHttpRequest request) throws HttpRequestException {
                return null;
            }
        };
    }
}
