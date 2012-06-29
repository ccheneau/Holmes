/**
* Copyright (c) 2012 Cedric Cheneau
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/
package net.holmes.core.http.request;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
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

public final class HttpRequestBackendHandler implements IHttpRequestHandler {
    private static Logger logger = LoggerFactory.getLogger(HttpRequestBackendHandler.class);

    public final static String PATH = "/backend/";

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

        if (application == null) {
            application = WebApplicationFactory.createWebApplication();
            if (!application.isInitiated()) {
                Map<String, Object> props = new HashMap<String, Object>();

                props.put(PackagesResourceConfig.PROPERTY_PACKAGES, "net.holmes.core.backend");
                props.put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

                ResourceConfig rcf = new PackagesResourceConfig(props);
                application.initiate(rcf, new GuiceComponentProviderFactory(rcf, injector));
            }
        }
        if (logger.isDebugEnabled()) logger.debug("[END] initHandler");
    }

    /* (non-Javadoc)
     * @see net.holmes.core.http.request.IHttpRequestHandler#processRequest(org.jboss.netty.handler.codec.http.HttpRequest, org.jboss.netty.channel.Channel)
     */
    @Override
    public void processRequest(HttpRequest request, Channel channel) throws HttpRequestException {
        if (logger.isDebugEnabled()) {
            logger.debug("[START] processRequest");
            logger.debug("Request uri: " + request.getUri());
            for (Entry<String, String> entry : request.getHeaders()) {
                logger.debug("Request header: " + entry.getKey() + " ==> " + entry.getValue());
            }

            if (request.getMethod().equals(HttpMethod.POST)) {
                ChannelBuffer content = request.getContent();
                if (content.readable()) {
                    QueryStringDecoder queryStringDecoder = new QueryStringDecoder("/?" + content.toString(Charset.forName("utf-8")));
                    Map<String, List<String>> params = queryStringDecoder.getParameters();
                    if (params != null) {
                        for (String paramKey : params.keySet()) {
                            logger.debug("Post parameter: " + paramKey + " => " + params.get(paramKey));
                        }
                    }
                }
            }
        }
        try {
            String base = "http://" + request.getHeader(HttpHeaders.Names.HOST) + PATH;
            final URI baseUri = new URI(base);
            final URI requestUri = new URI(base.substring(0, base.length() - 1) + request.getUri());

            final ContainerRequest cRequest = new ContainerRequest(application, request.getMethod().getName(), baseUri, requestUri, getHeaders(request),
                    new ChannelBufferInputStream(request.getContent()));

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

    private final static class BackendResponseWriter implements ContainerResponseWriter {

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
