/**
* Copyright (c) 2012 Cedric Cheneau
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
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

    public static final String DEFAULT_UPNP_SERVER_NAME = "Holmes";
    public static final int DEFAULT_HTTP_PORT = 8085;

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

}