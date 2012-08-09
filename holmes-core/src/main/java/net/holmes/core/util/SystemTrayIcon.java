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
package net.holmes.core.util;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * System tray icon 
 * Freely inspired from <a href="http://grepcode.com/file/repo1.maven.org/maven2/org.jvnet.hudson.plugins.hudsontrayapp/client-jdk16/0.7.3/org/jdesktop/swinghelper/tray/JXTrayIcon.java">org.jdesktop.swinghelper.tray.JXTrayIcon</a> class (under GPL v2.1 license)
 */
public class SystemTrayIcon extends TrayIcon {

    private JPopupMenu popupMenu;
    private static JDialog dialog;

    static {
        dialog = new JDialog((Frame) null, "TrayDialog");
        dialog.setUndecorated(true);
        dialog.setAlwaysOnTop(true);
    }

    private static PopupMenuListener popupListener = new PopupMenuListener() {

        /* (non-Javadoc)
         * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent)
         */
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        }

        /* (non-Javadoc)
         * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent)
         */
        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            dialog.setVisible(false);
        }

        /* (non-Javadoc)
         * @see javax.swing.event.PopupMenuListener#popupMenuCanceled(javax.swing.event.PopupMenuEvent)
         */
        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
            dialog.setVisible(false);
        }

    };

    public SystemTrayIcon(Image image, String tooltip) {
        super(image, tooltip);
        addMouseListener(new MouseAdapter() {

            /* (non-Javadoc)
             * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                showJPopupMenu(e);
            }
        });
    }

    private void showJPopupMenu(MouseEvent e) {
        if (popupMenu != null) {
            Dimension size = popupMenu.getPreferredSize();
            dialog.setLocation(e.getX(), e.getY() - size.height);
            dialog.setVisible(true);
            popupMenu.show(dialog.getContentPane(), 0, 0);
            dialog.toFront();
        }
    }

    public void setJPopupMenu(JPopupMenu popupMenu) {
        if (this.popupMenu != null) {
            this.popupMenu.removePopupMenuListener(popupListener);
        }

        if (popupMenu != null) {
            this.popupMenu = popupMenu;
            this.popupMenu.addPopupMenuListener(popupListener);
        }
    }
}
