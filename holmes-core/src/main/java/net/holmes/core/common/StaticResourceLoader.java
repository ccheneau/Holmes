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

package net.holmes.core.common;

import org.fourthline.cling.model.meta.Icon;

import java.io.IOException;
import java.nio.file.Paths;

import static java.nio.file.Files.readAllBytes;
import static net.holmes.core.common.SystemProperty.HOLMES_HOME;

/**
 * Loader for Holmes static resources
 */
public final class StaticResourceLoader {
    /**
     * Resource directory name under HOLMES_HOME directory
     */
    private static final String RESOURCE_PATH = "resources";

    /**
     * Private constructor
     */
    private StaticResourceLoader() {
        // Nothing
    }

    /**
     * Get static resource data.
     *
     * @param resourceDir resource sub directory
     * @param fileName    resource file name
     * @return resource byte data
     * @throws IOException if resource is not found
     */
    public static byte[] getData(final StaticResourceDir resourceDir, final String fileName) throws IOException {
        return readAllBytes(Paths.get(HOLMES_HOME.getValue(), RESOURCE_PATH, resourceDir.getValue(), fileName));
    }

    /**
     * Get UPnP icon.
     *
     * @param icon icon
     * @return UPnP icon
     * @throws IOException
     */
    public static Icon getUpnpIcon(final UpnpIcon icon) throws IOException {
        return new Icon(icon.getMimeType(), icon.getSize(), icon.getSize(), icon.getDepth(), icon.getName(), getData(StaticResourceDir.UPNP, icon.getPath()));
    }

    /**
     * Static resources directory.
     */
    public enum StaticResourceDir {
        UPNP("upnp"),
        SYSTRAY("systray");

        private final String value;

        /**
         * New static resource directory.
         *
         * @param value value
         */
        private StaticResourceDir(final String value) {
            this.value = value;
        }

        /**
         * Gets the resource sub directory value.
         *
         * @return the resource sub directory value
         */
        public String getValue() {
            return value;
        }
    }

    /**
     * UPnP icon.
     */
    public enum UpnpIcon {
        SMALL("icon-32.png", "image/png", 32, 8),
        LARGE("icon-256.png", "image/png", 256, 8);

        private final String name;
        private final String path;
        private final String mimeType;
        private final int size;
        private final int depth;

        /**
         * Instantiates a new UPnP icon.
         *
         * @param path     icon file path
         * @param mimeType icon mime type
         * @param size     icon size
         * @param depth    icon depth
         */
        private UpnpIcon(String path, String mimeType, int size, int depth) {
            this.name = "upnp-" + path;
            this.path = path;
            this.mimeType = mimeType;
            this.size = size;
            this.depth = depth;
        }

        /**
         * Get icon name.
         *
         * @return icon name
         */
        public String getName() {
            return name;
        }

        /**
         * Get icon path.
         *
         * @return icon path
         */
        public String getPath() {
            return path;
        }

        /**
         * Get icon mime type.
         *
         * @return icon mime type
         */
        public String getMimeType() {
            return mimeType;
        }

        /**
         * Get icon size.
         *
         * @return icon size
         */
        public int getSize() {
            return size;
        }

        /**
         * Get icon depth.
         *
         * @return icon depth
         */
        public int getDepth() {
            return depth;
        }
    }
}
