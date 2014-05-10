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
import net.holmes.core.business.media.dao.icecast.IcecastDao;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static net.holmes.core.common.parameter.ConfigurationParameter.ICECAST_YELLOW_PAGE_DOWNLOAD_DELAY_HOURS;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.fail;

public class IcecastDownloadServiceTest {

    @Test
    public void testIcecastDownloadService() {
        IcecastDao icecastDao = createMock(IcecastDao.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(configurationDao.getParameter(ICECAST_YELLOW_PAGE_DOWNLOAD_DELAY_HOURS)).andReturn(1);
        icecastDao.checkYellowPage();
        expectLastCall().atLeastOnce();

        replay(icecastDao, configurationDao);

        IcecastDownloadServiceTester service = new IcecastDownloadServiceTester(icecastDao, configurationDao);

        try {
            service.startAsync();
            service.awaitRunning(10, SECONDS);
            service.run();
        } catch (TimeoutException e) {
            fail(e.getMessage());
        } finally {
            verify(icecastDao, configurationDao);
            if (service.isRunning()) {
                service.stopAsync();
                service.awaitTerminated();
            }
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testIcecastDownloadServiceNoDelay() {
        IcecastDao icecastDao = createMock(IcecastDao.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(configurationDao.getParameter(ICECAST_YELLOW_PAGE_DOWNLOAD_DELAY_HOURS)).andReturn(0);

        replay(icecastDao, configurationDao);

        IcecastDownloadServiceTester service = new IcecastDownloadServiceTester(icecastDao, configurationDao);

        try {
            service.startAsync();
            service.awaitRunning(10, SECONDS);
        } catch (TimeoutException e) {
            fail(e.getMessage());
        } finally {
            verify(icecastDao, configurationDao);
            if (service.isRunning()) {
                service.stopAsync();
                service.awaitTerminated();
            }
        }
    }

    private class IcecastDownloadServiceTester extends IcecastDownloadService {

        public IcecastDownloadServiceTester(final IcecastDao icecastDao, final ConfigurationDao configurationDao) {
            super(icecastDao, configurationDao);
        }

        public void run() {
            runOneIteration();
        }
    }
}
