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
package net.holmes.core;

import javax.inject.Inject;
import javax.inject.Named;

import net.holmes.core.util.inject.Loggable;

import org.slf4j.Logger;

/**
 * Holmes server main class
 */
@Loggable
public final class HolmesServer implements Server {
    private Logger logger;

    private final Server httpServer;
    private final Server upnpServer;
    private final Server systray;

    @Inject
    public HolmesServer(@Named("http") Server httpServer, @Named("upnp") Server upnpServer, @Named("systray") Server systray) {
        this.httpServer = httpServer;
        this.upnpServer = upnpServer;
        this.systray = systray;
    }

    @Override
    public void start() {
        if (logger.isInfoEnabled()) logger.info("Starting Holmes server");

        // Start Holmes server
        httpServer.start();
        upnpServer.start();
        systray.start();

        if (logger.isInfoEnabled()) logger.info("Holmes server started");
    }

    @Override
    public void stop() {
        if (logger.isInfoEnabled()) logger.info("Stopping Holmes server");

        // Stop Holmes server
        systray.stop();
        upnpServer.stop();
        httpServer.stop();

        if (logger.isInfoEnabled()) logger.info("Holmes server stopped");
    }
}
