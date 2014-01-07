/*
 * Copyright (C) 2012-2014  Cedric Cheneau
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

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
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
import net.holmes.core.media.MediaService;
import net.holmes.core.media.MediaServiceImpl;
import net.holmes.core.media.dao.MediaDao;
import net.holmes.core.media.dao.MediaDaoImpl;
import net.holmes.core.media.dao.icecast.IcecastDao;
import net.holmes.core.media.dao.icecast.IcecastDaoImpl;
import net.holmes.core.media.index.MediaIndexManager;
import net.holmes.core.media.index.MediaIndexManagerImpl;
import net.holmes.core.scheduled.CacheCleanerService;
import net.holmes.core.scheduled.HolmesSchedulerService;
import net.holmes.core.scheduled.IcecastDownloadService;
import net.holmes.core.transport.TransportService;
import net.holmes.core.transport.TransportServiceImpl;
import net.holmes.core.transport.airplay.AirplayStreamerImpl;
import net.holmes.core.transport.airplay.controlpoint.AsyncControlPoint;
import net.holmes.core.transport.airplay.controlpoint.ControlPoint;
import net.holmes.core.transport.device.DeviceDao;
import net.holmes.core.transport.device.DeviceDaoImpl;
import net.holmes.core.transport.device.DeviceStreamer;
import net.holmes.core.transport.session.SessionDao;
import net.holmes.core.transport.session.SessionDaoImpl;
import net.holmes.core.transport.upnp.UpnpStreamerImpl;
import net.holmes.core.upnp.UpnpServer;
import org.fourthline.cling.UpnpService;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
        bind(DeviceDao.class).to(DeviceDaoImpl.class).in(Singleton.class);
        bind(SessionDao.class).to(SessionDaoImpl.class).in(Singleton.class);
        bind(ControlPoint.class).to(AsyncControlPoint.class);
        bind(DeviceStreamer.class).annotatedWith(Names.named("upnp")).to(UpnpStreamerImpl.class).in(Singleton.class);
        bind(DeviceStreamer.class).annotatedWith(Names.named("airplay")).to(AirplayStreamerImpl.class).in(Singleton.class);
        bind(TransportService.class).to(TransportServiceImpl.class).in(Singleton.class);
        bind(MediaService.class).to(MediaServiceImpl.class);
        bind(MediaIndexManager.class).to(MediaIndexManagerImpl.class);

        bindConstant().annotatedWith(Names.named("mimeTypePath")).to("/mimetypes.properties");
        bindConstant().annotatedWith(Names.named("uiDirectory")).to(System.getProperty("java.io.tmpdir"));
        bindConstant().annotatedWith(Names.named("localHolmesDataDir")).to(System.getProperty("java.io.tmpdir"));

        try {
            bind(InetAddress.class).annotatedWith(Names.named("localAddress")).toInstance(InetAddress.getByName("localhost"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        bind(MimeTypeManager.class).to(MimeTypeManagerImpl.class);

        bind(BackendManager.class).to(BackendManagerImpl.class);

        bind(Service.class).annotatedWith(Names.named("http")).to(HttpServer.class);
        bind(Service.class).annotatedWith(Names.named("upnp")).to(UpnpServer.class);
        bind(Service.class).annotatedWith(Names.named("systray")).toInstance(createMock(Service.class));
        bind(Service.class).annotatedWith(Names.named("scheduler")).to(HolmesSchedulerService.class);

        bind(UpnpService.class).toInstance(createMock(UpnpService.class));

        bind(AbstractScheduledService.class).annotatedWith(Names.named("cacheCleaner")).to(CacheCleanerService.class);
        bind(AbstractScheduledService.class).annotatedWith(Names.named("icecast")).to(IcecastDownloadService.class);

        bind(BackendExceptionMapper.class);
    }
}
