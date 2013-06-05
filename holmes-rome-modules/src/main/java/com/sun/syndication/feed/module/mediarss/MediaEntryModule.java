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

import com.sun.syndication.feed.module.mediarss.types.MediaContent;
import com.sun.syndication.feed.module.mediarss.types.MediaGroup;

/**
 * Represents entry/item level information.
 *
 * @author cooper
 */
public interface MediaEntryModule extends MediaModule {
    /**
     * Returns the MediaContent items for the entry.
     *
     * @return Returns the MediaContent items for the entry.
     */
    MediaContent[] getMediaContents();

    /**
     * Returns the media groups for the entry.
     *
     * @return Returns the media groups for the entry.
     */
    MediaGroup[] getMediaGroups();
}
