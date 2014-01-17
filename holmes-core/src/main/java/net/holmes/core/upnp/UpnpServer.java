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

package net.holmes.core.upnp;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import net.holmes.core.common.Service;
import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.common.configuration.Parameter;
import net.holmes.core.upnp.metadata.UpnpDeviceMetadata;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;
import org.fourthline.cling.support.connectionmanager.callback.GetProtocolInfo;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.ProtocolInfos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

/**
 * UPnP server main class.
 */
public final class UpnpServer implements Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpnpServer.class);
    private static final ServiceType CONNECTION_MANAGER_SERVICE_TYPE = ServiceType.valueOf("urn:schemas-upnp-org:service:ConnectionManager:1");
    private final Injector injector;
    private final Configuration configuration;
    private final UpnpDeviceMetadata upnpDeviceMetadata;
    private UpnpService upnpService = null;

    /**
     * Instantiates a new UPnP server.
     *
     * @param injector      Guice injector
     * @param configuration configuration
     */
    @Inject
    public UpnpServer(final Injector injector, final Configuration configuration, final UpnpDeviceMetadata upnpDeviceMetadata) {
        this.injector = injector;
        this.configuration = configuration;
        this.upnpDeviceMetadata = upnpDeviceMetadata;
    }

    @Override
    public void start() {
        if (configuration.getBooleanParameter(Parameter.ENABLE_UPNP)) {
            LOGGER.info("Starting UPnP server");
            upnpService = injector.getInstance(UpnpService.class);

            // Add registry listener
            upnpService.getRegistry().addListener(new UpnpRegistryListener());

            // Search for UPnp devices
            upnpService.getControlPoint().search();

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

    /**
     * Upnp registry listener
     */
    private final class UpnpRegistryListener implements RegistryListener {

        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
            // Ignore
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex) {
            // Ignore
        }

        @Override
        public void remoteDeviceAdded(final Registry registry, final RemoteDevice device) {
            final String deviceDisplay = device.getDisplayString();
            LOGGER.info("Remote device available: " + deviceDisplay);

            if (configuration.getBooleanParameter(Parameter.ENABLE_UPNP_PROTOCOL_INFO)) {
                // Search device's connection manager.
                RemoteService service = device.findService(CONNECTION_MANAGER_SERVICE_TYPE);
                if (service != null && device.getIdentity() != null && device.getIdentity().getDescriptorURL() != null) {
                    // Device host IP
                    final String deviceHost = device.getIdentity().getDescriptorURL().getHost();

                    LOGGER.info("Get protocol info for {} [{}]", deviceDisplay, deviceHost);

                    // Get remote device protocol info
                    upnpService.getControlPoint().execute(new GetProtocolInfo(service) {
                        @Override
                        public void received(ActionInvocation actionInvocation, ProtocolInfos sinkProtocolInfo, ProtocolInfos sourceProtocolInfo) {
                            // Got protocol info, add available mime types to Upnp device metadata
                            List<String> mimeTypes = Lists.newArrayList();
                            for (ProtocolInfo protocolInfo : sinkProtocolInfo) {
                                mimeTypes.add(protocolInfo.getContentFormatMimeType().toString());
                            }
                            upnpDeviceMetadata.addDevice(deviceHost, mimeTypes);
                        }

                        @Override
                        public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                            LOGGER.debug("Failed to get protocol info for {}: {}", deviceDisplay, defaultMsg);
                        }
                    });
                }
            }
        }

        @Override
        public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
            // Ignore
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            LOGGER.info("Remote device removed: " + device.getDisplayString());
            upnpDeviceMetadata.removeDevice(device.getIdentity().getDescriptorURL().getHost());
        }

        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {
            // Ignore
        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
            // Ignore
        }

        @Override
        public void beforeShutdown(Registry registry) {
            // Ignore
        }

        @Override
        public void afterShutdown() {
            // Ignore
        }
    }
}
