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

package net.holmes.core.airplay;

import net.holmes.core.common.Service;
import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.transport.TransportService;
import net.holmes.core.transport.airplay.device.AirplayDevice;
import net.holmes.core.transport.airplay.device.AirplayFeatures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;

import static net.holmes.core.common.configuration.Parameter.ENABLE_AIRPLAY;

/**
 * Airplay server
 */
public final class AirplayServer implements Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(AirplayServer.class);
    private static final String AIRPLAY_TCP = "_airplay._tcp.local.";
    private static final String AIRPLAY_FEATURES = "features";
    private final Configuration configuration;
    private final InetAddress localAddress;
    private final TransportService transportService;
    private JmDNS jmDNS = null;

    /**
     * Instantiates a new Airplay server.
     *
     * @param configuration    configuration
     * @param localAddress     local address
     * @param transportService transport service
     */
    @Inject
    public AirplayServer(final Configuration configuration, final @Named("localAddress") InetAddress localAddress, final TransportService transportService) {
        this.configuration = configuration;
        this.localAddress = localAddress;
        this.transportService = transportService;
    }

    @Override
    public void start() {
        if (configuration.getBooleanParameter(ENABLE_AIRPLAY)) {
            LOGGER.info("Starting Airplay server");
            try {
                // Create JmDNS
                jmDNS = JmDNS.create(localAddress);

                // Loop up for available devices
                for (ServiceInfo serviceInfo : jmDNS.list(AIRPLAY_TCP))
                    transportService.addDevice(buildDevice(serviceInfo));

                // Add Listener to manager inbound and outbound devices
                jmDNS.addServiceListener(AIRPLAY_TCP, new ServiceListener() {

                    @Override
                    public void serviceAdded(ServiceEvent event) {
                        // Nothing, waiting for service to be resolved with serviceResolved method
                    }

                    @Override
                    public void serviceRemoved(ServiceEvent event) {
                        transportService.removeDevice(event.getInfo().getKey());
                    }

                    @Override
                    public void serviceResolved(ServiceEvent event) {
                        transportService.addDevice(buildDevice(event.getInfo()));
                    }
                });

            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
            LOGGER.info("Airplay server started");
        }
    }

    @Override
    public void stop() {
        if (jmDNS != null) {
            LOGGER.info("Stopping Airplay server");
            try {
                jmDNS.close();
                LOGGER.info("Airplay server stopped");
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Build Airplay device associated to jmDNS service info.
     * For now, only IPV4 addresses are accepted.
     *
     * @param serviceInfo jmDNS service information
     * @return Airplay device
     */
    private AirplayDevice buildDevice(ServiceInfo serviceInfo) {
        if (serviceInfo != null && serviceInfo.getInet4Addresses() != null) {
            for (Inet4Address inet4Address : serviceInfo.getInet4Addresses())
                if (!inet4Address.isLoopbackAddress()) {
                    if (LOGGER.isDebugEnabled()) LOGGER.debug("Build Airplay device for {}", serviceInfo.toString());
                    AirplayFeatures features = new AirplayFeatures(serviceInfo.getPropertyString(AIRPLAY_FEATURES));
                    return new AirplayDevice(serviceInfo.getKey(), serviceInfo.getName(), inet4Address, serviceInfo.getPort(), features);
                }
        }
        return null;
    }
}
