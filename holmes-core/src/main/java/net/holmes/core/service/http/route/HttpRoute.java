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

package net.holmes.core.service.http.route;

/**
 * Http route for Holmes web applications
 */
public class HttpRoute {
    private final String path;
    private final String defaultFile;

    /**
     * Instantiates a new Http route
     *
     * @param path        route path
     * @param defaultFile default welcome file
     */
    public HttpRoute(final String path, final String defaultFile) {
        this.path = path;
        this.defaultFile = defaultFile;
    }

    public String getPath() {
        return path;
    }

    public String getDefaultFile() {
        return defaultFile;
    }
}
