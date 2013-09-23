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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import net.holmes.core.common.NodeFile;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.Values.BYTES;
import static io.netty.handler.codec.http.HttpHeaders.Values.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Http request handler.
 */
public abstract class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final String HTTP_SERVER_NAME = "Holmes HTTP server";
    private static final Pattern PATTERN_RANGE_START_OFFSET = Pattern.compile("^(?i)\\s*bytes\\s*=\\s*(\\d+)\\s*-.*$");
    private static final int CHUNK_SIZE = 8192;

    @Override
    public void channelRead0(final ChannelHandlerContext context, final FullHttpRequest request) throws HttpRequestException {
        String requestPath = new QueryStringDecoder(request.getUri()).path();
        if (accept(requestPath, request.getMethod()))
            // Process request
            processRequest(request, context);
        else
            // Forward request to pipeline
            context.fireChannelRead(request);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext context, final Throwable cause) {
        if (context.channel().isActive())
            if (cause instanceof HttpRequestException) {
                HttpRequestException httpRequestException = (HttpRequestException) cause;
                sendError(context, httpRequestException.getMessage(), httpRequestException.getStatus());
            } else
                sendError(context, cause.getMessage(), INTERNAL_SERVER_ERROR);
    }

    /**
     * Process request.
     *
     * @param request Http request
     * @param context Channel context
     * @throws HttpRequestException Http request exception
     */
    private void processRequest(final FullHttpRequest request, final ChannelHandlerContext context) throws HttpRequestException {
        // Get request file
        HttpRequestFile requestFile = getRequestFile(request);

        // Check file
        NodeFile file = requestFile.getNodeFile();
        if (!file.isValidFile())
            throw new HttpRequestException(file.getPath(), NOT_FOUND);

        try {
            // Get file descriptor
            RandomAccessFile randomFile = new RandomAccessFile(file, "r");
            long fileLength = randomFile.length();

            // Get start offset
            long startOffset = 0;
            String range = request.headers().get(RANGE);
            if (range != null) {
                Matcher matcher = PATTERN_RANGE_START_OFFSET.matcher(range);
                if (matcher.find()) startOffset = Long.parseLong(matcher.group(1));
                else throw new HttpRequestException(range, REQUESTED_RANGE_NOT_SATISFIABLE);
            }

            // Build response
            HttpResponse response;
            if (startOffset == 0) {
                response = new DefaultHttpResponse(HTTP_1_1, OK);
                response.headers().set(ACCEPT_RANGES, BYTES);
            } else if (startOffset < fileLength) {
                response = new DefaultHttpResponse(HTTP_1_1, PARTIAL_CONTENT);
                response.headers().set(CONTENT_RANGE, startOffset + "-" + (fileLength - 1) + "/" + fileLength);
            } else {
                throw new HttpRequestException("Invalid start offset", REQUESTED_RANGE_NOT_SATISFIABLE);
            }

            // Add response headers
            response.headers().set(SERVER, HTTP_SERVER_NAME);
            HttpHeaders.setContentLength(response, fileLength - startOffset);
            if (requestFile.getMimeType() != null) {
                response.headers().set(CONTENT_TYPE, requestFile.getMimeType().getMimeType());
            }
            if (isKeepAlive(request)) {
                response.headers().set(CONNECTION, KEEP_ALIVE);
            }

            // Write the response
            context.write(response);

            // Write the content
            context.write(new ChunkedFile(randomFile, startOffset, fileLength - startOffset, CHUNK_SIZE));

            // Write the end marker
            ChannelFuture lastContentFuture = context.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

            // Decide whether to close the connection or not when the whole content is written out.
            if (!isKeepAlive(request))
                lastContentFuture.addListener(ChannelFutureListener.CLOSE);

        } catch (IOException e) {
            throw new HttpRequestException(e, INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Send error.
     *
     * @param context channel context
     * @param message message
     * @param status  response status
     */
    private void sendError(final ChannelHandlerContext context, final String message, final HttpResponseStatus status) {
        // Build error response
        ByteBuf buffer = Unpooled.copiedBuffer("Failure: " + message + " " + status.toString() + "\r\n", CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, buffer);
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

        // Close the connection as soon as the error message is sent.
        context.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * Check if handler can process request.
     *
     * @param requestPath request path
     * @param method      Http method (GET, POST...)
     * @return true if handler can process request
     */
    abstract boolean accept(final String requestPath, final HttpMethod method);

    /**
     * Get request file
     *
     * @param request HTTP request
     * @return request file
     * @throws HttpRequestException
     */
    abstract HttpRequestFile getRequestFile(final FullHttpRequest request) throws HttpRequestException;
}
