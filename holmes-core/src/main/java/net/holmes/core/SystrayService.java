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

import net.holmes.core.common.ResourceLoader;
import net.holmes.core.common.Service;
import net.holmes.core.common.SystemTrayIcon;
import net.holmes.core.common.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import static net.holmes.core.common.Constants.HOLMES_SITE_URL;
import static net.holmes.core.common.Constants.HOLMES_WIKI_URL;
import static net.holmes.core.common.ResourceLoader.ResourceDir.SYSTRAY;
import static net.holmes.core.common.configuration.Parameter.ENABLE_SYSTRAY;
import static net.holmes.core.common.configuration.Parameter.ICONS_IN_SYSTRAY_MENU;

/**
 * Manages system tray icon.
 */
public final class SystrayService implements Service {
    private static final String MENU_ITEM_FONT = "MenuItem.font";
    private static final String MENU_ITEM_BOLD_FONT = "MenuItem.bold.font";
    private static final Logger LOGGER = LoggerFactory.getLogger(SystrayService.class);
    private final Configuration configuration;
    private final ResourceBundle resourceBundle;
    private final String localHolmesDataDir;

    /**
     * Instantiates a new systray service.
     *
     * @param configuration      configuration
     * @param resourceBundle     resource bundle
     * @param localHolmesDataDir local Holmes data directory
     */
    @Inject
    public SystrayService(final Configuration configuration, final ResourceBundle resourceBundle, @Named("localHolmesDataDir") String localHolmesDataDir) {
        this.configuration = configuration;
        this.resourceBundle = resourceBundle;
        this.localHolmesDataDir = localHolmesDataDir;
    }

    @Override
    public void start() {
        // Add system tray icon
        if (configuration.getParameter(ENABLE_SYSTRAY) && initUIManager()) initSystemTrayMenu();
    }

    @Override
    public void stop() {
        // Nothing
    }

    /**
     * Initializes UI manager.
     *
     * @return true on success
     */
    private boolean initUIManager() {
        boolean result = true;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Add bold font for systray menu item
            Font menuItemFont = UIManager.getFont(MENU_ITEM_FONT);
            if (menuItemFont != null)
                UIManager.put(MENU_ITEM_BOLD_FONT, new FontUIResource(menuItemFont.getFamily(), Font.BOLD, menuItemFont.getSize()));

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            result = false;
        }
        return result;
    }

    /**
     * Initialize system tray menu.
     */
    private void initSystemTrayMenu() {
        // Check the SystemTray is supported
        if (!SystemTray.isSupported()) return;

        // Initialize systray icon
        Image image;
        try {
            image = Toolkit.getDefaultToolkit().createImage(ResourceLoader.getData(SYSTRAY, "logo.png"));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return;
        }
        final SystemTrayIcon holmesTrayIcon = new SystemTrayIcon(image, resourceBundle.getString("systray.title"));
        final SystemTray systemTray = SystemTray.getSystemTray();

        // Create a popup menu
        final JPopupMenu popupMenu = new JPopupMenu();

        boolean showMenuIcon = configuration.getParameter(ICONS_IN_SYSTRAY_MENU);

        // Quit Holmes menu item
        JMenuItem quitItem = new SystrayMenuItem() {
            @Override
            public void onClick(ActionEvent event) {
                try {
                    System.exit(0);
                } catch (SecurityException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }.getMenuItem(resourceBundle.getString("systray.quit"), "icon-exit.png", showMenuIcon);

        // Holmes logs menu item
        JMenuItem logsItem = new SystrayMenuItem() {
            @Override
            public void onClick(ActionEvent event) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(Paths.get(localHolmesDataDir, "log", "holmes.log").toFile());
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
        }.getMenuItem(resourceBundle.getString("systray.logs"), "icon-logs.png", showMenuIcon);

        // Holmes ui menu item
        JMenuItem holmesUiItem = new SystrayMenuItem() {
            @Override
            public void onClick(ActionEvent event) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        String holmesUrl = "http://localhost:" + configuration.getHttpServerPort() + "/";
                        Desktop.getDesktop().browse(new URI(holmesUrl));
                    } catch (IOException | URISyntaxException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
        }.getMenuItem(resourceBundle.getString("systray.holmes.ui"), "icon-logo.png", showMenuIcon, UIManager.getFont(MENU_ITEM_BOLD_FONT));

        // Holmes site menu item
        JMenuItem holmesSiteItem = new SystrayMenuItem() {
            @Override
            public void onClick(ActionEvent event) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI(HOLMES_SITE_URL.toString()));
                    } catch (IOException | URISyntaxException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
        }.getMenuItem(resourceBundle.getString("systray.holmes.home"), "icon-site.png", showMenuIcon);

        // Holmes wiki menu item
        JMenuItem holmesWikiItem = new SystrayMenuItem() {
            @Override
            public void onClick(ActionEvent event) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI(HOLMES_WIKI_URL.toString()));
                    } catch (IOException | URISyntaxException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
        }.getMenuItem(resourceBundle.getString("systray.holmes.wiki"), "icon-info.png", showMenuIcon);

        // Add items to popup menu
        popupMenu.add(holmesUiItem);
        popupMenu.addSeparator();
        popupMenu.add(holmesSiteItem);
        popupMenu.add(holmesWikiItem);
        popupMenu.add(logsItem);
        popupMenu.addSeparator();
        popupMenu.add(quitItem);

        // Add tray icon
        holmesTrayIcon.setImageAutoSize(true);
        holmesTrayIcon.setPopupMenu(popupMenu);
        try {
            systemTray.add(holmesTrayIcon);
        } catch (AWTException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * System tray menu item.
     */
    private abstract static class SystrayMenuItem {

        /**
         * Get menu item.
         *
         * @param text     menu item text
         * @param iconPath path to icon resource
         * @param showIcon whether to show icon
         * @return menu item
         */
        public JMenuItem getMenuItem(final String text, final String iconPath, final boolean showIcon) {
            return getMenuItem(text, iconPath, showIcon, null);
        }

        /**
         * Get menu item.
         *
         * @param text     menu item text
         * @param iconPath path to icon resource
         * @param showIcon whether to show icon
         * @param font     menu item font
         * @return menu item
         */
        public JMenuItem getMenuItem(final String text, final String iconPath, final boolean showIcon, final Font font) {
            Icon icon = null;
            if (showIcon)
                try {
                    icon = new ImageIcon(ResourceLoader.getData(SYSTRAY, iconPath));
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }

            JMenuItem menuItem = new JMenuItem(text, icon);
            if (font != null) menuItem.setFont(font);

            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    onClick(event);
                }
            });
            return menuItem;
        }

        /**
         * Fires onClick event.
         *
         * @param event event
         */
        public abstract void onClick(final ActionEvent event);
    }
}
