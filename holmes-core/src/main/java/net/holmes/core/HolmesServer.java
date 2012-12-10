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

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;

import net.holmes.core.configuration.Configuration;
import net.holmes.core.configuration.Parameter;
import net.holmes.core.util.SystemTrayIcon;
import net.holmes.core.util.SystemUtils;
import net.holmes.core.util.bundle.Bundle;
import net.holmes.core.util.inject.Loggable;

import org.slf4j.Logger;

/**
 * Holmes server main class
 */
@Loggable
public final class HolmesServer implements Server {
    private Logger logger;

    private final Server httpServer;
    private final Server upnpServer;
    private final Configuration configuration;
    private final Bundle bundle;

    @Inject
    public HolmesServer(@Named("http") Server httpServer, @Named("upnp") Server upnpServer, Configuration configuration, Bundle bundle) {
        this.httpServer = httpServer;
        this.upnpServer = upnpServer;
        this.configuration = configuration;
        this.bundle = bundle;
    }

    @Override
    public void start() {
        if (logger.isInfoEnabled()) logger.info("Starting Holmes server");

        // Start Holmes server
        httpServer.start();
        upnpServer.start();

        if (configuration.getParameter(Parameter.ENABLE_SYSTRAY)) {
            // Add system tray icon
            if (initUI()) initSystemTrayIcon();
        }

        if (logger.isInfoEnabled()) logger.info("Holmes server started");
    }

    @Override
    public void stop() {
        if (logger.isInfoEnabled()) logger.info("Stopping Holmes server");

        // Stop Holmes server
        upnpServer.stop();
        httpServer.stop();

        if (logger.isInfoEnabled()) logger.info("Holmes server stopped");
    }

    private boolean initUI() {
        boolean init = true;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Add bold font for systray menu item
            Font menuItemFont = UIManager.getFont("MenuItem.font");
            if (menuItemFont != null) {
                FontUIResource menuItemBoldFont = new FontUIResource(menuItemFont.getFamily(), Font.BOLD, menuItemFont.getSize());
                UIManager.put("MenuItem.bold.font", menuItemBoldFont);
            }
        } catch (ClassNotFoundException e) {
            init = false;
        } catch (InstantiationException e) {
            init = false;
        } catch (IllegalAccessException e) {
            init = false;
        } catch (UnsupportedLookAndFeelException e) {
            init = false;
        }
        return init;
    }

    private void initSystemTrayIcon() {
        // Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            return;
        }

        // Initialize systray icon
        final Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/systray.gif"));
        final SystemTrayIcon systemTrayIcon = new SystemTrayIcon(image, bundle.getString("systray.title"));
        final SystemTray systemTray = SystemTray.getSystemTray();

        // Create a popup menu
        final JPopupMenu popupMenu = new JPopupMenu();

        // Quit Holmes menu item
        JMenuItem quitItem = new JMenuItem(bundle.getString("systray.quit"));
        quitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }
        });

        // Holmes logs menu item
        JMenuItem logsItem = new JMenuItem(bundle.getString("systray.logs"));
        logsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        StringBuilder logFile = new StringBuilder();
                        logFile.append(SystemUtils.getLocalHolmesDataDir().getAbsolutePath()).append(File.separator) //
                                .append("log").append(File.separator).append("holmes.log");
                        Desktop.getDesktop().open(new File(logFile.toString()));
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });

        // Holmes admin site menu item
        JMenuItem holmesItem = new JMenuItem(bundle.getString("systray.holmes"));
        Font boldFont = UIManager.getFont("MenuItem.bold.font");
        if (boldFont != null) holmesItem.setFont(boldFont);

        holmesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        StringBuilder holmesUrl = new StringBuilder();
                        holmesUrl.append("http://localhost:").append(configuration.getHttpServerPort()).append("/");
                        Desktop.getDesktop().browse(new URI(holmesUrl.toString()));
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    } catch (URISyntaxException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });

        // Add items to popup menu
        popupMenu.add(holmesItem);
        popupMenu.add(logsItem);
        popupMenu.addSeparator();
        popupMenu.add(quitItem);

        // Add tray icon
        systemTrayIcon.setImageAutoSize(true);
        systemTrayIcon.setPopupMenu(popupMenu);
        try {
            systemTray.add(systemTrayIcon);
        } catch (AWTException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
