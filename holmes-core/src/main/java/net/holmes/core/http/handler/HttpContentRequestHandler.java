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

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import net.holmes.core.common.NodeFile;
import net.holmes.core.http.HttpServer;
import net.holmes.core.media.MediaManager;
import net.holmes.core.media.model.AbstractNode;
import net.holmes.core.media.model.ContentNode;

import javax.inject.Inject;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.Values.BYTES;
import static io.netty.handler.codec.http.HttpHeaders.Values.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Handler for content (i.e. video, audio or picture) streaming to UPnP media renderer.
 */
public final class HttpContentRequestHandler implements HttpRequestHandler {
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
    public boolean accept(final String requestPath, final HttpMethod method) {
        return method.equals(HttpMethod.GET) && requestPath.startsWith(REQUEST_PATH);
    }

    @Override
    public void processRequest(final FullHttpRequest request, final ChannelHandlerContext context) throws HttpRequestException {
        // Get content node
        ContentNode node = getContentNode(request.getUri());
        if (node == null)
            throw new HttpRequestException(request.getUri(), NOT_FOUND);

        // Check node
        NodeFile file = new NodeFile(node.getPath());
        if (!file.isValidFile())
            throw new HttpRequestException(node.getPath(), NOT_FOUND);

        // Get start offset
        long startOffset = 0;
        String range = request.headers().get(RANGE);
        if (range != null) {
            Matcher matcher = PATTERN_RANGE_START_OFFSET.matcher(range);
            if (matcher.find()) startOffset = Long.parseLong(matcher.group(1));
            else throw new HttpRequestException(range, REQUESTED_RANGE_NOT_SATISFIABLE);
        }

        // Get file descriptor
        RandomAccessFile randomFile;
        long fileLength;
        try {
            randomFile = new RandomAccessFile(file, "r");
            fileLength = randomFile.length();
        } catch (IOException e) {
            throw new HttpRequestException(e, INTERNAL_SERVER_ERROR);
        }

        // Build response header
        HttpResponse response;
        if (startOffset == 0) {
            response = new DefaultFullHttpResponse(HTTP_1_1, OK);
            HttpHeaders.setContentLength(response, fileLength);
            response.headers().add(CONTENT_TYPE, node.getMimeType().getMimeType());
            response.headers().add(ACCEPT_RANGES, BYTES);
        } else if (startOffset > 0 && startOffset < fileLength) {
            response = new DefaultHttpResponse(HTTP_1_1, PARTIAL_CONTENT);
            HttpHeaders.setContentLength(response, fileLength - startOffset);
            response.headers().add(CONTENT_RANGE, startOffset + "-" + (fileLength - 1) + "/" + fileLength);
        } else {
            throw new HttpRequestException("Invalid start offset", REQUESTED_RANGE_NOT_SATISFIABLE);
        }
        if (HttpHeaders.isKeepAlive(request)) {
            response.headers().add(CONNECTION, KEEP_ALIVE);
        }
        response.headers().add(SERVER, HttpServer.HTTP_SERVER_NAME);

        // Write the response
        context.write(response);

        // Write the content
        try {
            context.write(new ChunkedFile(randomFile, startOffset, fileLength - startOffset, CHUNK_SIZE), context.newProgressivePromise());
        } catch (IOException e) {
            throw new HttpRequestException(e, INTERNAL_SERVER_ERROR);
        }

        // Write the end marker
        ChannelFuture lastContentFuture = context.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

        // Decide whether to close the connection or not.
        if (!HttpHeaders.isKeepAlive(request)) {
            // Close the connection when the whole content is written out.
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
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
        if (contentId != null) {
            AbstractNode node = mediaManager.getNode(contentId);
            if (node instanceof ContentNode)
                contentNode = (ContentNode) node;
        }
        return contentNode;
    }
}
