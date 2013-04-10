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

package net.holmes.core.backend;

import java.util.Collection;

import net.holmes.common.media.RootNode;
import net.holmes.core.backend.response.ConfigurationFolder;
import net.holmes.core.backend.response.IndexElement;
import net.holmes.core.backend.response.Settings;

/**
 * Manager for requests coming from UI
 */
public interface BackendManager {

    Collection<ConfigurationFolder> getFolders(RootNode rootNode);

    ConfigurationFolder getFolder(String id, RootNode rootNode);

    void addFolder(ConfigurationFolder folder, RootNode rootNode);

    void editFolder(String id, ConfigurationFolder folder, RootNode rootNode);

    void removeFolder(String id, RootNode rootNode);

    Settings getSettings();

    void saveSettings(Settings settings);

    Collection<IndexElement> getMediaIndexElements();

    void scanAllMedia();
}