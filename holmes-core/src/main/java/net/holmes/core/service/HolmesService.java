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

package net.holmes.core.service;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;
import net.holmes.core.common.exception.HolmesRuntimeException;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Holmes service main class.
 */
public final class HolmesService implements Service {
    private static final Logger LOGGER = getLogger(HolmesService.class);
    private static final String LOCK_FILE_NAME = "holmes.lock";

    private final Service httpService;
    private final Service upnpService;
    private final Service airplayService;
    private final Service systrayService;
    private final Service schedulerService;
    private final String localHolmesDataDir;

    private RandomAccessFile randomAccessFile = null;
    private FileLock fileLock = null;

    /**
     * Instantiates a new holmes service.
     *
     * @param httpService         Http service
     * @param upnpService         UPnP service
     * @param systrayService            Systray service
     * @param schedulerService          Scheduler service
     * @param localHolmesDataDir local Holmes data directory
     */
    @Inject
    public HolmesService(@Named("http") final Service httpService, @Named("upnp") final Service upnpService, @Named("airplay") final Service airplayService,
                         @Named("systray") final Service systrayService, @Named("scheduler") final Service schedulerService,
                         @Named("localHolmesDataDir") String localHolmesDataDir) {
        this.httpService = httpService;
        this.upnpService = upnpService;
        this.airplayService = airplayService;
        this.systrayService = systrayService;
        this.schedulerService = schedulerService;
        this.localHolmesDataDir = localHolmesDataDir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        if (lockInstance()) {
            LOGGER.info("Starting Holmes service");

            // Start Holmes services
            httpService.start();
            upnpService.start();
            airplayService.start();
            systrayService.start();
            schedulerService.start();

            LOGGER.info("Holmes service started");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        LOGGER.info("Stopping Holmes service");

        // Remove lock
        unlockInstance();

        // Stop Holmes service
        schedulerService.stop();
        systrayService.stop();
        airplayService.stop();
        upnpService.stop();
        httpService.stop();

        LOGGER.info("Holmes service stopped");
    }

    /**
     * Receive dead event from event bus.
     *
     * @param deadEvent dead event
     */
    @Subscribe
    public void handleDeadEvent(final DeadEvent deadEvent) {
        LOGGER.warn("Event not handled: {}", deadEvent.getEvent().toString());
    }

    /**
     * Create Holmes lock file.
     *
     * @throws RuntimeException if lock fails
     */
    private boolean lockInstance() {
        try {
            if (fileLock == null) {
                // Create lock file
                randomAccessFile = new RandomAccessFile(new File(localHolmesDataDir, LOCK_FILE_NAME), "rw");
                fileLock = randomAccessFile.getChannel().tryLock();
                if (fileLock == null) {
                    throw new HolmesRuntimeException("Holmes service is already running");
                }
                return true;
            }
        } catch (IOException e) {
            LOGGER.error("Unable to create and/or lock file: " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Release and remove Holmes lock file
     */
    private void unlockInstance() {
        // Release lock file on system exit
        if (fileLock != null) {
            File lockFile = new File(localHolmesDataDir, LOCK_FILE_NAME);
            try {
                fileLock.release();
                randomAccessFile.close();
                if (lockFile.exists() && !lockFile.delete()) {
                    LOGGER.error("Unable to remove lock file: {}", lockFile.getPath());
                }
            } catch (IOException e) {
                LOGGER.error("Unable to unlock file: " + lockFile.getPath() + " " + e.getMessage(), e);
            }
        }
    }
}
