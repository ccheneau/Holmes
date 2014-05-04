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
    private static final String RESOURCE_PATH = "resources";
    private static final int LARGE_ICON_SIZE = 120;
    private static final int SMALL_ICON_SIZE = 32;
    private static final int ICON_DEPTH = 8;
    private static final String ICON_MIME_TYPE = "image/png";

    /**
     * Private constructor
     */
    private StaticResourceLoader() {
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
     * Get UPnP large icon.
     *
     * @return large icon
     * @throws IOException
     */
    public static Icon getUpnpLargeIcon() throws IOException {
        return new Icon(ICON_MIME_TYPE, LARGE_ICON_SIZE, LARGE_ICON_SIZE, ICON_DEPTH, "upnp-icon-256.png", getData(StaticResourceDir.UPNP, "icon-256.png"));
    }

    /**
     * Get UPnP small icon.
     *
     * @return small icon
     * @throws IOException
     */
    public static Icon getUpnpSmallIcon() throws IOException {
        return new Icon(ICON_MIME_TYPE, SMALL_ICON_SIZE, SMALL_ICON_SIZE, ICON_DEPTH, "upnp-icon-32.png", getData(StaticResourceDir.UPNP, "icon-32.png"));
    }

    /**
     * Static resources directory.
     */
    public enum StaticResourceDir {
        UPNP("upnp"), //
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
}
