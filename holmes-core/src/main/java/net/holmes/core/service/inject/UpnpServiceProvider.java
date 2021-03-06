/*
 * Copyright (C) 2012-2015  Cedric Cheneau
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

package net.holmes.core.service.inject;

import com.google.inject.Injector;
import net.holmes.core.business.configuration.ConfigurationManager;
import net.holmes.core.business.version.VersionManager;
import net.holmes.core.common.exception.HolmesRuntimeException;
import net.holmes.core.service.upnp.directory.ContentDirectoryService;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

import static net.holmes.core.business.streaming.upnp.UpnpUtils.*;
import static net.holmes.core.common.ConfigurationParameter.*;

/**
 * Guice provider for UPnP service.
 */
class UpnpServiceProvider implements Provider<UpnpService> {

    private final Injector injector;
    private final ConfigurationManager configurationManager;
    private final String version;

    /**
     * Instantiates a new UPnP service provider.
     *
     * @param injector             Guice injector
     * @param configurationManager configuration manager
     * @param versionManager       Holmes version manager
     */
    @Inject
    public UpnpServiceProvider(final Injector injector, final ConfigurationManager configurationManager, final VersionManager versionManager) {
        this.injector = injector;
        this.configurationManager = configurationManager;
        this.version = versionManager.getCurrentVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpnpService get() {
        // Upnp service
        UpnpService upnpService = buildUpnpService(configurationManager.getParameter(UPNP_SERVICE_PORT));

        // Device details
        DeviceDetails deviceDetails = buildDeviceDetails(configurationManager.getParameter(UPNP_SERVER_NAME), version);

        // Create local services
        LocalService<?>[] localServices = new LocalService[]{buildContentDirectoryService(), buildConnectionManagerService()};

        try {
            // Add local device to UPnP service registry
            upnpService.getRegistry().addDevice(new LocalDevice(DEVICE_IDENTITY, DEVICE_TYPE, deviceDetails, getIcons(), localServices));
        } catch (ValidationException | IOException e) {
            throw new HolmesRuntimeException(e);
        }
        return upnpService;
    }

    /**
     * Build a new content directory service.
     *
     * @return content directory service
     */
    @SuppressWarnings("unchecked")
    private LocalService<ContentDirectoryService> buildContentDirectoryService() {
        // Instantiates a new contentDirectoryService
        LocalService<ContentDirectoryService> contentDirectoryService = new AnnotationLocalServiceBinder().read(ContentDirectoryService.class);
        contentDirectoryService.setManager(new DefaultServiceManager<>(contentDirectoryService, ContentDirectoryService.class));

        // Inject members to contentDirectoryService instance
        injector.injectMembers(contentDirectoryService.getManager().getImplementation());
        return contentDirectoryService;
    }
}
