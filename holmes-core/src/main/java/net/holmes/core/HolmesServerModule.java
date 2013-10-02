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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.Cache;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.thoughtworks.xstream.XStream;
import net.holmes.core.backend.BackendManager;
import net.holmes.core.backend.BackendManagerImpl;
import net.holmes.core.backend.exception.BackendExceptionMapper;
import net.holmes.core.backend.handler.*;
import net.holmes.core.common.Service;
import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.common.configuration.XmlConfigurationImpl;
import net.holmes.core.common.mimetype.MimeTypeManager;
import net.holmes.core.common.mimetype.MimeTypeManagerImpl;
import net.holmes.core.http.HttpServer;
import net.holmes.core.http.file.HttpFileRequestDecoder;
import net.holmes.core.inject.CustomTypeListener;
import net.holmes.core.inject.provider.ImageCacheProvider;
import net.holmes.core.inject.provider.PodcastCacheProvider;
import net.holmes.core.inject.provider.UpnpServiceProvider;
import net.holmes.core.inject.provider.XStreamProvider;
import net.holmes.core.media.MediaManager;
import net.holmes.core.media.MediaManagerImpl;
import net.holmes.core.media.dao.MediaDao;
import net.holmes.core.media.dao.MediaDaoImpl;
import net.holmes.core.media.index.MediaIndexManager;
import net.holmes.core.media.index.MediaIndexManagerImpl;
import net.holmes.core.media.model.AbstractNode;
import net.holmes.core.scheduled.CacheCleanerService;
import net.holmes.core.scheduled.HolmesSchedulerService;
import net.holmes.core.scheduled.MediaIndexCleanerService;
import net.holmes.core.scheduled.MediaScannerService;
import net.holmes.core.upnp.UpnpServer;
import org.fourthline.cling.UpnpService;

import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
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
    private final String localIP = getLocalIPV4();

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
    static String getLocalIPV4() {
        try {
            for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
                NetworkInterface networkInterface = interfaces.nextElement();
                for (Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses(); inetAddresses.hasMoreElements(); ) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress())
                        return inetAddress.getHostAddress();
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void configure() {
        // Bind utils
        bind(EventBus.class).toInstance(eventBus);
        bindListener(Matchers.any(), new CustomTypeListener(eventBus));
        bind(Configuration.class).to(XmlConfigurationImpl.class).in(Singleton.class);
        bind(ResourceBundle.class).toInstance(resourceBundle);
        bind(MediaDao.class).to(MediaDaoImpl.class).in(Singleton.class);
        bind(XStream.class).toProvider(XStreamProvider.class);

        // Bind media service
        bind(MediaManager.class).to(MediaManagerImpl.class).in(Singleton.class);
        bind(MimeTypeManager.class).to(MimeTypeManagerImpl.class).in(Singleton.class);
        bind(MediaIndexManager.class).to(MediaIndexManagerImpl.class).in(Singleton.class);


        // Bind caches
        bind(new TypeLiteral<Cache<String, List<AbstractNode>>>() {
        }).annotatedWith(Names.named("podcastCache")).toProvider(PodcastCacheProvider.class).in(Singleton.class);
        bind(new TypeLiteral<Cache<String, String>>() {
        }).annotatedWith(Names.named("imageCache")).toProvider(ImageCacheProvider.class).in(Singleton.class);

        // Bind constants
        bindConstant().annotatedWith(Names.named("localHolmesDataDir")).to(localHolmesDataDir);
        bindConstant().annotatedWith(Names.named("mimeTypePath")).to("/mimetypes.properties");
        bindConstant().annotatedWith(Names.named("uiDirectory")).to(uiDirectory);
        bindConstant().annotatedWith(Names.named("localIP")).to(localIP);

        // Bind scheduled services
        bind(AbstractScheduledService.class).annotatedWith(Names.named("mediaIndexCleaner")).to(MediaIndexCleanerService.class);
        bind(AbstractScheduledService.class).annotatedWith(Names.named("podcastCacheCleaner")).to(CacheCleanerService.class);
        bind(AbstractScheduledService.class).annotatedWith(Names.named("mediaScanner")).to(MediaScannerService.class);

        // Bind servers
        bind(Service.class).annotatedWith(Names.named("http")).to(HttpServer.class).in(Singleton.class);
        bind(Service.class).annotatedWith(Names.named("upnp")).to(UpnpServer.class).in(Singleton.class);
        bind(Service.class).annotatedWith(Names.named("systray")).to(SystrayService.class).in(Singleton.class);
        bind(Service.class).annotatedWith(Names.named("scheduler")).to(HolmesSchedulerService.class).in(Singleton.class);

        // Bind backend
        bind(BackendManager.class).to(BackendManagerImpl.class).in(Singleton.class);

        // Bind Upnp service
        bind(UpnpService.class).toProvider(UpnpServiceProvider.class);

        // Bind Http decoder
        bind(HttpFileRequestDecoder.class);

        // Bind Rest handlers
        bind(AudioFoldersHandler.class);
        bind(PictureFoldersHandler.class);
        bind(PodcastsHandler.class);
        bind(SettingsHandler.class);
        bind(UtilHandler.class);
        bind(VideoFoldersHandler.class);

        // Bind Rest exception mapper
        bind(BackendExceptionMapper.class);
    }
}
