/**
* Copyright (C) 2012  Cedric Cheneau
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

import java.io.IOException;

import javax.inject.Inject;

import net.holmes.core.Server;
import net.holmes.core.configuration.Configuration;
import net.holmes.core.configuration.Parameter;
import net.holmes.core.media.MediaService;
import net.holmes.core.util.inject.Loggable;

import org.slf4j.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.binding.LocalServiceBindingException;
import org.teleal.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.teleal.cling.model.DefaultServiceManager;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.DeviceDetails;
import org.teleal.cling.model.meta.DeviceIdentity;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.support.connectionmanager.ConnectionManagerService;

/**
 * UPnP server main class
 */
@Loggable
public final class UpnpServer implements Server {
    private Logger logger;

    private UpnpService upnpService = null;

    private final MediaService mediaService;
    private final Configuration configuration;

    @Inject
    public UpnpServer(MediaService mediaService, Configuration configuration) {
        this.mediaService = mediaService;
        this.configuration = configuration;
    }

    @Override
    public void start() {
        try {
            if (configuration.getParameter(Parameter.ENABLE_UPNP)) {
                if (logger.isInfoEnabled()) logger.info("Starting UPnP server");
                upnpService = new UpnpServiceImpl();

                // Add the bound local device to the registry
                LocalDevice localDevice = createDevice();
                upnpService.getRegistry().addDevice(localDevice);

                if (logger.isInfoEnabled()) logger.info("UPnP server started");
            } else {
                if (logger.isInfoEnabled()) logger.info("UPnP server is disabled");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private LocalDevice createDevice() throws ValidationException, LocalServiceBindingException, IOException {
        // Device identity
        DeviceIdentity identity = new DeviceIdentity(UDN.uniqueSystemIdentifier("Holmes UPnP Server"));

        // Device type
        DeviceType type = new UDADeviceType("MediaServer", 1);

        // Device name
        DeviceDetails details = new DeviceDetails(configuration.getUpnpServerName());

        // Content directory service
        LocalService<ContentDirectoryService> contentDirectoryService = new AnnotationLocalServiceBinder().read(ContentDirectoryService.class);
        DefaultServiceManager serviceManager = new DefaultServiceManager(contentDirectoryService, ContentDirectoryService.class);
        contentDirectoryService.setManager(serviceManager);

        ContentDirectoryService contentDirectory = (ContentDirectoryService) serviceManager.getImplementation();
        contentDirectory.setConfiguration(configuration);
        contentDirectory.setMediaService(mediaService);

        // Connection service
        LocalService<ConnectionManagerService> connectionService = new AnnotationLocalServiceBinder().read(ConnectionManagerService.class);
        connectionService.setManager(new DefaultServiceManager<ConnectionManagerService>(connectionService, ConnectionManagerService.class));

        // Create local device
        LocalService[] services = new LocalService[2];
        services[0] = contentDirectoryService;
        services[1] = connectionService;
        return new LocalDevice(identity, type, details, services);
    }
}
