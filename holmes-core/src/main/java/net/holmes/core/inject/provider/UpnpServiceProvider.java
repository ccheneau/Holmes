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
import net.holmes.core.common.ResourceLoader;
import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.upnp.ContentDirectoryService;
import org.fourthline.cling.DefaultUpnpServiceConfiguration;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.DLNADoc;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.support.connectionmanager.ConnectionManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

import static net.holmes.core.common.Constants.*;
import static net.holmes.core.common.ResourceLoader.ResourceDir.UPNP;
import static net.holmes.core.common.configuration.Parameter.UPNP_SERVICE_PORT;

/**
 * Guice provider for UPnP service.
 */
public class UpnpServiceProvider implements Provider<UpnpService> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpnpServiceProvider.class);
    private static final int LARGE_ICON_SIZE = 120;
    private static final int SMALL_ICON_SIZE = 32;
    private static final int ICON_DEPTH = 8;
    private static final String ICON_MIME_TYPE = "image/png";
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
        UpnpServiceConfiguration upnpConfiguration = new DefaultUpnpServiceConfiguration(configuration.getIntParameter(UPNP_SERVICE_PORT));
        UpnpService upnpService = new UpnpServiceImpl(upnpConfiguration);

        // Device identity
        DeviceIdentity identity = new DeviceIdentity(UDN.uniqueSystemIdentifier(HOLMES_UPNP_SERVER_NAME.toString()));

        // Device type
        DeviceType type = DeviceType.valueOf("urn:schemas-upnp-org:device:MediaServer:1");

        // Device details
        ModelDetails modelDetails = new ModelDetails(HOLMES_UPNP_SHORT_NAME.toString(), HOLMES_UPNP_DESCRIPTION.toString(), HOLMES_UPNP_MODEL_NUMBER.toString(), HOLMES_SITE_URL.toString());
        ManufacturerDetails manufacturerDetails = new ManufacturerDetails(HOLMES_UPNP_SHORT_NAME.toString(), HOLMES_SITE_URL.toString());
        DLNADoc dD1 = new DLNADoc("DMS", "1.50");
        DLNADoc dD2 = new DLNADoc("M-DMS", "1.50");
        DLNADoc[] dlnaDocs = new DLNADoc[]{dD1, dD2};
        DeviceDetails details = new DeviceDetails(configuration.getUpnpServerName(), manufacturerDetails, modelDetails, dlnaDocs, null);

        // Content directory service
        LocalService<ContentDirectoryService> contentDirectoryService = new AnnotationLocalServiceBinder().read(ContentDirectoryService.class);
        contentDirectoryService.setManager(new DefaultServiceManager<>(contentDirectoryService, ContentDirectoryService.class));
        injector.injectMembers(contentDirectoryService.getManager().getImplementation());

        // Connection manager service
        LocalService<ConnectionManagerService> connectionManagerService = new AnnotationLocalServiceBinder().read(ConnectionManagerService.class);
        connectionManagerService.setManager(new DefaultServiceManager<>(connectionManagerService, ConnectionManagerService.class));

        Icon[] icons = null;
        try {
            // Set icons
            byte[] largeIconData = ResourceLoader.getData(UPNP, "icon-256.png");
            byte[] smallIconData = ResourceLoader.getData(UPNP, "icon-32.png");
            Icon largeIcon = new Icon(ICON_MIME_TYPE, LARGE_ICON_SIZE, LARGE_ICON_SIZE, ICON_DEPTH, "upnp-icon-256.png", largeIconData);
            Icon smallIcon = new Icon(ICON_MIME_TYPE, SMALL_ICON_SIZE, SMALL_ICON_SIZE, ICON_DEPTH, "upnp-icon-32.png", smallIconData);
            icons = new Icon[]{largeIcon, smallIcon};
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        try {
            // Create local device
            LocalService<?>[] services = new LocalService[2];
            services[0] = contentDirectoryService;
            services[1] = connectionManagerService;
            upnpService.getRegistry().addDevice(new LocalDevice(identity, type, details, icons, services));
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
        return upnpService;
    }
}
