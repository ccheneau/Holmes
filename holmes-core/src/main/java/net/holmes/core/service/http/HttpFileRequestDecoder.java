/*
 * Copyright (C) 2012-2015  Cedric Cheneau
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
import net.holmes.core.business.mimetype.model.MimeType;
import net.holmes.core.common.WebApplication;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.util.List;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static net.holmes.core.common.Constants.*;

/**
 * Decode FullHttpRequest messages to HttpFileRequest.
 * Two kinds of files are handled:
 * <ul>
 * <li>static files for messages with requested file name having a valid mime type.</li>
 * <li>content files for messages with request parameter "id" matching content in media index.</li>
 * </ul>
 * If message does not fit previous criteria, message is forwarded to the Netty pipeline.
 */
public final class HttpFileRequestDecoder extends MessageToMessageDecoder<FullHttpRequest> {
    private final MediaManager mediaManager;
    private final MimeTypeManager mimeTypeManager;
    private final String uiDirectory;

    /**
     * Instantiates a new HTTP file request decoder.
     *
     * @param mediaManager    media manager
     * @param mimeTypeManager mime type manager
     * @param uiDirectory     UI base directory
     */
    @Inject
    public HttpFileRequestDecoder(final MediaManager mediaManager, final MimeTypeManager mimeTypeManager, @Named("uiDirectory") final String uiDirectory) {
        this.mediaManager = mediaManager;
        this.mimeTypeManager = mimeTypeManager;
        this.uiDirectory = uiDirectory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void decode(final ChannelHandlerContext context, final FullHttpRequest request, final List<Object> out) {
        HttpFileRequest fileRequest = null;

        // only GET requests are handled
        if (request.getMethod().equals(GET)) {
            QueryStringDecoder requestDecoder = new QueryStringDecoder(request.getUri());
            if (requestDecoder.path().startsWith(HTTP_CONTENT_REQUEST_PATH.toString()) && requestDecoder.parameters().get(HTTP_CONTENT_ID.toString()) != null) {
                // Content file request is valid if content is found in media index
                AbstractNode node = mediaManager.getNode(requestDecoder.parameters().get(HTTP_CONTENT_ID.toString()).get(0));
                if (node instanceof ContentNode) {
                    // Content found in media index, build a file request based on this content
                    ContentNode contentNode = (ContentNode) node;
                    fileRequest = new HttpFileRequest(request, new File(contentNode.getPath()), contentNode.getMimeType(), false);
                }
            } else {
                // Static file request is valid if requested file name has a valid mime type
                String requestedFileName = getRequestedFileName(requestDecoder);
                MimeType mimeType = mimeTypeManager.getMimeType(requestedFileName);
                if (mimeType != null) {
                    // Found valid mime type, build a static file request
                    fileRequest = new HttpFileRequest(request, new File(uiDirectory, requestedFileName), mimeType, true);
                }
            }
        }

        if (fileRequest != null) {
            // Add file request to message list
            out.add(fileRequest);
        } else {
            // Forward request to pipeline
            out.add(request.retain());
        }
    }

    /**
     * Get requested file name.
     *
     * @param requestDecoder request decoder
     * @return requested file name
     */
    private String getRequestedFileName(final QueryStringDecoder requestDecoder) {
        // Get request path and remove trailing slashes
        String fileName = requestDecoder.path().replaceAll("/+$", "");

        // Check if fileName is a web application
        WebApplication webApplication = WebApplication.findByPath(fileName);

        // Return web application welcome file or requested file name
        return webApplication != null ? fileName + webApplication.getWelcomeFile() : fileName;
    }
}
