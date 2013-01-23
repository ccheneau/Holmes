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

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import net.holmes.core.Server;
import net.holmes.core.configuration.Configuration;
import net.holmes.core.util.inject.Loggable;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;

import com.sun.jersey.spi.container.WebApplication;

/**
 * HTTP server main class  
 */
@Loggable
public final class HttpServer implements Server {
    private Logger logger;

    public static final String HTTP_SERVER_NAME = "Holmes HTTP server";

    private ServerBootstrap bootstrap = null;
    private ExecutorService executor = null;
    private final ChannelPipelineFactory pipelineFactory;
    private final Configuration configuration;
    private final WebApplication webApplication;

    @Inject
    public HttpServer(ChannelPipelineFactory pipelineFactory, WebApplication webApplication, Configuration configuration) {
        this.pipelineFactory = pipelineFactory;
        this.configuration = configuration;
        this.webApplication = webApplication;
    }

    @Override
    public void start() {
        if (logger.isInfoEnabled()) logger.info("Starting HTTP server");

        InetSocketAddress bindAddress = new InetSocketAddress(configuration.getHttpServerPort());

        // Configure the server.
        executor = Executors.newCachedThreadPool();
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(executor, executor));

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(pipelineFactory);

        // Bind and start server to accept incoming connections.
        bootstrap.bind(bindAddress);

        if (logger.isInfoEnabled()) logger.info("HTTP server bound on " + bindAddress);
    }

    @Override
    public void stop() {
        if (logger.isInfoEnabled()) logger.info("Stopping HTTP server");

        // Stop the server
        if (bootstrap != null) bootstrap.shutdown();
        if (executor != null) executor.shutdown();
        webApplication.destroy();

        if (logger.isInfoEnabled()) logger.info("HTTP server stopped");
    }
}
