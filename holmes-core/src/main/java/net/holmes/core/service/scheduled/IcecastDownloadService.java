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
import net.holmes.core.business.media.dao.icecast.IcecastDao;

import javax.inject.Inject;

import static java.util.concurrent.TimeUnit.HOURS;
import static net.holmes.core.common.parameter.ConfigurationParameter.ICECAST_YELLOW_PAGE_DOWNLOAD_DELAY_HOURS;

/**
 * Scheduled service used to download new Icecast directory.
 */
public class IcecastDownloadService extends AbstractScheduledService {
    private final IcecastDao icecastDao;
    private final int downloadDelayHours;

    /**
     * Instantiates a new Icecast directory download service.
     *
     * @param icecastDao       Icecast dao
     * @param configurationDao configuration dDao
     */
    @Inject
    public IcecastDownloadService(final IcecastDao icecastDao, final ConfigurationDao configurationDao) {
        this.icecastDao = icecastDao;
        this.downloadDelayHours = configurationDao.getParameter(ICECAST_YELLOW_PAGE_DOWNLOAD_DELAY_HOURS);

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
