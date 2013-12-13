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

package net.holmes.core.transport;

import com.google.common.eventbus.Subscribe;
import net.holmes.core.transport.airplay.model.AirplayDevice;
import net.holmes.core.transport.device.DeviceStreamer;
import net.holmes.core.transport.device.dao.DeviceDao;
import net.holmes.core.transport.device.exception.UnknownDeviceException;
import net.holmes.core.transport.device.model.Device;
import net.holmes.core.transport.event.StreamingEvent;
import net.holmes.core.transport.upnp.model.UpnpDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;

/**
 * Transport service implementation.
 */
public class TransportServiceImpl implements TransportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransportServiceImpl.class);

    private final DeviceDao deviceDao;
    private final DeviceStreamer upnpStreamer;
    private final DeviceStreamer airplayStreamer;

    /**
     * Instantiates a new transport service implementation.
     *
     * @param deviceDao       device DAO
     * @param upnpStreamer    upnp streamer
     * @param airplayStreamer airplay streamer
     */
    @Inject
    public TransportServiceImpl(final DeviceDao deviceDao, @Named("upnp") final DeviceStreamer upnpStreamer, @Named("airplay") final DeviceStreamer airplayStreamer) {
        this.deviceDao = deviceDao;
        this.upnpStreamer = upnpStreamer;
        this.airplayStreamer = airplayStreamer;
    }

    @Override
    public void addDevice(final Device device) {
        LOGGER.info("Add device {}", device);
        deviceDao.addDevice(device);
    }

    @Override
    public void removeDevice(final String deviceId) {
        LOGGER.info("Remove device {}", deviceId);
        deviceDao.removeDevice(deviceId);
    }

    @Override
    public Collection<Device> findDevices(final String hostAddress) {
        return deviceDao.findDevices(hostAddress);
    }

    @Override
    public Collection<Device> getDevices() {
        return deviceDao.getDevices();
    }

    @Override
    public void play(final String deviceId, final String contentUrl, final String contentName) throws UnknownDeviceException {
        Device device = deviceDao.getDevice(deviceId);
        getStreamer(device).play(device, contentUrl);
    }

    @Override
    public void stop(final String deviceId) throws UnknownDeviceException {
        Device device = deviceDao.getDevice(deviceId);
        getStreamer(device).stop(device);
    }

    @Override
    public void pause(final String deviceId) throws UnknownDeviceException {
        Device device = deviceDao.getDevice(deviceId);
        getStreamer(device).pause(device);
    }

    @Override
    public void resume(final String deviceId) throws UnknownDeviceException {
        Device device = deviceDao.getDevice(deviceId);
        getStreamer(device).resume(device);
    }

    @Override
    public void updateStatus(final String deviceId) throws UnknownDeviceException {
        Device device = deviceDao.getDevice(deviceId);
        getStreamer(device).updateStatus(device);
    }

    /**
     * Handle streaming event.
     *
     * @param streamingEvent streaming event
     */
    @Subscribe
    public void handleStreamingEvent(final StreamingEvent streamingEvent) {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("handle streaming event: {}", streamingEvent);
        switch (streamingEvent.getType()) {
            case PLAY:
            case STOP:
            case PAUSE:
            case RESUME:
            case STATUS:
                break;
            default:
                break;
        }
    }

    /**
     * Get streamer associated to device
     *
     * @param device device
     * @return streamer
     */
    private DeviceStreamer getStreamer(final Device device) {
        if (device instanceof UpnpDevice)
            return upnpStreamer;
        else if (device instanceof AirplayDevice)
            return airplayStreamer;
        throw new IllegalArgumentException("Unknown device type " + device);
    }
}
