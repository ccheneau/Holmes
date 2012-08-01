/**
* Copyright (c) 2012 Cedric Cheneau
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/
package net.holmes.core.http.request;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map.Entry;

import net.holmes.core.http.HttpServer;
import net.holmes.core.media.IMediaService;
import net.holmes.core.media.node.AbstractNode;
import net.holmes.core.media.node.ContentNode;
import net.holmes.core.media.node.NodeType;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.handler.stream.ChunkedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Handler to serve contents (i.e. videos, audios or pictures) to UPnP media renderer
 */
public final class HttpRequestContentHandler implements IHttpRequestHandler {
    private static Logger logger = LoggerFactory.getLogger(HttpRequestContentHandler.class);

    public final static String REQUEST_PATH = "/content";

    @Inject
    private IMediaService mediaService;

    public HttpRequestContentHandler() {
    }

    /* (non-Javadoc)
     * @see net.holmes.core.http.request.IHttpRequestHandler#initHandler()
     */
    @Override
    @Inject
    public void initHandler() {
    }

    /* (non-Javadoc)
     * @see net.holmes.core.http.request.IHttpRequestHandler#canProcess(java.lang.String)
     */
    @Override
    public boolean canProcess(String requestPath) {
        return requestPath.startsWith(REQUEST_PATH);
    }

    /* (non-Javadoc)
     * @see net.holmes.core.http.request.IHttpRequestHandler#processRequest(org.jboss.netty.handler.codec.http.HttpRequest, org.jboss.netty.channel.Channel)
     */
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
            }
            else if (!file.canRead() || file.isHidden()) {
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
            }
            catch (IOException e) {
                throw new HttpRequestException(e.getMessage(), HttpResponseStatus.NOT_FOUND);
            }

            // Build response header
            HttpResponse response = null;
            if (startOffset == 0) {
                response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                HttpHeaders.setContentLength(response, fileLength);
                response.setHeader(HttpHeaders.Names.CONTENT_TYPE, node.getMimeType().getMimeType());
                response.setHeader(HttpHeaders.Names.ACCEPT_RANGES, "bytes");
            }
            else if (startOffset > 0 && startOffset < fileLength) {
                response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.PARTIAL_CONTENT);
                HttpHeaders.setContentLength(response, fileLength - startOffset);
                response.setHeader(HttpHeaders.Names.CONTENT_RANGE, startOffset + "-" + (fileLength - 1) + "/" + fileLength);
            }
            else {
                throw new HttpRequestException("Invalid start offset", HttpResponseStatus.BAD_REQUEST);
            }
            response.setHeader(HttpHeaders.Names.SERVER, HttpServer.HTTP_SERVER_NAME);

            if (logger.isDebugEnabled()) {
                logger.debug("Response: " + response);
                for (Entry<String, String> entry : response.getHeaders()) {
                    logger.debug("Response header: " + entry.getKey() + " ==> " + entry.getValue());
                }
            }

            // Write the header.
            channel.write(response);

            // Write the content.
            ChannelFuture writeFuture = null;
            try {
                writeFuture = channel.write(new ChunkedFile(raf, startOffset, fileLength - startOffset, 8192));
            }
            catch (IOException e) {
                throw new HttpRequestException(e.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
            }

            // Decide whether to close the connection or not.
            if (!HttpHeaders.isKeepAlive(request)) {
                // Close the connection when the whole content is written out.
                writeFuture.addListener(ChannelFutureListener.CLOSE);
            }
        }
        finally {
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
