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

import com.google.inject.Injector;
import com.sun.jersey.spi.container.WebApplication;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import net.holmes.common.Service;
import net.holmes.common.configuration.Configuration;
import net.holmes.core.inject.InjectLogger;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.net.InetSocketAddress;

/**
 * HTTP server main class.
 */
public final class HttpServer implements Service {
    public static final String HTTP_SERVER_NAME = "Holmes HTTP server";
    private static final int MAX_CONTENT_LENGTH = 65536;
    private final Injector injector;
    private final Configuration configuration;
    private final WebApplication webApplication;
    private final EventLoopGroup eventLoopGroup;
    @InjectLogger
    private Logger logger;

    /**
     * Instantiates a new http server.
     *
     * @param injector       injector
     * @param webApplication web application
     * @param configuration  configuration
     */
    @Inject
    public HttpServer(final Injector injector, final WebApplication webApplication, final Configuration configuration) {
        this.injector = injector;
        this.configuration = configuration;
        this.webApplication = webApplication;
        this.eventLoopGroup = new NioEventLoopGroup();
    }

    @Override
    public void start() {
        logger.info("Starting HTTP server");

        InetSocketAddress bindAddress = new InetSocketAddress(configuration.getHttpServerPort());

        // Configure the server.
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(eventLoopGroup) //
                .channel(NioServerSocketChannel.class) //
                .childOption(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT) //
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(final SocketChannel channel) {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast("decoder", new HttpRequestDecoder()) //
                                .addLast("aggregator", new HttpObjectAggregator(MAX_CONTENT_LENGTH))//
                                .addLast("encoder", new HttpResponseEncoder())//
                                .addLast("chunkedWriter", new ChunkedWriteHandler())//
                                        // Add HTTP request handler
                                .addLast("httpChannelHandler", injector.getInstance(ChannelInboundMessageHandler.class));
                    }
                });

        // Bind and start server to accept incoming connections.
        try {
            bootstrap.bind(bindAddress).sync();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }

        logger.info("HTTP server bound on " + bindAddress);
    }

    @Override
    public void stop() {
        logger.info("Stopping HTTP server");

        // Stop the server
        eventLoopGroup.shutdownGracefully();
        webApplication.destroy();

        logger.info("HTTP server stopped");
    }
}
