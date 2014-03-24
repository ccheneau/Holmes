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

package net.holmes.core.service.scheduled;

import com.google.common.util.concurrent.AbstractScheduledService;
import net.holmes.core.business.configuration.ConfigurationDao;
import net.holmes.core.business.version.VersionManager;

import javax.inject.Inject;

import static java.util.concurrent.TimeUnit.HOURS;
import static net.holmes.core.business.configuration.Parameter.RELEASE_CHECK_DELAY_HOURS;

/**
 * Scheduled service used to check for new Holmes releases.
 */
public class ReleaseCheckService extends AbstractScheduledService {

    private final VersionManager versionManager;
    private final Integer checkDelayHours;

    /**
     * Instantiates a new release check service.
     *
     * @param versionManager   version manager
     * @param configurationDao configuration DAO
     */
    @Inject
    public ReleaseCheckService(final VersionManager versionManager, final ConfigurationDao configurationDao) {
        this.versionManager = versionManager;
        this.checkDelayHours = configurationDao.getIntParameter(RELEASE_CHECK_DELAY_HOURS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runOneIteration() throws Exception {
        versionManager.updateReleaseInfo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Scheduler scheduler() {
        return checkDelayHours > 0 ? Scheduler.newFixedDelaySchedule(0, checkDelayHours, HOURS) : null;
    }
}
