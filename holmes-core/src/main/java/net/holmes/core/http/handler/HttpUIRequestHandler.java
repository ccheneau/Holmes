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

import net.holmes.common.SystemProperty;
import net.holmes.common.configuration.Configuration;
import net.holmes.common.inject.Loggable;
import net.holmes.common.mimetype.MimeType;
import net.holmes.common.mimetype.MimeTypeManager;
import net.holmes.core.http.HttpServer;

import org.slf4j.Logger;

import com.google.common.base.Strings;

/**
 * Handler for Holmes UI pages
 */
@Loggable
public final class HttpUIRequestHandler implements HttpRequestHandler {
    private Logger logger;

    private static final String UI_DIRECTORY = getUiDirectory("ui");

    private final Configuration configuration;
    private final MimeTypeManager mimeTypeManager;

    @Inject
    public HttpUIRequestHandler(Configuration configuration, MimeTypeManager mimeTypeManager) {
        this.configuration = configuration;
        this.mimeTypeManager = mimeTypeManager;
    }

    /**
     * Get UI base directory
     */
    private static String getUiDirectory(String uiSubDir) {
        File uiDir = new File(SystemProperty.HOLMES_HOME.getValue(), uiSubDir);
        if (!uiDir.exists()) {
            throw new RuntimeException(uiDir.getAbsolutePath() + " does not exist. Check " + SystemProperty.HOLMES_HOME.getName() + " ["
                    + SystemProperty.HOLMES_HOME.getValue() + "] system property");
        }
        return uiDir.getAbsolutePath();
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
        String fileName = decoder.path().trim();
        if ("/".equals(fileName)) {
            fileName = new StringBuilder().append("/").append(configuration.getTheme()).append("/index.html").toString();
        }

        if (logger.isDebugEnabled()) logger.debug("file name:{}", fileName);

        if (Strings.isNullOrEmpty(fileName)) {
            throw new HttpRequestException("file name is null", HttpResponseStatus.NOT_FOUND);
        }

        try {
            // Get file
            File file = new File(UI_DIRECTORY, fileName);
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
            MimeType mimeType = mimeTypeManager.getMimeType(fileName);
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
