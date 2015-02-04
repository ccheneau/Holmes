/*
 * Copyright (C) 2012-2015  Cedric Cheneau
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
import net.holmes.core.business.configuration.ConfigurationManager;
import net.holmes.core.service.Service;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.guice.ModuleProcessor;
import org.jboss.resteasy.plugins.server.netty.RequestDispatcher;
import org.jboss.resteasy.plugins.server.netty.RequestHandler;
import org.jboss.resteasy.plugins.server.netty.RestEasyHttpRequestDecoder;
import org.jboss.resteasy.plugins.server.netty.RestEasyHttpResponseEncoder;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static io.netty.buffer.UnpooledByteBufAllocator.DEFAULT;
import static io.netty.channel.ChannelOption.*;
import static net.holmes.core.common.ConfigurationParameter.*;
import static org.jboss.resteasy.plugins.server.netty.RestEasyHttpRequestDecoder.Protocol.HTTP;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * HTTP service main class.
 */
@Singleton
public final class HttpService implements Service {
    private static final Logger LOGGER = getLogger(HttpService.class);
    private static final int MAX_CONTENT_LENGTH = 65536;
    private static final int MAX_INITIAL_LINE_LENGTH = 4096;
    private static final int MAX_HEADER_SIZE = 8192;
    private static final int MAX_CHUNK_SIZE = 8192;
    private static final int BACKLOG = 128;
    private static final String RESTEASY_MAPPING_PREFIX = "/";

    private final Injector injector;
    private final ConfigurationManager configurationManager;
    private final EventLoopGroup nettyBossGroup;
    private final EventLoopGroup nettyWorkerGroup;
    private final ResteasyDeployment resteasy;

    /**
     * Instantiates a new HTTP service.
     *
     * @param injector             injector
     * @param configurationManager configuration manager
     */
    @Inject
    public HttpService(final Injector injector, final ConfigurationManager configurationManager) {
        this.injector = injector;
        this.configurationManager = configurationManager;
        this.nettyBossGroup = new NioEventLoopGroup(configurationManager.getParameter(HTTP_SERVER_BOSS_THREADS));
        this.nettyWorkerGroup = new NioEventLoopGroup(configurationManager.getParameter(HTTP_SERVER_WORKER_THREADS));
        this.resteasy = new ResteasyDeployment();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        LOGGER.info("Starting HTTP service");

        // Start RestEasy deployment
        resteasy.start();

        // Create a RestEasy request dispatcher
        final RequestDispatcher resteasyDispatcher = new RequestDispatcher((SynchronousDispatcher) resteasy.getDispatcher(), resteasy.getProviderFactory(), null);

        // Configure the service.
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(nettyBossGroup, nettyWorkerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(final SocketChannel channel) {
                        ChannelPipeline pipeline = channel.pipeline();
                        // Add default handlers
                        pipeline.addLast("decoder", new HttpRequestDecoder(MAX_INITIAL_LINE_LENGTH, MAX_HEADER_SIZE, MAX_CHUNK_SIZE, false))
                                .addLast("aggregator", new HttpObjectAggregator(MAX_CONTENT_LENGTH))
                                .addLast("encoder", new HttpResponseEncoder())
                                .addLast("chunkedWriter", new ChunkedWriteHandler());

                        // Add HTTP file request handlers
                        pipeline.addLast("httpFileRequestDecoder", injector.getInstance(HttpFileRequestDecoder.class))
                                .addLast("httpFileRequestHandler", injector.getInstance(HttpFileRequestHandler.class));

                        // Add RestEasy handlers
                        pipeline.addLast("restEasyHttpRequestDecoder", new RestEasyHttpRequestDecoder(resteasyDispatcher.getDispatcher(), RESTEASY_MAPPING_PREFIX, HTTP))
                                .addLast("restEasyHttpResponseEncoder", new RestEasyHttpResponseEncoder())
                                .addLast("restEasyRequestHandler", new RequestHandler(resteasyDispatcher));
                    }
                })
                .option(SO_BACKLOG, BACKLOG)
                .childOption(ALLOCATOR, DEFAULT)
                .childOption(SO_KEEPALIVE, true);

        // Register backend JAX-RS handlers (declared in Guice injector) to RestEasy
        ModuleProcessor guiceProcessor = new ModuleProcessor(resteasy.getRegistry(), resteasy.getProviderFactory());
        guiceProcessor.processInjector(injector);

        // Bind and start service to accept incoming connections
        SocketAddress boundAddress = new InetSocketAddress(configurationManager.getParameter(HTTP_SERVER_PORT));
        serverBootstrap.bind(boundAddress).syncUninterruptibly();

        LOGGER.info("HTTP service bound on {}", boundAddress);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        // Stop HTTP service
        LOGGER.info("Stopping HTTP service");

        // Stop Netty event executors
        nettyBossGroup.shutdownGracefully();
        nettyWorkerGroup.shutdownGracefully();

        // Stop RestEasy
        resteasy.stop();

        LOGGER.info("HTTP service stopped");
    }
}
