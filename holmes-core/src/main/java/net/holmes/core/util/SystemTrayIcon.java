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

    private JPopupMenu menu;
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
        if (menu != null) {
            Dimension size = menu.getPreferredSize();
            dialog.setLocation(e.getX(), e.getY() - size.height);
            dialog.setVisible(true);
            menu.show(dialog.getContentPane(), 0, 0);
            dialog.toFront();
        }
    }

    public void setJPopupMenu(JPopupMenu menu) {
        if (this.menu != null) {
            this.menu.removePopupMenuListener(popupListener);
        }

        this.menu = menu;
        menu.addPopupMenuListener(popupListener);

    }
}
