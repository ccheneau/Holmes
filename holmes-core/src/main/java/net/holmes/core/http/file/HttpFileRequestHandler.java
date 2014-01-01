/*
 * Copyright (C) 2012-2014  Cedric Cheneau
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

package net.holmes.core.http.file;

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
import net.holmes.core.common.mimetype.MimeType;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.Values.BYTES;
import static io.netty.handler.codec.http.HttpHeaders.Values.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static net.holmes.core.common.Constants.HOLMES_HTTP_SERVER_NAME;

/**
 * Http file request handler.
 */
public final class HttpFileRequestHandler extends SimpleChannelInboundHandler<HttpFileRequest> {
    private static final Pattern PATTERN_RANGE_START_OFFSET = Pattern.compile("^(?i)\\s*bytes\\s*=\\s*(\\d+)\\s*-.*$");
    private static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    private static final int CHUNK_SIZE = 8192;

    /**
     * Instantiates a new Http file request handler.
     */
    public HttpFileRequestHandler() {
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext context, final HttpFileRequest request) throws HttpFileRequestException, IOException {
        // Check file
        NodeFile file = request.getNodeFile();
        if (!file.isValidFile())
            throw new HttpFileRequestException(file.getPath(), NOT_FOUND);

        // Get file descriptor
        RandomAccessFile randomFile = new RandomAccessFile(file, "r");
        long fileLength = randomFile.length();

        // Get start offset
        long startOffset = getStartOffset(request.getHttpRequest());

        // Build response
        HttpResponse response = buildHttpResponse(startOffset, fileLength);

        // Add http headers
        response.headers().set(SERVER, HOLMES_HTTP_SERVER_NAME.toString());
        addContentHeaders(response, fileLength - startOffset, request.getMimeType());
        addDateHeader(response, file);

        // Keep alive header
        boolean keepAlive = isKeepAlive(request.getHttpRequest());
        if (keepAlive)
            response.headers().set(CONNECTION, KEEP_ALIVE);

        // Write the response
        context.write(response);

        // Write the content
        context.write(new ChunkedFile(randomFile, startOffset, fileLength - startOffset, CHUNK_SIZE));

        // Write the end marker
        ChannelFuture lastContentFuture = context.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

        // Decide whether to close the connection or not when the whole content is written out.
        if (!keepAlive)
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);

    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext context, final Throwable cause) {
        if (context.channel().isActive())
            if (cause instanceof HttpFileRequestException) {
                HttpFileRequestException httpFileRequestException = (HttpFileRequestException) cause;
                sendError(context, httpFileRequestException.getMessage(), httpFileRequestException.getStatus());
            } else
                sendError(context, cause.getMessage(), INTERNAL_SERVER_ERROR);
    }

    /**
     * Build Http response.
     *
     * @param startOffset start offset
     * @param fileLength  file length
     * @return Http response
     * @throws HttpFileRequestException
     */
    private HttpResponse buildHttpResponse(final long startOffset, final long fileLength) throws HttpFileRequestException {
        HttpResponse response;
        if (startOffset == 0) {
            response = new DefaultHttpResponse(HTTP_1_1, OK);
            response.headers().set(ACCEPT_RANGES, BYTES);
        } else if (startOffset < fileLength) {
            response = new DefaultHttpResponse(HTTP_1_1, PARTIAL_CONTENT);
            response.headers().set(CONTENT_RANGE, startOffset + "-" + (fileLength - 1) + "/" + fileLength);
        } else
            throw new HttpFileRequestException("Invalid start offset", REQUESTED_RANGE_NOT_SATISFIABLE);
        return response;
    }

    /**
     * Get start offset from Http request.
     *
     * @param httpRequest Http request
     * @return start offset
     * @throws HttpFileRequestException
     */
    private long getStartOffset(final FullHttpRequest httpRequest) throws HttpFileRequestException {
        long startOffset = 0;
        String range = httpRequest.headers().get(RANGE);
        if (range != null) {
            Matcher matcher = PATTERN_RANGE_START_OFFSET.matcher(range);
            if (matcher.find())
                startOffset = Long.parseLong(matcher.group(1));
            else
                throw new HttpFileRequestException(range, REQUESTED_RANGE_NOT_SATISFIABLE);
        }
        return startOffset;
    }

    /**
     * Add content length and type headers.
     *
     * @param response   HTTP response
     * @param fileLength file length
     * @param mimeType   mime type
     */
    private void addContentHeaders(final HttpResponse response, final long fileLength, final MimeType mimeType) {
        setContentLength(response, fileLength);
        response.headers().set(CONTENT_TYPE, mimeType.getMimeType());
    }

    /**
     * Add date header.
     *
     * @param response HTTP response
     */
    private void addDateHeader(final HttpResponse response, final NodeFile file) {
        // Add date header
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));
        response.headers().set(DATE, dateFormatter.format(file.lastModified()));
    }

    /**
     * Send error.
     *
     * @param context channel context                                                    z
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
}
