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
import net.holmes.core.media.MediaManager;
import net.holmes.core.media.model.AbstractNode;
import net.holmes.core.media.model.ContentNode;

import javax.inject.Inject;

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;

/**
 * Handler for content (i.e. video, audio or picture) streaming to UPnP media renderer.
 */
public final class HttpContentRequestHandler extends HttpRequestHandler {
    private static final String REQUEST_PATH = "/content";
    private final MediaManager mediaManager;

    /**
     * Instantiates a new http content request handler.
     *
     * @param mediaManager media manager
     */
    @Inject
    public HttpContentRequestHandler(final MediaManager mediaManager) {
        this.mediaManager = mediaManager;
    }

    @Override
    boolean accept(final String requestPath, final HttpMethod method) {
        return method.equals(HttpMethod.GET) && requestPath.startsWith(REQUEST_PATH);
    }

    @Override
    HttpRequestFile getRequestFile(FullHttpRequest request) throws HttpRequestException {
        // Get content node
        ContentNode node = getContentNode(request.getUri());
        if (node == null)
            throw new HttpRequestException(request.getUri(), NOT_FOUND);

        return new HttpRequestFile(new NodeFile(node.getPath()), node.getMimeType());
    }

    /**
     * Get content node from {@link net.holmes.core.media.MediaManager}.
     *
     * @param uri content Uri
     * @return content node
     */
    private ContentNode getContentNode(final String uri) {
        ContentNode contentNode = null;
        QueryStringDecoder decoder = new QueryStringDecoder(uri);

        String contentId = decoder.parameters().get("id") != null ? decoder.parameters().get("id").get(0) : null;
        if (!Strings.isNullOrEmpty(contentId)) {
            AbstractNode node = mediaManager.getNode(contentId);
            if (node instanceof ContentNode)
                contentNode = (ContentNode) node;
        }
        return contentNode;
    }
}
