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

import static net.holmes.core.common.Constants.HOLMES_APPLICATION_WELCOME_FILE;

/**
 * Holmes web applications
 */
public enum WebApplication {
    DEFAULT("", HOLMES_APPLICATION_WELCOME_FILE),
    ADMIN("/admin", HOLMES_APPLICATION_WELCOME_FILE),
    PLAY("/play", HOLMES_APPLICATION_WELCOME_FILE);

    /**
     * Web application path on file system.
     */
    private final String path;

    /**
     * Web application welcome file name.
     */
    private final String welcomeFile;

    /**
     * Instantiates a new web application.
     *
     * @param path        web application path on file system
     * @param welcomeFile web application welcome file
     */
    private WebApplication(final String path, final Constants welcomeFile) {
        this.path = path;
        this.welcomeFile = welcomeFile.toString();
    }

    /**
     * Get web application path.
     *
     * @return web application path
     */
    public String getPath() {
        return path;
    }

    /**
     * Get web application welcome file.
     *
     * @return web application welcome file
     */
    public String getWelcomeFile() {
        return welcomeFile;
    }

    /**
     * Find web application by path.
     *
     * @param path web application path to find
     * @return found web application or null
     */
    public static WebApplication findByPath(final String path) {
        for (WebApplication webApplication : values()) {
            if (webApplication.getPath().equals(path)) {
                return webApplication;
            }
        }
        return null;
    }
}
