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

package net.holmes.core.http.file;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import net.holmes.core.common.mimetype.MimeTypeManager;
import net.holmes.core.media.MediaManager;
import net.holmes.core.media.model.AbstractNode;
import net.holmes.core.media.model.ContentNode;
import net.holmes.core.media.model.RootNode;
import net.holmes.core.test.TestModule;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class HttpFileRequestDecoderTest {

    @Inject
    private MediaManager mediaManager;
    @Inject
    private MimeTypeManager mimeTypeManager;
    private Injector injector;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    private HttpFileRequestDecoder getDecoder() {
        HttpFileRequestDecoder decoder = new HttpFileRequestDecoder(mediaManager, mimeTypeManager, System.getProperty("java.io.tmpdir"));
        injector.injectMembers(decoder);
        return decoder;
    }

    private AbstractNode getContentNodeFromMediaManager() {
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
    public void testDecodeUiFile() throws Exception {
        FullHttpRequest request = createMock(FullHttpRequest.class);
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        List<Object> out = Lists.newArrayList();

        expect(request.getMethod()).andReturn(GET).atLeastOnce();
        expect(request.getUri()).andReturn("/index.html").atLeastOnce();

        replay(context, request);
        HttpFileRequestDecoder decoder = getDecoder();
        decoder.decode(context, request, out);
        assertEquals(out.size(), 1);
        assertEquals(out.get(0).getClass(), HttpFileRequest.class);
        HttpFileRequest fileRequest = (HttpFileRequest) out.get(0);
        assertNotNull(fileRequest.getNodeFile());
        assertNotNull(fileRequest.getMimeType());
        assertNotNull(fileRequest.getHttpRequest());
        verify(context, request);
    }

    @Test
    public void testDecodeUiFileEmpty() throws Exception {
        FullHttpRequest request = createMock(FullHttpRequest.class);
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        List<Object> out = Lists.newArrayList();

        expect(request.getMethod()).andReturn(GET).atLeastOnce();
        expect(request.getUri()).andReturn("/").atLeastOnce();

        replay(context, request);
        HttpFileRequestDecoder decoder = getDecoder();
        decoder.decode(context, request, out);
        assertEquals(out.size(), 1);
        assertEquals(out.get(0).getClass(), HttpFileRequest.class);
        verify(context, request);
    }

    @Test
    public void testDecodeUiFileNoMimeType() throws Exception {
        FullHttpRequest request = createMock(FullHttpRequest.class);
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        List<Object> out = Lists.newArrayList();

        expect(request.getMethod()).andReturn(GET).atLeastOnce();
        expect(request.getUri()).andReturn("/index.html1").atLeastOnce();
        expect(request.retain()).andReturn(request).atLeastOnce();

        replay(context, request);
        HttpFileRequestDecoder decoder = getDecoder();
        decoder.decode(context, request, out);
        assertEquals(out.size(), 1);
        assertEquals(out.get(0), request);
        verify(context, request);
    }

    @Test
    public void testDecodePostMessage() throws Exception {
        FullHttpRequest request = createMock(FullHttpRequest.class);
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        List<Object> out = Lists.newArrayList();

        expect(request.getMethod()).andReturn(POST).atLeastOnce();
        expect(request.retain()).andReturn(request).atLeastOnce();

        replay(context, request);
        HttpFileRequestDecoder decoder = getDecoder();
        decoder.decode(context, request, out);
        assertEquals(out.size(), 1);
        assertEquals(out.get(0), request);
        verify(context, request);
    }

    @Test
    public void testDecodeContentFile() {
        FullHttpRequest request = createMock(FullHttpRequest.class);
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        List<Object> out = Lists.newArrayList();

        AbstractNode node = getContentNodeFromMediaManager();

        expect(request.getMethod()).andReturn(GET).atLeastOnce();
        expect(request.getUri()).andReturn("/content?id=" + node.getId()).atLeastOnce();

        replay(context, request);
        HttpFileRequestDecoder decoder = getDecoder();
        decoder.decode(context, request, out);
        assertEquals(out.size(), 1);
        assertEquals(out.get(0).getClass(), HttpFileRequest.class);
        HttpFileRequest fileRequest = (HttpFileRequest) out.get(0);
        assertNotNull(fileRequest.getNodeFile());
        assertNotNull(fileRequest.getMimeType());
        assertNotNull(fileRequest.getHttpRequest());
        verify(context, request);
    }

    @Test
    public void testDecodeContentFileEmptyContentId() {
        FullHttpRequest request = createMock(FullHttpRequest.class);
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        List<Object> out = Lists.newArrayList();

        AbstractNode node = getContentNodeFromMediaManager();

        expect(request.getMethod()).andReturn(GET).atLeastOnce();
        expect(request.getUri()).andReturn("/content?id=").atLeastOnce();
        expect(request.retain()).andReturn(request).atLeastOnce();

        replay(context, request);
        HttpFileRequestDecoder decoder = getDecoder();
        decoder.decode(context, request, out);
        assertEquals(out.size(), 1);
        assertEquals(out.get(0), request);
        verify(context, request);
    }

    @Test
    public void testDecodeContentFileNullContentId() {
        FullHttpRequest request = createMock(FullHttpRequest.class);
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        List<Object> out = Lists.newArrayList();

        expect(request.getMethod()).andReturn(GET).atLeastOnce();
        expect(request.getUri()).andReturn("/content").atLeastOnce();
        expect(request.retain()).andReturn(request).atLeastOnce();

        replay(context, request);
        HttpFileRequestDecoder decoder = getDecoder();
        decoder.decode(context, request, out);
        assertEquals(out.size(), 1);
        assertEquals(out.get(0), request);
        verify(context, request);
    }
}
