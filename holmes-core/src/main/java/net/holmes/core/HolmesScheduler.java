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

package net.holmes.core;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.util.concurrent.AbstractScheduledService;

/**
 * Manages scheduled services
 */
public class HolmesScheduler implements Server {

    private final AbstractScheduledService mediaIndexCleanerService;
    private final AbstractScheduledService podcastCacheCleanerService;
    private final AbstractScheduledService mediaScannerService;

    @Inject
    public HolmesScheduler(@Named("mediaIndexCleaner") AbstractScheduledService mediaIndexCleanerService, //
            @Named("podcastCacheCleaner") AbstractScheduledService podcastCacheCleanerService, //
            @Named("mediaScanner") AbstractScheduledService mediaScannerService) {
        this.mediaIndexCleanerService = mediaIndexCleanerService;
        this.podcastCacheCleanerService = podcastCacheCleanerService;
        this.mediaScannerService = mediaScannerService;
    }

    @Override
    public void start() {
        mediaIndexCleanerService.start();
        podcastCacheCleanerService.start();
        mediaScannerService.start();
    }

    @Override
    public void stop() {
        mediaScannerService.stop();
        podcastCacheCleanerService.stop();
        mediaIndexCleanerService.stop();
    }
}
