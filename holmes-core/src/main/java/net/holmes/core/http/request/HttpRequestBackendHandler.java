/**
* Copyright (C) 2012  Cedric Cheneau
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
package net.holmes.core.http.request;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import net.holmes.core.http.HttpServer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.guice.spi.container.GuiceComponentProviderFactory;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.WebApplicationFactory;

/**
 * Handler for backend requests from Holmes administration site
 */
public final class HttpRequestBackendHandler implements IHttpRequestHandler {
    private static Logger logger = LoggerFactory.getLogger(HttpRequestBackendHandler.class);

    private final static String REQUEST_PATH = "/backend/";

    private WebApplication application;

    @Inject
    private Injector injector;

    public HttpRequestBackendHandler() {
    }

    /* (non-Javadoc)
     * @see net.holmes.core.http.request.IHttpRequestHandler#initHandler()
     */
    @Override
    @Inject
    public void initHandler() {
        if (logger.isDebugEnabled()) logger.debug("[START] initHandler");

        // Jersey initialization
        if (application == null) {
            application = WebApplicationFactory.createWebApplication();
            if (!application.isInitiated()) {

                // Set web application properties
                Map<String, Object> props = new HashMap<String, Object>();
                props.put(PackagesResourceConfig.PROPERTY_PACKAGES, "net.holmes.core.backend");
                props.put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

                // Initialize web application
                ResourceConfig rcf = new PackagesResourceConfig(props);
                application.initiate(rcf, new GuiceComponentProviderFactory(rcf, injector));
            }
        }
        if (logger.isDebugEnabled()) logger.debug("[END] initHandler");
    }

    /* (non-Javadoc)
     * @see net.holmes.core.http.request.IHttpRequestHandler#canProcess(java.lang.String)
     */
    @Override
    public boolean canProcess(String requestPath) {
        return requestPath.startsWith(REQUEST_PATH);
    }

    /* (non-Javadoc)
     * @see net.holmes.core.http.request.IHttpRequestHandler#processRequest(org.jboss.netty.handler.codec.http.HttpRequest, org.jboss.netty.channel.Channel)
     */
    @Override
    public void processRequest(HttpRequest request, Channel channel) throws HttpRequestException {
        if (logger.isDebugEnabled()) logger.debug("[START] processRequest");

        try {
            // Define base URL
            String base = "http://" + request.getHeader(HttpHeaders.Names.HOST) + REQUEST_PATH;
            final URI baseUri = new URI(base);
            final URI requestUri = new URI(base.substring(0, base.length() - 1) + request.getUri());

            // Build request
            final ContainerRequest cRequest = new ContainerRequest(application, request.getMethod().getName(), baseUri, requestUri, getHeaders(request),
                    new ChannelBufferInputStream(request.getContent()));

            // Process request
            application.handleRequest(cRequest, new BackendResponseWriter(channel));
        }
        catch (URISyntaxException e) {
            throw new HttpRequestException(e.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
        catch (IOException e) {
            throw new HttpRequestException(e.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
        finally {
            if (logger.isDebugEnabled()) logger.debug("[END] processRequest");
        }
    }

    private InBoundHeaders getHeaders(HttpRequest request) {
        InBoundHeaders headers = new InBoundHeaders();

        for (String name : request.getHeaderNames()) {
            headers.put(name, request.getHeaders(name));
        }
        return headers;
    }

    /**
     * Response writer for backend requests     
     *
     */
    private final class BackendResponseWriter implements ContainerResponseWriter {

        private final Channel channel;
        private HttpResponse response;

        private BackendResponseWriter(Channel channel) {
            this.channel = channel;
        }

        /* (non-Javadoc)
         * @see com.sun.jersey.spi.container.ContainerResponseWriter#writeStatusAndHeaders(long, com.sun.jersey.spi.container.ContainerResponse)
         */
        @Override
        public OutputStream writeStatusAndHeaders(long contentLength, ContainerResponse cResponse) throws IOException {
            // Set http headers
            response = new DefaultHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.valueOf(cResponse.getStatus()));

            for (Entry<String, List<Object>> headerEntry : cResponse.getHttpHeaders().entrySet()) {
                List<String> values = new ArrayList<String>();
                for (Object v : headerEntry.getValue())
                    values.add(ContainerResponse.getHeaderValue(v));
                response.setHeader(headerEntry.getKey(), values);
            }
            response.setHeader(HttpHeaders.Names.SERVER, HttpServer.HTTP_SERVER_NAME);

            // Return output stream
            ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
            response.setContent(buffer);
            return new ChannelBufferOutputStream(buffer);
        }

        /* (non-Javadoc)
         * @see com.sun.jersey.spi.container.ContainerResponseWriter#finish()
         */
        @Override
        public void finish() throws IOException {
            // Streaming is not supported. Entire response will be written
            // downstream once finish() is called.
            channel.write(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
