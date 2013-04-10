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
package net.holmes.core.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;

import net.holmes.core.http.handler.HttpRequestException;
import net.holmes.core.http.handler.HttpRequestHandler;
import net.holmes.core.inject.Loggable;

import org.slf4j.Logger;

/**
 * HttpChannelHandler redirect HTTP requests to proper handler
 */
@Loggable
public final class HttpChannelHandler extends ChannelInboundMessageHandlerAdapter<FullHttpRequest> {
    private Logger logger;

    private final HttpRequestHandler contentRequestHandler;
    private final HttpRequestHandler backendRequestHandler;
    private final HttpRequestHandler uiRequestHandler;

    @Inject
    public HttpChannelHandler(@Named("content") HttpRequestHandler contentRequestHandler, @Named("backend") HttpRequestHandler backendRequestHandler,
            @Named("ui") HttpRequestHandler uiRequestHandler) {
        this.contentRequestHandler = contentRequestHandler;
        this.backendRequestHandler = backendRequestHandler;
        this.uiRequestHandler = uiRequestHandler;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug("[START] messageReceived url:{}", request.getUri());
            for (Entry<String, String> entry : request.headers()) {
                logger.debug("Request header: {} ==> {}", entry.getKey(), entry.getValue());
            }

            if (request.getMethod().equals(HttpMethod.POST) && request.data().isReadable()) {
                QueryStringDecoder queryStringDecoder = new QueryStringDecoder("/?" + request.data().toString(Charset.forName("utf-8")));
                Map<String, List<String>> params = queryStringDecoder.parameters();
                if (params != null) {
                    for (Entry<String, List<String>> entry : params.entrySet()) {
                        logger.debug("Post parameter: {} ==> {}", entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        String requestPath = new QueryStringDecoder(request.getUri()).path();
        try {
            // Dispatch request to proper handler
            if (contentRequestHandler.canProcess(requestPath, request.getMethod())) contentRequestHandler.processRequest(request, ctx.channel());
            else if (backendRequestHandler.canProcess(requestPath, request.getMethod())) backendRequestHandler.processRequest(request, ctx.channel());
            else if (uiRequestHandler.canProcess(requestPath, request.getMethod())) uiRequestHandler.processRequest(request, ctx.channel());
            else sendError(ctx, HttpResponseStatus.BAD_REQUEST);

        } catch (HttpRequestException ex) {
            sendError(ctx, ex.getStatus());
        }

        if (logger.isDebugEnabled()) logger.debug("[END] messageReceived");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof TooLongFrameException) {
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("exceptionCaught: {} : {}", cause.getClass().toString(), cause.getMessage());
        if (ctx.channel().isActive()) sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendError(ChannelHandlerContext context, HttpResponseStatus status) {
        // Build error response
        ByteBuf buffer = Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buffer);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");

        if (logger.isDebugEnabled()) logger.debug("sendError: {}", status.toString());

        // Close the connection as soon as the error message is sent.
        context.channel().write(response).addListener(ChannelFutureListener.CLOSE);
    }

}