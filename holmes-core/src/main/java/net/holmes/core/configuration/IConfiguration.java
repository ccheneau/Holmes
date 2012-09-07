/**
* Copyright (C) 2012  Cedric Cheneau
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
package net.holmes.core.configuration;

import java.util.List;

/**
 * Holmes configuration contains:
 * <ul>
 * <li>UPnP server name</li>
 * <li>HTTP server port</li>
 * <li>video folders</li>
 * <li>audio folders</li>
 * <li>picture folder</li>
 * <li>pod-cast URLs</li>
 * <li>misc. parameters</li>
 * </ul>
 *
 */
public interface IConfiguration {

    static final String DEFAULT_UPNP_SERVER_NAME = "Holmes";
    static final int DEFAULT_HTTP_SERVER_PORT = 8085;

    public void loadConfig();

    public void saveConfig();

    public String getUpnpServerName();

    public void setUpnpServerName(String upnpServerName);

    public Integer getHttpServerPort();

    public void setHttpServerPort(Integer httpServerPort);

    public List<ConfigurationNode> getVideoFolders();

    public List<ConfigurationNode> getPodcasts();

    public List<ConfigurationNode> getAudioFolders();

    public List<ConfigurationNode> getPictureFolders();

    public Boolean getParameter(Parameter param);

    public void setParameter(Parameter param, Boolean value);
}