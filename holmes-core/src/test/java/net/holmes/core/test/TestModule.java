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
import net.holmes.core.business.configuration.Configuration;
import net.holmes.core.business.media.MediaManager;
import net.holmes.core.business.media.MediaManagerImpl;
import net.holmes.core.business.media.dao.MediaDao;
import net.holmes.core.business.media.dao.MediaDaoImpl;
import net.holmes.core.business.media.dao.icecast.IcecastDao;
import net.holmes.core.business.media.dao.icecast.IcecastDaoImpl;
import net.holmes.core.business.media.dao.index.MediaIndexDao;
import net.holmes.core.business.media.dao.index.MediaIndexDaoImpl;
import net.holmes.core.business.mimetype.MimeTypeManager;
import net.holmes.core.business.mimetype.MimeTypeManagerImpl;
import net.holmes.core.business.streaming.StreamingManager;
import net.holmes.core.business.streaming.StreamingManagerImpl;
import net.holmes.core.business.streaming.airplay.AirplayStreamerImpl;
import net.holmes.core.business.streaming.airplay.controlpoint.AsyncSocketControlPoint;
import net.holmes.core.business.streaming.airplay.controlpoint.ControlPoint;
import net.holmes.core.business.streaming.device.DeviceDao;
import net.holmes.core.business.streaming.device.DeviceDaoImpl;
import net.holmes.core.business.streaming.device.DeviceStreamer;
import net.holmes.core.business.streaming.session.SessionDao;
import net.holmes.core.business.streaming.session.SessionDaoImpl;
import net.holmes.core.business.streaming.upnp.UpnpStreamerImpl;
import net.holmes.core.common.CustomTypeListener;
import net.holmes.core.service.Service;
import net.holmes.core.service.http.HttpServer;
import net.holmes.core.service.scheduled.CacheCleanerService;
import net.holmes.core.service.scheduled.HolmesSchedulerService;
import net.holmes.core.service.scheduled.IcecastDownloadService;
import net.holmes.core.service.upnp.UpnpServer;
import org.fourthline.cling.UpnpService;

import javax.net.SocketFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import static org.easymock.EasyMock.createMock;

public class TestModule extends AbstractModule {
    private final EventBus eventBus = new EventBus("Holmes EventBus");
    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("message");
    private final SocketFactory socketFactory = SocketFactory.getDefault();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        bindListener(Matchers.any(), new CustomTypeListener(eventBus));

        bind(Configuration.class).to(TestConfiguration.class).in(Singleton.class);
        bind(ResourceBundle.class).toInstance(resourceBundle);
        bind(SocketFactory.class).toInstance(socketFactory);

        bind(MediaDao.class).to(MediaDaoImpl.class).in(Singleton.class);
        bind(IcecastDao.class).to(IcecastDaoImpl.class).in(Singleton.class);
        bind(DeviceDao.class).to(DeviceDaoImpl.class).in(Singleton.class);
        bind(SessionDao.class).to(SessionDaoImpl.class).in(Singleton.class);
        bind(ControlPoint.class).to(AsyncSocketControlPoint.class);
        bind(DeviceStreamer.class).annotatedWith(Names.named("upnp")).to(UpnpStreamerImpl.class).in(Singleton.class);
        bind(DeviceStreamer.class).annotatedWith(Names.named("airplay")).to(AirplayStreamerImpl.class).in(Singleton.class);
        bind(StreamingManager.class).to(StreamingManagerImpl.class).in(Singleton.class);
        bind(MediaManager.class).to(MediaManagerImpl.class);
        bind(MediaIndexDao.class).to(MediaIndexDaoImpl.class);

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
