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

import java.util.Map;

import static com.google.common.collect.Maps.newConcurrentMap;

/**
 * Http route manager implementation
 */
public class HttpRouteManagerImpl implements HttpRouteManager {
    private final Map<String, HttpRoute> routes;

    /**
     * Instantiates a new Http route manager implementation
     */
    public HttpRouteManagerImpl() {
        routes = newConcurrentMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addHttpRoute(HttpRoute route) {
        routes.put(route.getPath(), route);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRoute getHttpRoute(String path) {
        return routes.get(path);
    }
}
