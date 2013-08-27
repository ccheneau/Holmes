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

import com.google.common.base.Strings;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import net.holmes.core.common.mimetype.MimeType;
import net.holmes.core.common.mimetype.MimeTypeManager;
import net.holmes.core.http.HttpServer;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.Values.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Handler for Holmes UI pages.
 */
public final class HttpUIRequestHandler implements HttpRequestHandler {
    private static final String DEFAULT_PAGE = "index.html";
    private final String uiDirectory;
    private final MimeTypeManager mimeTypeManager;

    /**
     * Instantiates a new http ui request handler.
     *
     * @param mimeTypeManager mime type manager
     * @param uiDirectory     UI base directory
     */
    @Inject
    public HttpUIRequestHandler(final MimeTypeManager mimeTypeManager, @Named("uiDirectory") final String uiDirectory) {
        this.mimeTypeManager = mimeTypeManager;
        this.uiDirectory = uiDirectory;
    }

    @Override
    public boolean accept(final String requestPath, final HttpMethod method) {
        return method.equals(HttpMethod.GET);
    }

    @Override
    public void processRequest(final FullHttpRequest request, final ChannelHandlerContext context) throws HttpRequestException {
        // Get file name
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        String fileName = decoder.path().trim();
        if ("/".equals(fileName))
            fileName += DEFAULT_PAGE;

        if (Strings.isNullOrEmpty(fileName))
            throw new HttpRequestException("file name is null", NOT_FOUND);

        try {
            // Get file
            File file = new File(uiDirectory, fileName);
            if (!file.exists()) throw new HttpRequestException(fileName, NOT_FOUND);

            // Read the file
            RandomAccessFile randomFile = new RandomAccessFile(file, "r");

            // Define response header
            HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
            HttpHeaders.setContentLength(response, randomFile.length());
            response.headers().set(SERVER, HttpServer.HTTP_SERVER_NAME);
            MimeType mimeType = mimeTypeManager.getMimeType(fileName);
            if (mimeType != null)
                response.headers().set(CONTENT_TYPE, mimeType.getMimeType());

            if (HttpHeaders.isKeepAlive(request))
                response.headers().set(CONNECTION, KEEP_ALIVE);

            // Write the response header.
            context.write(response);

            // Write the content
            context.write(new ChunkedFile(randomFile, 0, randomFile.length(), CHUNK_SIZE));

            // Write the end marker
            ChannelFuture lastContentFuture = context.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

            // Decide whether to close the connection or not when the whole content is written out.
            if (!HttpHeaders.isKeepAlive(request))
                lastContentFuture.addListener(ChannelFutureListener.CLOSE);

        } catch (IOException e) {
            throw new HttpRequestException(e, INTERNAL_SERVER_ERROR);
        }
    }
}
