/*
 * Copyright (C) 2012-2013  Cedric Cheneau
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

package net.holmes.core.backend.exception;

/**
 * Configuration exception.
 */
public class ConfigurationException extends RuntimeException {
    private static final long serialVersionUID = 4365631762884392538L;

    /**
     * Instantiates a new configuration exception.
     *
     * @param exception the exception
     */
    public ConfigurationException(final Throwable exception) {
        super(exception);
    }
}
