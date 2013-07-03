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

package net.holmes.core.upnp;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.holmes.core.common.Service;
import net.holmes.core.test.TestModule;
import org.fourthline.cling.UpnpService;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Named;

import static org.easymock.EasyMock.*;

public class UpnpServerTest {

    @Inject
    UpnpService upnpService;

    @Inject
    @Named("upnp")
    Service upnpServer;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    @Test
    public void testUPnPServer() {
        upnpService.shutdown();
        expectLastCall();

        replay(upnpService);
        upnpServer.start();
        upnpServer.stop();
        verify(upnpService);
    }

}
