/*
 * Copyright (C) 2012-2014  Cedric Cheneau
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

import net.holmes.core.common.Service;
import net.holmes.core.common.StaticResourceLoader;
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
import static net.holmes.core.common.StaticResourceLoader.StaticResourceDir.SYSTRAY;
import static net.holmes.core.common.configuration.Parameter.*;

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
        if (configuration.getBooleanParameter(ENABLE_SYSTRAY) && initUIManager()) initSystemTrayMenu();
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
        if (!SystemTray.isSupported() || !Desktop.isDesktopSupported()) return;

        // Initialize systray icon
        Image image;
        try {
            image = Toolkit.getDefaultToolkit().createImage(StaticResourceLoader.getData(SYSTRAY, "logo.png"));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return;
        }
        final SystemTrayIcon holmesTrayIcon = new SystemTrayIcon(image, resourceBundle.getString("systray.title"));
        final SystemTray systemTray = SystemTray.getSystemTray();
        final String holmesAdminUrl = "http://localhost:" + configuration.getIntParameter(HTTP_SERVER_PORT) + "/admin";

        // Create a popup menu
        final JPopupMenu popupMenu = new JPopupMenu();

        boolean showMenuIcon = configuration.getBooleanParameter(ICONS_IN_SYSTRAY_MENU);

        // Quit Holmes menu item
        JMenuItem quitItem = new SystrayMenuItem() {
            @Override
            public void onClick() {
                System.exit(0);
            }
        }.getMenuItem(resourceBundle.getString("systray.quit"), "icon-exit.png", showMenuIcon, null);

        // Holmes logs menu item
        JMenuItem logsItem = new SystrayMenuItem() {
            @Override
            public void onClick() throws IOException {
                Desktop.getDesktop().open(Paths.get(localHolmesDataDir, "log", "holmes.log").toFile());
            }
        }.getMenuItem(resourceBundle.getString("systray.logs"), "icon-logs.png", showMenuIcon, null);

        // Holmes admin ui menu item
        JMenuItem holmesAdminUiItem = new SystrayMenuItem() {
            @Override
            public void onClick() throws URISyntaxException, IOException {
                Desktop.getDesktop().browse(new URI(holmesAdminUrl));
            }
        }.getMenuItem(resourceBundle.getString("systray.holmes.ui"), "icon-logo.png", showMenuIcon, UIManager.getFont(MENU_ITEM_BOLD_FONT));

        // Holmes site menu item
        JMenuItem holmesSiteItem = new SystrayMenuItem() {
            @Override
            public void onClick() throws URISyntaxException, IOException {
                Desktop.getDesktop().browse(new URI(HOLMES_SITE_URL.toString()));
            }
        }.getMenuItem(resourceBundle.getString("systray.holmes.home"), "icon-site.png", showMenuIcon, null);

        // Holmes wiki menu item
        JMenuItem holmesWikiItem = new SystrayMenuItem() {
            @Override
            public void onClick() throws URISyntaxException, IOException {
                Desktop.getDesktop().browse(new URI(HOLMES_WIKI_URL.toString()));
            }
        }.getMenuItem(resourceBundle.getString("systray.holmes.wiki"), "icon-info.png", showMenuIcon, null);

        // Add items to popup menu
        popupMenu.add(holmesAdminUiItem);
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
         * @param font     menu item font
         * @return menu item
         */
        public JMenuItem getMenuItem(final String text, final String iconPath, final boolean showIcon, final Font font) {
            Icon icon = null;
            if (showIcon)
                try {
                    icon = new ImageIcon(StaticResourceLoader.getData(SYSTRAY, iconPath));
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }

            JMenuItem menuItem = new JMenuItem(text, icon);
            if (font != null) menuItem.setFont(font);

            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    try {
                        onClick();
                    } catch (URISyntaxException | IOException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            });
            return menuItem;
        }

        /**
         * Fires onClick event.
         *
         * @throws URISyntaxException
         * @throws IOException
         */
        public abstract void onClick() throws URISyntaxException, IOException;
    }
}
