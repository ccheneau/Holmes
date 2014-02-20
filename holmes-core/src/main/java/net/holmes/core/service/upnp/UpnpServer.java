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

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import net.holmes.core.business.configuration.ConfigurationDao;
import net.holmes.core.business.streaming.StreamingManager;
import net.holmes.core.business.streaming.upnp.device.UpnpDevice;
import net.holmes.core.service.Service;
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

import javax.inject.Inject;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static net.holmes.core.business.configuration.Parameter.ENABLE_UPNP;
import static org.fourthline.cling.support.model.Protocol.HTTP_GET;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * UPnP service.
 */
public final class UpnpServer implements Service {
    private static final Logger LOGGER = getLogger(UpnpServer.class);
    private static final ServiceType CONNECTION_MANAGER_SERVICE_TYPE = ServiceType.valueOf("urn:schemas-upnp-org:service:ConnectionManager:1");
    private static final ServiceType AV_TRANSPORT_SERVICE_TYPE = ServiceType.valueOf("urn:schemas-upnp-org:service:AVTransport:1");

    private final Injector injector;
    private final ConfigurationDao configurationDao;
    private final StreamingManager streamingManager;

    private UpnpService upnpService = null;

    /**
     * Instantiates a new UPnP server.
     *
     * @param injector         Guice injector
     * @param configurationDao configuration dao
     * @param streamingManager streaming manager
     */
    @Inject
    public UpnpServer(final Injector injector, final ConfigurationDao configurationDao, final StreamingManager streamingManager) {
        this.injector = injector;
        this.configurationDao = configurationDao;
        this.streamingManager = streamingManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        if (configurationDao.getBooleanParameter(ENABLE_UPNP)) {
            LOGGER.info("Starting UPnP server");
            upnpService = injector.getInstance(UpnpService.class);

            // Add registry listener
            upnpService.getRegistry().addListener(new UpnpRegistryListener());

            // Search for UPnp devices
            upnpService.getControlPoint().search();

            LOGGER.info("UPnP server started");
        } else LOGGER.info("UPnP server is disabled");
    }

    /**
     * {@inheritDoc}
     */
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
        /**
         * {@inheritDoc}
         */
        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
            // Ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex) {
            LOGGER.error("Remote device discovery failed:" + getDeviceName(device), ex);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void remoteDeviceAdded(final Registry registry, final RemoteDevice device) {
            // Get device's connection manager service and AvTransport service.
            RemoteService connectionService = device.findService(CONNECTION_MANAGER_SERVICE_TYPE);
            final RemoteService avTransportService = device.findService(AV_TRANSPORT_SERVICE_TYPE);

            if (connectionService != null && avTransportService != null && device.getIdentity() != null && device.getIdentity().getDescriptorURL() != null)
                try {
                    // Device info
                    final String deviceId = device.getIdentity().getUdn().getIdentifierString();
                    final String deviceName = getDeviceName(device);
                    final InetAddress deviceHost = InetAddress.getByName(device.getIdentity().getDescriptorURL().getHost());
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("Remote device added {} : {} [{}]", deviceId, deviceName, deviceHost);

                    // Get protocol info on remote device
                    upnpService.getControlPoint().execute(new GetProtocolInfo(connectionService) {
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public void received(ActionInvocation actionInvocation, ProtocolInfos sinkProtocolInfo, ProtocolInfos sourceProtocolInfo) {
                            // Got protocol info, get available mime types
                            List<String> availableMimeTypes = Lists.newArrayList();
                            for (ProtocolInfo protocolInfo : sinkProtocolInfo)
                                if (protocolInfo.getProtocol() == HTTP_GET && !availableMimeTypes.contains(protocolInfo.getContentFormat()))
                                    availableMimeTypes.add(protocolInfo.getContentFormat());

                            // Add device
                            streamingManager.addDevice(new UpnpDevice(deviceId, deviceName, deviceHost, availableMimeTypes, avTransportService));
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                            LOGGER.error("Failed to get protocol info for {}: {}", deviceName, defaultMsg);
                        }
                    });
                } catch (UnknownHostException e) {
                    LOGGER.error(e.getMessage(), e);
                }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
            // Ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            streamingManager.removeDevice(device.getIdentity().getUdn().getIdentifierString());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {
            // Ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
            // Ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void beforeShutdown(Registry registry) {
            // Ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void afterShutdown() {
            // Ignore
        }

        /**
         * Get Upnp device name
         *
         * @param device Upnp device
         * @return Upnp device name
         */
        private String getDeviceName(final RemoteDevice device) {
            if (device.getDetails() != null)
                return device.getDetails().getFriendlyName();
            else
                return device.getDisplayString();
        }
    }
}
