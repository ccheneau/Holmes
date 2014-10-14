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

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import net.holmes.core.business.configuration.ConfigurationDao;
import org.junit.Test;

import java.util.HashMap;

import static net.holmes.core.common.ConfigurationParameter.*;
import static org.easymock.EasyMock.*;

public class HttpServiceTest {

    @Test
    public void testHttpServer() {
        Injector injector = createMock(Injector.class);
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(configurationDao.getParameter(HTTP_SERVER_PORT)).andReturn(8080).atLeastOnce();
        expect(configurationDao.getParameter(HTTP_SERVER_BOSS_THREADS)).andReturn(0).atLeastOnce();
        expect(configurationDao.getParameter(HTTP_SERVER_WORKER_THREADS)).andReturn(0).atLeastOnce();
        expect(injector.getBindings()).andReturn(new HashMap<Key<?>, Binding<?>>(0)).atLeastOnce();

        replay(injector, configurationDao);
        try {
            HttpService httpService = new HttpService(injector, configurationDao);
            httpService.start();
            httpService.stop();
        } finally {
            verify(injector, configurationDao);
        }
    }
}
