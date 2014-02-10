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
import net.holmes.core.service.HolmesServer;
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
     * Instantiates a new bootstrap.
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
        Injector injector = Guice.createInjector(new HolmesServerModule());

        // Start Holmes server
        try {
            final Service holmesServer = injector.getInstance(HolmesServer.class);
            holmesServer.start();

            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void run() {
                    holmesServer.stop();
                }
            });
        } catch (RuntimeException e) {
            LoggerFactory.getLogger(Bootstrap.class).error(e.getMessage(), e);
            System.exit(1);
        }

    }

    /**
     * Configure logging.
     *
     * @param debug activates debug mode
     */
    private static void loadLogging(final boolean debug) {
        // Define logback configuration file name
        String logbackFileName = debug ? "logback-debug.xml" : "logback.xml";

        Path logFilePath = Paths.get(HOLMES_HOME.getValue(), "conf", logbackFileName);
        if (Files.exists(logFilePath)) {
            try {
                // Load logback configuration
                LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
                context.reset();
                JoranConfigurator configurator = new JoranConfigurator();
                configurator.setContext(context);
                configurator.doConfigure(logFilePath.toFile());

                // Install java.util.logging bridge
                SLF4JBridgeHandler.removeHandlersForRootLogger();
                SLF4JBridgeHandler.install();
            } catch (JoranException e) {
                throw new RuntimeException(e);
            }
        } else
            throw new RuntimeException(logFilePath + " does not exist. Check " + HOLMES_HOME.getName() + " [" + HOLMES_HOME.getValue() + "] system property");
    }
}
