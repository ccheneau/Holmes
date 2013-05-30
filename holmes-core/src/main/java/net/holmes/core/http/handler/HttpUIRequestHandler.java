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
import net.holmes.common.mimetype.MimeType;
import net.holmes.common.mimetype.MimeTypeManager;
import net.holmes.core.http.HttpServer;
import net.holmes.core.inject.InjectLogger;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import static net.holmes.common.SystemProperty.HOLMES_HOME;

/**
 * Handler for Holmes UI pages.
 */
public final class HttpUIRequestHandler implements HttpRequestHandler {
    private static final String UI_DIRECTORY = getUiDirectory("ui");
    private final MimeTypeManager mimeTypeManager;
    @InjectLogger
    private Logger logger;

    /**
     * Instantiates a new http ui request handler.
     *
     * @param mimeTypeManager mime type manager
     */
    @Inject
    public HttpUIRequestHandler(final MimeTypeManager mimeTypeManager) {
        this.mimeTypeManager = mimeTypeManager;
    }

    /**
     * Get UI base directory.
     *
     * @param uiSubDir UI sub directory
     * @return UI directory
     */
    private static String getUiDirectory(final String uiSubDir) {
        File uiDir = new File(HOLMES_HOME.getValue(), uiSubDir);
        if (!uiDir.exists()) {
            throw new RuntimeException(uiDir.getAbsolutePath() + " does not exist. Check " + HOLMES_HOME.getName() + " [" + HOLMES_HOME.getValue()
                    + "] system property");
        }
        return uiDir.getAbsolutePath();
    }

    @Override
    public boolean canProcess(final String requestPath, final HttpMethod method) {
        return method.equals(HttpMethod.GET);
    }

    @Override
    public void processRequest(final FullHttpRequest request, final Channel channel) throws HttpRequestException {
        if (logger.isDebugEnabled()) logger.debug("[START] processRequest");

        // Get file name
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        String fileName = decoder.path().trim();
        if ("/".equals(fileName)) {
            fileName = "/index.html";
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
            throw new HttpRequestException(e, HttpResponseStatus.NOT_FOUND);
        } catch (IOException e) {
            if (logger.isErrorEnabled()) logger.error(e.getMessage(), e);
            throw new HttpRequestException(e, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        } finally {
            if (logger.isDebugEnabled()) logger.debug("[END] processRequest");
        }
    }
}
