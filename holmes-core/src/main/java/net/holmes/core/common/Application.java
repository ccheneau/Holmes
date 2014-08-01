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
 * Holmes applications
 */
public enum Application {
    DEFAULT("", HOLMES_APPLICATION_WELCOME_FILE.toString()),
    ADMIN("/admin", HOLMES_APPLICATION_WELCOME_FILE.toString()),
    PLAY("/play", HOLMES_APPLICATION_WELCOME_FILE.toString());

    /**
     * Application path.
     */
    private final String path;

    /**
     * Application welcome file name.
     */
    private final String welcomeFile;

    /**
     * Instantiates a new Holmes application.
     *
     * @param path        application path
     * @param welcomeFile application welcome file
     */
    private Application(final String path, final String welcomeFile) {
        this.path = path;
        this.welcomeFile = welcomeFile;
    }

    /**
     * Get application path.
     *
     * @return application path
     */
    public String getPath() {
        return path;
    }

    /**
     * Get application welcome file.
     *
     * @return application welcome file
     */
    public String getWelcomeFile() {
        return welcomeFile;
    }

    /**
     * Find application by path.
     *
     * @param path application path to find
     * @return found application or null
     */
    public static Application findByPath(final String path) {
        for (Application application : values()) {
            if (application.getPath().equals(path)) {
                return application;
            }
        }
        return null;
    }
}
