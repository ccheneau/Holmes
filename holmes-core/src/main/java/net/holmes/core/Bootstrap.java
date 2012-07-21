package net.holmes.core;

import net.holmes.core.util.LogUtil;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Bootstrap {

    public static void main(String[] args) {
        // Load log configuration
        LogUtil.loadConfig();

        // Create Guice injector
        Injector injector = Guice.createInjector(new HolmesServerModule());

        // Start Holmes server
        IServer holmesServer = injector.getInstance(HolmesServer.class);
        holmesServer.start();

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(holmesServer));
    }

    /**
     * Shutdown hook: properly stop Holmes server on system exit
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
