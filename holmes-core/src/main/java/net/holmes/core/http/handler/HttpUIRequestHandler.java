/**
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
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.stream.ChunkedFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.inject.Inject;
import javax.inject.Named;

import net.holmes.core.configuration.Configuration;
import net.holmes.core.http.HttpServer;
import net.holmes.core.util.inject.Loggable;
import net.holmes.core.util.mimetype.MimeType;
import net.holmes.core.util.mimetype.MimeTypeFactory;

import org.slf4j.Logger;

/**
 * Handler for Holmes UI pages
 */
@Loggable
public final class HttpUIRequestHandler implements HttpRequestHandler {
    private Logger logger;

    private final Configuration configuration;
    private final MimeTypeFactory mimeTypeFactory;
    private final String uiDirectory;

    @Inject
    public HttpUIRequestHandler(Configuration configuration, MimeTypeFactory mimeTypeFactory, @Named("uiDirectory") String uiDirectory) {
        this.configuration = configuration;
        this.mimeTypeFactory = mimeTypeFactory;
        this.uiDirectory = uiDirectory;
    }

    @Override
    public boolean canProcess(String requestPath, HttpMethod method) {
        return method.equals(HttpMethod.GET);
    }

    @Override
    public void processRequest(FullHttpRequest request, Channel channel) throws HttpRequestException {
        if (logger.isDebugEnabled()) logger.debug("[START] processRequest");

        // Get file name
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        String fileName = decoder.path();
        if ("/".equals(fileName)) {
            fileName = "/" + configuration.getTheme() + "/index.html";
        }

        if (fileName == null || fileName.trim().isEmpty()) {
            throw new HttpRequestException("file name is null", HttpResponseStatus.NOT_FOUND);
        }
        if (logger.isDebugEnabled()) logger.debug("file name:{}", fileName);

        try {
            // Get file
            File file = new File(uiDirectory, fileName);
            if (!file.exists()) {
                if (logger.isDebugEnabled()) logger.debug("resource not found:{}", fileName);
                throw new HttpRequestException(fileName, HttpResponseStatus.NOT_FOUND);
            }

            // Read the file
            RandomAccessFile raf = new RandomAccessFile(file, "r");

            // Define response header
            HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            HttpHeaders.setContentLength(response, raf.length());
            response.headers().add(HttpHeaders.Names.SERVER, HttpServer.HTTP_SERVER_NAME);
            MimeType mimeType = mimeTypeFactory.getMimeType(fileName);
            if (mimeType != null) {
                response.headers().add(HttpHeaders.Names.CONTENT_TYPE, mimeType.getMimeType());
            }
            if (HttpHeaders.isKeepAlive(request)) {
                response.headers().add(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }
            // Write the response header.
            channel.write(response);

            // Write the file.
            ChannelFuture writeFuture = channel.write(new ChunkedFile(raf, 0, raf.length(), CHUNK_SIZE));

            // Decide whether to close the connection or not.
            if (!HttpHeaders.isKeepAlive(request)) {
                // Close the connection when the whole content is written out.
                writeFuture.addListener(ChannelFutureListener.CLOSE);
            }
        } catch (FileNotFoundException e) {
            if (logger.isDebugEnabled()) logger.debug("resource not found:{}", fileName);
            throw new HttpRequestException(e.getMessage(), HttpResponseStatus.NOT_FOUND);
        } catch (IOException e) {
            if (logger.isErrorEnabled()) logger.error(e.getMessage(), e);
            throw new HttpRequestException(e.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
        } finally {
            if (logger.isDebugEnabled()) logger.debug("[END] processRequest");
        }
    }
}
