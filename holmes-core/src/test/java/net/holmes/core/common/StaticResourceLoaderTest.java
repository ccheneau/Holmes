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

package net.holmes.core.common;

import org.fourthline.cling.model.meta.Icon;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;

import static net.holmes.core.common.StaticResourceLoader.StaticResourceDir.*;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for ResourceLoader
 */
public class StaticResourceLoaderTest {

    @Test
    public void testTestPrivateConstructor() throws Exception {
        Constructor<StaticResourceLoader> cnt = StaticResourceLoader.class.getDeclaredConstructor();
        cnt.setAccessible(true);
        cnt.newInstance();
    }

    @Test
    public void testGetData() throws IOException {
        byte[] data = StaticResourceLoader.getData(UPNP, "icon-32.png");
        assertNotNull(data);
    }

    @Test(expected = IOException.class)
    public void testGetBadData() throws IOException {
        StaticResourceLoader.getData(SYSTRAY, "bad_resource.png");
    }

    @Test
    public void testGetUpnpLargeIcon() throws IOException {
        Icon icon = StaticResourceLoader.getUpnpLargeIcon();
        assertNotNull(icon);
    }

    @Test
    public void testGetUpnpSmallIcon() throws IOException {
        Icon icon = StaticResourceLoader.getUpnpSmallIcon();
        assertNotNull(icon);
    }
}
