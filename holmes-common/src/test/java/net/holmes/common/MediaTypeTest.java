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

package net.holmes.common;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * MediaType Tester.
 */
public class MediaTypeTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: getValue()
     */
    @Test
    public void testGetValue() throws Exception {
        Assert.assertEquals(MediaType.TYPE_VIDEO.getValue(), "video");
    }

    /**
     * Method: getByValue(String mediaTypeValue)
     */
    @Test
    public void testGetByValue() throws Exception {
        Assert.assertEquals(MediaType.getByValue("video"), MediaType.TYPE_VIDEO);
        Assert.assertEquals(MediaType.getByValue("non existing value"), MediaType.TYPE_NONE);
    }
}
