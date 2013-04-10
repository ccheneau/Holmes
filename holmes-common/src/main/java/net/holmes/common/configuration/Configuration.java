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
package net.holmes.common.configuration;

import java.io.IOException;
import java.util.List;

import net.holmes.common.media.RootNode;

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
public interface Configuration {
    String DEFAULT_UPNP_SERVER_NAME = "Holmes";
    int DEFAULT_HTTP_SERVER_PORT = 8085;

    void saveConfig() throws IOException;

    String getUpnpServerName();

    void setUpnpServerName(String upnpServerName);

    Integer getHttpServerPort();

    void setHttpServerPort(Integer httpServerPort);

    List<ConfigurationNode> getFolders(RootNode rootNode);

    Boolean getParameter(Parameter param);

    Integer getIntParameter(Parameter prop);

    void setParameter(Parameter param, Boolean value);
}