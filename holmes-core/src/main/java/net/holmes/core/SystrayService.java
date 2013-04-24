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
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.FontUIResource;

import net.holmes.common.Service;
import net.holmes.common.SystemUtils;
import net.holmes.common.configuration.Configuration;
import net.holmes.common.configuration.Parameter;
import net.holmes.core.inject.Loggable;

import org.slf4j.Logger;

/**
 * Manages system tray icon.
 */
@Loggable
public final class SystrayService implements Service {
    private Logger logger;

    private static final String HOLMES_SITE_URL = "http://ccheneau.github.io/Holmes/";
    private static final String HOLMES_WIKI_URL = "https://github.com/ccheneau/Holmes/wiki";

    private final Configuration configuration;
    private final ResourceBundle resourceBundle;

    /**
     * Constructor.
     * @param configuration
     *      configuration
     * @param resourceBundle
     *      resource bundle
     */
    @Inject
    public SystrayService(final Configuration configuration, final ResourceBundle resourceBundle) {
        this.configuration = configuration;
        this.resourceBundle = resourceBundle;
    }

    @Override
    public void start() {
        // Add system tray icon
        if (configuration.getParameter(Parameter.ENABLE_SYSTRAY) && initUI()) initSystemTrayIcon();
    }

    @Override
    public void stop() {
        // Nothing
    }

    /**
     * UI initialization.
     * @return true on success
     */
    private boolean initUI() {
        boolean result = true;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Add bold font for systray menu item
            Font menuItemFont = UIManager.getFont("MenuItem.font");
            if (menuItemFont != null) {
                FontUIResource menuItemBoldFont = new FontUIResource(menuItemFont.getFamily(), Font.BOLD, menuItemFont.getSize());
                UIManager.put("MenuItem.bold.font", menuItemBoldFont);
            }
        } catch (ClassNotFoundException e) {
            result = false;
        } catch (InstantiationException e) {
            result = false;
        } catch (IllegalAccessException e) {
            result = false;
        } catch (UnsupportedLookAndFeelException e) {
            result = false;
        }
        return result;
    }

    /**
     * Initialize system tray icon.
     */
    private void initSystemTrayIcon() {
        // Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            return;
        }

        // Initialize systray icon
        final Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/logo.png"));
        final SystemTrayIcon systemTrayIcon = new SystemTrayIcon(image, resourceBundle.getString("systray.title"));
        final SystemTray systemTray = SystemTray.getSystemTray();

        // Create a popup menu
        final JPopupMenu popupMenu = new JPopupMenu();

        // Quit Holmes menu item
        Icon holmesExitIcon = new ImageIcon(getClass().getResource("/icon-exit.png"));
        JMenuItem quitItem = new JMenuItem(resourceBundle.getString("systray.quit"), holmesExitIcon);
        quitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                try {
                    System.exit(0);
                } catch (SecurityException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });

        // Holmes logs menu item
        JMenuItem logsItem = new JMenuItem(resourceBundle.getString("systray.logs"));
        logsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        StringBuilder logFile = new StringBuilder().append(SystemUtils.getLocalUserDataDir().getAbsolutePath()).append(File.separator)
                                .append("log").append(File.separator).append("holmes.log");
                        Desktop.getDesktop().open(new File(logFile.toString()));
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });

        // Holmes ui menu item
        Icon holmesUiIcon = new ImageIcon(getClass().getResource("/icon-logo.png"));
        JMenuItem holmesUiItem = new JMenuItem(resourceBundle.getString("systray.holmes.ui"), holmesUiIcon);
        Font boldFont = UIManager.getFont("MenuItem.bold.font");
        if (boldFont != null) holmesUiItem.setFont(boldFont);

        holmesUiItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        StringBuilder holmesUrl = new StringBuilder().append("http://localhost:").append(configuration.getHttpServerPort()).append("/");
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
        Icon holmesSiteIcon = new ImageIcon(getClass().getResource("/icon-site.png"));
        JMenuItem holmesSiteItem = new JMenuItem(resourceBundle.getString("systray.holmes.home"), holmesSiteIcon);
        holmesSiteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI(HOLMES_SITE_URL));
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    } catch (URISyntaxException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });

        // Holmes wiki menu item
        JMenuItem holmesWikiItem = new JMenuItem(resourceBundle.getString("systray.holmes.wiki"));
        holmesWikiItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI(HOLMES_WIKI_URL));
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
     * System tray icon.
     * Freely inspired from <a href="http://grepcode.com/file/repo1.maven.org/maven2/org.jvnet.hudson.plugins.hudsontrayapp/client-jdk16/0.7.3/org/jdesktop/swinghelper/tray/JXTrayIcon.java">org.jdesktop.swinghelper.tray.JXTrayIcon</a> class (under LGPL v2.1 license)
     */
    public static class SystemTrayIcon extends TrayIcon {

        private JPopupMenu popupMenu;
        private static final JDialog DIALOG;

        static {
            DIALOG = new JDialog((Frame) null, "HolmesSysTray");
            DIALOG.setUndecorated(true);
            DIALOG.setAlwaysOnTop(true);
        }

        private static final PopupMenuListener POPUP_LISTENER = new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(final PopupMenuEvent event) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(final PopupMenuEvent event) {
                DIALOG.setVisible(false);
            }

            @Override
            public void popupMenuCanceled(final PopupMenuEvent event) {
                DIALOG.setVisible(false);
            }
        };

        /**
         * SystemTrayIcon Constructor.
         * @param image
         *      icon image
         * @param tooltip
         *      icon tooltip
         */
        public SystemTrayIcon(final Image image, final String tooltip) {
            super(image, tooltip);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(final MouseEvent event) {
                    showPopupMenu(event);
                }
            });
        }

        /**
         * Show popup menu.
         * @param event
         *      mose event
         */
        private void showPopupMenu(final MouseEvent event) {
            if (popupMenu != null) {
                Dimension size = popupMenu.getPreferredSize();
                DIALOG.setLocation(event.getX(), event.getY() - size.height);
                DIALOG.setVisible(true);
                popupMenu.show(DIALOG.getContentPane(), 0, 0);
                DIALOG.toFront();
            }
        }

        /**
         * Set popup menu.
         * @param popupMenu
         *      popup menu
         */
        public void setPopupMenu(final JPopupMenu popupMenu) {
            if (this.popupMenu != null) this.popupMenu.removePopupMenuListener(POPUP_LISTENER);

            if (popupMenu != null) {
                this.popupMenu = popupMenu;
                this.popupMenu.addPopupMenuListener(POPUP_LISTENER);
            }
        }
    }
}
