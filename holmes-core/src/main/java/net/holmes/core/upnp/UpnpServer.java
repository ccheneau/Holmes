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
package net.holmes.core.upnp;

import java.io.IOException;

import net.holmes.core.IServer;
import net.holmes.core.configuration.IConfiguration;
import net.holmes.core.media.IMediaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.google.inject.Inject;

public final class UpnpServer implements IServer {
    private static Logger logger = LoggerFactory.getLogger(UpnpServer.class);

    private UpnpService upnpService = null;

    @Inject
    private IMediaService mediaService;

    @Inject
    private IConfiguration configuration;

    public UpnpServer() {
    }

    /* (non-Javadoc)
     * @see net.holmes.core.IServer#start()
     */
    @Override
    public void start() {
        logger.info("Starting Upnp server");
        try {
            upnpService = new UpnpServiceImpl();

            // Add the bound local device to the registry
            LocalDevice localDevice = createDevice();
            upnpService.getRegistry().addDevice(localDevice);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /* (non-Javadoc)
     * @see net.holmes.core.IServer#stop()
     */
    @Override
    public void stop() {
        logger.info("Stopping UPnP server");
        if (upnpService != null) {
            upnpService.shutdown();
        }
        logger.info("UPnP server stopped");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private LocalDevice createDevice() throws ValidationException, LocalServiceBindingException, IOException {
        // Device identity
        DeviceIdentity identity = new DeviceIdentity(UDN.uniqueSystemIdentifier("Holmes UPnP Server"));

        // Device type
        DeviceType type = new UDADeviceType("MediaServer", 1);

        // Device name
        String serverName = configuration.getConfig().getUpnpServerName();
        DeviceDetails details = new DeviceDetails(serverName);

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
