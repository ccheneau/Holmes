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

package net.holmes.core.scheduled;

import com.google.common.util.concurrent.AbstractScheduledService;
import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.common.configuration.Parameter;
import net.holmes.core.media.MediaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Scheduled service used to scan all media.
 */
public class MediaScannerService extends AbstractScheduledService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MediaScannerService.class);
    private final MediaManager mediaManager;
    private final int scanAllDelayMinutes;

    /**
     * Instantiates a new media scanner service.
     *
     * @param mediaManager  media manager
     * @param configuration configuration
     */
    @Inject
    public MediaScannerService(final MediaManager mediaManager, final Configuration configuration) {
        this.mediaManager = mediaManager;
        this.scanAllDelayMinutes = configuration.getIntParameter(Parameter.MEDIA_SCAN_ALL_DELAY_MINUTES);
    }

    @Override
    protected void runOneIteration() {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Launch media scanner");
        mediaManager.scanAll();
    }

    @Override
    protected Scheduler scheduler() {
        if (scanAllDelayMinutes > 0)
            return Scheduler.newFixedRateSchedule(scanAllDelayMinutes, scanAllDelayMinutes, TimeUnit.MINUTES);
        return null;
    }

}
