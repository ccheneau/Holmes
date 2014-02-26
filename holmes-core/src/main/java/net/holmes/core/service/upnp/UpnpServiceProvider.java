/*
 * Copyright (C) 2012-2014  Cedric Cheneau
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

package net.holmes.core.service.upnp;

import com.google.inject.Injector;
import net.holmes.core.business.configuration.ConfigurationDao;
import net.holmes.core.service.upnp.directory.ContentDirectoryService;
import org.fourthline.cling.DefaultUpnpServiceConfiguration;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import static net.holmes.core.business.configuration.Parameter.UPNP_SERVER_NAME;
import static net.holmes.core.business.configuration.Parameter.UPNP_SERVICE_PORT;
import static net.holmes.core.common.UpnpUtils.*;

/**
 * Guice provider for UPnP service.
 */
public class UpnpServiceProvider implements Provider<UpnpService> {
    private final Injector injector;
    private final ConfigurationDao configurationDao;
    private final String version;

    /**
     * Instantiates a new upnp service provider.
     *
     * @param injector         Guice injector
     * @param configurationDao configuration dao
     * @param version          Holmes version
     */
    @Inject
    public UpnpServiceProvider(final Injector injector, final ConfigurationDao configurationDao, @Named("version") final String version) {
        this.injector = injector;
        this.configurationDao = configurationDao;
        this.version = version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public UpnpService get() {
        // Create Upnp service
        UpnpServiceConfiguration upnpConfiguration = new DefaultUpnpServiceConfiguration(configurationDao.getIntParameter(UPNP_SERVICE_PORT));
        UpnpService upnpService = new UpnpServiceImpl(upnpConfiguration);

        // Content directory service
        LocalService<ContentDirectoryService> contentDirectoryService = new AnnotationLocalServiceBinder().read(ContentDirectoryService.class);
        contentDirectoryService.setManager(new DefaultServiceManager<>(contentDirectoryService, ContentDirectoryService.class));
        injector.injectMembers(contentDirectoryService.getManager().getImplementation());

        // Device details
        DeviceDetails deviceDetails = getDeviceDetails(configurationDao.getParameter(UPNP_SERVER_NAME), version);

        try {
            // Create local services
            LocalService<?>[] localServices = new LocalService[]{contentDirectoryService, getConnectionManagerService()};

            // Add local device to UPnP service registry
            upnpService.getRegistry().addDevice(new LocalDevice(DEVICE_IDENTITY, DEVICE_TYPE, deviceDetails, getIcons(), localServices));
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
        return upnpService;
    }
}
