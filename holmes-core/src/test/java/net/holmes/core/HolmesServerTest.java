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

package net.holmes.core;

import com.google.common.eventbus.DeadEvent;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.holmes.core.common.event.MediaEvent;
import net.holmes.core.test.TestModule;
import org.junit.Before;
import org.junit.Test;

public class HolmesServerTest {

    private HolmesServer holmesServer;
    private HolmesServer holmesServer2;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
        holmesServer = injector.getInstance(HolmesServer.class);
        holmesServer2 = injector.getInstance(HolmesServer.class);
    }

    @Test
    public void testHolmesServer() {
        holmesServer.start();
        holmesServer.stop();
    }

    @Test
    public void testHolmesServerStartTwice() {
        try {
            holmesServer.start();
            holmesServer.start();
        } finally {
            holmesServer.stop();
        }
    }

    @Test(expected = RuntimeException.class)
    public void testHolmesServerStartTwoServers() {
        try {
            holmesServer.start();
            holmesServer2.start();
        } finally {
            holmesServer.stop();
        }
    }

    @Test
    public void testHandleDeadEvent() {
        holmesServer.handleDeadEvent(new DeadEvent("", new MediaEvent(MediaEvent.MediaEventType.UNKNOWN, "parameter")));
    }
}
