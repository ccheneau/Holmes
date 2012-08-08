package net.holmes.core;

import java.io.File;

import net.holmes.core.util.SystemProperty;

import org.apache.log4j.xml.DOMConfigurator;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Bootstrap {

    public static void main(String[] args) {
        String homeDir = System.getProperty(SystemProperty.HOLMES_HOME.getValue());
        if (homeDir != null && new File(homeDir).exists()) {
            // Load log4j configuration
            String logConfig = homeDir + File.separator + "conf" + File.separator + "log4j.xml";
            if (new File(logConfig).exists()) DOMConfigurator.configureAndWatch(logConfig, 10000l);

            // Create Guice injector
            Injector injector = Guice.createInjector(new HolmesServerModule());

            // Start Holmes server
            IServer holmesServer = injector.getInstance(HolmesServer.class);
            holmesServer.start();

            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new ShutdownHook(holmesServer));
        }
        else {
            System.err.println(SystemProperty.HOLMES_HOME.getValue() + " system variable undefined or not valid");
            System.exit(1);
        }

    }

    /**
     * Shutdown hook: stop Holmes server on system exit
     *
     */
    private static class ShutdownHook extends Thread {
        IServer holmesServer;

        public ShutdownHook(IServer holmesServer) {
            this.holmesServer = holmesServer;
        }

        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            holmesServer.stop();
        }
    }

}
