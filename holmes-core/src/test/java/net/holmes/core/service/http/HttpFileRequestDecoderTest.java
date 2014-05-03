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

package net.holmes.core.service.http;

import com.google.common.collect.Lists;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import net.holmes.core.business.media.MediaManager;
import net.holmes.core.business.media.model.ContentNode;
import net.holmes.core.business.mimetype.MimeTypeManager;
import net.holmes.core.common.MimeType;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HttpFileRequestDecoderTest {

    @Test
    public void testDecodeUiFile() throws Exception {
        FullHttpRequest request = createMock(FullHttpRequest.class);
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        MediaManager mediaManager = createMock(MediaManager.class);
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        List<Object> out = Lists.newArrayList();

        expect(request.getMethod()).andReturn(GET).atLeastOnce();
        expect(request.getUri()).andReturn("/index.html").atLeastOnce();
        expect(mimeTypeManager.getMimeType("/index.html")).andReturn(MimeType.valueOf("text/html")).atLeastOnce();

        replay(context, request, mediaManager, mimeTypeManager);
        HttpFileRequestDecoder decoder = new HttpFileRequestDecoder(mediaManager, mimeTypeManager, System.getProperty("java.io.tmpdir"));
        decoder.decode(context, request, out);
        assertEquals(out.size(), 1);
        assertEquals(out.get(0).getClass(), HttpFileRequest.class);
        HttpFileRequest fileRequest = (HttpFileRequest) out.get(0);
        assertNotNull(fileRequest.getFile());
        assertNotNull(fileRequest.getMimeType());
        assertNotNull(fileRequest.getHttpRequest());
        verify(context, request, mediaManager, mimeTypeManager);
    }

    @Test
    public void testDecodeUiFileEmpty() throws Exception {
        FullHttpRequest request = createMock(FullHttpRequest.class);
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        MediaManager mediaManager = createMock(MediaManager.class);
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        List<Object> out = Lists.newArrayList();

        expect(request.getMethod()).andReturn(GET).atLeastOnce();
        expect(request.getUri()).andReturn("/").atLeastOnce();
        expect(mimeTypeManager.getMimeType("/index.html")).andReturn(MimeType.valueOf("text/html")).atLeastOnce();

        replay(context, request, mediaManager, mimeTypeManager);
        HttpFileRequestDecoder decoder = new HttpFileRequestDecoder(mediaManager, mimeTypeManager, System.getProperty("java.io.tmpdir"));
        decoder.decode(context, request, out);
        assertEquals(out.size(), 1);
        assertEquals(out.get(0).getClass(), HttpFileRequest.class);
        verify(context, request, mediaManager, mimeTypeManager);
    }

    @Test
    public void testDecodeUiFileNoMimeType() throws Exception {
        FullHttpRequest request = createMock(FullHttpRequest.class);
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        MediaManager mediaManager = createMock(MediaManager.class);
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        List<Object> out = Lists.newArrayList();

        expect(request.getMethod()).andReturn(GET).atLeastOnce();
        expect(request.getUri()).andReturn("/index.html1").atLeastOnce();
        expect(request.retain()).andReturn(request).atLeastOnce();
        expect(mimeTypeManager.getMimeType("/index.html1")).andReturn(null).atLeastOnce();

        replay(context, request, mediaManager, mimeTypeManager);
        HttpFileRequestDecoder decoder = new HttpFileRequestDecoder(mediaManager, mimeTypeManager, System.getProperty("java.io.tmpdir"));
        decoder.decode(context, request, out);
        assertEquals(out.size(), 1);
        assertEquals(out.get(0), request);
        verify(context, request, mediaManager, mimeTypeManager);
    }

    @Test
    public void testDecodePostMessage() throws Exception {
        FullHttpRequest request = createMock(FullHttpRequest.class);
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        MediaManager mediaManager = createMock(MediaManager.class);
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        List<Object> out = Lists.newArrayList();

        expect(request.getMethod()).andReturn(POST).atLeastOnce();
        expect(request.retain()).andReturn(request).atLeastOnce();

        replay(context, request, mediaManager, mimeTypeManager);
        HttpFileRequestDecoder decoder = new HttpFileRequestDecoder(mediaManager, mimeTypeManager, System.getProperty("java.io.tmpdir"));
        decoder.decode(context, request, out);
        assertEquals(out.size(), 1);
        assertEquals(out.get(0), request);
        verify(context, request, mediaManager, mimeTypeManager);
    }

    @Test
    public void testDecodeContentFile() {
        FullHttpRequest request = createMock(FullHttpRequest.class);
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        MediaManager mediaManager = createMock(MediaManager.class);
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        List<Object> out = Lists.newArrayList();

        expect(request.getMethod()).andReturn(GET).atLeastOnce();
        expect(request.getUri()).andReturn("/content?id=1234").atLeastOnce();
        expect(mediaManager.getNode("1234")).andReturn(new ContentNode("id", "parentId", "name", new File("file"), MimeType.valueOf("video/x-msvideo"))).atLeastOnce();

        replay(context, request, mediaManager, mimeTypeManager);
        HttpFileRequestDecoder decoder = new HttpFileRequestDecoder(mediaManager, mimeTypeManager, System.getProperty("java.io.tmpdir"));
        decoder.decode(context, request, out);
        assertEquals(out.size(), 1);
        assertEquals(out.get(0).getClass(), HttpFileRequest.class);
        HttpFileRequest fileRequest = (HttpFileRequest) out.get(0);
        assertNotNull(fileRequest.getFile());
        assertNotNull(fileRequest.getMimeType());
        assertNotNull(fileRequest.getHttpRequest());
        verify(context, request, mediaManager, mimeTypeManager);
    }

    @Test
    public void testDecodeContentFileEmptyContentId() {
        FullHttpRequest request = createMock(FullHttpRequest.class);
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        MediaManager mediaManager = createMock(MediaManager.class);
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        List<Object> out = Lists.newArrayList();

        expect(request.getMethod()).andReturn(GET).atLeastOnce();
        expect(request.getUri()).andReturn("/content?id=").atLeastOnce();
        expect(request.retain()).andReturn(request).atLeastOnce();
        expect(mediaManager.getNode("")).andReturn(null).atLeastOnce();

        replay(context, request, mediaManager, mimeTypeManager);
        HttpFileRequestDecoder decoder = new HttpFileRequestDecoder(mediaManager, mimeTypeManager, System.getProperty("java.io.tmpdir"));
        decoder.decode(context, request, out);
        assertEquals(out.size(), 1);
        assertEquals(out.get(0), request);
        verify(context, request, mediaManager, mimeTypeManager);
    }

    @Test
    public void testDecodeContentFileNullContentId() {
        FullHttpRequest request = createMock(FullHttpRequest.class);
        ChannelHandlerContext context = createMock(ChannelHandlerContext.class);
        MediaManager mediaManager = createMock(MediaManager.class);
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        List<Object> out = Lists.newArrayList();

        expect(request.getMethod()).andReturn(GET).atLeastOnce();
        expect(request.getUri()).andReturn("/content").atLeastOnce();
        expect(request.retain()).andReturn(request).atLeastOnce();
        expect(mimeTypeManager.getMimeType("/content")).andReturn(null).atLeastOnce();

        replay(context, request, mediaManager, mimeTypeManager);
        HttpFileRequestDecoder decoder = new HttpFileRequestDecoder(mediaManager, mimeTypeManager, System.getProperty("java.io.tmpdir"));
        decoder.decode(context, request, out);
        assertEquals(out.size(), 1);
        assertEquals(out.get(0), request);
        verify(context, request, mediaManager, mimeTypeManager);
    }
}
