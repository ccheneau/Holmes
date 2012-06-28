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
package net.holmes.core.backend.model;

import java.io.Serializable;

/**
 * The Class ConfigurationResponse.
 */
public class ConfigurationResponse implements Serializable
{
    private static final long serialVersionUID = 337172491421293170L;

    /** The server name. */
    private String serverName;

    /** The http server port. */
    private Integer httpServerPort;

    /** The log level. */
    private String logLevel;

    /**
     * Gets the server name.
     *
     * @return the server name
     */
    public String getServerName()
    {
        return serverName;
    }

    /**
     * Sets the server name.
     *
     * @param serverName the new server name
     */
    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    /**
     * Gets the http server port.
     *
     * @return the http server port
     */
    public Integer getHttpServerPort()
    {
        return httpServerPort;
    }

    /**
     * Sets the http server port.
     *
     * @param httpServerPort the new http server port
     */
    public void setHttpServerPort(Integer httpServerPort)
    {
        this.httpServerPort = httpServerPort;
    }

    /**
     * Gets the log level.
     *
     * @return the log level
     */
    public String getLogLevel()
    {
        return logLevel;
    }

    /**
     * Sets the log level.
     *
     * @param logLevel the new log level
     */
    public void setLogLevel(String logLevel)
    {
        this.logLevel = logLevel;
    }
}
