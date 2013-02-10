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

package net.holmes.core.backend.backbone;

import java.util.Collection;
import java.util.List;

import net.holmes.core.backend.backbone.response.ConfigurationFolder;
import net.holmes.core.backend.backbone.response.Settings;
import net.holmes.core.configuration.ConfigurationNode;

public interface BackboneManager {

    public Collection<ConfigurationFolder> getFolders(List<ConfigurationNode> configNodes);

    public ConfigurationFolder getFolder(String id, List<ConfigurationNode> configNodes);

    public void addFolder(ConfigurationFolder folder, List<ConfigurationNode> configNodes, boolean podcast);

    public void editFolder(String id, ConfigurationFolder folder, List<ConfigurationNode> configNodes, boolean podcast);

    public void removeFolder(String id, List<ConfigurationNode> configNodes, boolean podcast);

    public Settings getSettings();

    public void updateSettings(Settings settings);
}