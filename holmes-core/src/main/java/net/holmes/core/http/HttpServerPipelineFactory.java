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

import net.holmes.core.http.request.IHttpRequestHandler;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public final class HttpServerPipelineFactory implements ChannelPipelineFactory {
    private static Logger logger = LoggerFactory.getLogger(HttpServerPipelineFactory.class);

    @Inject
    @Named("content")
    private IHttpRequestHandler contentRequestHandler;

    @Inject
    @Named("backend")
    private IHttpRequestHandler backendRequestHandler;

    @Inject
    @Named("site")
    private IHttpRequestHandler siteRequestHandler;

    /* (non-Javadoc)
    * @see org.jboss.netty.channel.ChannelPipelineFactory#getPipeline()
    */
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        if (logger.isDebugEnabled()) logger.debug("[START] getPipeline");

        // Create a default pipeline implementation.
        ChannelPipeline pipeline = Channels.pipeline();

        // Set default handlers
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());

        HttpServerHandler handler = new HttpServerHandler();

        // Set handler for streaming contents
        handler.setHttpContentHandler(contentRequestHandler);

        // Set handler for backend REST requests
        handler.setHttpBackendHandler(backendRequestHandler);

        // Set handler for admin site requests
        handler.setHttpSiteHandler(siteRequestHandler);

        // Add handler
        pipeline.addLast("handler", handler);

        if (logger.isDebugEnabled()) logger.debug("[END] getPipeline");
        return pipeline;
    }
}
