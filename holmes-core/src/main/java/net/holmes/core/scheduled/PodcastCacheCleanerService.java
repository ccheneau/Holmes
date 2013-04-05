/**
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

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import net.holmes.common.configuration.Configuration;
import net.holmes.common.configuration.Parameter;
import net.holmes.common.media.AbstractNode;
import net.holmes.core.inject.Loggable;

import org.slf4j.Logger;

import com.google.common.cache.Cache;
import com.google.common.util.concurrent.AbstractScheduledService;

/**
 * Scheduled service user to clean podcast cache
 */
@Loggable
public class PodcastCacheCleanerService extends AbstractScheduledService {
    private Logger logger;

    private final Cache<String, List<AbstractNode>> podcastCache;
    private final int cleanDelayMinutes;

    @Inject
    public PodcastCacheCleanerService(@Named("podcastCache") Cache<String, List<AbstractNode>> podcastCache, Configuration configuration) {
        this.podcastCache = podcastCache;
        this.cleanDelayMinutes = configuration.getIntParameter(Parameter.PODCAST_CACHE_CLEAN_DELAY_MINUTES);
    }

    @Override
    protected void runOneIteration() throws Exception {
        if (logger.isDebugEnabled()) logger.debug("Launch media scanner");
        podcastCache.cleanUp();
    }

    @Override
    protected Scheduler scheduler() {
        if (cleanDelayMinutes > 0) return Scheduler.newFixedRateSchedule(cleanDelayMinutes, cleanDelayMinutes, TimeUnit.MINUTES);
        return null;
    }
}
