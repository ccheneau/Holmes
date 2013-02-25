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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

import javax.inject.Inject;

import net.holmes.core.Server;
import net.holmes.core.configuration.Configuration;
import net.holmes.core.util.inject.Loggable;

import org.slf4j.Logger;

import com.google.inject.Injector;
import com.sun.jersey.spi.container.WebApplication;

/**
 * HTTP server main class  
 */
@Loggable
public final class HttpServer implements Server {
    private Logger logger;

    public static final String HTTP_SERVER_NAME = "Holmes HTTP server";

    private final ServerBootstrap bootstrap;
    private final Injector injector;
    private final Configuration configuration;
    private final WebApplication webApplication;

    @Inject
    public HttpServer(Injector injector, WebApplication webApplication, Configuration configuration) {
        this.injector = injector;
        this.configuration = configuration;
        this.webApplication = webApplication;
        this.bootstrap = new ServerBootstrap();
    }

    @Override
    public void start() {
        logger.info("Starting HTTP server");

        InetSocketAddress bindAddress = new InetSocketAddress(configuration.getHttpServerPort());

        // Configure the server.
        bootstrap.group(new NioEventLoopGroup()) //
                .channel(NioServerSocketChannel.class) //
                .childOption(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.HEAP_BY_DEFAULT) //
                .childHandler(injector.getInstance(ChannelInitializer.class));

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
        bootstrap.shutdown();
        webApplication.destroy();

        logger.info("HTTP server stopped");
    }
}
