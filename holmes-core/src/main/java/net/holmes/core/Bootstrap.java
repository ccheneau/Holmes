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

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.holmes.core.common.Service;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.xml.DOMConfigurator;
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

    private static final long LOG4J_WATCH_DELAY = 10000L;

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
        // Load log4j configuration
        Path logFilePath = Paths.get(HOLMES_HOME.getValue(), "conf", "log4j.xml");
        if (Files.exists(logFilePath))
            if (debug) {
                DOMConfigurator.configure(logFilePath.toString());
                LogManager.getLoggerRepository().setThreshold(Level.DEBUG);
            } else DOMConfigurator.configureAndWatch(logFilePath.toString(), LOG4J_WATCH_DELAY);
        else
            throw new RuntimeException(logFilePath + " does not exist. Check " + HOLMES_HOME.getName() + " [" + HOLMES_HOME.getValue() + "] system property");

        // Remove existing handlers attached to j.u.l root logger
        if (!debug) SLF4JBridgeHandler.removeHandlersForRootLogger();

        // Add SLF4JBridgeHandler to j.u.l's root logger
        SLF4JBridgeHandler.install();
    }
}
