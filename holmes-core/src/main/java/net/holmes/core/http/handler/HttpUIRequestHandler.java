/*
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

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import net.holmes.core.common.NodeFile;
import net.holmes.core.common.mimetype.MimeType;
import net.holmes.core.common.mimetype.MimeTypeManager;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Handler for Holmes UI pages.
 */
public final class HttpUIRequestHandler extends HttpRequestHandler {
    private static final String DEFAULT_PAGE = "index.html";
    private final String uiDirectory;
    private final MimeTypeManager mimeTypeManager;

    /**
     * Instantiates a new http ui request handler.
     *
     * @param mimeTypeManager mime type manager
     * @param uiDirectory     UI base directory
     */
    @Inject
    public HttpUIRequestHandler(final MimeTypeManager mimeTypeManager, @Named("uiDirectory") final String uiDirectory) {
        this.mimeTypeManager = mimeTypeManager;
        this.uiDirectory = uiDirectory;
    }

    @Override
    boolean accept(final FullHttpRequest request) {
        return mimeTypeManager.getMimeType(getFileName(request)) != null;
    }

    @Override
    HttpRequestFile getRequestFile(final FullHttpRequest request) throws HttpRequestException {
        // Get file name
        String fileName = getFileName(request);

        // Get file and mime type
        NodeFile file = new NodeFile(uiDirectory, fileName);
        MimeType mimeType = mimeTypeManager.getMimeType(fileName);

        return new HttpRequestFile(file, mimeType);
    }

    /**
     * Get file name from Http request.
     *
     * @param request Http request
     * @return file name
     */
    private String getFileName(final FullHttpRequest request) {
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        String fileName = decoder.path().trim();
        if ("/".equals(fileName))
            fileName += DEFAULT_PAGE;

        return fileName;
    }
}
