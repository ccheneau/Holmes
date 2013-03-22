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

package net.holmes.core.media;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import net.holmes.core.configuration.Configuration;
import net.holmes.core.configuration.Parameter;
import net.holmes.core.media.node.AbstractNode;

import com.google.common.cache.Cache;
import com.google.common.util.concurrent.AbstractScheduledService;

/**
 * Clean podcast cache
 */
public class PodcastCacheCleanerService extends AbstractScheduledService {

    private final Cache<String, List<AbstractNode>> podcastCache;
    private final int cleanDelayMinutes;

    @Inject
    public PodcastCacheCleanerService(@Named("podcastCache") Cache<String, List<AbstractNode>> podcastCache, Configuration configuration) {
        this.podcastCache = podcastCache;
        this.cleanDelayMinutes = configuration.getIntParameter(Parameter.PODCAST_CACHE_CLEAN_DELAY_MINUTES);
    }

    @Override
    protected void runOneIteration() throws Exception {
        podcastCache.cleanUp();
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(cleanDelayMinutes, cleanDelayMinutes, TimeUnit.MINUTES);
    }
}
