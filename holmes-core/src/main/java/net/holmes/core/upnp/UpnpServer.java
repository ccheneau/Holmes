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
package net.holmes.core.upnp;

import javax.inject.Inject;

import net.holmes.core.Server;
import net.holmes.core.configuration.Configuration;
import net.holmes.core.configuration.Parameter;
import net.holmes.core.util.inject.Loggable;

import org.fourthline.cling.UpnpService;
import org.slf4j.Logger;

import com.google.inject.Injector;

/**
 * UPnP server main class
 */
@Loggable
public final class UpnpServer implements Server {
    private Logger logger;

    private UpnpService upnpService = null;

    private final Injector injector;
    private final Configuration configuration;

    @Inject
    public UpnpServer(Injector injector, Configuration configuration) {
        this.injector = injector;
        this.configuration = configuration;
    }

    @Override
    public void start() {
        if (configuration.getParameter(Parameter.ENABLE_UPNP)) {
            if (logger.isInfoEnabled()) logger.info("Starting UPnP server");
            upnpService = injector.getInstance(UpnpService.class);
            if (logger.isInfoEnabled()) logger.info("UPnP server started");
        } else {
            if (logger.isInfoEnabled()) logger.info("UPnP server is disabled");
        }
    }

    @Override
    public void stop() {
        if (upnpService != null) {
            if (logger.isInfoEnabled()) logger.info("Stopping UPnP server");
            upnpService.shutdown();
            if (logger.isInfoEnabled()) logger.info("UPnP server stopped");
        }
    }
}
