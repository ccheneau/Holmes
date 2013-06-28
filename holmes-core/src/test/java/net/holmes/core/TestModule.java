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

package net.holmes.core;

import com.google.common.cache.Cache;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.core.ResourceContext;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.core.util.FeaturesAndProperties;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderFactory;
import com.sun.jersey.spi.MessageBodyWorkers;
import com.sun.jersey.spi.container.*;
import com.sun.jersey.spi.monitoring.DispatchingListener;
import com.sun.jersey.spi.monitoring.RequestListener;
import com.sun.jersey.spi.monitoring.ResponseListener;
import net.holmes.core.backend.BackendManager;
import net.holmes.core.backend.BackendManagerImpl;
import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.common.mimetype.MimeTypeManager;
import net.holmes.core.common.mimetype.MimeTypeManagerImpl;
import net.holmes.core.http.handler.HttpBackendRequestHandler;
import net.holmes.core.http.handler.HttpContentRequestHandler;
import net.holmes.core.http.handler.HttpRequestHandler;
import net.holmes.core.http.handler.HttpUIRequestHandler;
import net.holmes.core.inject.CustomTypeListener;
import net.holmes.core.inject.provider.ImageCacheProvider;
import net.holmes.core.inject.provider.PodcastCacheProvider;
import net.holmes.core.media.MediaManager;
import net.holmes.core.media.MediaManagerImpl;
import net.holmes.core.media.index.MediaIndexManager;
import net.holmes.core.media.index.MediaIndexManagerImpl;
import net.holmes.core.media.model.AbstractNode;

import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

public class TestModule extends AbstractModule {
    private final EventBus eventBus = new EventBus("Holmes EventBus");
    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("message");

    @Override
    protected void configure() {
        bindListener(Matchers.any(), new CustomTypeListener(eventBus));

        bind(Configuration.class).to(TestConfiguration.class).in(Singleton.class);
        bind(ResourceBundle.class).toInstance(resourceBundle);

        bind(MediaManager.class).to(MediaManagerImpl.class).in(Singleton.class);
        bind(MediaIndexManager.class).to(MediaIndexManagerImpl.class).in(Singleton.class);

        bindConstant().annotatedWith(Names.named("mimeTypePath")).to("/mimetypes.properties");
        bindConstant().annotatedWith(Names.named("uiDirectory")).to(System.getProperty("java.io.tmpdir"));

        bind(MimeTypeManager.class).to(MimeTypeManagerImpl.class).in(Singleton.class);

        bind(WebApplication.class).toInstance(new WebApplicationMock());
        bind(BackendManager.class).to(BackendManagerImpl.class);

        bind(new TypeLiteral<Cache<String, List<AbstractNode>>>() {
        }).annotatedWith(Names.named("podcastCache")).toProvider(PodcastCacheProvider.class).in(Singleton.class);
        bind(new TypeLiteral<Cache<String, String>>() {
        }).annotatedWith(Names.named("imageCache")).toProvider(ImageCacheProvider.class).in(Singleton.class);

        bind(HttpRequestHandler.class).annotatedWith(Names.named("content")).to(HttpContentRequestHandler.class);
        bind(HttpRequestHandler.class).annotatedWith(Names.named("backend")).to(HttpBackendRequestHandler.class);
        bind(HttpRequestHandler.class).annotatedWith(Names.named("ui")).to(HttpUIRequestHandler.class);

    }

    private class WebApplicationMock implements WebApplication {

        @Override
        public boolean isInitiated() {
            return true;
        }

        @Override
        public void initiate(ResourceConfig resourceConfig) throws IllegalArgumentException, ContainerException {
        }

        @Override
        public void initiate(ResourceConfig resourceConfig, IoCComponentProviderFactory provider) throws IllegalArgumentException, ContainerException {
        }

        @Override
        public WebApplication clone() {
            return new WebApplicationMock();
        }

        @Override
        public FeaturesAndProperties getFeaturesAndProperties() {
            return null;
        }

        @Override
        public Providers getProviders() {
            return null;
        }

        @Override
        public ResourceContext getResourceContext() {
            return null;
        }

        @Override
        public MessageBodyWorkers getMessageBodyWorkers() {
            return null;
        }

        @Override
        public ExceptionMapperContext getExceptionMapperContext() {
            return null;
        }

        @Override
        public HttpContext getThreadLocalHttpContext() {
            return null;
        }

        @Override
        public ServerInjectableProviderFactory getServerInjectableProviderFactory() {
            return null;
        }

        @Override
        public RequestListener getRequestListener() {
            return null;
        }

        @Override
        public DispatchingListener getDispatchingListener() {
            return null;
        }

        @Override
        public ResponseListener getResponseListener() {
            return null;
        }

        @Override
        public void handleRequest(ContainerRequest request, ContainerResponseWriter responseWriter) throws IOException {
        }

        @Override
        public void handleRequest(ContainerRequest request, ContainerResponse response) throws IOException {
        }

        @Override
        public void destroy() {
        }

        @Override
        public boolean isTracingEnabled() {
            return false;
        }

        @Override
        public void trace(String message) {
        }
    }
}
