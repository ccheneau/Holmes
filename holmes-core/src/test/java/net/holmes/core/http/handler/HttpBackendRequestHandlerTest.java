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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import com.sun.jersey.spi.container.WebApplication;
import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import net.holmes.core.test.TestModule;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

import static net.holmes.core.http.handler.HttpBackendRequestHandler.BackendResponseWriter;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HttpBackendRequestHandlerTest {

    private FullHttpRequest request = createMock(FullHttpRequest.class);
    @Inject
    private WebApplication webApplication;
    private Channel channel = createMock(Channel.class);
    private ContainerResponse response = createMock(ContainerResponse.class);
    private Injector injector;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    private HttpBackendRequestHandler getHandler() {
        HttpBackendRequestHandler backendRequestHandler = new HttpBackendRequestHandler(webApplication);
        injector.injectMembers(backendRequestHandler);
        return backendRequestHandler;
    }

    @Test
    public void testCanProcess() {
        replay(webApplication);
        HttpBackendRequestHandler backendRequestHandler = getHandler();
        assertTrue(backendRequestHandler.canProcess("/backend/request", HttpMethod.GET));
        assertTrue(backendRequestHandler.canProcess("/backend/request", HttpMethod.POST));
        assertFalse(backendRequestHandler.canProcess("bad_request", HttpMethod.GET));
        assertFalse(backendRequestHandler.canProcess("bad_request", HttpMethod.POST));
        verify(webApplication);
    }

    @Test
    public void testProcessRequest() throws Exception {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeaders.Names.HOST, "localhost");

        expect(request.headers()).andReturn(headers).atLeastOnce();
        expect(request.getUri()).andReturn("/util/getVersion");
        expect(request.getMethod()).andReturn(HttpMethod.GET);
        expect(request.content()).andReturn(new EmptyByteBuf(new UnpooledByteBufAllocator(false)));

        expect(webApplication.isTracingEnabled()).andReturn(true);
        webApplication.handleRequest(isA(ContainerRequest.class), isA(ContainerResponseWriter.class));
        expectLastCall().atLeastOnce();

        Channel channel = createMock(Channel.class);
        replay(request, webApplication);
        HttpBackendRequestHandler backendRequestHandler = getHandler();
        backendRequestHandler.processRequest(request, channel);
        verify(request, webApplication);
    }

    @Test(expected = HttpRequestException.class)
    public void testProcessRequestBadUri() throws Exception {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeaders.Names.HOST, "\\///bad_uri/");

        expect(request.headers()).andReturn(headers).atLeastOnce();

        Channel channel = createMock(Channel.class);
        replay(request);
        try {
            HttpBackendRequestHandler backendRequestHandler = getHandler();
            backendRequestHandler.processRequest(request, channel);
        } finally {
            verify(request);
        }
    }

    @Test(expected = HttpRequestException.class)
    public void testProcessRequestWebApplicationThrowsException() throws Exception {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeaders.Names.HOST, "localhost");

        expect(request.headers()).andReturn(headers).atLeastOnce();
        expect(request.getUri()).andReturn("/util/getVersion");
        expect(request.getMethod()).andReturn(HttpMethod.GET);
        expect(request.content()).andReturn(new EmptyByteBuf(new UnpooledByteBufAllocator(false)));

        expect(webApplication.isTracingEnabled()).andReturn(true);
        webApplication.handleRequest(isA(ContainerRequest.class), isA(ContainerResponseWriter.class));
        expectLastCall().andThrow(new IOException());

        Channel channel = createMock(Channel.class);
        replay(request, webApplication);
        try {
            HttpBackendRequestHandler backendRequestHandler = getHandler();
            backendRequestHandler.processRequest(request, channel);
        } finally {
            verify(request, webApplication);
        }
    }

    @Test
    public void testBackendResponseWriterWriteStatusAndHeaders() throws Exception {
        MultivaluedMap<String, Object> headers = new StringKeyIgnoreCaseMultivaluedMap<>();
        headers.add(HttpHeaders.Names.HOST, "localhost");

        expect(response.getStatus()).andReturn(0).atLeastOnce();
        expect(response.getHttpHeaders()).andReturn(headers).atLeastOnce();

        replay(channel, response);
        BackendResponseWriter backendResponseWriter = new BackendResponseWriter(channel);
        backendResponseWriter.writeStatusAndHeaders(1, response);
        verify(channel, response);
    }
}