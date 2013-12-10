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

package net.holmes.core.transport.airplay;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import net.holmes.core.transport.airplay.model.AbstractCommand;
import net.holmes.core.transport.airplay.model.AirplayDevice;

import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * Asynchronous Airplay http client.
 */
public abstract class AsyncHttpClient {
    private static final int MAX_REQUEST_LENGTH = 1048576;
    final AbstractCommand command;

    /**
     * Instantiates a new asynchronous http client.
     *
     * @param command Airplay command
     */
    public AsyncHttpClient(final AbstractCommand command) {
        this.command = command;
    }

    /**
     * Run Http request.
     *
     * @param device Airplay device
     */
    public void run(final AirplayDevice device) {
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();

                            pipeline.addLast("log", new LoggingHandler(LogLevel.TRACE));
                            pipeline.addLast("codec", new HttpClientCodec());
                            pipeline.addLast("aggregator", new HttpObjectAggregator(MAX_REQUEST_LENGTH));
                            pipeline.addLast("handler", new AsyncHttpClientHandler());
                        }
                    });

            // Make the connection attempt.
            Channel channel = bootstrap.connect(device.getHostAddress(), device.getPort()).sync().channel();

            // Send the HTTP request.
            channel.writeAndFlush(command.getHttpRequest(device.getHostAddress(), device.getPort()));

            // Wait for the server to close the connection.
            channel.closeFuture().sync();

        } catch (InterruptedException e) {
            onFailure(e);
        } finally {
            group.shutdownGracefully();
        }
    }

    /**
     * Success callback.
     *
     * @param contentParameters content parameters map
     */
    public abstract void onSuccess(Map<String, String> contentParameters);

    /**
     * Failure callback.
     *
     * @param throwable exception
     */
    public abstract void onFailure(Throwable throwable);

    /**
     * Asynchronous http client handler.
     */
    private class AsyncHttpClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
        private static final String CONTENT_TYPE_PARAMETERS = "text/parameters";

        @Override
        protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpResponse response) throws Exception {
            if (response.getStatus() == OK) {
                // Get content parameters if any
                Map<String, String> contentParameters = null;
                if (CONTENT_TYPE_PARAMETERS.equals(response.headers().get(CONTENT_TYPE))) {
                    contentParameters = Maps.newHashMap();
                    try (ByteBufInputStream in = new ByteBufInputStream(response.content())) {
                        String line;
                        while ((line = in.readLine()) != null) {
                            Iterable<String> it = Splitter.on(':').trimResults().split(line);
                            contentParameters.put(Iterables.getFirst(it, ""), Iterables.getLast(it));
                        }
                    }
                }
                onSuccess(contentParameters);
                return;
            }
            // Throw exception if response is not valid
            throw new Exception(response.toString());
        }

        @Override
        public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
            onFailure(cause);
            ctx.close();
        }
    }
}
