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
import net.holmes.core.media.MediaManager;
import net.holmes.core.media.model.AbstractNode;
import net.holmes.core.media.model.ContentNode;
import net.holmes.core.media.model.RootNode;
import net.holmes.core.test.TestModule;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static io.netty.handler.codec.http.HttpHeaders.Names.HOST;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class HttpContentRequestHandlerTest {

    @Inject
    private MediaManager mediaManager;
    private Injector injector;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    private HttpContentRequestHandler getHandler() {
        HttpContentRequestHandler contentRequestHandler = new HttpContentRequestHandler(mediaManager);
        injector.injectMembers(contentRequestHandler);
        return contentRequestHandler;
    }

    public AbstractNode getContentNodeFromMediaManager() {
        AbstractNode rootNode = mediaManager.getNode(RootNode.PICTURE.getId());
        assertNotNull(rootNode);
        List<AbstractNode> childNodes = mediaManager.getChildNodes(rootNode);
        assertNotNull(childNodes);
        assertNotNull(mediaManager.getNode(childNodes.get(0).getId()));

        List<AbstractNode> nodes = mediaManager.getChildNodes(childNodes.get(0));
        assertNotNull(nodes);
        for (AbstractNode node : nodes) {
            if (node instanceof ContentNode) return node;
        }
        fail();
        return null;
    }

    @Test
    public void testAccept() {
        HttpContentRequestHandler contentRequestHandler = getHandler();
        assertTrue(contentRequestHandler.accept(new DefaultFullHttpRequest(HTTP_1_1, GET, "/content/request?id=1")));
        assertFalse(contentRequestHandler.accept(new DefaultFullHttpRequest(HTTP_1_1, GET, "/bad_request?id=1")));
        assertFalse(contentRequestHandler.accept(new DefaultFullHttpRequest(HTTP_1_1, GET, "/content/request")));
    }

    @Test
    public void testProcessRequestNoContentId() throws Exception {
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        FullHttpRequest request = createMock(FullHttpRequest.class);

        expect(request.getUri()).andReturn("/content").atLeastOnce();
        expect(request.getMethod()).andReturn(GET).atLeastOnce();
        expect(context.fireChannelRead(request)).andReturn(context).atLeastOnce();
        replay(request, context);
        try {
            HttpContentRequestHandler contentRequestHandler = getHandler();
            contentRequestHandler.channelRead0(context, request);
        } finally {
            verify(request, context);
        }
    }

    @Test
    public void testProcessRequestPost() throws Exception {
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        FullHttpRequest request = createMock(FullHttpRequest.class);

        expect(request.getMethod()).andReturn(POST).atLeastOnce();
        expect(context.fireChannelRead(request)).andReturn(context).atLeastOnce();
        replay(request, context);
        try {
            HttpContentRequestHandler contentRequestHandler = getHandler();
            contentRequestHandler.channelRead0(context, request);
        } finally {
            verify(request, context);
        }
    }

    @Test(expected = HttpRequestException.class)
    public void testProcessRequestNullContentId() throws Exception {
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        FullHttpRequest request = createMock(FullHttpRequest.class);

        expect(request.getUri()).andReturn("/content?id=").atLeastOnce();
        expect(request.getMethod()).andReturn(GET).atLeastOnce();
        replay(request, context);
        try {
            HttpContentRequestHandler contentRequestHandler = getHandler();
            contentRequestHandler.channelRead0(context, request);
        } finally {
            verify(request, context);
        }
    }

    @Test(expected = HttpRequestException.class)
    public void testProcessRequestUnknownContentId() throws Exception {
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        FullHttpRequest request = createMock(FullHttpRequest.class);

        expect(request.getUri()).andReturn("/content?id=25").atLeastOnce();
        expect(request.getMethod()).andReturn(GET).atLeastOnce();
        replay(request, context);
        try {
            HttpContentRequestHandler contentRequestHandler = getHandler();
            contentRequestHandler.channelRead0(context, request);
        } finally {
            verify(request, context);
        }
    }

    @Test
    public void testProcessRequestWithoutKeepAlive() throws Exception {
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        FullHttpRequest request = createMock(FullHttpRequest.class);

        AbstractNode node = getContentNodeFromMediaManager();
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HOST, "localhost");
        headers.add(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);

        expect(request.getUri()).andReturn("/content?id=" + node.getId()).atLeastOnce();
        expect(request.headers()).andReturn(headers).atLeastOnce();
        expect(request.getMethod()).andReturn(GET).atLeastOnce();

        Channel channel = createMock(Channel.class);

        expect(context.write(isA(HttpResponse.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(context.write(isA(ChunkedFile.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(context.writeAndFlush(isA(LastHttpContent.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();

        replay(request, context, channel);
        HttpContentRequestHandler contentRequestHandler = getHandler();
        contentRequestHandler.channelRead0(context, request);
        verify(request, context, channel);
    }

    @Test(expected = HttpRequestException.class)
    public void testProcessRequestEmptyOffset() throws Exception {
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        FullHttpRequest request = createMock(FullHttpRequest.class);

        AbstractNode node = getContentNodeFromMediaManager();
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HOST, "localhost");
        headers.add(HttpHeaders.Names.RANGE, "");

        expect(request.getUri()).andReturn("/content?id=" + node.getId()).atLeastOnce();
        expect(request.headers()).andReturn(headers).atLeastOnce();
        expect(request.getMethod()).andReturn(GET).atLeastOnce();

        replay(request, context);
        try {
            HttpContentRequestHandler contentRequestHandler = getHandler();
            contentRequestHandler.channelRead0(context, request);
        } finally {
            verify(request, context);
        }
    }

    @Test
    public void testProcessRequestWithOffset() throws Exception {
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        FullHttpRequest request = createMock(FullHttpRequest.class);

        AbstractNode node = getContentNodeFromMediaManager();
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HOST, "localhost");
        headers.add(HttpHeaders.Names.RANGE, "bytes=5-");

        expect(request.getUri()).andReturn("/content?id=" + node.getId()).atLeastOnce();
        expect(request.headers()).andReturn(headers).atLeastOnce();
        expect(request.getMethod()).andReturn(GET).atLeastOnce();
        expect(request.getProtocolVersion()).andReturn(HTTP_1_1).atLeastOnce();

        Channel channel = createMock(Channel.class);

        expect(context.write(isA(HttpResponse.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(context.write(isA(ChunkedFile.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(context.writeAndFlush(isA(LastHttpContent.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();

        replay(request, context, channel);
        HttpContentRequestHandler contentRequestHandler = getHandler();
        contentRequestHandler.channelRead0(context, request);
        verify(request, context, channel);
    }

    @Test(expected = HttpRequestException.class)
    public void testProcessRequestWithBadOffset() throws Exception {
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        FullHttpRequest request = createMock(FullHttpRequest.class);

        AbstractNode node = getContentNodeFromMediaManager();
        HttpHeaders headers = new DefaultHttpHeaders();
        expect(request.getUri()).andReturn("/content?id=" + node.getId()).atLeastOnce();
        expect(request.headers()).andReturn(headers).atLeastOnce();
        expect(request.getMethod()).andReturn(GET).atLeastOnce();

        headers.add(HOST, "localhost");
        headers.add(HttpHeaders.Names.RANGE, "bytes=500000-");

        replay(request, context);
        try {
            HttpContentRequestHandler contentRequestHandler = getHandler();
            contentRequestHandler.channelRead0(context, request);
        } finally {
            verify(request, context);
        }
    }

    @Test(expected = HttpRequestException.class)
    public void testProcessRequestWithNegativeOffset() throws Exception {
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        FullHttpRequest request = createMock(FullHttpRequest.class);

        AbstractNode node = getContentNodeFromMediaManager();
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HOST, "localhost");
        headers.add(HttpHeaders.Names.RANGE, "bytes=-1-");

        expect(request.getUri()).andReturn("/content?id=" + node.getId()).atLeastOnce();
        expect(request.getMethod()).andReturn(GET).atLeastOnce();
        expect(request.headers()).andReturn(headers).atLeastOnce();

        replay(request, context);
        try {
            HttpContentRequestHandler contentRequestHandler = getHandler();
            contentRequestHandler.channelRead0(context, request);
        } finally {
            verify(request, context);
        }
    }

    @Test
    public void testProcessRequest() throws Exception {
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        FullHttpRequest request = createMock(FullHttpRequest.class);
        Channel channel = createMock(Channel.class);

        AbstractNode node = getContentNodeFromMediaManager();
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HOST, "localhost");

        expect(request.getUri()).andReturn("/content?id=" + node.getId()).atLeastOnce();
        expect(request.getMethod()).andReturn(GET).atLeastOnce();
        expect(request.headers()).andReturn(headers).atLeastOnce();
        expect(request.getProtocolVersion()).andReturn(HTTP_1_1).atLeastOnce();

        expect(context.write(isA(HttpResponse.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(context.write(isA(ChunkedFile.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(context.writeAndFlush(isA(LastHttpContent.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();

        replay(request, context, channel);
        HttpContentRequestHandler contentRequestHandler = getHandler();
        contentRequestHandler.channelRead0(context, request);
        verify(request, context, channel);
    }
}
