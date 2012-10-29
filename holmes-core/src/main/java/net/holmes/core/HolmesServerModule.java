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
package net.holmes.core;

import net.holmes.core.configuration.Configuration;
import net.holmes.core.configuration.XmlConfigurationImpl;
import net.holmes.core.http.HttpChannelHandler;
import net.holmes.core.http.HttpServer;
import net.holmes.core.http.HttpServerPipelineFactory;
import net.holmes.core.http.HttpServerPipelineFactoryImpl;
import net.holmes.core.http.handler.HttpBackendRequestHandler;
import net.holmes.core.http.handler.HttpContentRequestHandler;
import net.holmes.core.http.handler.HttpRequestHandler;
import net.holmes.core.http.handler.HttpUIRequestHandler;
import net.holmes.core.media.MediaService;
import net.holmes.core.media.MediaServiceImpl;
import net.holmes.core.media.index.MediaIndex;
import net.holmes.core.media.index.MediaIndexImpl;
import net.holmes.core.upnp.UpnpServer;
import net.holmes.core.util.bundle.Bundle;
import net.holmes.core.util.bundle.BundleImpl;
import net.holmes.core.util.inject.WebApplicationProvider;
import net.holmes.core.util.mimetype.MimeTypeFactory;
import net.holmes.core.util.mimetype.MimeTypeFactoryImpl;

import org.jboss.netty.channel.ChannelHandler;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.sun.jersey.spi.container.WebApplication;

public final class HolmesServerModule extends AbstractModule {

    @Override
    protected void configure() {

        // Bind configuration
        bind(Configuration.class).to(XmlConfigurationImpl.class).in(Singleton.class);
        bind(Bundle.class).to(BundleImpl.class).in(Singleton.class);

        // Bind media service
        bind(MediaService.class).to(MediaServiceImpl.class).in(Singleton.class);
        bind(MimeTypeFactory.class).to(MimeTypeFactoryImpl.class).in(Singleton.class);
        bind(MediaIndex.class).to(MediaIndexImpl.class).in(Singleton.class);

        // Bind servers
        bind(Server.class).annotatedWith(Names.named("http")).to(HttpServer.class).in(Singleton.class);
        bind(Server.class).annotatedWith(Names.named("upnp")).to(UpnpServer.class).in(Singleton.class);

        // Bind Jersey application
        bind(WebApplication.class).toProvider(WebApplicationProvider.class).in(Singleton.class);

        // Bind Http handlers
        bind(HttpServerPipelineFactory.class).to(HttpServerPipelineFactoryImpl.class);
        bind(ChannelHandler.class).to(HttpChannelHandler.class);
        bind(HttpRequestHandler.class).annotatedWith(Names.named("content")).to(HttpContentRequestHandler.class);
        bind(HttpRequestHandler.class).annotatedWith(Names.named("backend")).to(HttpBackendRequestHandler.class);
        bind(HttpRequestHandler.class).annotatedWith(Names.named("ui")).to(HttpUIRequestHandler.class);
    }
}
