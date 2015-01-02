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

package net.holmes.core.business.media.model;

import net.holmes.core.common.MediaType;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * RootNode Tester.
 */
public class RootNodeTest {

    /**
     * Method: getId()
     */
    @Test
    public void testGetId() throws Exception {
        assertEquals(RootNode.VIDEO.getId(), "1_VIDEOS");
    }

    /**
     * Method: getParentId()
     */
    @Test
    public void testGetParentId() throws Exception {
        assertEquals(RootNode.VIDEO.getParentId(), "0");
    }

    /**
     * Method: getMediaType()
     */
    @Test
    public void testGetMediaType() throws Exception {
        assertEquals(RootNode.VIDEO.getMediaType(), MediaType.TYPE_VIDEO);
    }

    /**
     * Method: getBundleKey()
     */
    @Test
    public void testGetBundleKey() throws Exception {
        assertNotNull(RootNode.VIDEO.getBundleKey());
    }

    /**
     * Method: getById(final String id)
     */
    @Test
    public void testGetById() throws Exception {
        assertEquals(RootNode.getById("1_VIDEOS"), RootNode.VIDEO);
        assertEquals(RootNode.getById("non existing root node"), RootNode.NONE);
    }


}
