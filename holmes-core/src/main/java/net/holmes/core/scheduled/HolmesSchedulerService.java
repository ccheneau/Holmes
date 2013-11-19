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

import com.google.common.util.concurrent.AbstractScheduledService;
import net.holmes.core.common.Service;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Holder for scheduled services.
 */
public class HolmesSchedulerService implements Service {
    private final AbstractScheduledService cacheCleanerService;
    private final AbstractScheduledService icecastDownloadService;

    /**
     * Instantiates a new holmes scheduler service.
     *
     * @param cacheCleanerService cache cleaner
     */
    @Inject
    public HolmesSchedulerService(@Named("cacheCleaner") final AbstractScheduledService cacheCleanerService,
                                  @Named("icecast") final AbstractScheduledService icecastDownloadService) {
        this.cacheCleanerService = cacheCleanerService;
        this.icecastDownloadService = icecastDownloadService;
    }

    @Override
    public void start() {
        cacheCleanerService.startAsync();
        icecastDownloadService.startAsync();
    }

    @Override
    public void stop() {
        icecastDownloadService.stopAsync();
        cacheCleanerService.stopAsync();
    }
}
