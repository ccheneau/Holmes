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

package net.holmes.core.http.handler;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.netty.handler.codec.http.HttpMethod;
import net.holmes.core.TestModule;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Named;

import static org.junit.Assert.*;

public class HttpBackendRequestHandlerTest {

    @Inject
    @Named("backend")
    private HttpRequestHandler backendRequestHandler;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    @Test
    public void testHttpBackendRequestHandler() {
        assertNotNull(backendRequestHandler);
    }

    @Test
    public void testCanProcess() {
        assertTrue(backendRequestHandler.canProcess("/backend/request", HttpMethod.GET));
        assertTrue(backendRequestHandler.canProcess("/backend/request", HttpMethod.POST));
        assertFalse(backendRequestHandler.canProcess("bad_request", HttpMethod.GET));
        assertFalse(backendRequestHandler.canProcess("bad_request", HttpMethod.POST));
    }
}
