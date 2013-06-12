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

package net.holmes.common.configuration;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Parameter Tester.
 */
public class ParameterTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: getName()
     */
    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals(Parameter.PREPEND_PODCAST_ENTRY_NAME.getName(), "prepend_podcast_entry_name");
    }

    /**
     * Method: getDefaultValue()
     */
    @Test
    public void testGetDefaultValue() throws Exception {
        Assert.assertEquals(Parameter.PREPEND_PODCAST_ENTRY_NAME.getDefaultValue(), "false");
    }
}
