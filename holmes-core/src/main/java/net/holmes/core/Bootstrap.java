/**
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

import java.io.File;

import net.holmes.common.Service;
import net.holmes.common.SystemProperty;
import net.holmes.common.SystemUtils;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Bootstrap {

    public static void main(String... args) {
        // Check lock file
        if (SystemUtils.lockInstance()) {
            // Load log4j
            loadLog4j(args.length > 0 && "debug".equals(args[0]));

            // Create Guice injector
            Injector injector = Guice.createInjector(new HolmesServerModule());

            // Start Holmes server
            final Service holmesServer = injector.getInstance(HolmesServer.class);
            try {
                holmesServer.start();
            } catch (RuntimeException e) {
                LoggerFactory.getLogger(Bootstrap.class).error(e.getMessage(), e);
                System.exit(1);
            }

            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    holmesServer.stop();
                }
            });
        } else {
            System.err.println("Holmes is already running");
            System.exit(1);
        }
    }

    private static void loadLog4j(boolean debug) {
        // Load log4j configuration
        File confDir = new File(SystemProperty.HOLMES_HOME.getValue(), "conf");
        String logConfig = confDir.getAbsolutePath() + File.separator + "log4j.xml";

        if (new File(logConfig).exists()) {
            if (debug) {
                DOMConfigurator.configure(logConfig);
                LogManager.getLoggerRepository().setThreshold(Level.DEBUG);
            } else DOMConfigurator.configureAndWatch(logConfig, 10000l);
        } else throw new RuntimeException(logConfig + " does not exist. Check " + SystemProperty.HOLMES_HOME.getName() + " ["
                + SystemProperty.HOLMES_HOME.getValue() + "] system property");

        // Remove existing handlers attached to j.u.l root logger
        SLF4JBridgeHandler.removeHandlersForRootLogger();

        // Add SLF4JBridgeHandler to j.u.l's root logger
        SLF4JBridgeHandler.install();
    }
}
