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

package net.holmes.core.service.http;

import com.google.common.collect.Maps;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import net.holmes.core.business.configuration.ConfigurationDao;
import org.junit.Test;

import static net.holmes.core.common.ConfigurationParameter.HTTP_SERVER_PORT;
import static org.easymock.EasyMock.*;

public class HttpServerTest {

    @Test
    public void testHttpServer() {
        Injector injector = createMock(Injector.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        HttpServer httpServer = new HttpServer(injector, configurationDao);

        expect(configurationDao.getParameter(HTTP_SERVER_PORT)).andReturn(8080).atLeastOnce();
        expect(injector.getBindings()).andReturn(Maps.<Key<?>, Binding<?>>newHashMap()).atLeastOnce();

        replay(injector, configurationDao);
        httpServer.start();
        httpServer.stop();
        verify(injector, configurationDao);
    }
}
