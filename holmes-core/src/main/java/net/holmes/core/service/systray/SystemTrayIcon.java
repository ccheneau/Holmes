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

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * System tray icon.
 * Freely inspired from <a href="http://grepcode.com/file/repo1.maven.org/maven2/org.jvnet.hudson.plugins.hudsontrayapp/client-jdk16/0.7.3/org/jdesktop/swinghelper/tray/JXTrayIcon.java">org.jdesktop.swinghelper.tray.JXTrayIcon</a> class (under LGPL v2.1 license)
 */
public final class SystemTrayIcon extends TrayIcon {
    private final JDialog popupDialog;
    private final JPopupMenu popupMenu;

    /**
     * Instantiates a new system tray icon.
     *
     * @param image     system tray image
     * @param tooltip   system tray tooltip
     * @param popupMenu popup menu
     */
    public SystemTrayIcon(final Image image, final String tooltip, final JPopupMenu popupMenu) {
        super(image, tooltip);

        setImageAutoSize(true);

        // Initialize popup dialog
        this.popupDialog = new JDialog((Frame) null, "HolmesSysTray");
        this.popupDialog.setUndecorated(true);
        this.popupDialog.setAlwaysOnTop(true);

        // Initialize popup menu
        this.popupMenu = popupMenu;
        this.popupMenu.addPopupMenuListener(
                new PopupMenuListener() {
                    @Override
                    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                        // Nothing
                    }

                    @Override
                    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                        popupDialog.setVisible(false);
                    }

                    @Override
                    public void popupMenuCanceled(PopupMenuEvent e) {
                        popupDialog.setVisible(false);
                    }
                }
        );

        // Add mouse listener
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent event) {
                showPopupMenu(event);
            }
        });
    }

    /**
     * Show popup menu.
     *
     * @param event mouse event
     */
    private void showPopupMenu(final MouseEvent event) {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        int maxWindowHeight = env.getMaximumWindowBounds().height;

        // Get y location for popup menu
        int yLocation;
        if (event.getY() > maxWindowHeight) {
            // TaskBar is on bottom of the screen
            yLocation = maxWindowHeight - popupMenu.getPreferredSize().height;
        } else {
            // TaskBar is on top of the screen
            yLocation = env.getDefaultScreenDevice().getDisplayMode().getHeight() - maxWindowHeight;
        }

        // Show popup menu
        popupDialog.setLocation(event.getX(), yLocation);
        popupDialog.setVisible(true);
        popupMenu.show(popupDialog.getContentPane(), 0, 0);
        popupDialog.toFront();
    }
}
