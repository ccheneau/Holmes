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
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.stream.ChunkedFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import net.holmes.common.media.AbstractNode;
import net.holmes.common.media.ContentNode;
import net.holmes.core.http.HttpServer;
import net.holmes.core.inject.Loggable;
import net.holmes.core.media.MediaManager;

import org.slf4j.Logger;

/**
 * Handler for content (i.e. video, audio or picture) streaming to UPnP media renderer.
 */
@Loggable
public final class HttpContentRequestHandler implements HttpRequestHandler {
    private Logger logger;

    private static final String REQUEST_PATH = "/content";
    private static final Pattern PATTERN_RANGE_START_OFFSET = Pattern.compile("^(?i)\\s*bytes\\s*=\\s*(\\d+)\\s*-.*$");

    private final MediaManager mediaManager;

    /**
     * Instantiates a new http content request handler.
     *
     * @param mediaManager media manager
     */
    @Inject
    public HttpContentRequestHandler(final MediaManager mediaManager) {
        this.mediaManager = mediaManager;
    }

    @Override
    public boolean canProcess(final String requestPath, final HttpMethod method) {
        return method.equals(HttpMethod.GET) && requestPath.startsWith(REQUEST_PATH);
    }

    @Override
    public void processRequest(final FullHttpRequest request, final Channel channel) throws HttpRequestException {
        if (logger.isDebugEnabled()) logger.debug("[START] processRequest");

        try {
            // Get content node
            ContentNode node = getContentNode(request.getUri());
            if (node == null) {
                throw new HttpRequestException(request.getUri(), HttpResponseStatus.NOT_FOUND);
            }

            // Check node
            File file = new File(node.getPath());
            if (!file.exists() || !file.isFile()) {
                throw new HttpRequestException(node.getPath(), HttpResponseStatus.NOT_FOUND);
            } else if (!file.canRead() || file.isHidden()) {
                throw new HttpRequestException(node.getPath(), HttpResponseStatus.FORBIDDEN);
            }

            // Get start offset
            long startOffset = 0;
            String range = request.headers().get(HttpHeaders.Names.RANGE);
            if (range != null) {
                Matcher matcher = PATTERN_RANGE_START_OFFSET.matcher(range);
                if (matcher.find()) startOffset = Long.parseLong(matcher.group(1));
                else throw new HttpRequestException(range, HttpResponseStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
            }

            // Get file descriptor
            RandomAccessFile randomFile;
            long fileLength = 0;
            try {
                randomFile = new RandomAccessFile(file, "r");
                fileLength = randomFile.length();
            } catch (IOException e) {
                throw new HttpRequestException(e, HttpResponseStatus.NOT_FOUND);
            }

            // Build response header
            HttpResponse response = null;
            if (startOffset == 0) {
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                HttpHeaders.setContentLength(response, fileLength);
                response.headers().add(HttpHeaders.Names.CONTENT_TYPE, node.getMimeType().getMimeType());
                response.headers().add(HttpHeaders.Names.ACCEPT_RANGES, HttpHeaders.Values.BYTES);
            } else if (startOffset > 0 && startOffset < fileLength) {
                response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.PARTIAL_CONTENT);
                HttpHeaders.setContentLength(response, fileLength - startOffset);
                response.headers().add(HttpHeaders.Names.CONTENT_RANGE, startOffset + "-" + (fileLength - 1) + "/" + fileLength);
            } else {
                throw new HttpRequestException("Invalid start offset", HttpResponseStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
            }
            if (HttpHeaders.isKeepAlive(request)) {
                response.headers().add(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }
            response.headers().add(HttpHeaders.Names.SERVER, HttpServer.HTTP_SERVER_NAME);

            // Write the response
            channel.write(response);

            // Write the content
            try {
                ChannelFuture writeFuture = channel.write(new ChunkedFile(randomFile, startOffset, fileLength - startOffset, CHUNK_SIZE));
                // Decide whether to close the connection or not.
                if (!HttpHeaders.isKeepAlive(request)) {
                    // Close the connection when the whole content is written out.
                    writeFuture.addListener(ChannelFutureListener.CLOSE);
                }
            } catch (IOException e) {
                throw new HttpRequestException(e, HttpResponseStatus.INTERNAL_SERVER_ERROR);
            }

        } finally {
            if (logger.isDebugEnabled()) logger.debug("[END] processRequest");
        }
    }

    /**
     * Get content node from {@link net.holmes.core.media.MediaManager}.
     * 
     * @param uri content Uri
     * @return content node
     */
    private ContentNode getContentNode(final String uri) {
        ContentNode contentNode = null;
        QueryStringDecoder decoder = new QueryStringDecoder(uri);

        String contentId = decoder.parameters().get("id").get(0);
        if (logger.isDebugEnabled()) logger.debug("file Id :{}", contentId);

        if (contentId != null) {
            AbstractNode node = mediaManager.getNode(contentId);
            if (logger.isDebugEnabled()) logger.debug("node :{}", node);
            if (node instanceof ContentNode) {
                contentNode = (ContentNode) node;
            }
        }
        return contentNode;
    }
}
