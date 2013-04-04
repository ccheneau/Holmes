/**
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

package net.holmes.core.media.index;

import net.holmes.common.configuration.ConfigurationNode;
import net.holmes.common.media.RootNode;

/**
 * Factory for media index elements
 */
public final class MediaIndexElementFactory {

    private MediaIndexElementFactory() {
    }

    public static MediaIndexElement get(RootNode rootNode, ConfigurationNode configNode) {
        return new MediaIndexElement(rootNode.getId(), rootNode.getMediaType().getValue(), configNode.getPath(), configNode.getLabel(),
                rootNode != RootNode.PODCAST);
    }
}
