/**
* Copyright (c) 2012 Cedric Cheneau
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/
package net.holmes.core;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

import net.holmes.core.configuration.IConfiguration;
import net.holmes.core.util.LogUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

public final class HolmesServer implements IServer {
    private static Logger logger = LoggerFactory.getLogger(HolmesServer.class);

    @Inject
    private IConfiguration configuration;

    @Inject
    @Named("http")
    private IServer httpServer;

    @Inject
    @Named("upnp")
    private IServer upnpServer;

    public HolmesServer() {
    }

    /* (non-Javadoc)
     * @see net.holmes.core.IServer#start()
     */
    @Override
    public void start() {
        logger.info("Starting Holmes server");
        // Start Holmes server
        httpServer.start();
        upnpServer.start();
        initSystray();
        logger.info("Holmes server started");
    }

    /* (non-Javadoc)
     * @see net.holmes.core.IServer#stop()
     */
    @Override
    public void stop() {
        logger.info("Stopping Holmes server");
        // Stop Holmes server
        upnpServer.stop();
        httpServer.stop();
        logger.info("Holmes server stopped");
    }

    private void initSystray() {
        // Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            return;
        }

        ResourceBundle bundle = ResourceBundle.getBundle("message");

        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/systray.gif")), bundle.getString("systray.title"));
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a popup menu components
        // Quit Holmes menu item
        MenuItem quitItem = new MenuItem(bundle.getString("systray.quit"));
        quitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }
        });
        // Holmes admin site menu item
        MenuItem holmesItem = new MenuItem(bundle.getString("systray.holmes"));
        holmesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        StringBuilder holmesUrl = new StringBuilder();
                        holmesUrl.append("http://localhost:").append(configuration.getConfig().getHttpServerPort()).append("/");
                        Desktop.getDesktop().browse(new URI(holmesUrl.toString()));
                    }
                    catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                    catch (URISyntaxException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });

        // Add components to popup menu
        popup.add(holmesItem);
        popup.addSeparator();
        popup.add(quitItem);

        trayIcon.setImageAutoSize(true);
        trayIcon.setPopupMenu(popup);
        try {
            tray.add(trayIcon);
        }
        catch (AWTException e) {
            logger.error(e.getMessage(), e);
        }
    }

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
