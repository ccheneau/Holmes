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

package net.holmes.core.manager.media.dao.index;

import net.holmes.core.manager.configuration.ConfigurationNode;
import net.holmes.core.manager.media.model.RootNode;

/**
 * Media index element factory.
 */
public final class MediaIndexElementFactory {

    private MediaIndexElementFactory() {
    }

    /**
     * Gets the media index element for configuration node.
     *
     * @param rootNode   root node
     * @param configNode config node
     * @return media index element
     */
    public static MediaIndexElement buildConfigMediaIndexElement(final RootNode rootNode, final ConfigurationNode configNode) {
        return new MediaIndexElement(rootNode.getId(), rootNode.getMediaType().getValue(), null, configNode.getPath(), configNode.getLabel(),
                rootNode.isLocalPath(), true);
    }
}
