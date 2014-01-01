/*
 * Copyright (C) 2012-2014  Cedric Cheneau
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

package net.holmes.core.http.file;

import io.netty.handler.codec.http.FullHttpRequest;
import net.holmes.core.common.NodeFile;
import net.holmes.core.common.mimetype.MimeType;

/**
 * Http file request.
 */
public final class HttpFileRequest {
    private final FullHttpRequest httpRequest;
    private final NodeFile nodeFile;
    private final MimeType mimeType;

    /**
     * Instantiates a new HttpFileRequest.
     *
     * @param httpRequest HTTP request
     * @param nodeFile    file
     * @param mimeType    mime type
     */
    public HttpFileRequest(final FullHttpRequest httpRequest, final NodeFile nodeFile, final MimeType mimeType) {
        this.httpRequest = httpRequest;
        this.nodeFile = nodeFile;
        this.mimeType = mimeType;
    }

    public NodeFile getNodeFile() {
        return nodeFile;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public FullHttpRequest getHttpRequest() {
        return httpRequest;
    }
}