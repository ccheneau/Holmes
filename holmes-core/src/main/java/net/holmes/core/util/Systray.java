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

package net.holmes.core.util;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.FontUIResource;

import net.holmes.core.Server;
import net.holmes.core.configuration.Configuration;
import net.holmes.core.configuration.Parameter;
import net.holmes.core.util.bundle.Bundle;
import net.holmes.core.util.inject.Loggable;

import org.slf4j.Logger;

@Loggable
public class Systray implements Server {
    private Logger logger;

    private final Configuration configuration;
    private final Bundle bundle;

    @Inject
    public Systray(Configuration configuration, Bundle bundle) {
        this.configuration = configuration;
        this.bundle = bundle;
    }

    @Override
    public void start() {
        if (configuration.getParameter(Parameter.ENABLE_SYSTRAY)) {
            // Add system tray icon
            if (initUI()) initSystemTrayIcon();
        }
    }

    @Override
    public void stop() {
        // Nothing
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
        } catch (Exception e) {
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
        final Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/logo.png"));
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
                        logFile.append(SystemUtils.getLocalUserDataDir().getAbsolutePath()).append(File.separator) //
                                .append("log").append(File.separator).append("holmes.log");
                        Desktop.getDesktop().open(new File(logFile.toString()));
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });

        // Holmes ui menu item
        JMenuItem holmesUiItem = new JMenuItem(bundle.getString("systray.holmes.ui"));
        Font boldFont = UIManager.getFont("MenuItem.bold.font");
        if (boldFont != null) holmesUiItem.setFont(boldFont);

        holmesUiItem.addActionListener(new ActionListener() {
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

        // Holmes site menu item
        JMenuItem holmesSiteItem = new JMenuItem(bundle.getString("systray.holmes.home"));
        holmesSiteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI("http://ccheneau.github.com/Holmes/"));
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    } catch (URISyntaxException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });

        // Holmes wiki menu item
        JMenuItem holmesWikiItem = new JMenuItem(bundle.getString("systray.holmes.wiki"));
        holmesWikiItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://github.com/ccheneau/Holmes/wiki"));
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    } catch (URISyntaxException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });

        // Add items to popup menu
        popupMenu.add(holmesUiItem);
        popupMenu.addSeparator();
        popupMenu.add(holmesSiteItem);
        popupMenu.add(holmesWikiItem);
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

    /**
     * System tray icon 
     * Freely inspired from <a href="http://grepcode.com/file/repo1.maven.org/maven2/org.jvnet.hudson.plugins.hudsontrayapp/client-jdk16/0.7.3/org/jdesktop/swinghelper/tray/JXTrayIcon.java">org.jdesktop.swinghelper.tray.JXTrayIcon</a> class (under LGPL v2.1 license)
     */
    public static class SystemTrayIcon extends TrayIcon {

        private JPopupMenu popupMenu;
        private static final JDialog dialog;

        static {
            dialog = new JDialog((Frame) null, "HolmesSysTray");
            dialog.setUndecorated(true);
            dialog.setAlwaysOnTop(true);
        }

        private static final PopupMenuListener popupListener = new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                dialog.setVisible(false);
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                dialog.setVisible(false);
            }
        };

        public SystemTrayIcon(Image image, String tooltip) {
            super(image, tooltip);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showPopupMenu(e);
                }
            });
        }

        private void showPopupMenu(MouseEvent e) {
            if (popupMenu != null) {
                Dimension size = popupMenu.getPreferredSize();
                dialog.setLocation(e.getX(), e.getY() - size.height);
                dialog.setVisible(true);
                popupMenu.show(dialog.getContentPane(), 0, 0);
                dialog.toFront();
            }
        }

        public void setPopupMenu(JPopupMenu popupMenu) {
            if (this.popupMenu != null) this.popupMenu.removePopupMenuListener(popupListener);

            if (popupMenu != null) {
                this.popupMenu = popupMenu;
                this.popupMenu.addPopupMenuListener(popupListener);
            }
        }
    }
}
