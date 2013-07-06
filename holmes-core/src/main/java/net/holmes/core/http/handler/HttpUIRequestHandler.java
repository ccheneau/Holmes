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
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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

/**
 * Handler for Holmes UI pages.
 */
public final class HttpUIRequestHandler implements HttpRequestHandler {
    private final String uiDirectory;
    private final MimeTypeManager mimeTypeManager;

    /**
     * Instantiates a new http ui request handler.
     *
     * @param mimeTypeManager mime type manager
     */
    @Inject
    public HttpUIRequestHandler(final MimeTypeManager mimeTypeManager, @Named("uiDirectory") final String uiDirectory) {
        this.mimeTypeManager = mimeTypeManager;
        this.uiDirectory = uiDirectory;
    }

    @Override
    public boolean canProcess(final String requestPath, final HttpMethod method) {
        return method.equals(HttpMethod.GET);
    }

    @Override
    public void processRequest(final FullHttpRequest request, final Channel channel) throws HttpRequestException {
        // Get file name
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        String fileName = decoder.path().trim();
        if ("/".equals(fileName))
            fileName = "/index.html";

        if (Strings.isNullOrEmpty(fileName))
            throw new HttpRequestException("file name is null", HttpResponseStatus.NOT_FOUND);

        try {
            // Get file
            File file = new File(uiDirectory, fileName);
            if (!file.exists()) throw new HttpRequestException(fileName, HttpResponseStatus.NOT_FOUND);

            // Read the file
            RandomAccessFile raf = new RandomAccessFile(file, "r");

            // Define response header
            HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            HttpHeaders.setContentLength(response, raf.length());
            response.headers().add(HttpHeaders.Names.SERVER, HttpServer.HTTP_SERVER_NAME);
            MimeType mimeType = mimeTypeManager.getMimeType(fileName);
            if (mimeType != null)
                response.headers().add(HttpHeaders.Names.CONTENT_TYPE, mimeType.getMimeType());

            if (HttpHeaders.isKeepAlive(request))
                response.headers().add(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);

            // Write the response header.
            channel.write(response);

            // Write the file.
            ChannelFuture writeFuture = channel.write(new ChunkedFile(raf, 0, raf.length(), CHUNK_SIZE));

            // Decide whether to close the connection or not.
            if (!HttpHeaders.isKeepAlive(request)) {
                // Close the connection when the whole content is written out.
                writeFuture.addListener(ChannelFutureListener.CLOSE);
            }
        } catch (IOException e) {
            throw new HttpRequestException(e, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
