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
import net.holmes.core.common.configuration.Parameter;
import net.holmes.core.media.index.MediaIndexManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Scheduled service used to clean index.
 */
public final class MediaIndexCleanerService extends AbstractScheduledService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MediaIndexCleanerService.class);
    private final MediaIndexManager mediaIndexManager;
    private final int cleanDelayMinutes;

    /**
     * Instantiates a new media index cleaner service.
     *
     * @param mediaIndexManager media index manager
     * @param configuration     configuration
     */
    @Inject
    public MediaIndexCleanerService(final MediaIndexManager mediaIndexManager, final Configuration configuration) {
        this.mediaIndexManager = mediaIndexManager;
        this.cleanDelayMinutes = configuration.getIntParameter(Parameter.MEDIA_INDEX_CLEAN_DELAY_MINUTES);
    }

    @Override
    protected void runOneIteration() {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Launch media index cleaner");
        mediaIndexManager.clean();
    }

    @Override
    protected Scheduler scheduler() {
        if (cleanDelayMinutes > 0)
            return Scheduler.newFixedDelaySchedule(cleanDelayMinutes, cleanDelayMinutes, TimeUnit.MINUTES);
        return null;
    }
}
