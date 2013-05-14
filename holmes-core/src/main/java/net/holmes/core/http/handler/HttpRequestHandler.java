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
package net.holmes.core.http.handler;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

/**
 * Http request handler.
 */
public interface HttpRequestHandler {
    Integer CHUNK_SIZE = 8192;

    /**
     * Check if handler can process request.
     *
     * @param requestPath request path
     * @param method Http method (GET, POST...)
     * @return true if handler can process request
     */
    boolean canProcess(String requestPath, HttpMethod method);

    /**
     * Process request.
     * 
     * @param request Http request
     * @param channel Channel
     * @throws HttpRequestException Http request exception
     */
    void processRequest(FullHttpRequest request, Channel channel) throws HttpRequestException;
}
