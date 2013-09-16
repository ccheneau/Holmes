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

package net.holmes.core.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import net.holmes.core.http.handler.HttpRequestException;
import net.holmes.core.http.handler.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * HttpChannelHandler redirect HTTP requests to proper handler.
 */
public final class HttpChannelHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpChannelHandler.class);
    private final HttpRequestHandler contentRequestHandler;
    private final HttpRequestHandler backendRequestHandler;
    private final HttpRequestHandler uiRequestHandler;

    /**
     * Instantiates a new http channel handler.
     *
     * @param contentRequestHandler content request handler
     * @param backendRequestHandler backend request handler
     * @param uiRequestHandler      UI request handler
     */
    @Inject
    public HttpChannelHandler(@Named("content") final HttpRequestHandler contentRequestHandler,
                              @Named("backend") final HttpRequestHandler backendRequestHandler,
                              @Named("ui") final HttpRequestHandler uiRequestHandler) {
        this.contentRequestHandler = contentRequestHandler;
        this.backendRequestHandler = backendRequestHandler;
        this.uiRequestHandler = uiRequestHandler;
    }

    @Override
    public void channelRead0(final ChannelHandlerContext context, final FullHttpRequest request) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("messageReceived url:{}", request.getUri());
            for (Entry<String, String> entry : request.headers())
                LOGGER.debug("Request header: {} ==> {}", entry.getKey(), entry.getValue());

            if (request.getMethod().equals(HttpMethod.POST) && request.content().isReadable()) {
                QueryStringDecoder queryStringDecoder = new QueryStringDecoder("/?" + request.content().toString(HttpConstants.DEFAULT_CHARSET));
                Map<String, List<String>> params = queryStringDecoder.parameters();
                if (params != null)
                    for (Entry<String, List<String>> entry : params.entrySet())
                        LOGGER.debug("Post parameter: {} ==> {}", entry.getKey(), entry.getValue());
            }
        }

        String requestPath = new QueryStringDecoder(request.getUri()).path();
        try {
            // Process request
            getRequestHandler(requestPath, request.getMethod()).processRequest(request, context);

        } catch (HttpRequestException ex) {
            sendError(context, ex.getMessage(), ex.getStatus());
        }
    }

    /**
     * Gets the handler that matches the request.
     *
     * @param requestPath request path
     * @param method      http method
     * @return handler that matches the request
     * @throws HttpRequestException
     */
    private HttpRequestHandler getRequestHandler(String requestPath, HttpMethod method) throws HttpRequestException {
        if (contentRequestHandler.accept(requestPath, method))
            return contentRequestHandler;
        else if (backendRequestHandler.accept(requestPath, method))
            return backendRequestHandler;
        else if (uiRequestHandler.accept(requestPath, method))
            return uiRequestHandler;
        else throw new HttpRequestException("Cannot process request", BAD_REQUEST);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext context, final Throwable cause) {
        if (context.channel().isActive())
            sendError(context, cause.getMessage(), INTERNAL_SERVER_ERROR);
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
}
