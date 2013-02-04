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
package net.holmes.core;

import net.holmes.core.backend.backbone.BackboneManager;
import net.holmes.core.backend.backbone.BackboneManagerImpl;
import net.holmes.core.configuration.Configuration;
import net.holmes.core.configuration.XmlConfigurationImpl;
import net.holmes.core.http.HttpChannelHandler;
import net.holmes.core.http.HttpServer;
import net.holmes.core.http.HttpServerPipelineFactory;
import net.holmes.core.http.handler.HttpBackendRequestHandler;
import net.holmes.core.http.handler.HttpContentRequestHandler;
import net.holmes.core.http.handler.HttpRequestHandler;
import net.holmes.core.http.handler.HttpUIRequestHandler;
import net.holmes.core.media.MediaService;
import net.holmes.core.media.MediaServiceImpl;
import net.holmes.core.media.index.MediaIndex;
import net.holmes.core.media.index.MediaIndexImpl;
import net.holmes.core.upnp.UpnpServer;
import net.holmes.core.util.Systray;
import net.holmes.core.util.bundle.Bundle;
import net.holmes.core.util.bundle.BundleImpl;
import net.holmes.core.util.inject.LocalIPv4Provider;
import net.holmes.core.util.inject.LoggerTypeListener;
import net.holmes.core.util.inject.UiDirectoryProvider;
import net.holmes.core.util.inject.UpnpServiceProvider;
import net.holmes.core.util.inject.WebApplicationProvider;
import net.holmes.core.util.mimetype.MimeTypeFactory;
import net.holmes.core.util.mimetype.MimeTypeFactoryImpl;

import org.fourthline.cling.UpnpService;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.sun.jersey.spi.container.WebApplication;

public final class HolmesServerModule extends AbstractModule {

    @Override
    protected void configure() {

        // Bind slf4j loggers
        bindListener(Matchers.any(), new LoggerTypeListener());

        // Bind configuration
        bind(Configuration.class).to(XmlConfigurationImpl.class).in(Singleton.class);
        bind(Bundle.class).to(BundleImpl.class).in(Singleton.class);
        bind(String.class).annotatedWith(Names.named("localIPv4")).toProvider(LocalIPv4Provider.class).in(Singleton.class);

        // Bind media service
        bind(MediaService.class).to(MediaServiceImpl.class).in(Singleton.class);
        bind(MimeTypeFactory.class).to(MimeTypeFactoryImpl.class).in(Singleton.class);
        bind(MediaIndex.class).to(MediaIndexImpl.class).in(Singleton.class);
        bind(String.class).annotatedWith(Names.named("uiDirectory")).toProvider(UiDirectoryProvider.class).in(Singleton.class);

        // Bind servers
        bind(Server.class).annotatedWith(Names.named("http")).to(HttpServer.class).in(Singleton.class);
        bind(Server.class).annotatedWith(Names.named("upnp")).to(UpnpServer.class).in(Singleton.class);
        bind(Server.class).annotatedWith(Names.named("systray")).to(Systray.class).in(Singleton.class);

        // Bind Jersey application
        bind(WebApplication.class).toProvider(WebApplicationProvider.class).in(Singleton.class);
        bind(BackboneManager.class).to(BackboneManagerImpl.class).in(Singleton.class);

        // Bind Upnp service
        bind(UpnpService.class).toProvider(UpnpServiceProvider.class);

        // Bind Http handlers
        bind(ChannelPipelineFactory.class).to(HttpServerPipelineFactory.class);
        bind(ChannelGroup.class).to(DefaultChannelGroup.class).in(Singleton.class);
        bind(SimpleChannelHandler.class).to(HttpChannelHandler.class);
        bind(HttpRequestHandler.class).annotatedWith(Names.named("content")).to(HttpContentRequestHandler.class);
        bind(HttpRequestHandler.class).annotatedWith(Names.named("backend")).to(HttpBackendRequestHandler.class);
        bind(HttpRequestHandler.class).annotatedWith(Names.named("ui")).to(HttpUIRequestHandler.class);
    }
}
