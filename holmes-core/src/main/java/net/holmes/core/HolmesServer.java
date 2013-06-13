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

package net.holmes.core;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;
import net.holmes.core.common.Service;
import net.holmes.core.inject.InjectLogger;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Holmes server main class.
 */
public final class HolmesServer implements Service {
    private final Service httpServer;
    private final Service upnpServer;
    private final Service systray;
    private final Service scheduler;
    @InjectLogger
    private Logger logger;

    /**
     * Instantiates a new holmes server.
     *
     * @param httpServer Http server
     * @param upnpServer UPnP server
     * @param systray    Systray
     * @param scheduler  Scheduler
     */
    @Inject
    public HolmesServer(@Named("http") final Service httpServer, @Named("upnp") final Service upnpServer, @Named("systray") final Service systray,
                        @Named("scheduler") final Service scheduler) {
        this.httpServer = httpServer;
        this.upnpServer = upnpServer;
        this.systray = systray;
        this.scheduler = scheduler;
    }

    @Override
    public void start() {
        logger.info("Starting Holmes server");

        // Start Holmes server
        httpServer.start();
        upnpServer.start();
        systray.start();
        scheduler.start();

        logger.info("Holmes server started");
    }

    @Override
    public void stop() {
        logger.info("Stopping Holmes server");

        // Stop Holmes server
        scheduler.stop();
        systray.stop();
        upnpServer.stop();
        httpServer.stop();

        logger.info("Holmes server stopped");
    }

    /**
     * Receive dead event from event bus.
     *
     * @param deadEvent dead event
     */
    @Subscribe
    public void handleDeadEvent(final DeadEvent deadEvent) {
        logger.warn("Event not handled: {}", deadEvent.getEvent().toString());
    }
}
