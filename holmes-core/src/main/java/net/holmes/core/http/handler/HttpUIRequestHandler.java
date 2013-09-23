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

import com.google.common.base.Strings;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import net.holmes.core.common.NodeFile;
import net.holmes.core.common.mimetype.MimeType;
import net.holmes.core.common.mimetype.MimeTypeManager;

import javax.inject.Inject;
import javax.inject.Named;

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;

/**
 * Handler for Holmes UI pages.
 */
public final class HttpUIRequestHandler extends HttpRequestHandler {
    private static final String DEFAULT_PAGE = "index.html";
    private static final String BACKEND_REQUEST_PATH = "/backend";
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
    boolean accept(final String requestPath, final HttpMethod method) {
        return method.equals(HttpMethod.GET) && !requestPath.startsWith(BACKEND_REQUEST_PATH);
    }

    @Override
    HttpRequestFile getRequestFile(final FullHttpRequest request) throws HttpRequestException {
        // Get file name
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        String fileName = decoder.path().trim();
        if ("/".equals(fileName))
            fileName += DEFAULT_PAGE;

        if (Strings.isNullOrEmpty(fileName))
            throw new HttpRequestException("file name is null", NOT_FOUND);

        // Get file and mime type
        NodeFile file = new NodeFile(uiDirectory, fileName);
        MimeType mimeType = mimeTypeManager.getMimeType(fileName);

        return new HttpRequestFile(file, mimeType);
    }
}
