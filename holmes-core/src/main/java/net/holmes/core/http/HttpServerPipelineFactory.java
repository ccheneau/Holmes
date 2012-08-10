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
package net.holmes.core.http;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

public final class HttpServerPipelineFactory implements IChannelPipelineFactory {

    @Inject
    @Named("http")
    private ChannelHandler httpRequestHandler;

    private ChannelGroup channelGroup = null;

    /* (non-Javadoc)
     * @see org.jboss.netty.channel.ChannelPipelineFactory#getPipeline()
     */
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = Channels.pipeline();

        // Set default handlers
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());

        // Add event handler for channel registration to channel group
        pipeline.addLast("channelEvent", new ChannelEventHandler(this.channelGroup));

        // Add http request handler
        pipeline.addLast("httpRequestHandler", httpRequestHandler);
        return pipeline;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.http.IChannelPipelineFactory#setChannelGroup(org.jboss.netty.channel.group.ChannelGroup)
     */
    @Override
    public void setChannelGroup(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }
}
