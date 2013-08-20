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

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultChannelPromise;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import net.holmes.core.common.mimetype.MimeTypeManager;
import net.holmes.core.test.TestModule;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.io.File;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HttpUIRequestHandlerTest {

    private Injector injector;
    @Inject
    private MimeTypeManager mimeTypeManager;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    private HttpUIRequestHandler getHandler() {
        HttpUIRequestHandler httpUIRequestHandler = new HttpUIRequestHandler(mimeTypeManager, System.getProperty("java.io.tmpdir"));
        injector.injectMembers(httpUIRequestHandler);
        return httpUIRequestHandler;
    }

    @Test
    public void testAccept() {
        HttpUIRequestHandler httpUIRequestHandler = getHandler();
        assertTrue(httpUIRequestHandler.accept("", HttpMethod.GET));
        assertFalse(httpUIRequestHandler.accept("", HttpMethod.POST));
    }

    @Test
    public void testProcessRequest() throws Exception {
        File indexHtml = File.createTempFile("index", ".html");
        indexHtml.deleteOnExit();
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeaders.Names.HOST, "localhost");

        FullHttpRequest request = createMock(FullHttpRequest.class);
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        expect(request.headers()).andReturn(headers).atLeastOnce();
        expect(request.getUri()).andReturn("/" + indexHtml.getName());
        expect(request.getProtocolVersion()).andReturn(HttpVersion.HTTP_1_1).atLeastOnce();

        Channel channel = createMock(Channel.class);

        expect(context.write(isA(HttpResponse.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(context.write(isA(ChunkedFile.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(context.writeAndFlush(isA(LastHttpContent.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();

        replay(request, context, channel);
        HttpUIRequestHandler httpUIRequestHandler = getHandler();
        httpUIRequestHandler.processRequest(request, context);
        verify(request, context, channel);
    }

    @Test
    public void testProcessRequestWithoutKeepAlive() throws Exception {
        File indexHtml = File.createTempFile("index", ".html");
        indexHtml.deleteOnExit();
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeaders.Names.HOST, "localhost");
        headers.add(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);

        FullHttpRequest request = createMock(FullHttpRequest.class);
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        expect(request.headers()).andReturn(headers).atLeastOnce();
        expect(request.getUri()).andReturn("/" + indexHtml.getName());

        Channel channel = createMock(Channel.class);

        expect(context.write(isA(HttpResponse.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(context.write(isA(ChunkedFile.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(context.writeAndFlush(isA(LastHttpContent.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();

        replay(request, context, channel);
        HttpUIRequestHandler httpUIRequestHandler = getHandler();
        httpUIRequestHandler.processRequest(request, context);
        verify(request, context, channel);
    }

    @Test
    public void testProcessRequestBaMimeTyped() throws Exception {
        File indexHtml = File.createTempFile("index", ".html1");
        indexHtml.deleteOnExit();
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeaders.Names.HOST, "localhost");

        FullHttpRequest request = createMock(FullHttpRequest.class);
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        expect(request.headers()).andReturn(headers).atLeastOnce();
        expect(request.getUri()).andReturn("/" + indexHtml.getName());
        expect(request.getProtocolVersion()).andReturn(HttpVersion.HTTP_1_1).atLeastOnce();

        Channel channel = createMock(Channel.class);

        expect(context.write(isA(HttpResponse.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(context.write(isA(ChunkedFile.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(context.writeAndFlush(isA(LastHttpContent.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();

        replay(request, context, channel);
        HttpUIRequestHandler httpUIRequestHandler = getHandler();
        httpUIRequestHandler.processRequest(request, context);
        verify(request, context, channel);
    }

    @Test(expected = HttpRequestException.class)
    public void testProcessRequestNonExistingIndex() throws Exception {
        FullHttpRequest request = createMock(FullHttpRequest.class);
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);

        expect(request.getUri()).andReturn("/");

        replay(request, context);
        try {
            HttpUIRequestHandler httpUIRequestHandler = getHandler();
            httpUIRequestHandler.processRequest(request, context);
        } finally {
            verify(request, context);
        }
    }

    @Test(expected = HttpRequestException.class)
    public void testProcessRequestEmptyFile() throws Exception {
        FullHttpRequest request = createMock(FullHttpRequest.class);
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);

        expect(request.getUri()).andReturn("");

        replay(request, context);
        try {
            HttpUIRequestHandler httpUIRequestHandler = getHandler();
            httpUIRequestHandler.processRequest(request, context);
        } finally {
            verify(request, context);
        }
    }

    @Test(expected = HttpRequestException.class)
    public void testProcessRequestNonExistingFile() throws Exception {
        FullHttpRequest request = createMock(FullHttpRequest.class);
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);

        expect(request.getUri()).andReturn("/badFile");

        replay(request, context);
        try {
            HttpUIRequestHandler httpUIRequestHandler = getHandler();
            httpUIRequestHandler.processRequest(request, context);
        } finally {
            verify(request, context);
        }
    }

}
