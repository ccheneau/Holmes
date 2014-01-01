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

package net.holmes.core.scheduled;

import com.google.common.util.concurrent.AbstractScheduledService;
import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.media.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static java.util.concurrent.TimeUnit.MINUTES;
import static net.holmes.core.common.configuration.Parameter.CACHE_CLEAN_DELAY_MINUTES;

/**
 * Scheduled service used to clean caches.
 */
public class CacheCleanerService extends AbstractScheduledService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheCleanerService.class);
    private final MediaService mediaService;
    private final int cleanDelayMinutes;

    /**
     * Instantiates a new cache cleaner service.
     *
     * @param mediaService  media service
     * @param configuration configuration
     */
    @Inject
    public CacheCleanerService(final MediaService mediaService, final Configuration configuration) {
        this.mediaService = mediaService;
        this.cleanDelayMinutes = configuration.getIntParameter(CACHE_CLEAN_DELAY_MINUTES);
    }

    @Override
    protected void runOneIteration() {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Launch cache cleaner");
        mediaService.cleanUpCache();
    }

    @Override
    protected Scheduler scheduler() {
        return cleanDelayMinutes > 0 ? Scheduler.newFixedDelaySchedule(cleanDelayMinutes, cleanDelayMinutes, MINUTES) : null;
    }
}
