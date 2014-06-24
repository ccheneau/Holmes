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

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import net.holmes.core.business.media.MediaManager;
import net.holmes.core.business.media.model.AbstractNode;
import net.holmes.core.business.media.model.ContentNode;
import net.holmes.core.business.mimetype.MimeTypeManager;
import net.holmes.core.common.MimeType;
import net.holmes.core.service.http.route.HttpRoute;
import net.holmes.core.service.http.route.HttpRouteManager;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.util.List;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static net.holmes.core.common.Constants.HTTP_CONTENT_ID;
import static net.holmes.core.common.Constants.HTTP_CONTENT_REQUEST_PATH;

/**
 * Decode FullHttpRequest to HttpFileRequest
 */
public final class HttpFileRequestDecoder extends MessageToMessageDecoder<FullHttpRequest> {
    private final MediaManager mediaManager;
    private final MimeTypeManager mimeTypeManager;
    private final HttpRouteManager routeManager;
    private final String uiDirectory;

    /**
     * Instantiates a new HTTP file request decoder.
     *
     * @param mediaManager    media manager
     * @param mimeTypeManager mime type manager
     * @param routeManager    Http route manager
     * @param uiDirectory     UI base directory
     */
    @Inject
    public HttpFileRequestDecoder(final MediaManager mediaManager, final MimeTypeManager mimeTypeManager, final HttpRouteManager routeManager, @Named("uiDirectory") final String uiDirectory) {
        this.mediaManager = mediaManager;
        this.mimeTypeManager = mimeTypeManager;
        this.routeManager = routeManager;
        this.uiDirectory = uiDirectory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void decode(ChannelHandlerContext context, FullHttpRequest request, List<Object> out) {
        HttpFileRequest fileRequest = null;
        if (request.getMethod().equals(GET)) {
            QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
            if (decoder.path().startsWith(HTTP_CONTENT_REQUEST_PATH.toString()) && decoder.parameters().get(HTTP_CONTENT_ID.toString()) != null) {
                // Request for a content file is valid if content is found in media index
                AbstractNode node = mediaManager.getNode(decoder.parameters().get(HTTP_CONTENT_ID.toString()).get(0));
                if (node instanceof ContentNode) {
                    ContentNode contentNode = (ContentNode) node;
                    fileRequest = new HttpFileRequest(request, new File(contentNode.getPath()), contentNode.getMimeType(), false);
                }
            } else {
                // Request for UI static file is valid if requested file name has a correct mime type
                String fileName = getFileName(decoder);
                MimeType mimeType = mimeTypeManager.getMimeType(fileName);
                if (mimeType != null) {
                    fileRequest = new HttpFileRequest(request, new File(uiDirectory, fileName), mimeType, true);
                }
            }
        }

        if (fileRequest != null) {
            // Add file request to message list
            out.add(fileRequest);
        } else {
            // Forward request to pipeline
            request.retain();
            out.add(request);
        }
    }

    /**
     * Get file name from query.
     *
     * @param decoder query string decoder
     * @return file name
     */
    private String getFileName(final QueryStringDecoder decoder) {
        String fileName = decoder.path().trim();
        if (fileName.endsWith("/")) {
            // Remove trailing '/'
            fileName = fileName.substring(0, fileName.length() - 1);
        }

        // Check if fileName is a default Holmes web application
        HttpRoute route = routeManager.getHttpRoute(fileName);
        if (route != null) {
            fileName += route.getDefaultFile();
        }

        return fileName;
    }
}
