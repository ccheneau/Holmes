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

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;

public class UniqueIdGeneratorTest {

    @Test
    public void testTestPrivateConstructor() throws Exception {
        Constructor<UniqueIdGenerator> cnt = UniqueIdGenerator.class.getDeclaredConstructor();
        cnt.setAccessible(true);
        cnt.newInstance();
    }

    @Test
    public void tesNewUniqueId() {
        Assert.assertNotEquals(UniqueIdGenerator.newUniqueId(), UniqueIdGenerator.newUniqueId());
    }
}
