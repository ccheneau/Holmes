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

package com.sun.syndication.feed.module.mediarss;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.mediarss.types.Metadata;

/**
 * This is the base module for MediaRSS.
 * <p>It represents information that can be stored at the feed level,
 * as well is a base for entry level information, as the same
 * information can apply.</p>
 *
 * @author Nathanial X. Freitas
 */
public interface MediaModule extends Module {
    //the URI of the MediaRSS specification as hosted by yahoo
    String URI = "http://search.yahoo.com/mrss/";

    /**
     * Returns Metadata associated with the feed.
     *
     * @return Returns Metadata associated with the feed.
     */
    Metadata getMetadata();
}
