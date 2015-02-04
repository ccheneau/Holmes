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

import net.holmes.core.business.mimetype.model.MimeType;

/**
 * Abstract node with mime type (i.e. url or content)
 */
public abstract class MimeTypeNode extends AbstractNode {

    final MimeType mimeType;

    /**
     * Instantiates a new MimeTyeNode.
     *
     * @param type     node type
     * @param id       node id
     * @param parentId parent node id
     * @param name     node name
     * @param mimeType mime type
     */
    MimeTypeNode(final NodeType type, final String id, final String parentId, final String name, final MimeType mimeType) {
        super(type, id, parentId, name);
        this.mimeType = mimeType;
    }

    /**
     * Gets mime type.
     *
     * @return mime type
     */
    public MimeType getMimeType() {
        return mimeType;
    }
}
