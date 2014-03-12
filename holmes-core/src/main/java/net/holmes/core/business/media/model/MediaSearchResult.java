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

package net.holmes.core.business.media.model;

import java.util.Collection;

/**
 * Result a search media via media search request
 */
public final class MediaSearchResult {
    private final Collection<AbstractNode> childNodes;

    /**
     * Instantiates a new media search result.
     *
     * @param childNodes child nodes
     */
    public MediaSearchResult(Collection<AbstractNode> childNodes) {
        this.childNodes = childNodes;
    }

    /**
     * Get child nodes.
     *
     * @return child nodes
     */
    public Collection<AbstractNode> getChildNodes() {
        return childNodes;
    }

    /**
     * Get total count.
     *
     * @return total count
     */
    public int getTotalCount() {
        return childNodes.size();
    }
}
