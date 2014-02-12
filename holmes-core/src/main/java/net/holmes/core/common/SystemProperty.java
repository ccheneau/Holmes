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

/**
 * Java system properties used by Holmes.
 */
public enum SystemProperty {
    HOLMES_HOME("net.holmes.home"), //
    USER_HOME("user.home");

    private final String name;

    /**
     * Instantiates a new system property.
     *
     * @param name name
     */
    private SystemProperty(final String name) {
        this.name = name;
    }

    /**
     * Gets the system property name.
     *
     * @return the system property name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the system property value.
     *
     * @return the system property value
     */
    public String getValue() {
        return System.getProperty(name);
    }
}
