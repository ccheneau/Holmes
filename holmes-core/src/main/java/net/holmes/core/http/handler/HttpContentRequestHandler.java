/**
* Copyright (C) 2012  Cedric Cheneau
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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.inject.Inject;

import net.holmes.core.http.HttpRequestException;
import net.holmes.core.http.HttpServer;
import net.holmes.core.http.IHttpRequestHandler;
import net.holmes.core.media.IMediaService;
import net.holmes.core.media.node.AbstractNode;
import net.holmes.core.media.node.ContentNode;
import net.holmes.core.media.node.NodeType;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.handler.stream.ChunkedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for content (i.e. video, audio or picture) streaming to UPnP media renderer
 */
public final class HttpContentRequestHandler implements IHttpRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpContentRequestHandler.class);

    private static final String REQUEST_PATH = "/content";

    private final IMediaService mediaService;

    @Inject
    public HttpContentRequestHandler(IMediaService mediaService) {
        this.mediaService = mediaService;
    }

    @Override
    public boolean canProcess(String requestPath, HttpMethod method) {
        return method.equals(HttpMethod.GET) && requestPath.startsWith(REQUEST_PATH);
    }

    @Override
    public void processRequest(HttpRequest request, Channel channel) throws HttpRequestException {
        if (logger.isDebugEnabled()) logger.debug("[START] processRequest");

        try {
            // Get content node
            ContentNode node = getContentNode(request.getUri());
            if (node == null) {
                throw new HttpRequestException("Invalid node", HttpResponseStatus.NOT_FOUND);
            }

            // Check node
            File file = new File(node.getPath());
            if (!file.exists() || !file.isFile()) {
                throw new HttpRequestException(node.getPath(), HttpResponseStatus.NOT_FOUND);
            } else if (!file.canRead() || file.isHidden()) {
                throw new HttpRequestException(node.getPath(), HttpResponseStatus.FORBIDDEN);
            }

            // Get startOffset
            long startOffset = 0;
            String range = request.getHeader(HttpHeaders.Names.RANGE);
            if (range != null) {
                String[] token = range.split("=|-");
                if (token != null && token.length > 1 && token[0].equals("bytes")) {
                    startOffset = Long.parseLong(token[1]);
                }
                if (logger.isDebugEnabled()) logger.debug("startOffset: " + startOffset);
            }

            // Get file descriptor
            RandomAccessFile raf;
            long fileLength = 0;
            try {
                raf = new RandomAccessFile(file, "r");
                fileLength = raf.length();
            } catch (IOException e) {
                throw new HttpRequestException(e.getMessage(), HttpResponseStatus.NOT_FOUND);
            }

            // Build response header
            HttpResponse response = null;
            if (startOffset == 0) {
                response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                HttpHeaders.setContentLength(response, fileLength);
                response.setHeader(HttpHeaders.Names.CONTENT_TYPE, node.getMimeType().getMimeType());
                response.setHeader(HttpHeaders.Names.ACCEPT_RANGES, "bytes");
            } else if (startOffset > 0 && startOffset < fileLength) {
                response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.PARTIAL_CONTENT);
                HttpHeaders.setContentLength(response, fileLength - startOffset);
                response.setHeader(HttpHeaders.Names.CONTENT_RANGE, startOffset + "-" + (fileLength - 1) + "/" + fileLength);
            } else {
                throw new HttpRequestException("Invalid start offset", HttpResponseStatus.BAD_REQUEST);
            }
            response.setHeader(HttpHeaders.Names.SERVER, HttpServer.HTTP_SERVER_NAME);

            // Write the response.
            channel.write(response);

            // Write the content.
            try {
                ChannelFuture writeFuture = channel.write(new ChunkedFile(raf, startOffset, fileLength - startOffset, 8192));
                // Decide whether to close the connection or not.
                if (!HttpHeaders.isKeepAlive(request)) {
                    // Close the connection when the whole content is written out.
                    writeFuture.addListener(ChannelFutureListener.CLOSE);
                }
            } catch (IOException e) {
                throw new HttpRequestException(e.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
            }

        } finally {
            if (logger.isDebugEnabled()) logger.debug("[END] processRequest");
        }
    }

    /**
     * Get content node from {@link net.holmes.core.media.IMediaService} 
     */
    private ContentNode getContentNode(String uri) {
        ContentNode contentNode = null;
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        String contentId = decoder.getParameters().get("id").get(0);

        if (logger.isDebugEnabled()) logger.debug("file Id :" + contentId);

        if (contentId != null) {
            AbstractNode node = mediaService.getNode(contentId);
            if (logger.isDebugEnabled()) logger.debug("node :" + node);
            if (node != null && node.getType() == NodeType.TYPE_CONTENT && node instanceof ContentNode) {
                contentNode = (ContentNode) node;
            }
        }
        return contentNode;
    }
}
