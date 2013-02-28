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

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map.Entry;

import javax.inject.Inject;

import net.holmes.core.http.HttpServer;
import net.holmes.core.util.inject.Loggable;

import org.slf4j.Logger;

import com.google.common.collect.Lists;
import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import com.sun.jersey.spi.container.WebApplication;

/**
 * Handler for backend requests from Holmes UI
 */
@Loggable
public final class HttpBackendRequestHandler implements HttpRequestHandler {
    private Logger logger;

    private static final String REQUEST_PATH = "/backend/";

    private final WebApplication webApplication;

    @Inject
    public HttpBackendRequestHandler(WebApplication webApplication) {
        this.webApplication = webApplication;
    }

    @Override
    public boolean canProcess(String requestPath, HttpMethod method) {
        return requestPath.startsWith(REQUEST_PATH);
    }

    @Override
    public void processRequest(FullHttpRequest request, Channel channel) throws HttpRequestException {
        if (logger.isDebugEnabled()) logger.debug("[START] processRequest");

        try {
            // Build backend request
            StringBuilder base = new StringBuilder();
            base.append("http://").append(request.headers().get(HttpHeaders.Names.HOST)).append(REQUEST_PATH);
            final URI baseUri = new URI(base.toString());
            final URI requestUri = new URI(base.substring(0, base.length() - 1) + request.getUri());
            final ContainerRequest backendRequest = new ContainerRequest(webApplication, request.getMethod().name(), baseUri, requestUri, getHeaders(request),
                    new ByteBufInputStream(request.data()));

            // Process backend request
            webApplication.handleRequest(backendRequest, new BackendResponseWriter(channel));
        } catch (URISyntaxException e) {
            throw new HttpRequestException(e.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            throw new HttpRequestException(e.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
        } finally {
            if (logger.isDebugEnabled()) logger.debug("[END] processRequest");
        }
    }

    private InBoundHeaders getHeaders(HttpRequest request) {
        InBoundHeaders headers = new InBoundHeaders();

        for (Entry<String, String> header : request.headers()) {
            headers.add(header.getKey(), header.getValue());
        }
        return headers;
    }

    /**
     * Response writer for backend requests     
     *
     */
    private final static class BackendResponseWriter implements ContainerResponseWriter {

        private final Channel channel;
        private FullHttpResponse response;

        private BackendResponseWriter(Channel channel) {
            this.channel = channel;
        }

        @Override
        public OutputStream writeStatusAndHeaders(long contentLength, ContainerResponse cResponse) throws IOException {
            // Set http headers
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(cResponse.getStatus()));
            for (Entry<String, List<Object>> headerEntry : cResponse.getHttpHeaders().entrySet()) {
                List<String> values = Lists.newArrayList();
                for (Object v : headerEntry.getValue())
                    values.add(ContainerResponse.getHeaderValue(v));
                response.headers().add(headerEntry.getKey(), values);
            }
            response.headers().add(HttpHeaders.Names.SERVER, HttpServer.HTTP_SERVER_NAME);

            return new ByteBufOutputStream(response.data());
        }

        @Override
        public void finish() throws IOException {
            // Streaming is not supported. Entire response will be written
            // downstream once finish() is called.
            channel.write(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}