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
import net.holmes.core.media.dao.icecast.IcecastDao;

import javax.inject.Inject;

import static java.util.concurrent.TimeUnit.HOURS;
import static net.holmes.core.common.configuration.Parameter.ICECAST_YELLOW_PAGE_DOWNLOAD_DELAY_HOURS;

/**
 * Icecast directory download service.
 */
public class IcecastDownloadService extends AbstractScheduledService {
    private final IcecastDao icecastDao;
    private final int downloadDelayHours;

    /**
     * Instantiates a new Icecast directory download service.
     *
     * @param icecastDao    Icecast DAO
     * @param configuration configuration
     */
    @Inject
    public IcecastDownloadService(final IcecastDao icecastDao, final Configuration configuration) {
        this.icecastDao = icecastDao;
        this.downloadDelayHours = configuration.getIntParameter(ICECAST_YELLOW_PAGE_DOWNLOAD_DELAY_HOURS);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runOneIteration() {
        icecastDao.checkYellowPage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Scheduler scheduler() {
        return downloadDelayHours > 0 ? Scheduler.newFixedDelaySchedule(0, downloadDelayHours, HOURS) : null;
    }
}
