/*
 * Copyright (C) 2012-2015  Cedric Cheneau
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

import com.google.common.util.concurrent.AbstractScheduledService;
import net.holmes.core.business.configuration.ConfigurationManager;
import net.holmes.core.business.version.VersionManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import static java.util.concurrent.TimeUnit.HOURS;
import static net.holmes.core.common.ConfigurationParameter.RELEASE_CHECK_DELAY_HOURS;

/**
 * Scheduled service used to check for new Holmes releases.
 */
@Singleton
public class ReleaseCheckService extends AbstractScheduledService implements Service {

    private final VersionManager versionManager;
    private final Integer checkDelayHours;

    /**
     * Instantiates a new release check service.
     *
     * @param versionManager       version manager
     * @param configurationManager configuration manager
     */
    @Inject
    public ReleaseCheckService(final VersionManager versionManager, final ConfigurationManager configurationManager) {
        this.versionManager = versionManager;
        this.checkDelayHours = configurationManager.getParameter(RELEASE_CHECK_DELAY_HOURS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runOneIteration() {
        versionManager.updateRemoteReleaseInfo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Scheduler scheduler() {
        return checkDelayHours > 0 ? Scheduler.newFixedDelaySchedule(0, checkDelayHours, HOURS) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        startAsync();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        stopAsync();
    }
}
