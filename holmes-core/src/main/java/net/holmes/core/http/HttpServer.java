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
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipelineFactory;
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

    private Channel channel = null;
    private ServerBootstrap bootstrap = null;

    @Inject
    private ChannelPipelineFactory pipelineFactory;

    @Inject
    private IConfiguration configuration;

    public HttpServer() {
    }

    /* (non-Javadoc)
     * @see net.holmes.core.IServer#start()
     */
    @Override
    public void start() {
        logger.info("Starting HTTP server");

        int port = configuration.getConfig().getHttpServerPort();
        InetSocketAddress bindAddress = new InetSocketAddress(port);

        // Configure the server.
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(pipelineFactory);

        // Bind and start to accept incoming connections.
        channel = bootstrap.bind(bindAddress);

        logger.info("HTTP server bound on " + bindAddress);
    }

    /* (non-Javadoc)
     * @see net.holmes.core.IServer#stop()
     */
    @Override
    public void stop() {
        logger.info("Stopping HTTP server");

        ChannelFuture cf = channel.getCloseFuture();
        cf.addListener(new ServerChannelFutureListener());

        channel.close();
        cf.awaitUninterruptibly();
        bootstrap.getFactory().releaseExternalResources();

        logger.info("HTTP server stopped");
    }

    /**
     * The listener interface for receiving serverChannelFuture events.
     * The class that is interested in processing a serverChannelFuture
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addServerChannelFutureListener<code> method. When
     * the serverChannelFuture event occurs, that object's appropriate
     * method is invoked.
     *
     * @see ServerChannelFutureEvent
     */
    private class ServerChannelFutureListener implements ChannelFutureListener {

        /* (non-Javadoc)
         * @see org.jboss.netty.channel.ChannelFutureListener#operationComplete(org.jboss.netty.channel.ChannelFuture)
         */
        @Override
        public void operationComplete(ChannelFuture arg0) throws Exception {
            logger.info("Http server stop complete");
        }
    }
}
