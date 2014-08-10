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
import net.holmes.core.business.media.dao.index.MediaIndexDao;
import net.holmes.core.business.media.dao.index.MediaIndexDaoImpl;
import net.holmes.core.business.mimetype.MimeTypeManager;
import net.holmes.core.business.mimetype.MimeTypeManagerImpl;
import net.holmes.core.business.mimetype.dao.MimeTypeDao;
import net.holmes.core.business.mimetype.dao.MimeTypeDaoImpl;
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
import net.holmes.core.business.version.VersionManager;
import net.holmes.core.business.version.VersionManagerImpl;
import net.holmes.core.business.version.release.ReleaseDao;
import net.holmes.core.business.version.release.ReleaseDaoImpl;
import net.holmes.core.common.EventBusListener;
import net.holmes.core.common.exception.HolmesRuntimeException;
import net.holmes.core.service.Service;
import net.holmes.core.service.airplay.AirplayService;
import net.holmes.core.service.http.HttpFileRequestDecoder;
import net.holmes.core.service.http.HttpFileRequestHandler;
import net.holmes.core.service.http.HttpService;
import net.holmes.core.service.scheduled.CacheCleanerService;
import net.holmes.core.service.scheduled.HolmesSchedulerService;
import net.holmes.core.service.scheduled.ReleaseCheckService;
import net.holmes.core.service.systray.SystrayService;
import net.holmes.core.service.upnp.UpnpService;
import net.holmes.core.service.upnp.UpnpServiceProvider;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.inject.name.Names.named;
import static java.nio.file.Paths.get;
import static java.util.Collections.list;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static net.holmes.core.common.SystemProperty.*;

/**
 * Holmes Guice injector.
 */
public final class HolmesInjector extends AbstractModule {
    private final EventBus eventBus;
    private final ResourceBundle resourceBundle;
    private final String localHolmesDataDir;
    private final String uiDirectory;
    private final InetAddress localAddress;
    private final SocketFactory socketFactory;
    private final String currentVersion;

    /**
     * Instantiates Holmes injector.
     */
    public HolmesInjector() {
        eventBus = new AsyncEventBus("Holmes EventBus", newCachedThreadPool());
        resourceBundle = ResourceBundle.getBundle("message");
        localHolmesDataDir = getLocalHolmesDataDir();
        uiDirectory = getUiDirectory();
        socketFactory = SocketFactory.getDefault();
        currentVersion = nullToEmpty(this.getClass().getPackage().getImplementationVersion());
        try {
            localAddress = getLocalAddress();
        } catch (IOException e) {
            throw new HolmesRuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        // Bind constants
        bindConstant().annotatedWith(named("localHolmesDataDir")).to(localHolmesDataDir);
        bindConstant().annotatedWith(named("mimeTypePath")).to("/mimetypes.properties");
        bindConstant().annotatedWith(named("uiDirectory")).to(uiDirectory);
        bindConstant().annotatedWith(named("currentVersion")).to(currentVersion);
        bind(InetAddress.class).annotatedWith(named("localAddress")).toInstance(localAddress);

        // Bind utils
        bind(ResourceBundle.class).toInstance(resourceBundle);
        bind(SocketFactory.class).toInstance(socketFactory);

        // Bind event bus
        bind(EventBus.class).toInstance(eventBus);
        bindListener(Matchers.any(), new EventBusListener(eventBus));

        // Bind dao
        bind(ConfigurationDao.class).to(XmlConfigurationDaoImpl.class).in(Singleton.class);
        bind(MediaDao.class).to(MediaDaoImpl.class).in(Singleton.class);
        bind(MediaIndexDao.class).to(MediaIndexDaoImpl.class).in(Singleton.class);
        bind(DeviceDao.class).to(DeviceDaoImpl.class).in(Singleton.class);
        bind(SessionDao.class).to(SessionDaoImpl.class).in(Singleton.class);
        bind(ReleaseDao.class).to(ReleaseDaoImpl.class).in(Singleton.class);
        bind(MimeTypeDao.class).to(MimeTypeDaoImpl.class).in(Singleton.class);

        // Bind business managers
        bind(MimeTypeManager.class).to(MimeTypeManagerImpl.class).in(Singleton.class);
        bind(MediaManager.class).to(MediaManagerImpl.class).in(Singleton.class);
        bind(StreamingManager.class).to(StreamingManagerImpl.class).in(Singleton.class);
        bind(VersionManager.class).to(VersionManagerImpl.class).in(Singleton.class);

        // Bind services
        bind(Service.class).annotatedWith(named("http")).to(HttpService.class).in(Singleton.class);
        bind(Service.class).annotatedWith(named("upnp")).to(UpnpService.class).in(Singleton.class);
        bind(Service.class).annotatedWith(named("airplay")).to(AirplayService.class).in(Singleton.class);
        bind(Service.class).annotatedWith(named("systray")).to(SystrayService.class).in(Singleton.class);
        bind(Service.class).annotatedWith(named("scheduler")).to(HolmesSchedulerService.class).in(Singleton.class);

        // Bind scheduled services
        bind(AbstractScheduledService.class).annotatedWith(named("cacheCleaner")).to(CacheCleanerService.class);
        bind(AbstractScheduledService.class).annotatedWith(named("release")).to(ReleaseCheckService.class);

        // Bind backend
        bind(BackendManager.class).to(BackendManagerImpl.class).in(Singleton.class);

        // Bind Upnp service
        bind(org.fourthline.cling.UpnpService.class).toProvider(UpnpServiceProvider.class).in(Singleton.class);

        // Bind Http handlers
        bind(HttpFileRequestDecoder.class);
        bind(HttpFileRequestHandler.class);

        // Bind streaming utils
        bind(DeviceStreamer.class).annotatedWith(named("upnp")).to(UpnpStreamerImpl.class).in(Singleton.class);
        bind(DeviceStreamer.class).annotatedWith(named("airplay")).to(AirplayStreamerImpl.class).in(Singleton.class);
        bind(ControlPoint.class).to(AsyncSocketControlPoint.class);

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

    /**
     * Get local data directory where Holmes configuration and logs are stored.
     * This directory is a user home sub directory.
     *
     * @return local user data dir
     */
    private static String getLocalHolmesDataDir() {
        // Check directory and create it if it does not exist
        Path holmesDataPath = get(USER_HOME.getValue(), ".holmes");
        if ((Files.exists(holmesDataPath) && Files.isDirectory(holmesDataPath)) || holmesDataPath.toFile().mkdirs()) {
            return holmesDataPath.toString();
        }

        throw new HolmesRuntimeException("Failed to create " + holmesDataPath);
    }

    /**
     * Get UI base directory.
     *
     * @return UI directory
     */
    private static String getUiDirectory() {
        Path uiPath = get(HOLMES_HOME.getValue(), "ui");
        if (!Files.exists(uiPath)) {
            throw new HolmesRuntimeException(uiPath + " does not exist. Check " + HOLMES_HOME.getName() + " [" + HOLMES_HOME.getValue() + "] system property");
        }

        return uiPath.toString();
    }

    /**
     * Get local IPv4 address (InetAddress.getLocalHost() does not work on Linux).
     *
     * @return local IPv4 address
     */
    @VisibleForTesting
    static InetAddress getLocalAddress() throws IOException {
        for (NetworkInterface networkInterface : list(NetworkInterface.getNetworkInterfaces())) {
            for (InetAddress inetAddress : list(networkInterface.getInetAddresses())) {
                if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
                    return inetAddress;
                }
            }
        }
        return InetAddress.getLocalHost();
    }
}
