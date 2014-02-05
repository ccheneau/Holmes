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

package net.holmes.core.service.http;

import com.google.inject.Injector;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import net.holmes.core.business.configuration.Configuration;
import net.holmes.core.service.Service;
import net.holmes.core.service.http.file.HttpFileRequestDecoder;
import net.holmes.core.service.http.file.HttpFileRequestHandler;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.guice.ModuleProcessor;
import org.jboss.resteasy.plugins.server.netty.RequestDispatcher;
import org.jboss.resteasy.plugins.server.netty.RequestHandler;
import org.jboss.resteasy.plugins.server.netty.RestEasyHttpRequestDecoder;
import org.jboss.resteasy.plugins.server.netty.RestEasyHttpResponseEncoder;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.InetSocketAddress;

import static io.netty.buffer.UnpooledByteBufAllocator.DEFAULT;
import static io.netty.channel.ChannelOption.*;
import static net.holmes.core.business.configuration.Parameter.HTTP_SERVER_PORT;
import static org.jboss.resteasy.plugins.server.netty.RestEasyHttpRequestDecoder.Protocol.HTTP;

/**
 * HTTP service main class.
 */
public final class HttpServer implements Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);
    private static final int MAX_CONTENT_LENGTH = 65536;
    private static final int MAX_INITIAL_LINE_LENGTH = 4096;
    private static final int MAX_HEADER_SIZE = 8192;
    private static final int MAX_CHUNK_SIZE = 8192;
    private static final int BACKLOG = 128;
    private final Injector injector;
    private final Configuration configuration;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final ResteasyDeployment deployment;

    /**
     * Instantiates a new http service.
     *
     * @param injector      injector
     * @param configuration configuration
     */
    @Inject
    public HttpServer(final Injector injector, final Configuration configuration) {
        this.injector = injector;
        this.configuration = configuration;
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
        this.deployment = new ResteasyDeployment();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        LOGGER.info("Starting HTTP service");

        // Start RestEasy deployment
        deployment.start();

        // Create a RestEasy request dispatcher
        final RequestDispatcher dispatcher = new RequestDispatcher((SynchronousDispatcher) deployment.getDispatcher(), deployment.getProviderFactory(), null);

        // Configure the service.
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    protected void initChannel(final SocketChannel channel) {
                        ChannelPipeline pipeline = channel.pipeline();
                        // Add default handlers
                        pipeline.addLast("decoder", new HttpRequestDecoder(MAX_INITIAL_LINE_LENGTH, MAX_HEADER_SIZE, MAX_CHUNK_SIZE, false));
                        pipeline.addLast("aggregator", new HttpObjectAggregator(MAX_CONTENT_LENGTH));
                        pipeline.addLast("encoder", new HttpResponseEncoder());
                        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());

                        // Add HTTP file request handlers
                        pipeline.addLast("httpFileRequestDecoder", injector.getInstance(HttpFileRequestDecoder.class));
                        pipeline.addLast("httpFileRequestHandler", injector.getInstance(HttpFileRequestHandler.class));

                        // Add RestEasy handlers
                        pipeline.addLast("restEasyHttpRequestDecoder", new RestEasyHttpRequestDecoder(dispatcher.getDispatcher(), "", HTTP));
                        pipeline.addLast("restEasyHttpResponseEncoder", new RestEasyHttpResponseEncoder(dispatcher));
                        pipeline.addLast("restEasyRequestHandler", new RequestHandler(dispatcher));
                    }
                })
                .option(SO_BACKLOG, BACKLOG)
                .childOption(ALLOCATOR, DEFAULT)
                .childOption(SO_KEEPALIVE, true);

        // Register backend JAX-RS handlers declared in Guice injector
        ModuleProcessor processor = new ModuleProcessor(deployment.getRegistry(), deployment.getProviderFactory());
        processor.processInjector(injector);

        // Bind and start service to accept incoming connections.
        InetSocketAddress bindAddress = new InetSocketAddress(configuration.getIntParameter(HTTP_SERVER_PORT));
        bootstrap.bind(bindAddress).syncUninterruptibly();

        LOGGER.info("HTTP service bound on {}", bindAddress);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        LOGGER.info("Stopping HTTP service");

        // Stop the service
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        deployment.stop();

        LOGGER.info("HTTP service stopped");
    }
}
