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

package net.holmes.core.service;

import net.holmes.core.business.configuration.ConfigurationManager;
import net.holmes.core.business.version.VersionManager;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static net.holmes.core.common.ConfigurationParameter.RELEASE_CHECK_DELAY_HOURS;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.fail;

public class ReleaseCheckServiceTest {

    @Test
    public void testReleaseCheckService() {
        VersionManager versionManager = createMock(VersionManager.class);
        ConfigurationManager configurationManager = createMock(ConfigurationManager.class);

        expect(configurationManager.getParameter(RELEASE_CHECK_DELAY_HOURS)).andReturn(1);
        versionManager.updateRemoteReleaseInfo();
        expectLastCall().atLeastOnce();

        replay(versionManager, configurationManager);

        ReleaseCheckServiceTester service = new ReleaseCheckServiceTester(versionManager, configurationManager);

        try {
            service.start();
            service.awaitRunning(10, SECONDS);
            service.run();
        } catch (TimeoutException e) {
            fail(e.getMessage());
        } finally {
            verify(versionManager, configurationManager);
            if (service.isRunning()) {
                service.stop();
                service.awaitTerminated();
            }
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testReleaseCheckServiceNoDelay() {
        VersionManager versionManager = createMock(VersionManager.class);
        ConfigurationManager configurationManager = createMock(ConfigurationManager.class);

        expect(configurationManager.getParameter(RELEASE_CHECK_DELAY_HOURS)).andReturn(0);

        replay(versionManager, configurationManager);

        ReleaseCheckServiceTester service = new ReleaseCheckServiceTester(versionManager, configurationManager);

        try {
            service.start();
            service.awaitRunning(10, SECONDS);
        } catch (TimeoutException e) {
            fail(e.getMessage());
        } finally {
            verify(versionManager, configurationManager);
            if (service.isRunning()) {
                service.stop();
                service.awaitTerminated();
            }
        }
    }

    private class ReleaseCheckServiceTester extends ReleaseCheckService {

        public ReleaseCheckServiceTester(final VersionManager versionManager, final ConfigurationManager configurationManager) {
            super(versionManager, configurationManager);
        }

        public void run() {
            runOneIteration();
        }
    }
}
