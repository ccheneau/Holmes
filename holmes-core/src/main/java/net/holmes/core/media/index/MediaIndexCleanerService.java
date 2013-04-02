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

package net.holmes.core.media.index;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import net.holmes.common.configuration.Configuration;
import net.holmes.common.configuration.Parameter;
import net.holmes.core.inject.Loggable;

import org.slf4j.Logger;

import com.google.common.util.concurrent.AbstractScheduledService;

/**
 * Scheduled service used to clean index
 */
@Loggable
public final class MediaIndexCleanerService extends AbstractScheduledService {
    private Logger logger;

    private final MediaIndexManager mediaIndexManager;
    private final int cleanDelayMinutes;

    @Inject
    public MediaIndexCleanerService(MediaIndexManager mediaIndexManager, Configuration configuration) {
        this.mediaIndexManager = mediaIndexManager;
        this.cleanDelayMinutes = configuration.getIntParameter(Parameter.MEDIA_INDEX_CLEAN_DELAY_MINUTES);
    }

    @Override
    protected void runOneIteration() throws Exception {
        if (logger.isDebugEnabled()) logger.debug("Launch media index cleaner");
        mediaIndexManager.clean();
    }

    @Override
    protected Scheduler scheduler() {
        if (cleanDelayMinutes > 0) return Scheduler.newFixedRateSchedule(cleanDelayMinutes, cleanDelayMinutes, TimeUnit.MINUTES);
        return null;
    }
}
