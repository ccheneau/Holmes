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
package net.holmes.core.inject;

import io.netty.channel.ChannelInboundMessageHandler;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Executors;

import net.holmes.core.Server;
import net.holmes.core.backend.backbone.BackboneManager;
import net.holmes.core.backend.backbone.BackboneManagerImpl;
import net.holmes.core.configuration.Configuration;
import net.holmes.core.configuration.XmlConfigurationImpl;
import net.holmes.core.http.HttpChannelHandler;
import net.holmes.core.http.HttpServer;
import net.holmes.core.http.handler.HttpBackendRequestHandler;
import net.holmes.core.http.handler.HttpContentRequestHandler;
import net.holmes.core.http.handler.HttpRequestHandler;
import net.holmes.core.http.handler.HttpUIRequestHandler;
import net.holmes.core.inject.provider.PodcastCacheProvider;
import net.holmes.core.inject.provider.UpnpServiceProvider;
import net.holmes.core.inject.provider.WebApplicationProvider;
import net.holmes.core.media.MediaManager;
import net.holmes.core.media.MediaManagerImpl;
import net.holmes.core.media.index.MediaIndexCleanerService;
import net.holmes.core.media.index.MediaIndexManager;
import net.holmes.core.media.index.MediaIndexManagerImpl;
import net.holmes.core.media.node.AbstractNode;
import net.holmes.core.upnp.UpnpServer;
import net.holmes.core.util.SystemProperty;
import net.holmes.core.util.Systray;
import net.holmes.core.util.bundle.Bundle;
import net.holmes.core.util.bundle.BundleImpl;
import net.holmes.core.util.mimetype.MimeTypeFactory;
import net.holmes.core.util.mimetype.MimeTypeFactoryImpl;

import org.fourthline.cling.UpnpService;

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

public final class HolmesServerModule extends AbstractModule {
    //    private EventBus eventBus = new EventBus("Holmes EventBus");
    private EventBus eventBus = new AsyncEventBus("Holmes EventBus", Executors.newCachedThreadPool());

    @Override
    protected void configure() {

        // Bind utils
        bind(EventBus.class).toInstance(eventBus);
        bindListener(Matchers.any(), new CustomTypeListener(eventBus));
        bind(Configuration.class).to(XmlConfigurationImpl.class).in(Singleton.class);
        bind(Bundle.class).to(BundleImpl.class).in(Singleton.class);
        bindConstant().annotatedWith(Names.named("localIPv4")).to(getLocalIPV4());
        bindConstant().annotatedWith(Names.named("uiDirectory")).to(getUiDirectory());
        bind(new TypeLiteral<Cache<String, List<AbstractNode>>>() {
        }).annotatedWith(Names.named("podcastCache")).toProvider(PodcastCacheProvider.class).in(Singleton.class);

        // Bind media service
        bind(MediaManager.class).to(MediaManagerImpl.class).in(Singleton.class);
        bind(MimeTypeFactory.class).to(MimeTypeFactoryImpl.class).in(Singleton.class);
        bind(MediaIndexManager.class).to(MediaIndexManagerImpl.class).in(Singleton.class);
        bind(AbstractScheduledService.class).annotatedWith(Names.named("mediaIndexCleaner")).to(MediaIndexCleanerService.class);

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
        bind(ChannelInboundMessageHandler.class).to(HttpChannelHandler.class);
        bind(HttpRequestHandler.class).annotatedWith(Names.named("content")).to(HttpContentRequestHandler.class);
        bind(HttpRequestHandler.class).annotatedWith(Names.named("backend")).to(HttpBackendRequestHandler.class);
        bind(HttpRequestHandler.class).annotatedWith(Names.named("ui")).to(HttpUIRequestHandler.class);
    }

    /**
     * Get local IPv4 address (InetAddress.getLocalHost().getHostAddress() does not work on Linux)
     */
    public String getLocalIPV4() {
        try {
            for (Enumeration<NetworkInterface> intfaces = NetworkInterface.getNetworkInterfaces(); intfaces.hasMoreElements();) {
                NetworkInterface intf = intfaces.nextElement();
                for (Enumeration<InetAddress> inetAddresses = intf.getInetAddresses(); inetAddresses.hasMoreElements();) {
                    InetAddress inetAddr = inetAddresses.nextElement();
                    if (inetAddr instanceof Inet4Address && !inetAddr.isLoopbackAddress() && inetAddr.isSiteLocalAddress()) {
                        return inetAddr.getHostAddress();
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get UI base directory
     */
    public String getUiDirectory() {
        File uiDir = new File(SystemProperty.HOLMES_HOME.getValue(), "ui");
        if (!uiDir.exists()) {
            throw new RuntimeException(uiDir.getAbsolutePath() + " does not exist. Check " + SystemProperty.HOLMES_HOME.getName() + " ["
                    + SystemProperty.HOLMES_HOME.getValue() + "] system property");
        }
        return uiDir.getAbsolutePath();
    }
}