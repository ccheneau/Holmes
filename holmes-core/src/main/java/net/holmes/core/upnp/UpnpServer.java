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

package net.holmes.core.upnp;

import com.google.inject.Injector;
import net.holmes.core.common.Service;
import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.common.configuration.Parameter;
import org.fourthline.cling.UpnpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * UPnP server main class.
 */
public final class UpnpServer implements Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpnpServer.class);
    private final Injector injector;
    private final Configuration configuration;
    private UpnpService upnpService = null;

    /**
     * Instantiates a new UPnP server.
     *
     * @param injector      Guice injector
     * @param configuration configuration
     */
    @Inject
    public UpnpServer(final Injector injector, final Configuration configuration) {
        this.injector = injector;
        this.configuration = configuration;
    }

    @Override
    public void start() {
        if (configuration.getBooleanParameter(Parameter.ENABLE_UPNP)) {
            LOGGER.info("Starting UPnP server");
            upnpService = injector.getInstance(UpnpService.class);
            LOGGER.info("UPnP server started");
        } else LOGGER.info("UPnP server is disabled");
    }

    @Override
    public void stop() {
        if (upnpService != null) {
            LOGGER.info("Stopping UPnP server");
            upnpService.shutdown();
            LOGGER.info("UPnP server stopped");
        }
    }
}
