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

public interface IConfiguration {
    /**
     * Location of configuration folder relative to Holmes home directory
     */
    public static final String HOME_CONF_FOLDER = "conf";

    /**
     * Location of site folder relative to Holmes home directory
     */
    public static final String HOME_SITE_FOLDER = "site";

    /**
     * Save configuration
     */
    public abstract void saveConfig();

    /**
     * Get {@link net.holmes.core.configuration.Configuration} 
     */
    public abstract Configuration getConfig();

    /**
     * Get Holmes home directory
     */
    public abstract String getHomeDirectory();

    /**
     * Get directory that contains Holmes configuration (log file and {@link net.holmes.core.configuration.Configuration} file)
     */
    public abstract String getHomeConfigDirectory();

    /**
     * Get directory that contains sources for Holmes administration site
     */
    public abstract String getHomeSiteDirectory();
}