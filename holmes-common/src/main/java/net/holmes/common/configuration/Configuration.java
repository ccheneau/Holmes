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
 * Holmes configuration.
 */
public interface Configuration {
    String DEFAULT_UPNP_SERVER_NAME = "Holmes";
    int DEFAULT_HTTP_SERVER_PORT = 8085;
    int MIN_HTTP_SERVER_PORT = 1024;
    int MAX_HTTP_SERVER_PORT = 9999;

    /**
     * Save configuraton.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    void saveConfig() throws IOException;

    /**
     * Gets UPnP server name.
     *
     * @return UPnP server name
     */
    String getUpnpServerName();

    /**
     * Sets UPnP server name.
     *
     * @param upnpServerName 
     *      new UPnP server name
     */
    void setUpnpServerName(String upnpServerName);

    /**
     * Gets http server port.
     *
     * @return http server port
     */
    Integer getHttpServerPort();

    /**
     * Sets the http server port.
     *
     * @param httpServerPort 
     *      new http server port
     */
    void setHttpServerPort(Integer httpServerPort);

    /**
     * Gets the folders.
     *
     * @param folderRootNode 
     *      folder root node
     * @return folders
     */
    List<ConfigurationNode> getFolders(RootNode folderRootNode);

    /**
     * Gets the parameter.
     *
     * @param param 
     *      parameter
     * @return parameter
     */
    Boolean getParameter(Parameter param);

    /**
     * Gets the int parameter.
     *
     * @param param 
     *      parameter
     * @return parameter as int
     */
    Integer getIntParameter(Parameter param);

    /**
     * Sets parameter.
     *
     * @param param 
     *      parameter
     * @param value 
     *      parameter value
     */
    void setParameter(Parameter param, Boolean value);
}
