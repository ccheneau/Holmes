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


import javax.inject.Provider;

/**
 * Provider for Http route manager.
 */
public class HttpRouteManagerProvider implements Provider<HttpRouteManager> {
    private static final String DEFAULT_WELCOME_FILE = "/index.html";

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRouteManager get() {
        HttpRouteManager routeManager = new HttpRouteManagerImpl();

        // Add default routes
        routeManager.addHttpRoute(new HttpRoute("", DEFAULT_WELCOME_FILE));
        routeManager.addHttpRoute(new HttpRoute("/admin", DEFAULT_WELCOME_FILE));
        routeManager.addHttpRoute(new HttpRoute("/play", DEFAULT_WELCOME_FILE));

        return routeManager;
    }
}
