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

package net.holmes.core.business.streaming.upnp;

import net.holmes.core.business.mimetype.model.MimeType;
import org.fourthline.cling.DefaultUpnpServiceConfiguration;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.DLNADoc;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.support.connectionmanager.ConnectionManagerService;

import java.io.IOException;

import static net.holmes.core.common.Constants.*;
import static net.holmes.core.common.StaticResourceLoader.*;
import static org.fourthline.cling.model.types.UDN.uniqueSystemIdentifier;

/**
 * Upnp utils.
 */
public final class UpnpUtils {
    public static final DeviceType DEVICE_TYPE = DeviceType.valueOf("urn:schemas-upnp-org:device:MediaServer:1");
    public static final DeviceIdentity DEVICE_IDENTITY = new DeviceIdentity(uniqueSystemIdentifier(HOLMES_UPNP_SERVER_NAME.toString()));

    /**
     * Private constructor
     */
    private UpnpUtils() {
        // Nothing
    }

    /**
     * Get Upnp mime type.
     *
     * @param mimeType mime type
     * @return Upnp mime type
     */
    public static org.seamless.util.MimeType getUpnpMimeType(final MimeType mimeType) {
        return new org.seamless.util.MimeType(mimeType.getType().getValue(), mimeType.getSubType());
    }


    /**
     * Get Upnp device name associated to RemoteDevice
     *
     * @param device Upnp device
     * @return Upnp device name
     */
    public static String getDeviceName(final RemoteDevice device) {
        return device.getDetails() != null ? device.getDetails().getFriendlyName() : device.getDisplayString();
    }

    /**
     * Get Upnp device ID associated to RemoteDevice
     *
     * @param device Upnp device
     * @return Upnp device ID
     */
    public static String getDeviceId(final RemoteDevice device) {
        return device.getIdentity().getUdn().getIdentifierString();
    }

    /**
     * Build a new Upnp service.
     *
     * @param upnpPort UPnP port
     * @return Upnp service
     */
    public static UpnpService buildUpnpService(final int upnpPort) {
        return new UpnpServiceImpl(new DefaultUpnpServiceConfiguration(upnpPort));
    }

    /**
     * Build a new connection manager service.
     *
     * @return connection manager service
     */
    @SuppressWarnings("unchecked")
    public static LocalService<ConnectionManagerService> buildConnectionManagerService() {
        LocalService<ConnectionManagerService> connectionManagerService = new AnnotationLocalServiceBinder().read(ConnectionManagerService.class);
        connectionManagerService.setManager(new DefaultServiceManager<>(connectionManagerService, ConnectionManagerService.class));
        return connectionManagerService;
    }

    /**
     * Build a new device details.
     *
     * @param serverName server name
     * @param version    version
     * @return device details
     */
    public static DeviceDetails buildDeviceDetails(final String serverName, final String version) {
        ModelDetails modelDetails = new ModelDetails(HOLMES_UPNP_SERVER_NAME.toString(), HOLMES_UPNP_DESCRIPTION.toString(), version, HOLMES_SITE_URL.toString());
        ManufacturerDetails manufacturerDetails = new ManufacturerDetails(HOLMES_UPNP_SERVER_NAME.toString(), HOLMES_SITE_URL.toString());
        DLNADoc[] dlnaDocs = new DLNADoc[]{new DLNADoc("DMS", DLNADoc.Version.V1_5), new DLNADoc("M-DMS", DLNADoc.Version.V1_5)};
        return new DeviceDetails(serverName, manufacturerDetails, modelDetails, dlnaDocs, null);
    }

    /**
     * Get Upnp icons.
     *
     * @return icons
     * @throws IOException
     */
    public static Icon[] getIcons() throws IOException {
        return new Icon[]{getUpnpLargeIcon(), getUpnpSmallIcon()};
    }

}
