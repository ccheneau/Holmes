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
package net.holmes.core.http.request;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * The Class HttpRequestContentHandler.
 */
public final class HttpRequestException extends Exception
{
    private static final long serialVersionUID = 1022835130881123877L;

    /** The status. */
    HttpResponseStatus status;

    /**
     * Instantiates a new Http request exception.
     *
     * @param message the message
     * @param status the status
     */
    public HttpRequestException(String message, HttpResponseStatus status)
    {
        super(message);
        this.status = status;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public HttpResponseStatus getStatus()
    {
        return status;
    }
}
