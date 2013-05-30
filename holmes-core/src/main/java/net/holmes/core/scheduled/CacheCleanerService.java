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

import com.google.common.cache.Cache;
import com.google.common.util.concurrent.AbstractScheduledService;
import net.holmes.common.configuration.Configuration;
import net.holmes.common.configuration.Parameter;
import net.holmes.common.media.AbstractNode;
import net.holmes.core.inject.InjectLogger;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Scheduled service used to clean local caches.
 */
public class CacheCleanerService extends AbstractScheduledService {
    private final Cache<String, List<AbstractNode>> podcastCache;
    private final Cache<File, String> imageCache;
    private final int cleanDelayMinutes;
    @InjectLogger
    private Logger logger;

    /**
     * Instantiates a new cache cleaner service.
     *
     * @param podcastCache  podcast cache
     * @param imageCache    image cache
     * @param configuration configuration
     */
    @Inject
    public CacheCleanerService(@Named("podcastCache") final Cache<String, List<AbstractNode>> podcastCache,
                               @Named("imageCache") final Cache<File, String> imageCache, final Configuration configuration) {
        this.podcastCache = podcastCache;
        this.imageCache = imageCache;
        this.cleanDelayMinutes = configuration.getIntParameter(Parameter.LOCAL_CACHE_CLEAN_DELAY_MINUTES);
    }

    @Override
    protected void runOneIteration() {
        if (logger.isDebugEnabled()) logger.debug("Launch cache cleaner");
        podcastCache.cleanUp();
        imageCache.cleanUp();
    }

    @Override
    protected Scheduler scheduler() {
        if (cleanDelayMinutes > 0)
            return Scheduler.newFixedRateSchedule(cleanDelayMinutes, cleanDelayMinutes, TimeUnit.MINUTES);
        return null;
    }
}
