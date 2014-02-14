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
package com.sun.syndication.feed.module.mediarss;

import com.sun.syndication.feed.module.mediarss.types.Metadata;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class MediaModuleImplTest {

    @Test
    public void testMediaModuleImpl() {
        MediaModuleImpl mediaModule = new MediaModuleImpl();
        assertNotNull(mediaModule.getInterface());
    }

    @Test
    public void testCopyFrom() {
        MediaModuleImpl mediaModule1 = new MediaModuleImpl();
        mediaModule1.setMetadata(new Metadata());
        MediaModuleImpl mediaModule2 = new MediaModuleImpl();
        mediaModule2.copyFrom(mediaModule1);
    }
}
