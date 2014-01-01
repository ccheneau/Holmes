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

import org.junit.Test;

import java.io.IOException;

import static net.holmes.core.common.StaticResourceLoader.StaticResourceDir.SYSTRAY;
import static net.holmes.core.common.StaticResourceLoader.StaticResourceDir.UPNP;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for ResourceLoader
 */
public class StaticResourceLoaderTest {

    @Test
    public void testGetResource() throws IOException {
        byte[] data = StaticResourceLoader.getData(UPNP, "icon-32.png");
        assertNotNull(data);
    }

    @Test(expected = IOException.class)
    public void testGetBadResource() throws IOException {
        StaticResourceLoader.getData(SYSTRAY, "bad_resource.png");
    }
}
