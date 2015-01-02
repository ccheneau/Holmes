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

package net.holmes.core.business.media;

import net.holmes.core.business.media.model.AbstractNode;

import java.util.Collection;

/**
 * Media search request
 */
public final class MediaSearchRequest {
    private final AbstractNode parentNode;
    private final Collection<String> availableMimeTypes;

    /**
     * Instantiates a new media search request.
     *
     * @param parentNode         parent node
     * @param availableMimeTypes available mime types.
     */
    public MediaSearchRequest(final AbstractNode parentNode, final Collection<String> availableMimeTypes) {
        this.parentNode = parentNode;
        this.availableMimeTypes = availableMimeTypes;
    }

    /**
     * Get parent node.
     *
     * @return parent node
     */
    public AbstractNode getParentNode() {
        return parentNode;
    }

    /**
     * Get available mime types.
     *
     * @return available mime types
     */
    public Collection<String> getAvailableMimeTypes() {
        return availableMimeTypes;
    }
}
