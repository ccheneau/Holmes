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

package net.holmes.core.inject.provider;

import com.google.inject.Injector;
import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.upnp.ContentDirectoryService;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.support.connectionmanager.ConnectionManagerService;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Guice provider for UPnP service.
 */
public class UpnpServiceProvider implements Provider<UpnpService> {

    private final Injector injector;
    private final Configuration configuration;

    /**
     * Instantiates a new upnp service provider.
     *
     * @param injector      Guice injector
     * @param configuration configuration
     */
    @Inject
    public UpnpServiceProvider(final Injector injector, final Configuration configuration) {
        this.injector = injector;
        this.configuration = configuration;
    }

    @SuppressWarnings("unchecked")
    @Override
    public UpnpService get() {

        // Create Upnp service
        UpnpService upnpService = new UpnpServiceImpl();

        // Device identity
        DeviceIdentity identity = new DeviceIdentity(UDN.uniqueSystemIdentifier("Holmes UPnP Server"));

        // Device type
        DeviceType type = new UDADeviceType("MediaServer", 1);

        // Device name
        DeviceDetails details = new DeviceDetails(configuration.getUpnpServerName());

        // Content directory service
        LocalService<ContentDirectoryService> contentDirectoryService = new AnnotationLocalServiceBinder().read(ContentDirectoryService.class);
        contentDirectoryService.setManager(new DefaultServiceManager<>(contentDirectoryService, ContentDirectoryService.class));
        injector.injectMembers(contentDirectoryService.getManager().getImplementation());

        // Connection service
        LocalService<ConnectionManagerService> connectionManagerService = new AnnotationLocalServiceBinder().read(ConnectionManagerService.class);
        connectionManagerService.setManager(new DefaultServiceManager<>(connectionManagerService, ConnectionManagerService.class));

        // Create local device
        try {
            LocalService<?>[] services = new LocalService[2];
            services[0] = connectionManagerService;
            services[1] = contentDirectoryService;
            upnpService.getRegistry().addDevice(new LocalDevice(identity, type, details, services));
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
        return upnpService;
    }
}
