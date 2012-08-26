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

import net.holmes.core.configuration.IConfiguration;
import net.holmes.core.configuration.XmlConfiguration;
import net.holmes.core.http.HttpRequestHandler;
import net.holmes.core.http.HttpServer;
import net.holmes.core.http.HttpServerPipelineFactory;
import net.holmes.core.http.IChannelPipelineFactory;
import net.holmes.core.http.IHttpRequestHandler;
import net.holmes.core.http.request.HttpBackendRequestHandler;
import net.holmes.core.http.request.HttpContentRequestHandler;
import net.holmes.core.http.request.HttpSiteRequestHandler;
import net.holmes.core.media.IMediaService;
import net.holmes.core.media.MediaService;
import net.holmes.core.media.index.IMediaIndex;
import net.holmes.core.media.index.MediaIndex;
import net.holmes.core.upnp.UpnpServer;
import net.holmes.core.util.bundle.IBundle;
import net.holmes.core.util.bundle.Bundle;
import net.holmes.core.util.mimetype.IMimeTypeFactory;
import net.holmes.core.util.mimetype.MimeTypeFactory;

import org.jboss.netty.channel.ChannelHandler;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public final class HolmesServerModule extends AbstractModule {

    /* (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        // Bind configuration
        bind(IConfiguration.class).to(XmlConfiguration.class).in(Singleton.class);
        bind(IBundle.class).to(Bundle.class).in(Singleton.class);

        // Bind media service
        bind(IMediaService.class).to(MediaService.class).in(Singleton.class);
        bind(IMimeTypeFactory.class).to(MimeTypeFactory.class).in(Singleton.class);
        bind(IMediaIndex.class).to(MediaIndex.class).in(Singleton.class);

        // Bind servers
        bind(IServer.class).annotatedWith(Names.named("http")).to(HttpServer.class).in(Singleton.class);
        bind(IServer.class).annotatedWith(Names.named("upnp")).to(UpnpServer.class).in(Singleton.class);

        // Bind Http handlers
        bind(IChannelPipelineFactory.class).to(HttpServerPipelineFactory.class);
        bind(ChannelHandler.class).annotatedWith(Names.named("http")).to(HttpRequestHandler.class);
        bind(IHttpRequestHandler.class).annotatedWith(Names.named("content")).to(HttpContentRequestHandler.class);
        bind(IHttpRequestHandler.class).annotatedWith(Names.named("backend")).to(HttpBackendRequestHandler.class);
        bind(IHttpRequestHandler.class).annotatedWith(Names.named("site")).to(HttpSiteRequestHandler.class);
    }
}
