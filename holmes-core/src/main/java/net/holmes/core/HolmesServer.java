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
package net.holmes.core;

import net.holmes.core.util.LogUtil;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

public final class HolmesServer implements IServer {
    @Inject
    @Named("http")
    private IServer httpServer;

    @Inject
    @Named("upnp")
    private IServer upnpServer;

    public HolmesServer() {
    }

    /* (non-Javadoc)
     * @see net.holmes.core.IServer#start()
     */
    @Override
    public void start() {
        // Start Holmes server
        httpServer.start();
        upnpServer.start();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.IServer#stop()
     */
    @Override
    public void stop() {
        // Stop Holmes server
        httpServer.stop();
        upnpServer.stop();
    }

    public static void main(String[] args) {
        // Load log configuration
        LogUtil.loadConfig();

        // Create Guice injector
        Injector injector = Guice.createInjector(new HolmesServerModule());

        // Start Holmes server
        IServer holmesServer = injector.getInstance(HolmesServer.class);
        holmesServer.start();

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(holmesServer));
    }

    private static class ShutdownHook extends Thread {
        IServer holmesServer;

        public ShutdownHook(IServer holmesServer) {
            this.holmesServer = holmesServer;
        }

        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            holmesServer.stop();
        }
    }
}
