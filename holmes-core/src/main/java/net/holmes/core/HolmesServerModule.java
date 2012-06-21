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
package net.holmes.core;

import net.holmes.core.configuration.IConfiguration;
import net.holmes.core.configuration.XmlConfiguration;
import net.holmes.core.http.HttpServer;
import net.holmes.core.http.HttpServerPipelineFactory;
import net.holmes.core.http.request.HttpRequestBackendHandler;
import net.holmes.core.http.request.HttpRequestContentHandler;
import net.holmes.core.http.request.HttpRequestSiteHandler;
import net.holmes.core.http.request.IHttpRequestHandler;
import net.holmes.core.media.IMediaService;
import net.holmes.core.media.MediaService;
import net.holmes.core.media.dao.IMediaDao;
import net.holmes.core.media.dao.XmlMediaDao;
import net.holmes.core.model.ContentTypeFactory;
import net.holmes.core.model.IContentTypeFactory;
import net.holmes.core.upnp.UpnpServer;

import org.jboss.netty.channel.ChannelPipelineFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

/**
 * The Class HolmesServerModule.
 */
public final class HolmesServerModule extends AbstractModule
{

    /* (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure()
    {
        bind(IConfiguration.class).to(XmlConfiguration.class).in(Singleton.class);

        bind(IMediaService.class).to(MediaService.class).in(Singleton.class);
        bind(IMediaDao.class).to(XmlMediaDao.class).in(Singleton.class);

        bind(ChannelPipelineFactory.class).to(HttpServerPipelineFactory.class);

        bind(IContentTypeFactory.class).to(ContentTypeFactory.class).in(Singleton.class);

        bind(IServer.class).annotatedWith(Names.named("http")).to(HttpServer.class).in(Singleton.class);
        bind(IServer.class).annotatedWith(Names.named("upnp")).to(UpnpServer.class).in(Singleton.class);

        bind(IHttpRequestHandler.class).annotatedWith(Names.named("content")).to(HttpRequestContentHandler.class);
        bind(IHttpRequestHandler.class).annotatedWith(Names.named("backend")).to(HttpRequestBackendHandler.class);
        bind(IHttpRequestHandler.class).annotatedWith(Names.named("site")).to(HttpRequestSiteHandler.class);
    }

}
