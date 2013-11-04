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

package net.holmes.core.test;

import com.google.common.cache.Cache;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import net.holmes.core.backend.BackendManager;
import net.holmes.core.backend.BackendManagerImpl;
import net.holmes.core.backend.exception.BackendExceptionMapper;
import net.holmes.core.common.Service;
import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.common.mimetype.MimeTypeManager;
import net.holmes.core.common.mimetype.MimeTypeManagerImpl;
import net.holmes.core.http.HttpServer;
import net.holmes.core.inject.CustomTypeListener;
import net.holmes.core.inject.provider.PodcastCacheProvider;
import net.holmes.core.media.MediaManager;
import net.holmes.core.media.MediaManagerImpl;
import net.holmes.core.media.dao.MediaDao;
import net.holmes.core.media.dao.MediaDaoImpl;
import net.holmes.core.media.dao.icecast.IcecastDao;
import net.holmes.core.media.dao.icecast.IcecastDaoImpl;
import net.holmes.core.media.index.MediaIndexManager;
import net.holmes.core.media.index.MediaIndexManagerImpl;
import net.holmes.core.media.model.AbstractNode;
import net.holmes.core.scheduled.*;
import net.holmes.core.upnp.UpnpServer;
import net.holmes.core.upnp.metadata.UpnpDeviceMetadata;
import net.holmes.core.upnp.metadata.UpnpDeviceMetadataImpl;
import org.fourthline.cling.UpnpService;

import java.util.List;
import java.util.ResourceBundle;

import static org.easymock.EasyMock.createMock;

public class TestModule extends AbstractModule {
    private final EventBus eventBus = new EventBus("Holmes EventBus");
    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("message");

    @Override
    protected void configure() {
        bindListener(Matchers.any(), new CustomTypeListener(eventBus));

        bind(Configuration.class).to(TestConfiguration.class).in(Singleton.class);
        bind(ResourceBundle.class).toInstance(resourceBundle);

        bind(MediaDao.class).to(MediaDaoImpl.class).in(Singleton.class);
        bind(IcecastDao.class).to(IcecastDaoImpl.class).in(Singleton.class);
        bind(UpnpDeviceMetadata.class).to(UpnpDeviceMetadataImpl.class).in(Singleton.class);
        bind(MediaManager.class).to(MediaManagerImpl.class);
        bind(MediaIndexManager.class).to(MediaIndexManagerImpl.class);

        bindConstant().annotatedWith(Names.named("mimeTypePath")).to("/mimetypes.properties");
        bindConstant().annotatedWith(Names.named("uiDirectory")).to(System.getProperty("java.io.tmpdir"));
        bindConstant().annotatedWith(Names.named("localHolmesDataDir")).to(System.getProperty("java.io.tmpdir"));
        bindConstant().annotatedWith(Names.named("localIP")).to("localhost");

        bind(MimeTypeManager.class).to(MimeTypeManagerImpl.class);

        bind(BackendManager.class).to(BackendManagerImpl.class);

        bind(new TypeLiteral<Cache<String, List<AbstractNode>>>() {
        }).annotatedWith(Names.named("podcastCache")).toProvider(PodcastCacheProvider.class);

        bind(Service.class).annotatedWith(Names.named("http")).to(HttpServer.class);
        bind(Service.class).annotatedWith(Names.named("upnp")).to(UpnpServer.class);
        bind(Service.class).annotatedWith(Names.named("systray")).toInstance(createMock(Service.class));
        bind(Service.class).annotatedWith(Names.named("scheduler")).to(HolmesSchedulerService.class);

        bind(UpnpService.class).toInstance(createMock(UpnpService.class));

        bind(AbstractScheduledService.class).annotatedWith(Names.named("mediaIndexCleaner")).to(MediaIndexCleanerService.class);
        bind(AbstractScheduledService.class).annotatedWith(Names.named("podcastCacheCleaner")).to(CacheCleanerService.class);
        bind(AbstractScheduledService.class).annotatedWith(Names.named("mediaScanner")).to(MediaScannerService.class);
        bind(AbstractScheduledService.class).annotatedWith(Names.named("icecast")).to(IcecastDownloadService.class);

        bind(BackendExceptionMapper.class);
    }
}
