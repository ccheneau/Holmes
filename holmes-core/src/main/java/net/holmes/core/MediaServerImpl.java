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

import net.holmes.core.common.IServer;
import net.holmes.core.service.IMediaService;
import net.holmes.core.util.LogUtil;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

/**
 * The Class MediaServerImpl.
 */
public final class MediaServerImpl implements IServer
{

    /** The http server. */
    @Inject
    @Named("http")
    private IServer httpServer;

    /** The upnp server. */
    @Inject
    @Named("upnp")
    private IServer upnpServer;

    /** The media service. */
    @Inject
    private IMediaService mediaService;

    /**
     * Instantiates a new media server.
     */
    public MediaServerImpl()
    {
    }

    /* (non-Javadoc)
     * @see net.holmes.core.common.IServer#init()
     */
    public void init()
    {
        // Scan content nodes
        mediaService.scanAll();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.common.IServer#start()
     */
    public void start()
    {
        httpServer.start();
        upnpServer.start();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.common.IServer#stop()
     */
    public void stop()
    {
        httpServer.stop();
        upnpServer.stop();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.common.IServer#status()
     */
    @Override
    public boolean status()
    {
        return httpServer.status() && upnpServer.status();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.common.IServer#restart()
     */
    @Override
    public void restart()
    {
        upnpServer.restart();
        httpServer.restart();
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args)
    {
        LogUtil.loadConfig();

        Injector injector = Guice.createInjector(new MediaServerModule());

        IServer mediaServer = injector.getInstance(MediaServerImpl.class);
        mediaServer.init();
        mediaServer.start();

        Runtime.getRuntime().addShutdownHook(new ShutDownHook(mediaServer));
    }

    /**
     * The Class ShutDownHook.
     */
    private static class ShutDownHook extends Thread
    {

        /** The media server. */
        IServer mediaServer;

        /**
         * Instantiates a new shut down hook.
         *
         * @param mediaServer the media server
         */
        public ShutDownHook(IServer mediaServer)
        {
            this.mediaServer = mediaServer;
        }

        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        public void run()
        {
            mediaServer.stop();
        }
    }
}
