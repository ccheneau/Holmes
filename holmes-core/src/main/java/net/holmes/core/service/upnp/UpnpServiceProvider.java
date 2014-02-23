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
import net.holmes.core.business.upnp.ContentDirectoryService;
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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.io.IOException;

import static net.holmes.core.business.configuration.Parameter.UPNP_SERVER_NAME;
import static net.holmes.core.business.configuration.Parameter.UPNP_SERVICE_PORT;
import static net.holmes.core.common.Constants.*;
import static net.holmes.core.common.StaticResourceLoader.StaticResourceDir.UPNP;
import static net.holmes.core.common.StaticResourceLoader.getData;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Guice provider for UPnP service.
 */
public class UpnpServiceProvider implements Provider<UpnpService> {
    private static final Logger LOGGER = getLogger(UpnpServiceProvider.class);
    private static final int LARGE_ICON_SIZE = 120;
    private static final int SMALL_ICON_SIZE = 32;
    private static final int ICON_DEPTH = 8;
    private static final String ICON_MIME_TYPE = "image/png";
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
    @SuppressWarnings("unchecked")
    @Override
    public UpnpService get() {
        // Create Upnp service
        UpnpServiceConfiguration upnpConfiguration = new DefaultUpnpServiceConfiguration(configurationDao.getIntParameter(UPNP_SERVICE_PORT));
        UpnpService upnpService = new UpnpServiceImpl(upnpConfiguration);

        // Device identity
        DeviceIdentity identity = new DeviceIdentity(UDN.uniqueSystemIdentifier(HOLMES_UPNP_SERVER_NAME.toString()));

        // Device type
        DeviceType type = DeviceType.valueOf("urn:schemas-upnp-org:device:MediaServer:1");

        // Device details
        ModelDetails modelDetails = new ModelDetails(HOLMES_UPNP_SHORT_NAME.toString(), HOLMES_UPNP_DESCRIPTION.toString(), version, HOLMES_SITE_URL.toString());
        ManufacturerDetails manufacturerDetails = new ManufacturerDetails(HOLMES_UPNP_SHORT_NAME.toString(), HOLMES_SITE_URL.toString());
        DLNADoc[] dlnaDocs = new DLNADoc[]{new DLNADoc("DMS", DLNADoc.Version.V1_5), new DLNADoc("M-DMS", DLNADoc.Version.V1_5)};
        DeviceDetails details = new DeviceDetails(configurationDao.getParameter(UPNP_SERVER_NAME), manufacturerDetails, modelDetails, dlnaDocs, null);

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
            Icon largeIcon = new Icon(ICON_MIME_TYPE, LARGE_ICON_SIZE, LARGE_ICON_SIZE, ICON_DEPTH, "upnp-icon-256.png", getData(UPNP, "icon-256.png"));
            Icon smallIcon = new Icon(ICON_MIME_TYPE, SMALL_ICON_SIZE, SMALL_ICON_SIZE, ICON_DEPTH, "upnp-icon-32.png", getData(UPNP, "icon-32.png"));
            icons = new Icon[]{largeIcon, smallIcon};
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        try {
            // Create local services
            LocalService<?>[] localServices = new LocalService[]{contentDirectoryService, connectionManagerService};

            // Add local device to UPnP registry
            upnpService.getRegistry().addDevice(new LocalDevice(identity, type, details, icons, localServices));
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
        return upnpService;
    }
}
