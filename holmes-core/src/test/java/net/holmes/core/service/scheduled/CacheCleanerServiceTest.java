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


import net.holmes.core.business.configuration.ConfigurationDao;
import net.holmes.core.business.media.MediaManager;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static net.holmes.core.common.parameter.ConfigurationParameter.CACHE_CLEAN_DELAY_MINUTES;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.fail;

public class CacheCleanerServiceTest {

    @Test
    public void testCacheCleanerService() {
        MediaManager mediaManager = createMock(MediaManager.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(configurationDao.getParameter(CACHE_CLEAN_DELAY_MINUTES)).andReturn(1);
        mediaManager.cleanUpCache();
        expectLastCall();

        replay(mediaManager, configurationDao);

        CacheCleanerServiceTester service = new CacheCleanerServiceTester(mediaManager, configurationDao);

        try {
            service.startAsync();
            service.awaitRunning(10, SECONDS);
            service.run();
        } catch (TimeoutException e) {
            fail(e.getMessage());
        } finally {
            verify(mediaManager, configurationDao);
            if (service.isRunning()) {
                service.stopAsync();
                service.awaitTerminated();
            }
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testCacheCleanerServiceNoDelay() {
        MediaManager mediaManager = createMock(MediaManager.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(configurationDao.getParameter(CACHE_CLEAN_DELAY_MINUTES)).andReturn(0);

        replay(mediaManager, configurationDao);

        CacheCleanerServiceTester service = new CacheCleanerServiceTester(mediaManager, configurationDao);

        try {
            service.startAsync();
            service.awaitRunning(10, SECONDS);
        } catch (TimeoutException e) {
            fail(e.getMessage());
        } finally {
            verify(mediaManager, configurationDao);
            if (service.isRunning()) {
                service.stopAsync();
                service.awaitTerminated();
            }
        }
    }

    private class CacheCleanerServiceTester extends CacheCleanerService {

        public CacheCleanerServiceTester(final MediaManager mediaManager, final ConfigurationDao configurationDao) {
            super(mediaManager, configurationDao);
        }

        public void run() {
            runOneIteration();
        }
    }
}


