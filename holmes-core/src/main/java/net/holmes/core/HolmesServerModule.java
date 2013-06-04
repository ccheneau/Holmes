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
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.sun.jersey.spi.container.WebApplication;
import io.netty.channel.ChannelInboundMessageHandler;
import net.holmes.common.Service;
import net.holmes.common.configuration.Configuration;
import net.holmes.common.configuration.XmlConfigurationImpl;
import net.holmes.common.media.AbstractNode;
import net.holmes.common.mimetype.MimeTypeManager;
import net.holmes.common.mimetype.MimeTypeManagerImpl;
import net.holmes.core.backend.BackendManager;
import net.holmes.core.backend.BackendManagerImpl;
import net.holmes.core.http.HttpChannelHandler;
import net.holmes.core.http.HttpServer;
import net.holmes.core.http.handler.HttpBackendRequestHandler;
import net.holmes.core.http.handler.HttpContentRequestHandler;
import net.holmes.core.http.handler.HttpRequestHandler;
import net.holmes.core.http.handler.HttpUIRequestHandler;
import net.holmes.core.inject.CustomTypeListener;
import net.holmes.core.inject.provider.ImageCacheProvider;
import net.holmes.core.inject.provider.PodcastCacheProvider;
import net.holmes.core.inject.provider.UpnpServiceProvider;
import net.holmes.core.inject.provider.WebApplicationProvider;
import net.holmes.core.media.MediaManager;
import net.holmes.core.media.MediaManagerImpl;
import net.holmes.core.media.index.MediaIndexManager;
import net.holmes.core.media.index.MediaIndexManagerImpl;
import net.holmes.core.scheduled.CacheCleanerService;
import net.holmes.core.scheduled.MediaIndexCleanerService;
import net.holmes.core.scheduled.MediaScannerService;
import net.holmes.core.upnp.UpnpServer;
import org.fourthline.cling.UpnpService;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

/**
 * Guice module.
 */
final class HolmesServerModule extends AbstractModule {

    private final EventBus eventBus = new AsyncEventBus("Holmes EventBus", Executors.newCachedThreadPool());
    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("message");

    @Override
    protected void configure() {

        // Bind utils
        bind(EventBus.class).toInstance(eventBus);
        bindListener(Matchers.any(), new CustomTypeListener(eventBus));
        bind(Configuration.class).to(XmlConfigurationImpl.class).in(Singleton.class);
        bind(ResourceBundle.class).toInstance(resourceBundle);

        // Bind media service
        bind(MediaManager.class).to(MediaManagerImpl.class).in(Singleton.class);
        bind(MimeTypeManager.class).to(MimeTypeManagerImpl.class).in(Singleton.class);
        bind(MediaIndexManager.class).to(MediaIndexManagerImpl.class).in(Singleton.class);

        // Bind caches
        bind(new TypeLiteral<Cache<String, List<AbstractNode>>>() {
        }).annotatedWith(Names.named("podcastCache")).toProvider(PodcastCacheProvider.class).in(Singleton.class);
        bind(new TypeLiteral<Cache<File, String>>() {
        }).annotatedWith(Names.named("imageCache")).toProvider(ImageCacheProvider.class).in(Singleton.class);

        // Bind scheduled services
        bind(AbstractScheduledService.class).annotatedWith(Names.named("mediaIndexCleaner")).to(MediaIndexCleanerService.class);
        bind(AbstractScheduledService.class).annotatedWith(Names.named("podcastCacheCleaner")).to(CacheCleanerService.class);
        bind(AbstractScheduledService.class).annotatedWith(Names.named("mediaScanner")).to(MediaScannerService.class);

        // Bind servers
        bind(Service.class).annotatedWith(Names.named("http")).to(HttpServer.class).in(Singleton.class);
        bind(Service.class).annotatedWith(Names.named("upnp")).to(UpnpServer.class).in(Singleton.class);
        bind(Service.class).annotatedWith(Names.named("systray")).to(SystrayService.class).in(Singleton.class);
        bind(Service.class).annotatedWith(Names.named("scheduler")).to(HolmesSchedulerService.class).in(Singleton.class);

        // Bind Jersey application
        bind(WebApplication.class).toProvider(WebApplicationProvider.class).in(Singleton.class);
        bind(BackendManager.class).to(BackendManagerImpl.class).in(Singleton.class);

        // Bind Upnp service
        bind(UpnpService.class).toProvider(UpnpServiceProvider.class);

        // Bind Http handlers
        bind(ChannelInboundMessageHandler.class).to(HttpChannelHandler.class);
        bind(HttpRequestHandler.class).annotatedWith(Names.named("content")).to(HttpContentRequestHandler.class);
        bind(HttpRequestHandler.class).annotatedWith(Names.named("backend")).to(HttpBackendRequestHandler.class);
        bind(HttpRequestHandler.class).annotatedWith(Names.named("ui")).to(HttpUIRequestHandler.class);
    }
}
