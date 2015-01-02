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
package com.sun.syndication.feed.module.mediarss.types;

import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class ThumbnailTest {

    @Test
    public void testThumbnail() throws URISyntaxException, CloneNotSupportedException {
        URI url = new URI("http://thumbnail");
        Thumbnail thumbnail = new Thumbnail(url);
        assertEquals(url, thumbnail.getUrl());

        Thumbnail thumbnailClone = (Thumbnail) thumbnail.clone();
        assertEquals(url, thumbnailClone.getUrl());
    }
}
