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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

import net.holmes.core.util.LogUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * The Class XmlConfigurationImpl.
 */
public final class XmlConfiguration implements IConfiguration
{
    private static Logger logger = LoggerFactory.getLogger(XmlConfiguration.class);

    /** The Constant CONF_FILE_NAME. */
    private static final String CONF_FILE_NAME = "config.xml";

    /** The Constant HOME_CONF_FOLDER. */
    private static final String HOME_CONF_FOLDER = "conf";

    /** The Constant HOME_MEDIA_FOLDER. */
    private static final String HOME_MEDIA_FOLDER = "media";

    /** The Constant HOME_MEDIA_FOLDER. */
    private static final String HOME_SITE_FOLDER = "site";

    /** The Constant DEFAULT_HTTP_PORT. */
    private static final int DEFAULT_HTTP_PORT = 8085;

    /** The configuration. */
    private Configuration config = null;

    /**
     * Instantiates a new configuration.
     */
    public XmlConfiguration()
    {
        loadConfig();
    }

    /**
     * Load configuration from XML file.
     */
    private void loadConfig()
    {
        config = null;

        XStream xs = getXStream();

        String confPath = getHomeConfigDirectory();
        logger.info("Read config in directory: " + confPath);
        if (confPath != null)
        {
            String filePath = confPath + File.separator + CONF_FILE_NAME;
            File confFile = new File(filePath);
            if (confFile.exists() && confFile.canRead())
            {
                InputStream in = null;
                try
                {
                    in = new FileInputStream(confFile);
                    config = (Configuration) xs.fromXML(in);
                }
                catch (FileNotFoundException e)
                {
                    logger.error(e.getMessage(), e);
                }
                finally
                {
                    try
                    {
                        if (in != null) in.close();
                    }
                    catch (IOException e)
                    {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
        if (config == null) config = new Configuration();
        if (config.getVideoFolders() == null) config.setVideoFolders(new LinkedList<ContentFolder>());
        if (config.getAudioFolders() == null) config.setAudioFolders(new LinkedList<ContentFolder>());
        if (config.getPodcasts() == null) config.setPodcasts(new LinkedList<ContentFolder>());
        if (config.getPictureFolders() == null) config.setPictureFolders(new LinkedList<ContentFolder>());
        if (config.getHttpServerPort() == null) config.setHttpServerPort(DEFAULT_HTTP_PORT);

        LogUtil.setLevel(config.getLogLevel());
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#saveConfig()
     */
    @Override
    public void saveConfig()
    {
        XStream xs = getXStream();

        String confPath = getHomeConfigDirectory();
        if (confPath != null)
        {
            String filePath = confPath + File.separator + CONF_FILE_NAME;
            File confFile = new File(filePath);

            OutputStream out = null;
            try
            {
                out = new FileOutputStream(confFile);
                xs.toXML(config, out);
            }
            catch (FileNotFoundException e)
            {
                logger.error(e.getMessage(), e);
            }
            finally
            {
                try
                {
                    if (out != null) out.close();
                }
                catch (IOException e)
                {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getConfig()
     */
    @Override
    public Configuration getConfig()
    {
        return config;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getHomeDirectory()
     */
    @Override
    public String getHomeDirectory()
    {
        String homePath = null;
        if (System.getProperty("net.holmes.home") != null)
        {
            homePath = checkPath(System.getProperty("net.holmes.home"));
        }

        if (homePath != null)
        {
            File homeDir = new File(homePath);
            if (homeDir.exists() && homeDir.isDirectory() && homeDir.canWrite())
            {
                return homePath;
            }
        }

        throw new RuntimeException("net.holmes.home variable not defined or incorrect");
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getHomeConfigDirectory()
     */
    @Override
    public String getHomeConfigDirectory()
    {
        return getHomeSubDirectory(HOME_CONF_FOLDER);
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getHomeMediaDirectory()
     */
    @Override
    public String getHomeMediaDirectory()
    {
        return getHomeSubDirectory(HOME_MEDIA_FOLDER);
    }

    /* (non-Javadoc)
     * @see net.holmes.core.configuration.IConfiguration#getHomeSiteDirectory()
     */
    @Override
    public String getHomeSiteDirectory()
    {
        return getHomeSubDirectory(HOME_SITE_FOLDER);
    }

    /**
     * Gets the home sub directory.
     *
     * @param subDirName the sub dir name
     * @return the home sub directory
     */
    private String getHomeSubDirectory(String subDirName)
    {
        String homeSubDirectory = getHomeDirectory() + File.separator + subDirName;
        File confDir = new File(homeSubDirectory);
        if (!confDir.exists())
        {
            confDir.mkdir();
        }
        if (confDir.exists() && confDir.isDirectory() && confDir.canWrite())
        {
            return homeSubDirectory;
        }
        return null;
    }

    /**
     * Check path.
     *
     * @param path the path
     * @return the string
     */
    private String checkPath(String path)
    {
        File fPath = new File(path);
        if (fPath.exists() && fPath.isDirectory() && fPath.canWrite())
        {
            return path;
        }
        return null;
    }

    /**
     * Gets the x stream.
     *
     * @return the x stream
     */
    private XStream getXStream()
    {
        XStream xs = new XStream(new DomDriver("UTF-8"));
        xs.alias("config", Configuration.class);
        xs.alias("contentFolder", ContentFolder.class);

        return xs;
    }
}
