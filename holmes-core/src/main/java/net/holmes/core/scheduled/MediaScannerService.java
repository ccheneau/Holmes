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

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import net.holmes.common.configuration.Configuration;
import net.holmes.common.configuration.Parameter;
import net.holmes.core.inject.Loggable;
import net.holmes.core.media.MediaManager;

import org.slf4j.Logger;

import com.google.common.util.concurrent.AbstractScheduledService;

/**
 * Scheduled service used to scan all media.
 */
@Loggable
public class MediaScannerService extends AbstractScheduledService {
    private Logger logger;

    private final MediaManager mediaManager;
    private final int scanAllDelayMinutes;

    /**
     * Constructor.
     *
     * @param mediaManager 
     *      media manager
     * @param configuration 
     *      configuration
     */
    @Inject
    public MediaScannerService(final MediaManager mediaManager, final Configuration configuration) {
        this.mediaManager = mediaManager;
        this.scanAllDelayMinutes = configuration.getIntParameter(Parameter.MEDIA_SCAN_ALL_DELAY_MINUTES);
    }

    @Override
    protected void runOneIteration() throws Exception {
        if (logger.isDebugEnabled()) logger.debug("Launch media scanner");
        mediaManager.scanAll();
    }

    @Override
    protected Scheduler scheduler() {
        if (scanAllDelayMinutes > 0) return Scheduler.newFixedRateSchedule(scanAllDelayMinutes, scanAllDelayMinutes, TimeUnit.MINUTES);
        return null;
    }

}
