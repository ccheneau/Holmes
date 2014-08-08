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

package net.holmes.core.service.http;

import io.netty.handler.codec.http.FullHttpRequest;
import net.holmes.core.business.mimetype.model.MimeType;

import java.io.File;

/**
 * Http file request.
 */
public final class HttpFileRequest {
    private final FullHttpRequest httpRequest;
    private final File file;
    private final MimeType mimeType;
    private final boolean staticResource;

    /**
     * Instantiates a new HttpFileRequest.
     *
     * @param httpRequest    HTTP request
     * @param file           file
     * @param mimeType       mime type
     * @param staticResource request for a static resource
     */
    public HttpFileRequest(final FullHttpRequest httpRequest, final File file, final MimeType mimeType, final boolean staticResource) {
        this.httpRequest = httpRequest;
        this.file = file;
        this.mimeType = mimeType;
        this.staticResource = staticResource;
    }

    public File getFile() {
        return file;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public FullHttpRequest getHttpRequest() {
        return httpRequest;
    }

    public boolean isStaticResource() {
        return staticResource;
    }
}