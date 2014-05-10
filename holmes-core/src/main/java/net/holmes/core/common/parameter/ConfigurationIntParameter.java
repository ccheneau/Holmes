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

package net.holmes.core.common.parameter;

/**
 * Integer implementation for configuration parameter.
 */
public class ConfigurationIntParameter extends ConfigurationParameter<Integer> {

    /**
     * Instantiates a new Integer configuration parameter.
     *
     * @param name         parameter name
     * @param defaultValue default value
     */
    ConfigurationIntParameter(String name, Integer defaultValue) {
        super(name, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer parse(String stringValue) {
        return Integer.valueOf(stringValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String format(Integer value) {
        return value.toString();
    }
}
