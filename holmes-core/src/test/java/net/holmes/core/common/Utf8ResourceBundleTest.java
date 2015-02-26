/*
 * Copyright (C) 2012-2015  Cedric Cheneau
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

import java.util.Enumeration;
import java.util.ResourceBundle;

import static org.junit.Assert.*;

public class Utf8ResourceBundleTest {

    @Test
    public void testUtf8ResourceBundle() {
        ResourceBundle bundle = new Utf8ResourceBundle("messageTest");
        assertNotNull(bundle);

        Enumeration<String> keys = bundle.getKeys();
        assertNotNull(keys);

        String key = keys.nextElement();
        assertEquals(key, "backend.settings.server.name.error");

        String value = bundle.getString(key);
        assertNotNull(value);
        assertEquals(value, "Server name is mandatory");
    }
}
