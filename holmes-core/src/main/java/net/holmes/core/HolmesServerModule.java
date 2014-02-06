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

package net.holmes.core;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import net.holmes.core.backend.BackendManager;
import net.holmes.core.backend.BackendManagerImpl;
import net.holmes.core.backend.exception.BackendExceptionMapper;
import net.holmes.core.backend.handler.*;
import net.holmes.core.business.configuration.ConfigurationDao;
import net.holmes.core.business.configuration.XmlConfigurationDaoImpl;
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
import net.holmes.core.service.airplay.AirplayServer;
import net.holmes.core.service.http.HttpServer;
import net.holmes.core.service.http.file.HttpFileRequestDecoder;
import net.holmes.core.service.http.file.HttpFileRequestHandler;
import net.holmes.core.service.scheduled.CacheCleanerService;
import net.holmes.core.service.scheduled.HolmesSchedulerService;
import net.holmes.core.service.scheduled.IcecastDownloadService;
import net.holmes.core.service.systray.SystrayService;
import net.holmes.core.service.upnp.UpnpServer;
import net.holmes.core.service.upnp.UpnpServiceProvider;
import org.fourthline.cling.UpnpService;

import javax.net.SocketFactory;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

import static net.holmes.core.common.SystemProperty.HOLMES_HOME;
import static net.holmes.core.common.SystemProperty.USER_HOME;

/**
 * Guice module.
 */
final class HolmesServerModule extends AbstractModule {

    private final EventBus eventBus = new AsyncEventBus("Holmes EventBus", Executors.newCachedThreadPool());
    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("message");
    private final String localHolmesDataDir = getLocalHolmesDataDir();
    private final String uiDirectory = getUiDirectory();
    private final InetAddress localAddress = getLocalAddress();
    private final SocketFactory socketFactory = SocketFactory.getDefault();

    /**
     * Get local data directory where Holmes configuration and logs are saved.
     * This directory is stored in user home directory.
     *
     * @return local user data dir
     */
    @VisibleForTesting
    static String getLocalHolmesDataDir() {
        // Check directory and create it if it does not exist
        Path holmesDataPath = Paths.get(USER_HOME.getValue(), ".holmes");
        if ((Files.exists(holmesDataPath) && Files.isDirectory(holmesDataPath)) || holmesDataPath.toFile().mkdirs())
            return holmesDataPath.toString();

        throw new RuntimeException("Failed to create " + holmesDataPath);
    }

    /**
     * Get UI base directory.
     *
     * @return UI directory
     */
    @VisibleForTesting
    static String getUiDirectory() {
        Path uiPath = Paths.get(HOLMES_HOME.getValue(), "ui");
        if (!Files.exists(uiPath)) {
            throw new RuntimeException(uiPath + " does not exist. Check " + HOLMES_HOME.getName() + " [" + HOLMES_HOME.getValue()
                    + "] system property");
        }
        return uiPath.toString();
    }

    /**
     * Get local IPv4 address (InetAddress.getLocalHost().getHostAddress() does not work on Linux).
     *
     * @return local IPv4 address
     */
    @VisibleForTesting
    static InetAddress getLocalAddress() {
        try {
            for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
                NetworkInterface networkInterface = interfaces.nextElement();
                for (Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses(); inetAddresses.hasMoreElements(); ) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress())
                        return inetAddress;
                }
            }
            return InetAddress.getLocalHost();
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        // Bind constants
        bindConstant().annotatedWith(Names.named("localHolmesDataDir")).to(localHolmesDataDir);
        bindConstant().annotatedWith(Names.named("mimeTypePath")).to("/mimetypes.properties");
        bindConstant().annotatedWith(Names.named("uiDirectory")).to(uiDirectory);
        bind(InetAddress.class).annotatedWith(Names.named("localAddress")).toInstance(localAddress);

        // Bind utils
        bind(ResourceBundle.class).toInstance(resourceBundle);
        bind(SocketFactory.class).toInstance(socketFactory);

        // Bind event bus
        bind(EventBus.class).toInstance(eventBus);
        bindListener(Matchers.any(), new CustomTypeListener(eventBus));

        // Bind managers
        bind(MimeTypeManager.class).to(MimeTypeManagerImpl.class).in(Singleton.class);
        bind(MediaManager.class).to(MediaManagerImpl.class).in(Singleton.class);
        bind(StreamingManager.class).to(StreamingManagerImpl.class).in(Singleton.class);

        // Bind services
        bind(Service.class).annotatedWith(Names.named("http")).to(HttpServer.class).in(Singleton.class);
        bind(Service.class).annotatedWith(Names.named("upnp")).to(UpnpServer.class).in(Singleton.class);
        bind(Service.class).annotatedWith(Names.named("airplay")).to(AirplayServer.class).in(Singleton.class);
        bind(Service.class).annotatedWith(Names.named("systray")).to(SystrayService.class).in(Singleton.class);
        bind(Service.class).annotatedWith(Names.named("scheduler")).to(HolmesSchedulerService.class).in(Singleton.class);

        // Bind dao
        bind(ConfigurationDao.class).to(XmlConfigurationDaoImpl.class).in(Singleton.class);
        bind(MediaDao.class).to(MediaDaoImpl.class).in(Singleton.class);
        bind(IcecastDao.class).to(IcecastDaoImpl.class).in(Singleton.class);
        bind(MediaIndexDao.class).to(MediaIndexDaoImpl.class).in(Singleton.class);
        bind(DeviceDao.class).to(DeviceDaoImpl.class).in(Singleton.class);
        bind(SessionDao.class).to(SessionDaoImpl.class).in(Singleton.class);

        // Bind scheduled services
        bind(AbstractScheduledService.class).annotatedWith(Names.named("cacheCleaner")).to(CacheCleanerService.class);
        bind(AbstractScheduledService.class).annotatedWith(Names.named("icecast")).to(IcecastDownloadService.class);

        // Bind backend
        bind(BackendManager.class).to(BackendManagerImpl.class).in(Singleton.class);

        // Bind Upnp service
        bind(UpnpService.class).toProvider(UpnpServiceProvider.class).in(Singleton.class);

        // Bind Http handlers
        bind(HttpFileRequestDecoder.class);
        bind(HttpFileRequestHandler.class);

        // Bind streaming utils
        bind(DeviceStreamer.class).annotatedWith(Names.named("upnp")).to(UpnpStreamerImpl.class).in(Singleton.class);
        bind(ControlPoint.class).to(AsyncSocketControlPoint.class);
        bind(DeviceStreamer.class).annotatedWith(Names.named("airplay")).to(AirplayStreamerImpl.class).in(Singleton.class);

        // Bind Rest handlers
        bind(AudioFoldersHandler.class);
        bind(PictureFoldersHandler.class);
        bind(PodcastsHandler.class);
        bind(SettingsHandler.class);
        bind(UtilHandler.class);
        bind(VideoFoldersHandler.class);
        bind(BackendExceptionMapper.class);
        bind(StreamingHandler.class);
    }
}
