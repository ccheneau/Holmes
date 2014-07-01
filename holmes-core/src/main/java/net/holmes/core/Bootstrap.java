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
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.holmes.core.common.exception.HolmesRuntimeException;
import net.holmes.core.service.HolmesService;
import net.holmes.core.service.Service;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static net.holmes.core.common.SystemProperty.HOLMES_HOME;

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
        Injector injector = Guice.createInjector(new HolmesInjector());

        // Start Holmes service
        final Service holmesService = injector.getInstance(HolmesService.class);
        holmesService.start();

        // Add Shutdown hook to stop Holmes server
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
     * @param debug activates debug mode
     */
    private static void loadLogging(final boolean debug) {
        // Define logback configuration file name
        Path logbackFilePath = Paths.get(HOLMES_HOME.getValue(), "conf", debug ? "logback-debug.xml" : "logback.xml");
        if (Files.exists(logbackFilePath)) {
            try {
                // Load logback configuration
                LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
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
