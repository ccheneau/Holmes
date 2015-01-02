/*
 * Copyright (C) 2012-2015  Cedric Cheneau
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

import net.holmes.core.business.configuration.ConfigurationManager;
import net.holmes.core.common.exception.HolmesException;
import net.holmes.core.service.Service;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
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

import static java.awt.Desktop.*;
import static java.awt.Font.BOLD;
import static java.awt.SystemTray.*;
import static java.awt.Toolkit.getDefaultToolkit;
import static javax.swing.UIManager.getFont;
import static net.holmes.core.common.ConfigurationParameter.*;
import static net.holmes.core.common.Constants.*;
import static net.holmes.core.common.StaticResourceLoader.StaticResourceDir.SYSTRAY;
import static net.holmes.core.common.StaticResourceLoader.getData;
import static net.holmes.core.common.WebApplication.ADMIN;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Manages system tray icon.
 */
@Singleton
public final class SystrayService implements Service {
    private static final Logger LOGGER = getLogger(SystrayService.class);
    private static final String MENU_ITEM_FONT = "MenuItem.font";
    private static final String MENU_ITEM_BOLD_FONT = "MenuItem.bold.font";

    private final ConfigurationManager configurationManager;
    private final ResourceBundle resourceBundle;
    private final String localHolmesDataDir;

    /**
     * Instantiates a new systray service.
     *
     * @param configurationManager configuration manager
     * @param resourceBundle       resource bundle
     * @param localHolmesDataDir   local Holmes data directory
     */
    @Inject
    public SystrayService(final ConfigurationManager configurationManager, final ResourceBundle resourceBundle, @Named("localHolmesDataDir") final String localHolmesDataDir) {
        this.configurationManager = configurationManager;
        this.resourceBundle = resourceBundle;
        this.localHolmesDataDir = localHolmesDataDir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        // Add system tray icon
        if (configurationManager.getParameter(SYSTRAY_ENABLE) && initUIManager()) {
            initSystemTrayMenu();
        }
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
            if (menuItemFont != null) {
                UIManager.put(MENU_ITEM_BOLD_FONT, new FontUIResource(menuItemFont.getFamily(), BOLD, menuItemFont.getSize()));
            }

        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
            LOGGER.error(e.getMessage(), e);
            result = false;
        }
        return result;
    }

    /**
     * Initialize system tray menu.
     */
    private void initSystemTrayMenu() {
        // Check the SystemTray is supported
        if (!isSupported() || !isDesktopSupported()) {
            return;
        }

        // Initialize systray image
        Image sysTrayImage;
        try {
            sysTrayImage = getDefaultToolkit().createImage(getData(SYSTRAY, "logo.png"));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return;
        }

        // Create a popup menu
        JPopupMenu popupMenu = new JPopupMenu();

        // Add items to popup menu
        popupMenu.add(buildUIMenuItem());
        popupMenu.add(new JSeparatorEx());
        popupMenu.add(buildSiteMenuItem());
        popupMenu.add(buildWikiMenuItem());
        popupMenu.add(buildLogsMenuItem());
        popupMenu.add(new JSeparatorEx());
        popupMenu.add(buildQuitMenuItem());

        try {
            // Add system tray icon
            getSystemTray().add(new SystemTrayIcon(sysTrayImage, resourceBundle.getString("systray.title"), popupMenu));
        } catch (AWTException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Build Holmes wiki menu item.
     *
     * @return Holmes wiki menu item
     */
    private JMenuItem buildWikiMenuItem() {
        // Holmes wiki menu item
        return new SystrayMenuItem() {
            @Override
            public void onClick() throws HolmesException {
                try {
                    getDesktop().browse(new URI(HOLMES_WIKI_URL.toString()));
                } catch (URISyntaxException | IOException e) {
                    throw new HolmesException(e);
                }
            }
        }.getMenuItem(resourceBundle.getString("systray.holmes.wiki"), "icon-info.png", null);
    }

    /**
     * Build Holmes site menu item.
     *
     * @return Holmes site menu item
     */
    private JMenuItem buildSiteMenuItem() {
        return new SystrayMenuItem() {
            @Override
            public void onClick() throws HolmesException {
                try {
                    getDesktop().browse(new URI(HOLMES_SITE_URL.toString()));
                } catch (URISyntaxException | IOException e) {
                    throw new HolmesException(e);
                }
            }
        }.getMenuItem(resourceBundle.getString("systray.holmes.home"), "icon-site.png", null);
    }

    /**
     * Build Holmes UI menu item.
     *
     * @return Holmes UI menu item
     */
    private JMenuItem buildUIMenuItem() {
        final String holmesAdminUrl = "http://localhost:" + configurationManager.getParameter(HTTP_SERVER_PORT) + ADMIN.getPath();
        return new SystrayMenuItem() {
            @Override
            public void onClick() throws HolmesException {
                try {
                    getDesktop().browse(new URI(holmesAdminUrl));
                } catch (URISyntaxException | IOException e) {
                    throw new HolmesException(e);
                }
            }
        }.getMenuItem(resourceBundle.getString("systray.holmes.ui"), "icon-logo.png", getFont(MENU_ITEM_BOLD_FONT));
    }

    /**
     * Build Holmes logs menu item.
     *
     * @return Holmes logs menu item
     */
    private JMenuItem buildLogsMenuItem() {
        return new SystrayMenuItem() {
            @Override
            public void onClick() throws HolmesException {
                try {
                    getDesktop().open(Paths.get(localHolmesDataDir, "log", "holmes.log").toFile());
                } catch (IOException e) {
                    throw new HolmesException(e);
                }
            }
        }.getMenuItem(resourceBundle.getString("systray.logs"), "icon-logs.png", null);
    }

    /**
     * Build Holmes quit menu item.
     *
     * @return Holmes quit menu item
     */
    private JMenuItem buildQuitMenuItem() {
        return new SystrayMenuItem() {
            @Override
            public void onClick() {
                System.exit(0);
            }
        }.getMenuItem(resourceBundle.getString("systray.quit"), "icon-exit.png", null);
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
         * @param font     menu item font
         * @return menu item
         */
        public JMenuItem getMenuItem(final String text, final String iconPath, final Font font) {
            Icon icon = null;
            try {
                icon = new ImageIcon(getData(SYSTRAY, iconPath));
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }

            JMenuItem menuItem = new JMenuItem(text, icon);
            if (font != null) {
                menuItem.setFont(font);
            }

            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    try {
                        onClick();
                    } catch (HolmesException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            });
            return menuItem;
        }

        /**
         * Fires onClick event.
         *
         * @throws HolmesException
         */
        public abstract void onClick() throws HolmesException;
    }

    /**
     * Extends JSeparator with minimum height
     */
    private static final class JSeparatorEx extends JSeparator {
        private static final int SEPARATOR_MIN_HEIGHT = 4;

        /**
         * {@inheritDoc}
         */
        @Override
        public Dimension getPreferredSize() {
            Dimension dimension = super.getPreferredSize();
            if (dimension.height == 0) {
                dimension.height = SEPARATOR_MIN_HEIGHT;
            }
            return dimension;
        }
    }
}
