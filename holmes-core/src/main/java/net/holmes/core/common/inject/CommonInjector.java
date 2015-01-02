/*
 * Copyright (C) 2012-2015  Cedric Cheneau
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

package net.holmes.core.common.inject;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import net.holmes.core.common.exception.HolmesRuntimeException;

import java.net.InetAddress;
import java.nio.file.Path;
import java.util.ResourceBundle;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.inject.name.Names.named;
import static java.nio.file.Files.*;
import static java.nio.file.Paths.get;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static net.holmes.core.common.Constants.HOLMES_HOME_UI_DIRECTORY;
import static net.holmes.core.common.SystemProperty.*;

/**
 * Holmes common Guice injector.
 */
public class CommonInjector extends AbstractModule {
    private final EventBus eventBus;
    private final ResourceBundle resourceBundle;
    private final String localHolmesDataDir;
    private final String uiDirectory;
    private final String currentVersion;

    /**
     * Instantiates a new common injector
     */
    public CommonInjector() {
        eventBus = new AsyncEventBus("Holmes EventBus", newCachedThreadPool());
        resourceBundle = ResourceBundle.getBundle("message");
        localHolmesDataDir = getLocalHolmesDataDir();
        uiDirectory = getHolmesHomeSubDirectory(HOLMES_HOME_UI_DIRECTORY.toString());
        currentVersion = nullToEmpty(this.getClass().getPackage().getImplementationVersion());
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

        bind(InetAddress.class).annotatedWith(named("localAddress")).toProvider(LocalAddressProvider.class).in(Singleton.class);

        // Bind resource bundle
        bind(ResourceBundle.class).toInstance(resourceBundle);

        // Bind event bus
        bind(EventBus.class).toInstance(eventBus);
        bindListener(Matchers.any(), new EventBusListener(eventBus));
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
        if ((exists(holmesDataPath) && isDirectory(holmesDataPath)) || holmesDataPath.toFile().mkdirs()) {
            return holmesDataPath.toString();
        }

        throw new HolmesRuntimeException("Failed to create " + holmesDataPath);
    }

    /**
     * Get Holmes home sub directory.
     *
     * @param subDirName name of sub directory
     * @return Holmes home sub directory path.
     */
    @VisibleForTesting
    static String getHolmesHomeSubDirectory(final String subDirName) {
        Path uiPath = get(HOLMES_HOME.getValue(), subDirName);
        if (!exists(uiPath)) {
            throw new HolmesRuntimeException(uiPath + " does not exist. Check " + HOLMES_HOME.getName() + " [" + HOLMES_HOME.getValue() + "] system property");
        }

        return uiPath.toString();
    }
}
