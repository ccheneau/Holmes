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

package net.holmes.core.business.streaming.airplay.controlpoint;

import net.holmes.core.business.streaming.airplay.command.AirplayCommand;
import net.holmes.core.business.streaming.airplay.device.AirplayDevice;

import javax.inject.Inject;
import javax.net.SocketFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Asynchronous Airplay socket control point
 */
public class AsyncSocketControlPoint extends SocketControlPoint {
    private static final int EXECUTOR_POOL_SIZE = 4;

    private final ExecutorService executor;

    /**
     * Instantiates a new asynchronous Airplay control point.
     *
     * @param socketFactory socket factory
     */
    @Inject
    public AsyncSocketControlPoint(final SocketFactory socketFactory) {
        super(socketFactory);
        executor = Executors.newFixedThreadPool(EXECUTOR_POOL_SIZE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final AirplayDevice device, final AirplayCommand command) {
        executor.execute(() -> runDeviceCommand(device, command));
    }
}
