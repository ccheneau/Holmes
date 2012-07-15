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
package net.holmes.core.configuration.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import net.holmes.core.configuration.ConfigurationNode;
import net.holmes.core.configuration.IConfiguration;
import net.holmes.core.configuration.Parameter;
import net.holmes.core.util.HomeDirectory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 
 */
public final class XmlConfiguration implements IConfiguration {

    private static Logger logger = LoggerFactory.getLogger(XmlConfiguration.class);

    private static final String CONF_FILE_NAME = "config.xml";

    private XmlRootNode rootNode = null;

    public XmlConfiguration() {
        loadConfig();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#loadConfig()
     */
    @Override
    public void loadConfig() {
        rootNode = null;

        XStream xs = getXStream();

        String confPath = HomeDirectory.getConfigDirectory();
        if (confPath != null) {
            String filePath = confPath + File.separator + CONF_FILE_NAME;
            File confFile = new File(filePath);
            if (confFile.exists() && confFile.canRead()) {
                InputStream in = null;
                try {
                    in = new FileInputStream(confFile);
                    rootNode = (XmlRootNode) xs.fromXML(in);
                }
                catch (FileNotFoundException e) {
                    logger.error(e.getMessage(), e);
                }
                finally {
                    try {
                        if (in != null) in.close();
                    }
                    catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
        if (rootNode == null) rootNode = new XmlRootNode();
        rootNode.check();

    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#saveConfig()
     */
    @Override
    public void saveConfig() {
        XStream xs = getXStream();

        String confPath = HomeDirectory.getConfigDirectory();
        if (confPath != null) {
            String filePath = confPath + File.separator + CONF_FILE_NAME;
            File confFile = new File(filePath);

            OutputStream out = null;
            try {
                out = new FileOutputStream(confFile);
                xs.toXML(rootNode, out);
            }
            catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
            finally {
                try {
                    if (out != null) out.close();
                }
                catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    private XStream getXStream() {
        XStream xs = new XStream(new DomDriver("UTF-8"));
        xs.alias("config", XmlRootNode.class);
        xs.alias("node", ConfigurationNode.class);

        return xs;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getUpnpServerName()
     */
    @Override
    public String getUpnpServerName() {
        String upnpServerName = this.rootNode.getUpnpServerName();
        if (upnpServerName == null || upnpServerName.trim().length() == 0) {
            return DEFAULT_UPNP_SERVER_NAME;
        }
        else {
            return upnpServerName;
        }
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#setUpnpServerName(java.lang.String)
     */
    @Override
    public void setUpnpServerName(String upnpServerName) {
        this.rootNode.setUpnpServerName(upnpServerName);
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getHttpServerPort()
     */
    @Override
    public Integer getHttpServerPort() {
        Integer httpServerPort = rootNode.getHttpServerPort();
        if (httpServerPort == null) {
            return DEFAULT_HTTP_PORT;
        }
        else {
            return httpServerPort;
        }
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#setHttpServerPort(java.lang.Integer)
     */
    @Override
    public void setHttpServerPort(Integer httpServerPort) {
        this.rootNode.setHttpServerPort(httpServerPort);

    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getVideoFolders()
     */
    @Override
    public List<ConfigurationNode> getVideoFolders() {
        return this.rootNode.getVideoFolders();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getPodcasts()
     */
    @Override
    public List<ConfigurationNode> getPodcasts() {
        return this.rootNode.getPodcasts();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getAudioFolders()
     */
    @Override
    public List<ConfigurationNode> getAudioFolders() {
        return this.rootNode.getAudioFolders();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getPictureFolders()
     */
    @Override
    public List<ConfigurationNode> getPictureFolders() {
        return this.rootNode.getPictureFolders();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getParameter(net.holmes.core.configuration.Parameter)
     */
    @Override
    public Boolean getParameter(Parameter prop) {
        return this.rootNode.getParameter(prop);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.rootNode.toString();
    }

}
