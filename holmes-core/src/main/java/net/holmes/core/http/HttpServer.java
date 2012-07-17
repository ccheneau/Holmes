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

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import net.holmes.core.IServer;
import net.holmes.core.configuration.IConfiguration;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * HTTP server main class  
 */
public final class HttpServer implements IServer {
    private static Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public static final String HTTP_SERVER_NAME = "Holmes http server";

    private ChannelGroup allChannels = null;
    private ServerBootstrap bootstrap = null;

    @Inject
    private IChannelPipelineFactory pipelineFactory;

    @Inject
    private IConfiguration configuration;

    public HttpServer() {
        // Init channel group
        allChannels = new DefaultChannelGroup(HttpServer.class.getName());
    }

    /* (non-Javadoc)
     * @see net.holmes.core.IServer#start()
     */
    @Override
    public void start() {
        logger.info("Starting HTTP server");

        InetSocketAddress bindAddress = new InetSocketAddress(configuration.getHttpServerPort());

        // Configure the server.
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

        // Set up the event pipeline factory.
        pipelineFactory.setChannelGroup(allChannels);
        bootstrap.setPipelineFactory(pipelineFactory);

        // Bind and start to accept incoming connections.
        allChannels.add(bootstrap.bind(bindAddress));

        logger.info("HTTP server bound on " + bindAddress);
    }

    /* (non-Javadoc)
     * @see net.holmes.core.IServer#stop()
     */
    @Override
    public void stop() {
        logger.info("Stopping HTTP server");

        allChannels.close();
        bootstrap.releaseExternalResources();

        logger.info("HTTP server stopped");
    }
}
