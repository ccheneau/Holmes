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
package net.holmes.core.http;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class HttpServerHandler.
 */
public final class HttpServerHandler extends SimpleChannelUpstreamHandler
{

    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

    /** The http content handler. */
    private IHttpRequestHandler httpContentHandler;

    /** The http site handler. */
    private IHttpRequestHandler httpSiteHandler;

    /** The http backend handler. */
    private IHttpRequestHandler httpBackendHandler;

    /**
     * Sets the http content handler.
     *
     * @param httpContentHandler the new http content handler
     */
    public void setHttpContentHandler(IHttpRequestHandler httpContentHandler)
    {
        this.httpContentHandler = httpContentHandler;
    }

    /**
     * Sets the http site handler.
     *
     * @param httpSiteHandler the new http site handler
     */
    public void setHttpSiteHandler(IHttpRequestHandler httpSiteHandler)
    {
        this.httpSiteHandler = httpSiteHandler;
    }

    /**
     * Sets the http backend handler.
     *
     * @param httpBackendHandler the new http backend handler
     */
    public void setHttpBackendHandler(IHttpRequestHandler httpBackendHandler)
    {
        this.httpBackendHandler = httpBackendHandler;
    }

    /* (non-Javadoc)
     * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws IOException
    {
        if (logger.isDebugEnabled()) logger.debug("[START] messageReceived event:" + e);

        HttpRequest request = (HttpRequest) e.getMessage();
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());

        try
        {
            if (decoder.getPath().equals(HttpContentHandler.PATH))
            {
                httpContentHandler.processRequest(request, e.getChannel());
            }
            else if (decoder.getPath().startsWith(HttpBackendHandler.PATH))
            {
                httpBackendHandler.processRequest(request, e.getChannel());
            }
            else
            {
                httpSiteHandler.processRequest(request, e.getChannel());
            }
        }
        catch (HttpRequestException ex)
        {
            sendError(ctx, ex.getStatus());
        }

        if (logger.isDebugEnabled()) logger.debug("[END] messageReceived");

    }

    /* (non-Javadoc)
     * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#exceptionCaught(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ExceptionEvent)
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext context, ExceptionEvent event) throws Exception
    {
        Channel channel = event.getChannel();
        Throwable cause = event.getCause();
        if (cause instanceof TooLongFrameException)
        {
            sendError(context, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        if (channel.isConnected() && !event.getFuture().isSuccess())
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("isCancelled " + event.getFuture().isCancelled());
                logger.debug("isDone " + event.getFuture().isDone());
                logger.debug("isSuccess " + event.getFuture().isSuccess());
                logger.debug(cause.getMessage(), cause);
            }
            sendError(context, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Send error.
     *
     * @param context the context
     * @param status the status
     */
    private void sendError(ChannelHandlerContext context, HttpResponseStatus status)
    {
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ChannelBuffer buffer = ChannelBuffers.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8);
        response.setContent(buffer);

        if (logger.isDebugEnabled()) logger.debug("sendError: " + buffer);

        // Close the connection as soon as the error message is sent.
        context.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
    }
}