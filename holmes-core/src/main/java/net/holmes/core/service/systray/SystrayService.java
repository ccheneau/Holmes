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

package net.holmes.core.service.systray;

import net.holmes.core.business.configuration.ConfigurationDao;
import net.holmes.core.service.Service;
import org.slf4j.Logger;

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

import static java.awt.Desktop.getDesktop;
import static java.awt.Desktop.isDesktopSupported;
import static java.awt.Font.BOLD;
import static java.awt.SystemTray.getSystemTray;
import static java.awt.SystemTray.isSupported;
import static java.awt.Toolkit.getDefaultToolkit;
import static javax.swing.UIManager.getFont;
import static net.holmes.core.common.ConfigurationParameter.*;
import static net.holmes.core.common.Constants.HOLMES_SITE_URL;
import static net.holmes.core.common.Constants.HOLMES_WIKI_URL;
import static net.holmes.core.common.StaticResourceLoader.StaticResourceDir.SYSTRAY;
import static net.holmes.core.common.StaticResourceLoader.getData;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Manages system tray icon.
 */
public final class SystrayService implements Service {
    private static final Logger LOGGER = getLogger(SystrayService.class);
    private static final String MENU_ITEM_FONT = "MenuItem.font";
    private static final String MENU_ITEM_BOLD_FONT = "MenuItem.bold.font";

    private final ConfigurationDao configurationDao;
    private final ResourceBundle resourceBundle;
    private final String localHolmesDataDir;

    /**
     * Instantiates a new systray service.
     *
     * @param configurationDao   configuration dao
     * @param resourceBundle     resource bundle
     * @param localHolmesDataDir local Holmes data directory
     */
    @Inject
    public SystrayService(final ConfigurationDao configurationDao, final ResourceBundle resourceBundle, @Named("localHolmesDataDir") String localHolmesDataDir) {
        this.configurationDao = configurationDao;
        this.resourceBundle = resourceBundle;
        this.localHolmesDataDir = localHolmesDataDir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        // Add system tray icon
        if (configurationDao.getParameter(SYSTRAY_ENABLE) && initUIManager()) initSystemTrayMenu();
    }

    /**
     * {@inheritDoc}
     */
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
            Font menuItemFont = getFont(MENU_ITEM_FONT);
            if (menuItemFont != null)
                UIManager.put(MENU_ITEM_BOLD_FONT, new FontUIResource(menuItemFont.getFamily(), BOLD, menuItemFont.getSize()));

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
        if (!isSupported() || !isDesktopSupported()) return;

        // Initialize systray icon
        Image image;
        try {
            image = getDefaultToolkit().createImage(getData(SYSTRAY, "logo.png"));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return;
        }
        final SystemTrayIcon holmesTrayIcon = new SystemTrayIcon(image, resourceBundle.getString("systray.title"));
        final SystemTray systemTray = getSystemTray();
        final String holmesAdminUrl = "http://localhost:" + configurationDao.getParameter(HTTP_SERVER_PORT) + "/admin";

        // Create a popup menu
        final JPopupMenu popupMenu = new JPopupMenu();

        boolean showMenuIcon = configurationDao.getParameter(SYSTRAY_ICONS_IN_MENU);

        // Quit Holmes menu item
        JMenuItem quitItem = new SystrayMenuItem() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void onClick() {
                System.exit(0);
            }
        }.getMenuItem(resourceBundle.getString("systray.quit"), "icon-exit.png", showMenuIcon, null);

        // Holmes logs menu item
        JMenuItem logsItem = new SystrayMenuItem() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void onClick() throws IOException {
                getDesktop().open(Paths.get(localHolmesDataDir, "log", "holmes.log").toFile());
            }
        }.getMenuItem(resourceBundle.getString("systray.logs"), "icon-logs.png", showMenuIcon, null);

        // Holmes admin ui menu item
        JMenuItem holmesAdminUiItem = new SystrayMenuItem() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void onClick() throws URISyntaxException, IOException {
                getDesktop().browse(new URI(holmesAdminUrl));
            }
        }.getMenuItem(resourceBundle.getString("systray.holmes.ui"), "icon-logo.png", showMenuIcon, getFont(MENU_ITEM_BOLD_FONT));

        // Holmes site menu item
        JMenuItem holmesSiteItem = new SystrayMenuItem() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void onClick() throws URISyntaxException, IOException {
                getDesktop().browse(new URI(HOLMES_SITE_URL.toString()));
            }
        }.getMenuItem(resourceBundle.getString("systray.holmes.home"), "icon-site.png", showMenuIcon, null);

        // Holmes wiki menu item
        JMenuItem holmesWikiItem = new SystrayMenuItem() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void onClick() throws URISyntaxException, IOException {
                getDesktop().browse(new URI(HOLMES_WIKI_URL.toString()));
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
                    icon = new ImageIcon(getData(SYSTRAY, iconPath));
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }

            JMenuItem menuItem = new JMenuItem(text, icon);
            if (font != null) menuItem.setFont(font);

            menuItem.addActionListener(new ActionListener() {
                /**
                 * {@inheritDoc}
                 */
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
