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

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Injector;
import net.holmes.core.backend.inject.BackendInjector;
import net.holmes.core.business.inject.BusinessInjector;
import net.holmes.core.common.exception.HolmesRuntimeException;
import net.holmes.core.common.inject.CommonInjector;
import net.holmes.core.service.HolmesService;
import net.holmes.core.service.Service;
import net.holmes.core.service.inject.ServiceInjector;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.nio.file.Path;

import static com.google.inject.Guice.createInjector;
import static java.nio.file.Files.exists;
import static java.nio.file.Paths.get;
import static net.holmes.core.common.Constants.HOLMES_HOME_CONF_DIRECTORY;
import static net.holmes.core.common.SystemProperty.HOLMES_HOME;
import static org.slf4j.LoggerFactory.getILoggerFactory;

/**
 * Bootstrap for Holmes - main class.
 */
public final class Bootstrap {

    /**
     * Private constructor.
     */
    private Bootstrap() {
    }

    /**
     * Holmes launcher.
     *
     * @param args command line arguments
     */
    public static void main(final String... args) {
        // Load logging
        loadLogging(args.length > 0 && "debug".equals(args[0]));

        // Create Guice injector
        Injector injector = createInjector(new CommonInjector(), new BusinessInjector(),
                new ServiceInjector(), new BackendInjector());

        // Start Holmes service
        final Service holmesService = injector.getInstance(HolmesService.class);
        holmesService.start();

        // Add Shutdown hook to stop Holmes service
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                holmesService.stop();
            }
        });
    }

    /**
     * Load logging configuration.
     *
     * @param debug enables debug mode
     */
    @VisibleForTesting
    static void loadLogging(final boolean debug) {
        // Get logback configuration file path
        Path logbackFilePath = get(HOLMES_HOME.getValue(), HOLMES_HOME_CONF_DIRECTORY.toString(), debug ? "logback-debug.xml" : "logback.xml");
        if (exists(logbackFilePath)) {
            try {
                // Load logback configuration
                LoggerContext context = (LoggerContext) getILoggerFactory();
                context.reset();
                JoranConfigurator configurator = new JoranConfigurator();
                configurator.setContext(context);
                configurator.doConfigure(logbackFilePath.toFile());

                // Install java.util.logging bridge
                SLF4JBridgeHandler.removeHandlersForRootLogger();
                SLF4JBridgeHandler.install();
            } catch (JoranException e) {
                throw new HolmesRuntimeException(e);
            }
        } else {
            throw new HolmesRuntimeException(logbackFilePath + " does not exist. Check " + HOLMES_HOME.getName() + " [" + HOLMES_HOME.getValue() + "] system property");
        }
    }
}
