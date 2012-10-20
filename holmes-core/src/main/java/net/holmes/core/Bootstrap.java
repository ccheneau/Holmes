/**
* Copyright (C) 2012  Cedric Cheneau
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

import net.holmes.core.util.HolmesHomeDirectory;
import net.holmes.core.util.HolmesLock;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Bootstrap {

    public static void main(String[] args) {
        // Check lock file
        if (HolmesLock.lockInstance()) {
            // Load log4j configuration
            String logConfig = HolmesHomeDirectory.getConfigDirectory() + File.separator + "log4j.xml";
            if (new File(logConfig).exists()) DOMConfigurator.configureAndWatch(logConfig, 10000l);

            // Create Guice injector
            Injector injector = Guice.createInjector(new HolmesServerModule());

            // Start Holmes server
            final Server holmesServer = injector.getInstance(HolmesServer.class);
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
}
